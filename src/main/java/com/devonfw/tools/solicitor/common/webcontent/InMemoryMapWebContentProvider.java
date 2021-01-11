/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.webcontent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.IOHelper;
import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;

/**
 * A {@link WebContentProvider} which tries to lookup web content in a local in
 * memory cache.
 */
@Component
public class InMemoryMapWebContentProvider implements WebContentProvider {

    @Autowired
    private ClasspathWebContentProvider classPathWebContentProvider;
    
    int counter = 0;

    Map<String, String> contentMap = new TreeMap<>();

    /**
     * Constructor.
     */
    public InMemoryMapWebContentProvider() {

    }

    /**
     * {@inheritDoc}
     * 
     * Tries to find the web content in an in memory map. If not found then it
     * delegates to {@link ClasspathWebContentProvider}. The result will be
     * stored in the in Memory map for further calls to the same URL.
     */
    @Override
    public WebContentObject getWebContentForUrl(WebContentObject webObj) {
    	//TODO object in höhere Ebene erstellen statt nur URL
    	String url = webObj.getUrl();
        if (url == null) {
            return webObj;
        }

        if (contentMap.containsKey(url)) {
        	webObj.setContent(contentMap.get(url));
        } else {
            WebContentObject copyObj = classPathWebContentProvider.getWebContentForUrl(webObj);
            webObj.setContent(copyObj.getContent());
            webObj.setEffectiveURL(copyObj.getEffectiveURL());
            webObj.setTrace(copyObj.getTrace());
            writeTest(webObj);
            contentMap.put(url, webObj.getContent());
        }
        return webObj;
    }

    private void writeTest(WebContentObject webObj) {
	        File traceFile = new File("licenses/test/test_" + counter);
	        counter++;
	        File targetDirTrace = traceFile.getParentFile();
	   
	        try {
	        	IOHelper.checkAndCreateLocation(traceFile);
	        } catch(SolicitorRuntimeException e) {
	        	
	        }
	        try (FileWriter fwt = new FileWriter(traceFile)) {
	                fwt.append(webObj.getContent() + webObj.getEffectiveURL() + webObj.getTrace() + webObj.getUrl());


	            
	        } catch (IOException e) {
	        	
	        }
        }
    
}
