/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.maven;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.common.InputStreamFactory;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.AbstractReader;
import com.devonfw.tools.solicitor.reader.Reader;
import com.devonfw.tools.solicitor.reader.maven.model.Dependency;
import com.devonfw.tools.solicitor.reader.maven.model.License;
import com.devonfw.tools.solicitor.reader.maven.model.LicenseSummary;

import lombok.Setter;

@Component
public class MavenReader extends AbstractReader implements Reader {

    @Autowired
    @Setter
    private InputStreamFactory inputStreamFactory;

    @Override
    public String getSupportedType() {

        return "maven";
    }

    @Override
    public void readInventory(String sourceUrl, Application application,
            UsagePattern usagePattern) {

        // File file = new File(source);
        InputStream is;
        try {
            is = inputStreamFactory.createInputStreamFor(sourceUrl);
        } catch (IOException e1) {
            throw new SolicitorRuntimeException(
                    "Could not open inventory source +'" + sourceUrl
                            + "' for reading",
                    e1);
        }
        LicenseSummary ls;

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(LicenseSummary.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ls = (LicenseSummary) unmarshaller.unmarshal(is);
        } catch (JAXBException e) {
            throw new SolicitorRuntimeException(
                    "Could nor read maven license info", e);
        }

        for (Dependency dep : ls.getDependencies()) {
            ApplicationComponent appComponent = new ApplicationComponent();
            appComponent.setApplication(application);
            appComponent.setGroupId(dep.getGroupId());
            appComponent.setArtifactId(dep.getArtifactId());
            appComponent.setVersion(dep.getVersion());
            appComponent.setUsagePattern(usagePattern);
            if (dep.getLicenses().isEmpty()) {
                // in case no license is found insert an empty entry
                addRawLicense(appComponent, null, null, sourceUrl);
            } else {
                for (License lic : dep.getLicenses()) {
                    addRawLicense(appComponent, lic.getName(), lic.getUrl(),
                            sourceUrl);
                }
            }
        }
    }

}
