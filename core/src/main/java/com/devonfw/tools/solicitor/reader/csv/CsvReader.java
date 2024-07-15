/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.PackageURLHelper;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.AbstractReader;
import com.devonfw.tools.solicitor.reader.Reader;
import com.devonfw.tools.solicitor.reader.maven.MavenReader;

/**
 * A {@link Reader} for files in CSV format.
 * <p>
 * CSV files need to be configured within the solicitor.cfg and contain at least following parameters:
 * </p>
 * <ul>
 * <li>artifactId</li>
 * <li>version</li>
 * </ul>
 * Other optional (but recommended) parameters are:
 * <ul>
 * <li>groupId</li>
 * <li>license</li>
 * <li>licenseUrl</li>
 * </ul>
 * It is also possible to overwrite the default machine charset with the "charset" option. Furthermore, settings
 * concerning the csv format can be configured according to the methods specified in:
 * https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.Builder.html These options can
 * also be used to overwrite an already existing predefined format which can be set with "format" from:
 * https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.Predefined.html
 */

@Component
public class CsvReader extends AbstractReader implements Reader {
  private static final Logger LOG = LoggerFactory.getLogger(CsvReader.class);

  /**
   * The supported type of this {@link Reader}.
   */
  public static final String SUPPORTED_TYPE = "csv";

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
      is = inputStreamFactory.createInputStreamFor(sourceUrl);

