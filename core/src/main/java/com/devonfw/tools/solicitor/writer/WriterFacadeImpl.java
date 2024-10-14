/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.writer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.SolicitorSetup;
import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.ReportingGroupHandler;
import com.devonfw.tools.solicitor.config.WriterConfig;
import com.devonfw.tools.solicitor.model.ModelRoot;
import com.devonfw.tools.solicitor.writer.data.DataTable;
import com.devonfw.tools.solicitor.writer.data.DataTableDiffer;

/**
 * Implementation of the {@link WriterFacade} interface.
 */
@Component
public class WriterFacadeImpl implements WriterFacade {

  private static final Logger LOG = LoggerFactory.getLogger(WriterFacadeImpl.class);

  @Autowired
  private SolicitorSetup solicitorSetup;

  @Autowired
  private ResultDatabaseFactory resultDatabaseFactory;

  @Autowired
  private WriterFactory writerFactory;

  @Autowired
  private DataTableDiffer dataTableDiffer;

  @Autowired
  private ReportingGroupHandler reportingGroupHandler;

  /**
   * Constructor.
   */
  public WriterFacadeImpl() {

  }

  /**
   * Execute the configured transformations via the embedded SQL database and generated the data tables which will be
   * input for the report generation via XLS or velocity templating.
   *
   * @param modelRoot the current model
   * @param oldModelRoot the old modelto compare to; might be <code>null</code>
   * @param writerConfig the configuration of a {@link Writer} which also defines the SQL queries to perform
   * @param reportingGroup the name of the reporting group to be selected; the parameter will have no effect if the SQL
   *        statements do not contain the respective placeholder
   * @return a map of transformed data tables
   */
  private Map<String, DataTable> getDataTables(ModelRoot modelRoot, ModelRoot oldModelRoot, WriterConfig writerConfig,
      String reportingGroup) {

    // create the table for the current data model
    LOG.info(LogMessages.INIT_SQL.msg());
    modelRoot.setReportingGroup(reportingGroup);
    this.resultDatabaseFactory.initDataModel(modelRoot);
    Map<String, DataTable> result = new HashMap<>();
    for (Map.Entry<String, String> table : writerConfig.getDataTables().entrySet()) {
      LOG.info(LogMessages.EXECUTE_SQL.msg(), table.getKey(), table.getValue(), reportingGroup);
      result.put(table.getKey(), this.resultDatabaseFactory.getDataTable(table.getValue(), reportingGroup));
    }
    modelRoot.setReportingGroup(null);
    // if old model data is defined then transform it and create diff
    // between new and old
    if (oldModelRoot != null) {
      LOG.info(LogMessages.INIT_SQL_OLD.msg());
      oldModelRoot.setReportingGroup(reportingGroup);
      this.resultDatabaseFactory.initDataModel(oldModelRoot);
      for (Map.Entry<String, String> table : writerConfig.getDataTables().entrySet()) {
        DataTable newTable = result.get(table.getKey());
        LOG.info(LogMessages.EXECUTE_SQL.msg(), table.getKey() + " (old)", table.getValue(), reportingGroup);
        DataTable oldTable = this.resultDatabaseFactory.getDataTable(table.getValue(), reportingGroup);
        LOG.info(LogMessages.CREATING_DIFF.msg(), table.getKey());
        DataTable diffTable = this.dataTableDiffer.diff(newTable, oldTable);
        result.put(table.getKey(), diffTable);
      }
      oldModelRoot.setReportingGroup(null);
    }

    return result;
  }

  @Override
  public void writeResult(ModelRoot modelRoot, ModelRoot oldModelRoot) {

    for (WriterConfig writerConfig : this.solicitorSetup.getWriterSetups()) {
      List<String> writerReportingGroups;
      if (writerConfig.isEnableReportingGroups()) {
        writerReportingGroups = this.solicitorSetup.getReportingGroups();
      } else {
        writerReportingGroups = Collections.singletonList(ReportingGroupHandler.DEFAULT_REPORTING_GROUP_NAME);
      }
      String rawFilename = writerConfig.getTarget();
      for (String reportingGroup : writerReportingGroups) {
        if (this.reportingGroupHandler.matchesReportingGroupFilter(reportingGroup)) {
          String targetFilename = this.reportingGroupHandler.expandReportingGroupInFileName(rawFilename,
              reportingGroup);
          LOG.info(LogMessages.PREPARING_FOR_WRITER.msg(), writerConfig.getType(), writerConfig.getTemplateSource(),
              targetFilename);
          Writer writer = this.writerFactory.writerFor(writerConfig.getType());
          writer.writeReport(writerConfig.getTemplateSource(), targetFilename,
              getDataTables(modelRoot, oldModelRoot, writerConfig, reportingGroup));
          LOG.info(LogMessages.FINISHED_WRITER.msg(), writerConfig.getType(), writerConfig.getTemplateSource(),
              targetFilename);
        } else {
          LOG.info(LogMessages.REPORTING_GROUP_NOT_MATCHING_FILTER.msg(), reportingGroup,
              writerConfig.getTemplateSource());
        }
      }
    }
  }

}
