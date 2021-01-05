/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.webcontent;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.LogMessages;

/**
 * A {@link WebContentProvider} which tries to load the web content directly via
 * the given URL.
 */
@Component
public class DirectUrlWebContentProvider implements WebContentProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DirectUrlWebContentProvider.class);

    @Value("${webcontent.skipdownload}")
    private boolean skipdownload;

    /**
     * Constructor.
     */
    public DirectUrlWebContentProvider() {

    }

	//helper method that returns true if the given url is available and false if the given url is a 404 not found page
    private boolean pingURL(String url) {
    	try {
			URL testURL = new URL(url);
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection pingConnection = (HttpURLConnection)testURL.openConnection();
			pingConnection.setRequestMethod("HEAD");
			if(pingConnection.getResponseCode() == 200) {
				return true;
			} else if(pingConnection.getResponseCode() == 404) {
				return false;
			}
		} catch (MalformedURLException e) {
            LOG.warn("Invalid URL syntax '" + url + "'", e);
            return false;
		} catch (IOException e) {
            LOG.warn("Could not retieve content for url '" + url + "'", e);
            return false;
		}
		return false;
    }
    
    //helper method that normalizes a github url and retrieves the raw link to a given license
    private String normalizeGitURL(String url) {
    	String oldURL = url;
    	if(url.contains("github.com") && url.contains("raw/")) {
    		//case that declared license points to old raw link github license file; change it to new
    		url = url.replace("github.com","raw.githubusercontent.com");
    		url = url.replace("raw/","");
    		return url;
    	}else if(url.contains("github.com") && url.contains("blob/")) {
    		//case that declared license points to github non raw license file; change it to raw
    		url = url.replace("github.com","raw.githubusercontent.com");
    		url = url.replace("blob/","");
    		return url;
    	} else if(url.contains("github.com") && !(url.contains("blob/"))){
    		//case that declared license points to main github page but not file
    		//try different variations of license names in the mainfolder / take readme if non existent
    		String[] licensefilenames = {"LICENSE","License","license","LICENSE.md","LICENSE.txt","license.html","license.txt","COPYING","LICENSE-MIT","LICENSE-MIT.txt","README.md","Readme.md","readme.md"};
			url = url.replace("github.com","raw.githubusercontent.com");
    		for (String name : licensefilenames) {
    			String testURL = url.concat("/master/"+name);
        		if(pingURL(testURL)==true) {
        			return normalizeGitURL(testURL);
        		}
    		}
    	}
    	return oldURL;
    }
    
    /**
     * {@inheritDoc}
     *
     * Directly tries to access the given URL via the web.
     */
    @Override
    public String getWebContentForUrl(String url) {
    	url = normalizeGitURL(url);
    	
        URL webContentUrl;
        if (url == null) {
            return null;
        }
        if (skipdownload) {
            LOG.info(LogMessages.SKIP_DOWNLOAD.msg(), url);
            return null;
        }
        try {
            webContentUrl = new URL(url);
        } catch (MalformedURLException e) {
            LOG.warn("Invalid URL syntax '" + url + "'", e);
            return null;
        }

        try (InputStream is = webContentUrl.openConnection().getInputStream(); Scanner s = new Scanner(is)) {
            s.useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            if(url.contains("README.md")) {
            	//checks if a readme url contains the keyword "license" and cuts out overhead
        		int licenseIndex = result.toLowerCase().indexOf("license");
                if(licenseIndex != -1) {
                	return result.substring(licenseIndex);	     
                } else {
                	return "Could not retrieve license information in this repository (License file/Readme) \n Please check manually:" + url;
                }
        	}
            return result;
        } catch (IOException e) {
            LOG.warn("Could not retieve content for url '" + url + "'", e);
        }
        return null;
    }

}
