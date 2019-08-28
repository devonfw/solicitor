/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.webcontent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FilesystemCachingWebContentProvider
        extends CachingWebContentProviderBase {

    @Autowired
    private HttpWebContentProvider httpWebContentProvider;

    public FilesystemCachingWebContentProvider() {

        // TODO Auto-generated constructor stub
    }

    /**
     * @param key
     * @return
     */
    @Override
    protected String getCacheUrl(String key) {

        return "file:licenses/" + key;
    }

    @Override
    public String loadFromNext(String url) {

        String result = httpWebContentProvider.getWebContentForUrl(url);

        File file = new File("licenses/" + getKey(url));
        File targetDir = file.getParentFile();
        try {
            if (!targetDir.exists()) {
                Files.createDirectories(targetDir.toPath());
            }
        } catch (IOException e) {
            LOG.error(
                    "Could not create directory '{}' for caching downloaded web resources. Could not write data to file cache.",
                    targetDir.getAbsolutePath());
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
