/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.gradle;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.common.InputStreamFactory;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.AbstractReader;
import com.devonfw.tools.solicitor.reader.Reader;
import com.devonfw.tools.solicitor.reader.gradle.model.Dependency;
import com.devonfw.tools.solicitor.reader.gradle.model.License;
import com.devonfw.tools.solicitor.reader.gradle.model.LicenseSummary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component
public class GradleReader extends AbstractReader implements Reader {

    @Autowired
    private InputStreamFactory inputStreamFactory;

    @Override
    public String getSupportedType() {

        return "gradle";
    }

    @Override
    public void readInventory(String sourceUrl, Application application,
            UsagePattern usagePattern) {

        String input = "";
        LicenseSummary ls = new LicenseSummary();
        ls.setDependencies(new LinkedList<Dependency>());

        // According to tutorial https://github.com/FasterXML/jackson-databind/
        ObjectMapper mapper =
                new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {
            List<Map> l = mapper.readValue(
                    inputStreamFactory.createInputStreamFor(sourceUrl),
                    List.class);
            l = l.subList(1, l.size());
            for (Map<String, Object> m : l) {
                Dependency dep = new Dependency();
                dep.setProject((String) m.get("project"));
                dep.setVersion((String) m.get("version"));
                dep.setUrl((String) m.get("url"));
                dep.setYear((String) m.get("year"));
                dep.setDependency((String) m.get("dependency"));
                List<Map> lml = (List) m.get("licenses");
                List<License> ll = new LinkedList();
                for (Map<String, String> ml : lml) {
                    License license = new License();
                    license.setLicense(ml.get("license"));
                    license.setLicense_url(ml.get("license_url"));
                    ll.add(license);
                }
                dep.setLicenses(ll);
                ls.getDependencies().add(dep);
            }

        } catch (IOException e) {
            throw new SolicitorRuntimeException(
                    "Could not read Gradle inventory source +'" + sourceUrl
                            + "'",
                    e);
        }

        for (Dependency dep : ls.getDependencies()) {
            ApplicationComponent appComponent =
                    getModelFactory().newApplicationComponent();
            appComponent.setApplication(application);
            appComponent.setGroupId(dep.getProject());
            appComponent.setArtifactId(dep.getDependency());
            appComponent.setVersion(dep.getVersion());
            appComponent.setOssHomepage(dep.getUrl());
            appComponent.setUsagePattern(usagePattern);
            if (dep.getLicenses().isEmpty()) {
                // in case no license is found insert an empty entry
                addRawLicense(appComponent, null, null, sourceUrl);
            } else {
                for (License lic : dep.getLicenses()) {
                    addRawLicense(appComponent, lic.getLicense(),
                            lic.getLicense_url(), sourceUrl);

                }
            }
        }
    }

    /**
     * This method sets the field <tt>inputStreamFactory</tt>.
     *
     * @param inputStreamFactory the new value of the field inputStreamFactory
     */
    public void setInputStreamFactory(InputStreamFactory inputStreamFactory) {

        this.inputStreamFactory = inputStreamFactory;
    }

}
