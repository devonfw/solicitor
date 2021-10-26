/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.licensetexts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.content.ContentProvider;
import com.devonfw.tools.solicitor.common.content.web.WebContent;

/**
 * A {@link LicenseUrlGuesser} which tries to strategically find a possible better license URL.
 */
public class StrategyLicenseUrlGuesser implements LicenseUrlGuesser {

  private static final Logger LOG = LoggerFactory.getLogger(StrategyLicenseUrlGuesser.class);

  private ContentProvider<WebContent> webContentProvider;

  /**
   * The constructor.
   *
   * @param webContentProvider the provider for retrieving the content of URLs
   */
  public StrategyLicenseUrlGuesser(ContentProvider<WebContent> webContentProvider) {

    this.webContentProvider = webContentProvider;
  }

  // helper method that returns true if the given url is available and false if the given url is a 404 not found page
  private boolean pingURL(String url, StringBuilder traceBuilder) {

    WebContent content = this.webContentProvider.getContentForUri(url);
    if (content != null && content.getContent() != null && !content.getContent().isEmpty()) {
      setTrace("Found license." + "\n", traceBuilder);
      return true;
    } else {
      setTrace("Nothing found" + "\n", traceBuilder);
      return false;
    }
  }

  private void setTrace(String trace, StringBuilder traceBuilder) {

    traceBuilder.append(trace).append('\n');
  }

  // https://github.com/nodelib/nodelib/tree/master/packages/fs/fs.stat
  // https://raw.githubusercontent.com/nodelib/nodelib/master/packages/fs/fs.stat/README.md
  // helper method that normalizes a github url and retrieves the raw link to a given license
  private String normalizeGitURL(String url, StringBuilder traceBuilder) {

    String oldURL = url;
    if (url.contains("github.com") && url.contains("raw/")) {
      // case that declared license points to old raw link github license file; change it to new
      url = url.replace("github.com", "raw.githubusercontent.com");
      url = url.replace("raw/", "");
      setTrace("URL changed from " + oldURL + " to " + url, traceBuilder);
      return url;
    } else if (url.contains("github.com") && url.contains("blob/")) {
      // case that declared license points to github non raw license file; change it to raw
      url = url.replace("github.com", "raw.githubusercontent.com");
      url = url.replace("blob/", "");
      setTrace("URL changed from " + oldURL + " to " + url, traceBuilder);
      return url;
    } else if (url.contains("github.com") && url.contains("tree/")) {
      // case that declared license points to github non raw license file; change it to raw
      url = url.replace("tree/", "");
      setTrace("URL changed from " + oldURL + " to " + url, traceBuilder);
      return normalizeGitURL(url, traceBuilder);
    } else if (url.contains("github.com") && !(url.contains("blob/")) && !(url.contains("tree/"))) {
      // case that declared license points to main github page but not file
      // try different variations of license names in the mainfolder / take readme if non existent
      String[] licensefilenames = { "LICENSE", "License", "license", "LICENSE.md", "LICENSE.txt", "license.html",
      "license.txt", "COPYING", "LICENSE-MIT", "LICENSE-MIT.txt", "README.md", "Readme.md", "readme.md",
      "README.markdown" };
      url = url.replace("github.com", "raw.githubusercontent.com");
      for (String name : licensefilenames) {
        String testURL;
        if (!url.contains("/master/")) {
          testURL = url.concat("/master/" + name);
        } else {
          testURL = url.concat("/" + name);
        }
        setTrace("Searching for license: testing with " + testURL, traceBuilder);
        if (pingURL(testURL, traceBuilder) == true) {
          setTrace("URL changed from " + oldURL + " to " + testURL, traceBuilder);
          return normalizeGitURL(testURL, traceBuilder);
        }
      }
    }
    return oldURL;
  }

  // // helper method that normalizes a readme file; cuts off overhead and preserves license text
  // private String normalizeReadMe(String readme) {
  //
  // int licenseIndex = readme.toLowerCase().indexOf("License");
  // if (licenseIndex != -1) {
  // return readme.substring(licenseIndex);
  // } else {
  // return "Could not retrieve license information in this repository (License file/Readme) \n Please check manually.";
  // }
  // }

  // @Override
  // public WebContentObject getWebContentForUrl(WebContentObject webObj) {
  //
  // String url = webObj.getUrl();
  // webObj.setEffectiveURL(normalizeGitURL(url));
  // WebContentObject copyObj = this.directWebContentProvider.getWebContentForUrl(webObj);
  // webObj.setContent(copyObj.getContent());
  // // checks if a readme url contains the keyword "license" and cuts out overhead
  // if (webObj.getEffectiveURL().contains("README.md")) {
  // webObj.setContent(normalizeReadMe(webObj.getContent()));
  // }
  // webObj.setTrace(this.trace);
  // return webObj;
  // }

  @Override
  public GuessedLicenseUrlContent getContentForUri(String uri) {

    StringBuilder auditLogBuilder = new StringBuilder();
    String guessedUrl = normalizeGitURL(uri, auditLogBuilder);
    return new GuessedLicenseUrlContent(guessedUrl, auditLogBuilder.toString());
  }

}
