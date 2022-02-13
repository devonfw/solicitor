/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.writer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.IOHelper;
import com.devonfw.tools.solicitor.common.InputStreamFactory;
import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.ModelRoot;
import com.devonfw.tools.solicitor.model.impl.AbstractModelObject;
import com.devonfw.tools.solicitor.writer.data.DataTable;
import com.devonfw.tools.solicitor.writer.data.DataTableField;
import com.devonfw.tools.solicitor.writer.data.DataTableFieldImpl;
import com.devonfw.tools.solicitor.writer.data.DataTableImpl;
import com.devonfw.tools.solicitor.writer.data.DataTableRow;

/**
 * Transforms the internal Solicitor data model to result tables in {@link DataTable} format by loading the internal
 * data model into a temporary database and creating tablular result by executing SQL statements on the data.
 */
@Component
public class ResultDatabaseFactory {

  private static final Logger LOG = LoggerFactory.getLogger(ResultDatabaseFactory.class);

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private InputStreamFactory inputStreamFactory;

  @Autowired
  private ModelFactory modelFactory;

  private Set<Class<? extends AbstractModelObject>> definedTablesSet = new HashSet<>();

  private Map<String, AbstractModelObject> allModelObjectInstances = new TreeMap<>();

  /**
   * Creates a database table for storing the given {@link AbstractModelObject}.
   * 
   * @param modelObject the model object for which the table should be defined
   */
  private void createTable(AbstractModelObject modelObject) {

    StringBuilder sb = new StringBuilder();
    String name = determineTableName(modelObject.getClass());
    sb.append("create table ").append(name).append(" ( ");
    for (String fields : modelObject.getHeadElements()) {
      sb.append("\"").append(fields).append("\" ").append("LONGVARCHAR, ");
    }
    if (modelObject.getParent() != null) {
      sb.append("PARENT_").append(name).append(" ").append("LONGVARCHAR NOT NULL, ");
    }
    sb.append("ID_").append(name).append(" ").append("LONGVARCHAR NOT NULL, ");
    sb.append("PRIMARY KEY ( ID_").append(name).append(")");
    sb.append(" );");
    LOG.debug("Creating Reporting table '{}'", name);
    String sql = sb.toString();
    jdbcTemplate.execute(sql);

  }

  /**
   * Determine the table name for the given {@link AbstractModelObject} subtype.
   *
   * @param tableClass a class name of the {@link AbstractModelObject} subtype
   * @return the table name for storing this to the reporting database
   */
  public String determineTableName(Class<? extends AbstractModelObject> tableClass) {

    return tableClass.getSimpleName().toUpperCase().replace("IMPL", "");
  }

  /**
   * Drop the database table which corresponds to the given {@link AbstractModelObject}.
   * 
   * @param oneTable the model class for which the corresponding database table should be dropped
   */
  private void dropExistingTable(Class<? extends AbstractModelObject> oneTable) {

    StringBuilder sb = new StringBuilder();
    String name = determineTableName(oneTable);
    sb.append("drop table ").append(name).append(";");
    LOG.debug("Dropping Reporting table '{}'", name);
    String sql = sb.toString();
    jdbcTemplate.execute(sql);
  }

  /**
   * Creates a {@link DataTable} by executing the referenced SQL.
   *
   * @param sqlResourceUrl URL which references an SQL statement
   * @return the result of the SQL statement
   */
  public DataTable getDataTable(String sqlResourceUrl) {

    String sql;

    try (InputStream inp = inputStreamFactory.createInputStreamFor(sqlResourceUrl)) {
      sql = IOHelper.readStringFromInputStream(inp);
    } catch (IOException e) {
      throw new SolicitorRuntimeException("Could not read SQL statement", e);
    }

    List<Map<String, Object>> rawResult = jdbcTemplate.queryForList(sql);

    // put the final data in a result DataTable

    if (rawResult.isEmpty()) {
      // if no data is returned it is even not possible to extract the
      // fieldnames; return an empty DataTable in this case
      LOG.info(LogMessages.SQL_RETURNED_NO_DATA.msg(), sqlResourceUrl);
      return new DataTableImpl(new String[] {});
    }

    String[] headers = rawResult.get(0).keySet().toArray(new String[0]);
    // create the final header
    String[] finalHeaders = AbstractModelObject.concatRow(new String[] { "rowCount" }, headers);
    DataTableImpl result = new DataTableImpl(modifyHeaders(finalHeaders));
    // get the final data
    int i = 1;
    for (Map<String, Object> oneRow : rawResult) {
      List<DataTableField> fields = new ArrayList<>();
      for (String fieldname : finalHeaders) {
        Object entity = getEntity(oneRow, fieldname);
        if (entity != null) {
          fields.add(new DataTableFieldImpl(entity));
        } else if (fieldname.equals("rowCount")) {
          fields.add(new DataTableFieldImpl("" + i++));
        } else {
          fields.add(new DataTableFieldImpl(oneRow.get(fieldname)));
        }
      }

      result.addRow(fields.toArray(new DataTableField[0]));
    }

    logData(result);
    return result;
  }

