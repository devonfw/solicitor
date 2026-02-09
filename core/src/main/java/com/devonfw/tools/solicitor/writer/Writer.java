/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.writer;

import java.util.Map;

import com.devonfw.tools.solicitor.config.WriterConfig;
import com.devonfw.tools.solicitor.writer.data.DataTable;

/**
 * A {@link Writer} creates a report from the given data using a specified template.
 */
public interface Writer {

  /**
   * Determines if the {@link Writer} instance is capable of handling the given template type.
   *
   * @param type the template type
   * @return <code>true</code> if the writer is capable of handling this type, <code>false</code> otherwise
   */
  boolean accept(String type);

  /**
   * Creates the report.
   *
   * @param config the configuration for this writer
   * @param target the name of the output file
   * @param dataTables a map of data table which contain the data to be used for the report
   */
  void writeReport(WriterConfig config, String target, Map<String, DataTable> dataTables);

}