/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.maven;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.AbstractReader;
import com.devonfw.tools.solicitor.reader.Reader;
import com.devonfw.tools.solicitor.reader.maven.model.Dependency;
import com.devonfw.tools.solicitor.reader.maven.model.License;
import com.devonfw.tools.solicitor.reader.maven.model.LicenseSummary;

/**
 * A {@link Reader} which reads data produced by the
 * <a href="https://www.mojohaus.org/license-maven-plugin/">Maven License
 * Plugin</a>.
 */
@Component
public class MavenReader extends AbstractReader implements Reader {

    /**
     * The supported type of this {@link Reader}.
     */
    public static final String SUPPORTED_TYPE = "maven";

    /** {@inheritDoc} */
    @Override
    public Set<String> getSupportedTypes() {

        return Collections.singleton(SUPPORTED_TYPE);
    }

    /** {@inheritDoc} */
    @Override
    public void readInventory(String type, String sourceUrl, Application application, UsagePattern usagePattern,
            String repoType, Map<String,String> configuration) {

        int components = 0;
        int licenses = 0;
        InputStream is;
        try {
            is = inputStreamFactory.createInputStreamFor(sourceUrl);
        } catch (IOException e1) {
            throw new SolicitorRuntimeException("Could not open inventory source '" + sourceUrl + "' for reading", e1);
        }
        LicenseSummary ls;

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(LicenseSummary.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ls = (LicenseSummary) unmarshaller.unmarshal(is);
        } catch (JAXBException e) {
            throw new SolicitorRuntimeException("Could nor read maven license info", e);
        }

        for (Dependency dep : ls.getDependencies()) {
            ApplicationComponent appComponent = getModelFactory().newApplicationComponent();
            appComponent.setApplication(application);
            appComponent.setGroupId(dep.getGroupId());
            appComponent.setArtifactId(dep.getArtifactId());
            appComponent.setVersion(dep.getVersion());
            appComponent.setUsagePattern(usagePattern);
            appComponent.setRepoType(repoType);
            components++;
            if (dep.getLicenses().isEmpty()) {
                // in case no license is found insert an empty entry
                addRawLicense(appComponent, null, null, sourceUrl);
            } else {
                for (License lic : dep.getLicenses()) {
                    licenses++;
                    addRawLicense(appComponent, lic.getName(), lic.getUrl(), sourceUrl);
                }
            }
        }
        doLogging(sourceUrl, application, components, licenses);
    }

}