      if (configuration != null) {
        // CSVReader setting with customizable configuration
        java.io.Reader reader;

        if (configuration.get("charset") != null) {
          reader = new InputStreamReader(is, configuration.get("charset"));
        } else {
          reader = new InputStreamReader(is);
        }

        ApplicationComponent lastAppComponent = null;

        CSVFormat csvFormat;
        CSVFormat.Builder csvBuilder;

        // predefined format & overwrite values
        if (configuration.get("format") != null) {
          csvFormat = CSVFormat.valueOf(configuration.get("format"));
          csvBuilder = CSVFormat.Builder.create(csvFormat);
        } else {
          csvBuilder = CSVFormat.Builder.create();
        }

        if (configuration.get("allowDuplicateHeaderNames") != null) {
          if (configuration.get("allowDuplicateHeaderNames").equals("true")) {
            csvBuilder.setAllowDuplicateHeaderNames(true);
          } else {
            csvBuilder.setAllowDuplicateHeaderNames(false);
          }
        }

        if (configuration.get("allowMissingColumnNames") != null) {
          if (configuration.get("allowMissingColumnNames").equals("true")) {
            csvBuilder.setAllowMissingColumnNames(true);
          } else {
            csvBuilder.setAllowMissingColumnNames(false);
          }
        }

        if (configuration.get("autoFlush") != null) {
          if (configuration.get("autoFlush").equals("true")) {
            csvBuilder.setAutoFlush(true);
          } else {
            csvBuilder.setAutoFlush(false);
          }
        }

        if (configuration.get("commentMarker") != null) {
          csvBuilder.setCommentMarker(configuration.get("commentMarker").toCharArray()[0]);
        }

        if (configuration.get("delimiter") != null) {
          csvBuilder.setDelimiter(configuration.get("delimiter"));
        }

        if (configuration.get("escape") != null) {
          csvBuilder.setEscape(configuration.get("escape").toCharArray()[0]);
        }

        if (configuration.get("ignoreEmptyLines") != null) {
          if (configuration.get("ignoreEmptyLines").equals("true")) {
            csvBuilder.setIgnoreEmptyLines(true);
          } else {
            csvBuilder.setIgnoreEmptyLines(false);
          }
        }

        if (configuration.get("ignoreHeaderCase") != null) {
          if (configuration.get("ignoreHeaderCase").equals("true")) {
            csvBuilder.setIgnoreHeaderCase(true);
          } else {
            csvBuilder.setIgnoreHeaderCase(false);
          }
        }

        if (configuration.get("ignoreSurroundingSpaces") != null) {
          if (configuration.get("ignoreSurroundingSpaces").equals("true")) {
            csvBuilder.setIgnoreSurroundingSpaces(true);
          } else {
            csvBuilder.setIgnoreSurroundingSpaces(false);
          }
        }

        if (configuration.get("nullString") != null) {
          csvBuilder.setNullString(configuration.get("nullString"));
        }

        if (configuration.get("quote") != null) {
          csvBuilder.setQuote(configuration.get("quote").toCharArray()[0]);
        }

        if (configuration.get("recordSeparator") != null) {
          csvBuilder.setRecordSeparator(configuration.get("recordSeparator"));
        }

        if (configuration.get("skipHeaderRecord") != null) {
          if (configuration.get("skipHeaderRecord").equals("true")) {
            csvBuilder.setHeader().setSkipHeaderRecord(true);
          } else {
            csvBuilder.setHeader().setSkipHeaderRecord(false);
          }
        }

        if (configuration.get("trailingDelimiter") != null) {
          if (configuration.get("trailingDelimiter").equals("true")) {
            csvBuilder.setTrailingDelimiter(true);
          } else {
            csvBuilder.setTrailingDelimiter(false);
          }
        }

        if (configuration.get("trim") != null) {
          if (configuration.get("trim").equals("true")) {
            csvBuilder.setTrim(true);
          } else {
            csvBuilder.setTrim(false);
          }
        }

        csvFormat = csvBuilder.build();

        for (CSVRecord record : csvFormat.parse(reader)) {
          ApplicationComponent appComponent = getModelFactory().newApplicationComponent();

          String groupId = "";
          if (configuration.get("groupId") != null) {
            groupId = record.get(Integer.parseInt(configuration.get("groupId")));
          }
          String license = "";
          if (configuration.get("license") != null) {
            license = record.get(Integer.parseInt(configuration.get("license")));
          }
          String licenseURL = "";
          if (configuration.get("licenseUrl") != null) {
            licenseURL = record.get(Integer.parseInt(configuration.get("licenseUrl")));
          }
          String artifactId = record.get(Integer.parseInt(configuration.get("artifactId")));
          String version = record.get(Integer.parseInt(configuration.get("version")));

          appComponent.setGroupId(groupId);
          appComponent.setArtifactId(artifactId);
          appComponent.setVersion(version);
          appComponent.setUsagePattern(usagePattern);
          appComponent.setRepoType(repoType);

          // merge ApplicationComponentImpl with same key if they appear
          // on
          // subsequent lines (multilicensing)
          if (lastAppComponent != null && lastAppComponent.getGroupId().equals(appComponent.getGroupId())
              && lastAppComponent.getArtifactId().equals(appComponent.getArtifactId())
              && lastAppComponent.getVersion().equals(appComponent.getVersion())) {
            // same applicationComponent as previous line ->
            // append rawLicense to already existing
            // ApplicationComponent
          } else {
            // new ApplicationComponentImpl
            appComponent.setApplication(application);
            lastAppComponent = appComponent;
            components++;
          }
          licenses++;

          addRawLicense(lastAppComponent, license, licenseURL, sourceUrl);
        }
      } else {
        // previous CSVReader setting without configuration
        is = inputStreamFactory.createInputStreamFor(sourceUrl);

        java.io.Reader reader = new InputStreamReader(is);
        ApplicationComponent lastAppComponent = null;

        for (CSVRecord record : CSVFormat.newFormat(';').parse(reader)) {
          ApplicationComponent appComponent = getModelFactory().newApplicationComponent();
          appComponent.setGroupId(record.get(0));
          appComponent.setArtifactId(record.get(1));
          appComponent.setVersion(record.get(2));
          appComponent.setUsagePattern(usagePattern);
          appComponent.setRepoType(repoType);
          
          // Set packageURL depending on packageType
          switch(packageType) {
            case "maven":
              System.out.println("maven set");
              appComponent.setPackageUrl(
                  PackageURLHelper.fromMavenCoordinates(record.get(0), record.get(1), record.get(2)).toString());
              break;
            case "npm":
              System.out.println("npm set");
              appComponent.setPackageUrl(PackageURLHelper.fromNpmPackageNameAndVersion(record.get(1), record.get(2)).toString());
              break;
            case "pypi":
              System.out.println("python set");
              appComponent.setPackageUrl(PackageURLHelper.fromPyPICoordinates(record.get(1), record.get(2)).toString());
              break;
            default:
              LOG.warn(LogMessages.UNKNOWN_PACKAGE_TYPE.msg(), packageType);
          }
          
          
          // merge ApplicationComponentImpl with same key if they appear
          // on
          // subsequent lines (multilicensing)
          if (lastAppComponent != null && lastAppComponent.getGroupId().equals(appComponent.getGroupId())
              && lastAppComponent.getArtifactId().equals(appComponent.getArtifactId())
              && lastAppComponent.getVersion().equals(appComponent.getVersion())) {
            // same applicationComponent as previous line ->
            // append rawLicense to already existing
            // ApplicationComponent
          } else {
            // new ApplicationComponentImpl
            appComponent.setApplication(application);
            lastAppComponent = appComponent;
            components++;
          }
          licenses++;
          addRawLicense(lastAppComponent, record.get(3), record.get(4), sourceUrl);
        }
      }

      doLogging(sourceUrl, application, components, licenses);
    } catch (IOException e1) {
      throw new SolicitorRuntimeException("Could not read CSV inventory source '" + sourceUrl + "'", e1);
    }

  }

}
