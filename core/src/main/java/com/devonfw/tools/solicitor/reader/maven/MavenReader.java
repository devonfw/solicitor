/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.maven;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.devonfw.tools.solicitor.common.PackageURLHelper;
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
 * A {@link Reader} which reads data produced by the <a href="https://www.mojohaus.org/license-maven-plugin/">Maven
 * License Plugin</a>.
 */
@Component
public class MavenReader extends AbstractReader implements Reader {
  private static final Logger LOG = LoggerFactory.getLogger(MavenReader.class);

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
      String repoType, String packageType, Map<String, String> configuration) {

    int components = 0;
    int licenses = 0;
    InputStream is;
    try {
      is = this.inputStreamFactory.createInputStreamFor(sourceUrl);
    } catch (IOException e1) {
      throw new SolicitorRuntimeException("Could not open inventory source '" + sourceUrl + "' for reading", e1);
    }
    LicenseSummary ls;

    JAXBContext jaxbContext;
    try {
      SAXParserFactory secureParserFactory = SAXParserFactory.newInstance();
      secureParserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      secureParserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      secureParserFactory.setXIncludeAware(false);

      Source xmlSource = new SAXSource(secureParserFactory.newSAXParser().getXMLReader(), new InputSource(is));

      jaxbContext = JAXBContext.newInstance(LicenseSummary.class);
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      ls = (LicenseSummary) unmarshaller.unmarshal(xmlSource);
    } catch (JAXBException | SAXException | ParserConfigurationException e) {
      throw new SolicitorRuntimeException("Could not read maven license info", e);
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          LOG.debug("Exception while attemping to close inputs stream for reading maven license data", e);
        }
      }
    }

    for (Dependency dep : ls.getDependencies()) {
      ApplicationComponent appComponent = getModelFactory().newApplicationComponent();
      appComponent.setApplication(application);
      appComponent.setGroupId(dep.getGroupId());
      appComponent.setArtifactId(dep.getArtifactId());
      appComponent.setVersion(dep.getVersion());
      appComponent.setUsagePattern(usagePattern);
      appComponent.setRepoType(repoType);
      appComponent.setPackageUrl(
          PackageURLHelper.fromMavenCoordinates(dep.getGroupId(), dep.getArtifactId(), dep.getVersion()).toString());
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
