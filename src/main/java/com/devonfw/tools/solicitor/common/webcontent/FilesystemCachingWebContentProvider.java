/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.webcontent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.IOHelper;
import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;

/**
 * A {@link CachingWebContentProviderBase} which tries to load web content from
 * the file system.
 *
 */
@Component
public class FilesystemCachingWebContentProvider extends CachingWebContentProviderBase {

    private static final Logger LOG = LoggerFactory.getLogger(FilesystemCachingWebContentProvider.class);

    @Autowired
    private StrategyWebContentProvider strategyWebContentProvider;

    /**
     * Constructor.
     */
    public FilesystemCachingWebContentProvider() {

    }

    /**
     * {@inheritDoc}
     * 
     * Points to a subdirectory "licenses" in the current working directory.
     */
    @Override
    protected Collection<String> getCacheUrls(String key) {

        return Collections.singleton("file:licenses/" + key);
    }

    /**
     * {@inheritDoc}
     * 
     * Delegates to {@link DirectUrlWebContentProvider}. The result returned
     * will be stored in the file system in the subdirectory "licenses" of the
     * current working directory so that it will be taken from there in
     * subsequent attempts to load the same web content again.
     */
    @Override
    public WebContentObject loadFromNext(WebContentObject webObj) {
    	WebContentObject copyObj = strategyWebContentProvider.getWebContentForUrl(webObj);
    	webObj.setContent(copyObj.getContent());
        webObj.setEffectiveURL(copyObj.getEffectiveURL());
        webObj.setTrace(copyObj.getTrace());
        String result = webObj.getContent();
        writeTrace(webObj);
        File file = new File("licenses/" + getKey(webObj.getUrl()));
        File targetDir = file.getParentFile();
        
        try {
            IOHelper.checkAndCreateLocation(file);
        } catch (SolicitorRuntimeException e) {
            LOG.error(LogMessages.COULD_NOT_CREATE_CACHE.msg(), targetDir.getAbsolutePath());
            return webObj;
        }
        try (FileWriter fw = new FileWriter(file)) {
            if (result != null) {
                fw.append(result);
            }
        } catch (IOException e) {
            LOG.error("Could not write data to file cache.");
        }
        

        return webObj;
    }
    
    private void writeTrace(WebContentObject webObj) {
        String trace = webObj.getTrace();
        if(!trace.isEmpty()) {
	        strategyWebContentProvider.clearTrace();
	        File traceFile = new File("licenses/trace/trace_" + getKey(webObj.getUrl()));
	        File targetDirTrace = traceFile.getParentFile();
	        
	        try {
	        	IOHelper.checkAndCreateLocation(traceFile);
	        } catch(SolicitorRuntimeException e) {
	        	LOG.error(LogMessages.COULD_NOT_CREATE_CACHE.msg(), targetDirTrace.getAbsolutePath());
	        }
	        try (FileWriter fwt = new FileWriter(traceFile)) {
	            if (trace != null) {
	                fwt.append(trace);
	            }
	        } catch (IOException e) {
	            LOG.error("Could not write data to file trace cache.");
	        }
        }
    }

}
