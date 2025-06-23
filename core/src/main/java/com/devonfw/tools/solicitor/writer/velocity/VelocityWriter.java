/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.writer.velocity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.DeprecationChecker;
import com.devonfw.tools.solicitor.common.IOHelper;
import com.devonfw.tools.solicitor.common.InputStreamFactory;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.writer.Writer;
import com.devonfw.tools.solicitor.writer.data.DataTable;

/**
 * A {@link Writer} which uses a <a href="http://velocity.apache.org/">Velocity</a> template to create the report.
 */
@Component
public class VelocityWriter implements Writer {

  @Autowired
  private InputStreamFactory inputStreamFactory;

  @Autowired
  private AllKindsPackageURLHandler packageURLHandler;

  @Autowired
  private DeprecationChecker deprecationChecker;

  /**
   * {@inheritDoc}
   *
   * Accepted type is "velo".
   */

  @Override
  public boolean accept(String type) {

    return "velo".equals(type);
  }

  /**
   * {@inheritDoc}
   *
   * This function will generate a report based on the given velocity template.
   */
  @Override
  public void writeReport(String templateSource, String target, Map<String, DataTable> dataTables) {

    // (re)initialize velocity runtime engine
    Velocity.reset();
    Velocity.init();

    // set up the context
    VelocityContext context = new VelocityContext();
    for (Entry<String, DataTable> entry : dataTables.entrySet()) {
      context.put(entry.getKey(), entry.getValue());
    }
    context.put("esc", new EscapeTool());
    context.put("purlhandler", this.packageURLHandler);

    // read the template as a string (including the template as a property
    // when initializing the velocity engine doesn't work anymore with
    // springboot)

    String templateString;
    try (InputStream inp = this.inputStreamFactory.createInputStreamFor(templateSource)) {
      templateString = IOHelper.readStringFromInputStream(inp);
      // guessedLicenseUrl is deprecated and on stage 1
      if (templateString.toLowerCase().contains("guessedlicense")) {
        this.deprecationChecker.check(false,
            "This template " + (templateSource) + " uses the 'guessedLicenseUrl' feature which is deprecated.");
      }
    } catch (IOException e) {
      throw new SolicitorRuntimeException("Reading of template for Velocity report failed", e);
    }

    // write output
    File file = new File(target);
    IOHelper.checkAndCreateLocation(file);
    try (BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
      Velocity.evaluate(context, writer, "solicitor report velocity", templateString);
      writer.flush();
    } catch (IOException e) {
      throw new SolicitorRuntimeException("Processing of velocity report failed", e);
    }

  }

}
