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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.SolicitorSetup;
import com.devonfw.tools.solicitor.common.AbstractDataRowSource;
import com.devonfw.tools.solicitor.common.DataRowSource;
import com.devonfw.tools.solicitor.common.DataTable;
import com.devonfw.tools.solicitor.common.DataTableField;
import com.devonfw.tools.solicitor.common.DataTableFieldImpl;
import com.devonfw.tools.solicitor.common.DataTableImpl;
import com.devonfw.tools.solicitor.common.DataTableRow;
import com.devonfw.tools.solicitor.common.IOHelper;
import com.devonfw.tools.solicitor.common.InputStreamFactory;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.ModelRoot;

@Component
public class ResultDatabaseFactory {

    private static final Logger LOG =
            LoggerFactory.getLogger(ResultDatabaseFactory.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private InputStreamFactory inputStreamFactory;

    @Autowired
    private ModelFactory modelFactory;

    @Autowired
    private SolicitorSetup solicitorSetup;

    private Set<Class<? extends AbstractDataRowSource>> definedTablesSet =
            new HashSet<>();

    public void initDataModel(ModelRoot modelRoot) {

        // first drop any already existing tables
        for (Class<? extends AbstractDataRowSource> oneTable : definedTablesSet) {
            dropExistingTable(oneTable);
        }
        definedTablesSet.clear();
        // create all needed tables and add all data
        for (Object adrs : modelFactory.getAllModelObjects(modelRoot)) {
            saveToDatabase((AbstractDataRowSource) adrs);
        }
    }

    private void dropExistingTable(
            Class<? extends AbstractDataRowSource> oneTable) {

        StringBuilder sb = new StringBuilder();
        String name = determineTableName(oneTable);
        sb.append("drop table ").append(name).append(";");
        LOG.info("Dropping Reporting table '{}'", name);
        String sql = sb.toString();
        jdbcTemplate.execute(sql);
    }

    /**
     * @param sql
     * @return
     */
    public DataTable getDataTable(String sqlResourceUrl) {

        String sql;

        try (InputStream inp =
                inputStreamFactory.createInputStreamFor(sqlResourceUrl)) {
            sql = IOHelper.readStringFromInputStream(inp);
        } catch (IOException e) {
            throw new SolicitorRuntimeException("Could not read SQL statement",
                    e);
        }

        List<Map<String, Object>> rawResult = jdbcTemplate.queryForList(sql);

        // put the final data in a result DataTable

        String[] headers = rawResult.get(0).keySet().toArray(new String[0]);
        // create the final header
        String[] finalHeaders = DataRowSource
                .concatHeadRow(new String[] { "rowCount" }, headers);
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

    private Object getEntity(Map<String, Object> oneRow, String fieldname) {

        // TODO: Try to avoid this explicit coding
        switch (fieldname) {
        case "ID_MODELROOT":
        case "ID_ENGAGEMENT":
        case "ID_APPLICATION":
        case "ID_APPLICATIONCOMPONENT":
        case "ID_NORMALIZEDLICENSE":
            return AbstractDataRowSource
                    .getInstance((String) oneRow.get(fieldname));
        default:
            return null;
        }
    }

    private void logData(DataTable dataTable) {

        LOG.debug(String.join(",", dataTable.getHeadRow()));
        int size = dataTable.getHeadRow().length;
        for (DataTableRow row : dataTable) {
            String[] s = new String[size];
            for (int i = 0; i < size; i++) {
                s[i] = (row.getValueByIndex(i) == null) ? ""
                        : row.getValueByIndex(i).toString();
            }
            LOG.debug(String.join(",", s));
        }
    }

    public void saveToDatabase(AbstractDataRowSource row) {

        String[] params = row.getDataElements();

        Class<? extends AbstractDataRowSource> clazz = row.getClass();
        if (!definedTablesSet.contains(clazz)) {
            definedTablesSet.add(clazz);
            createTableDefinition(row);
        }
        StringBuilder sb = new StringBuilder();
        String name = determineTableName(row.getClass());
        sb.append("insert into ").append(name).append(" values ( ");
        for (String fields : row.getDataElements()) {
            sb.append("?").append(", ");
        }
        if (row.getParent() != null) {
            sb.append("?, ");
            params = DataRowSource.concatDataRow(params,
                    new String[] { row.getParent().getId() });
        }
        sb.append("?");
        sb.append(" );");
        String sql = sb.toString();
        params = DataRowSource.concatDataRow(params,
                new String[] { row.getId() });
        jdbcTemplate.update(sql, (Object[]) params);

    }

    private void createTableDefinition(AbstractDataRowSource row) {

        StringBuilder sb = new StringBuilder();
        String name = determineTableName(row.getClass());
        sb.append("create table ").append(name).append(" ( ");
        for (String fields : row.getHeadElements()) {
            sb.append("\"").append(fields).append("\" ")
                    .append("LONGVARCHAR, ");
        }
        if (row.getParent() != null) {
            sb.append("PARENT_").append(name).append(" ")
                    .append("LONGVARCHAR NOT NULL, ");
        }
        sb.append("ID_").append(name).append(" ")
                .append("LONGVARCHAR NOT NULL, ");
        sb.append("PRIMARY KEY ( ID_").append(name).append(")");
        sb.append(" );");
        LOG.info("Creating Reporting table '{}'", name);
        String sql = sb.toString();
        jdbcTemplate.execute(sql);

    }

    /**
     * Determine the table name for the given AbstratcDataRowSource.
     * 
     * @param row
     * @return the table name for storing this to the reporting database
     */
    public String determineTableName(
            Class<? extends AbstractDataRowSource> tableClass) {

        return tableClass.getSimpleName().toUpperCase().replace("IMPL", "");
    }

}
