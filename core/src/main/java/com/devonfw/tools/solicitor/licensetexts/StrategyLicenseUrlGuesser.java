/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.licensetexts;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.devonfw.tools.solicitor.SolicitorVersion;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.common.content.ContentProvider;
import com.devonfw.tools.solicitor.common.content.web.WebContent;

/**
 * A {@link LicenseUrlGuesser} which tries to strategically find a possible better license URL.
 */
public class StrategyLicenseUrlGuesser implements LicenseUrlGuesser {

  private static final Logger LOG = LoggerFactory.getLogger(StrategyLicenseUrlGuesser.class);

  private ContentProvider<WebContent> webContentProvider;

  private SolicitorVersion solicitorVersion;
  
  @Value("${solicitor.githubtoken}")
  private String token;

  /**
   * The constructor.
   *
   * @param webContentProvider the provider for retrieving the content of URLs
   * @param solicitorVersion version info to be used in audit info
   */
  public StrategyLicenseUrlGuesser(ContentProvider<WebContent> webContentProvider, SolicitorVersion solicitorVersion) {

    this.webContentProvider = webContentProvider;
    this.solicitorVersion = solicitorVersion;
  }

  // helper method that returns true if the given url is available and false
  // if the given url is a 404 not found page
  private boolean pingURL(String url, StringBuilder traceBuilder) {

    WebContent content = this.webContentProvider.getContentForUri(url);
    if (content != null && content.getContent() != null && !content.getContent().isEmpty()) {
      setTrace("Found content." + "\n", traceBuilder);
      return true;
    } else {
      setTrace("Nothing found" + "\n", traceBuilder);
      return false;
    }
  }

  private void setTrace(String trace, StringBuilder traceBuilder) {

    traceBuilder.append(trace).append('\n');
  }

  // helper method that normalizes a github url and retrieves the raw link to
  // a given license
  private String normalizeGitURL(String url, StringBuilder traceBuilder) {
    String oldURL = url;
    // case that github remote repository link is given
    if (url.contains("github.com") && url.endsWith(".git")) {
      url = githubAPILicenseUrl(url, token);
      if(!url.equals(oldURL) && !url.contains("api.github.com")) {
        setTrace("URL changed from " + oldURL + " to " + url, traceBuilder);
        return url;
      }
    }
    if (url.contains("github.com")) {
      // use https for all github URLs
      url = url.replace("http:", "https:");
      // omit repo suffix if existent
      url = url.replace(".git", "");
    }
    if (url.contains("github.com") && url.contains("raw/")) {
      // case that declared license points to old raw link github license
      // file; change it to new
      url = url.replace("github.com", "raw.githubusercontent.com");
      url = url.replace("raw/", "");
      setTrace("URL changed from " + oldURL + " to " + url, traceBuilder);
      return url;
    } else if (url.contains("github.com") && url.contains("blob/")) {
      // case that declared license points to github non raw license file;
      // change it to raw
      url = url.replace("github.com", "raw.githubusercontent.com");
      url = url.replace("blob/", "");
      setTrace("URL changed from " + oldURL + " to " + url, traceBuilder);
      return url;
    } else if (url.contains("github.com") && url.contains("tree/")) {
      // case that declared license points to github non raw license file;
      // change it to raw
      url = url.replace("tree/", "");
      setTrace("URL changed from " + oldURL + " to " + url, traceBuilder);
      return normalizeGitURL(url, traceBuilder);
    } else if (url.contains("github.com") && !(url.contains("blob/")) && !(url.contains("tree/"))) {
      // case that declared license points to main github page but not
      // file
      // try different variations of license names in the mainfolder /
      // take readme if non existent
      String[] licensefilenames = { "LICENSE", "License", "license", "LICENSE.md", "LICENSE.txt", "license.html",
      "license.txt", "COPYING", "LICENSE-MIT", "LICENSE-MIT.txt", "README.md", "Readme.md", "readme.md",
      "README.markdown" };
      url = url.replace("github.com", "raw.githubusercontent.com");
      if (url.endsWith("/")) {
        url = url.substring(0, url.length() - 1);
      }
      for (String name : licensefilenames) {
        String testURL;
        if (url.contains("/master/") || url.contains("/main/")) {
          testURL = url.concat("/" + name);
        } else {
          testURL = url.concat("/master/" + name);
        }
        setTrace("Searching for license: testing with " + testURL, traceBuilder);
        if (pingURL(testURL, traceBuilder) == true) {
          setTrace("URL changed from " + oldURL + " to " + testURL, traceBuilder);
          // return normalizeGitURL(testURL, traceBuilder);
          return testURL;
        }
        if (testURL.contains("master")) {
          testURL = testURL.replace("master", "main");
          setTrace("Searching for license: testing with " + testURL, traceBuilder);
          if (pingURL(testURL, traceBuilder) == true) {
            setTrace("URL changed from " + oldURL + " to " + testURL, traceBuilder);
            // return normalizeGitURL(testURL, traceBuilder);
            return testURL;

          }
        }
      }
    }
    if (url.contains("http:")) {
      String testURL = url.replace("http:", "https:");
      setTrace("Searching for license: testing with " + testURL, traceBuilder);
      if (pingURL(testURL, traceBuilder) == true) {
        setTrace("URL changed from " + oldURL + " to " + testURL, traceBuilder);
        return testURL;
      }
    }
    return oldURL;
  }

  @Override
  public GuessedLicenseUrlContent getContentForUri(String uri) {

    StringBuilder auditLogBuilder = new StringBuilder();
    String now = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    setTrace("(URL guessed by Solicitor " + this.solicitorVersion.getVersion() + " ["
        + this.solicitorVersion.getGithash() + "], " + now + " / unconfirmed)\n", auditLogBuilder);
    String guessedUrl = normalizeGitURL(uri, auditLogBuilder);
    if (uri != null && uri.equals(guessedUrl)) {
      setTrace("Original URL remains unchanged.\n", auditLogBuilder);
    }
    setTrace("Resulting URL: " + guessedUrl, auditLogBuilder);
    pingURL(guessedUrl, auditLogBuilder);

    return new GuessedLicenseUrlContent(guessedUrl, auditLogBuilder.toString());
  }

  //tries to get github license file location based of vsc-link 
  public String githubAPILicenseUrl(String link, String token) {

    String result = "";
    if (link.contains("github.com")) {
      if (link.endsWith(".git")) {
        link = link.substring(0, link.length() - 4);
    }
    link = link.replace("git://", "https://");
    link = link.replace("ssh://", "https://");
    link = link.replace("git@", "");
    if (!link.contains("api.github.com")) {
      link = link.replace("github.com/", "api.github.com/repos/");
      link = link.concat("/license");
    }
  
    String command = "curl -H \"Accept: application/vnd.github+json\" -H \"Authorization: token "+ token + "\" -i " + link;
    ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
    try {
      Process process = processBuilder.start();
      InputStream inputStream = process.getInputStream();
      result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        if (result.contains("download_url")) {
          result = result.substring(result.indexOf("\"download_url\": "));
          result = result.substring(17,result.indexOf(",")-1);
        }
        if (result.contains("\"message\": \"Moved Permanently\"")) {
          String tempLink = result.substring(result.indexOf("\"url\": "));
          tempLink = tempLink.substring(17,result.indexOf(",")-1);
          result = githubAPILicenseUrl(tempLink, token);
        }
        if (result.contains("\"message\": \"Not Found\"")) {
           result = link;
         }
      } catch (IOException e) {
        throw new SolicitorRuntimeException("Could not handle command call for api request'" + command + "'", e);
      }
    }
    return result;
  }  
}