  /**
   * Checks if the referenced field name starts with prefix "ID_" if yes then return the {@link AbstractModelObject}
   * given by its id.
   * 
   * @param oneRow the row of data
   * @param fieldname the name of the field
   * @return the {@link AbstractModelObject} or <code>null</code> if the field does not start with "ID_"
   */
  private Object getEntity(Map<String, Object> oneRow, String fieldname) {

    if (fieldname.startsWith("ID_")) {
      return allModelObjectInstances.get(oneRow.get(fieldname));
    } else {
      return null;
    }
  }

  /**
   * Initializes the database with the data of the internal data model.
   * 
   * @param modelRoot the root object of the internal data model which gives access to the complete data model
   */
  public void initDataModel(ModelRoot modelRoot) {

    // delete all possibly existing entries in the model instances map
    allModelObjectInstances.clear();
    // drop any already existing tables
    for (Class<? extends AbstractModelObject> oneTable : definedTablesSet) {
      dropExistingTable(oneTable);
    }
    definedTablesSet.clear();
    // create all needed tables and add all data; also store object in map
    // to access it via given id
    for (Object amo : modelFactory.getAllModelObjects(modelRoot)) {
      saveToDatabase((AbstractModelObject) amo);
      allModelObjectInstances.put(((AbstractModelObject) amo).getId(), (AbstractModelObject) amo);
    }
  }

  /**
   * Logs the data of the given table on level {@link Level#TRACE}.
   * 
   * @param dataTable the data to log
   */
  private void logData(DataTable dataTable) {

    if (LOG.isTraceEnabled()) {
      LOG.trace(String.join(",", dataTable.getHeadRow()));
      int size = dataTable.getHeadRow().length;
      for (DataTableRow row : dataTable) {
        String[] s = new String[size];
        for (int i = 0; i < size; i++) {
          s[i] = (row.getValueByIndex(i) == null) ? "" : row.getValueByIndex(i).toString();
        }
        LOG.trace(String.join(",", s));
      }
    }
  }

  /**
   * Modifies the array of header strings by replacing any prefixes "ID_" with "OBJ_".
   * 
   * @param finalHeaders the array of strings to modify
   * @return the modified array
   */
  private String[] modifyHeaders(String[] finalHeaders) {

    String[] modifiedHeaders = new String[finalHeaders.length];
    for (int i = 0; i < finalHeaders.length; i++) {
      String header = finalHeaders[i];
      if (header.startsWith("ID_")) {
        header = header.replace("ID_", "OBJ_");
      }
      modifiedHeaders[i] = header;
    }

    return modifiedHeaders;
  }

  /**
   * Save the given {@link AbstractModelObject} to the database. In case that no appropriate database table exist it
   * will be created.
   * 
   * @param modelObject the object to save
   */
  public void saveToDatabase(AbstractModelObject modelObject) {

    String[] params = modelObject.getDataElements();

    Class<? extends AbstractModelObject> clazz = modelObject.getClass();
    if (!definedTablesSet.contains(clazz)) {
      definedTablesSet.add(clazz);
      createTable(modelObject);
    }
    StringBuilder sb = new StringBuilder();
    String name = determineTableName(modelObject.getClass());
    sb.append("insert into ").append(name).append(" values ( ");
    for (String fields : modelObject.getDataElements()) {
      sb.append("?").append(", ");
    }
    if (modelObject.getParent() != null) {
      sb.append("?, ");
      params = AbstractModelObject.concatRow(params, new String[] { modelObject.getParent().getId() });
    }
    sb.append("?");
    sb.append(" );");
    String sql = sb.toString();
    params = AbstractModelObject.concatRow(params, new String[] { modelObject.getId() });
    jdbcTemplate.update(sql, (Object[]) params);

  }

}
