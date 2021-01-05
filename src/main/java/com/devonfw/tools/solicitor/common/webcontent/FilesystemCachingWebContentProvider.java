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
    public String loadFromNext(String url) {
    	//TODO here we can see how to trace into a file 
        String result = strategyWebContentProvider.getWebContentForUrl(url);

        File file = new File("licenses/" + getKey(url));
        File targetDir = file.getParentFile();
        try {
            IOHelper.checkAndCreateLocation(file);
        } catch (SolicitorRuntimeException e) {
            LOG.error(LogMessages.COULD_NOT_CREATE_CACHE.msg(), targetDir.getAbsolutePath());
            return result;
        }
        try (FileWriter fw = new FileWriter(file)) {
            if (result != null) {
                fw.append(result);
            }
        } catch (IOException e) {
            LOG.error("Could not write data to file cache.");
        }

        return result;
    }

}
