/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.writer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.common.AbstractDataRowSource;
import com.devonfw.tools.solicitor.common.DataRowSource;
import com.devonfw.tools.solicitor.common.DataTable;
import com.devonfw.tools.solicitor.common.DataTableImpl;
import com.devonfw.tools.solicitor.common.DataTableRow;
import com.devonfw.tools.solicitor.common.IOHelper;
import com.devonfw.tools.solicitor.common.InputStreamFactory;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ResultDatabaseFactory {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private InputStreamFactory inputStreamFactory;

    private Engagement engagement;

    private Map<String, Engagement> engagementMap = new HashMap<>();

    private Map<String, Application> applicationMap = new HashMap<>();

    private Map<String, ApplicationComponent> applicationComponentMap =
            new HashMap<>();

    private Map<String, NormalizedLicense> licenseMap = new HashMap<>();

    private Set<Class<? extends AbstractDataRowSource>> definedTablesSet =
            new HashSet<>();

    public enum Level {
        ENGAGEMENT, APPLICATION, APPLICATIONCOMPONENT, LICENSE
    } // level values fit the data model

    public void setEngagement(Engagement engagement) {

        this.engagement = engagement;
        createDatabaseTable(Level.ENGAGEMENT);
        createDatabaseTable(Level.APPLICATION);
        createDatabaseTable(Level.APPLICATIONCOMPONENT);
        createDatabaseTable(Level.LICENSE);
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
            List<Object> fields = new ArrayList<>();
            for (String fieldname : finalHeaders) {
                Object entity = getEntity(oneRow, fieldname);
                if (entity != null) {
                    fields.add(entity);
                } else if (fieldname.equals("rowCount")) {
                    fields.add("" + i++);
                } else {
                    fields.add(oneRow.get(fieldname));
                }
            }

            result.addRow(fields.toArray(new Object[0]));
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

        switch (fieldname) {
        case "ID_ENGAGEMENT":
            return engagementMap.get(oneRow.get(fieldname));
        case "ID_APPLICATION":
            return applicationMap.get(oneRow.get(fieldname));
        case "ID_APPLICATIONCOMPONENT":
            return applicationComponentMap.get(oneRow.get(fieldname));
        case "ID_NORMALIZEDLICENSE":
            return licenseMap.get(oneRow.get(fieldname));
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

    private void saveToDatabase(AbstractDataRowSource row) {

        String[] params = row.getDataElements();

        Class<? extends AbstractDataRowSource> clazz = row.getClass();
        if (!definedTablesSet.contains(clazz)) {
            definedTablesSet.add(clazz);
            createTableDefinition(row);
        }
        StringBuilder sb = new StringBuilder();
        String name = row.getClass().getSimpleName().toUpperCase();
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
        String name = row.getClass().getSimpleName().toUpperCase();
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
        String sql = sb.toString();
        jdbcTemplate.execute(sql);

    }

    /**
     * This function will return a DataTable that holds information about all
     * elements of the specified level. That means that the columns of the
     * DataTable will differ depending on the level. For information about what
     * columns will be provided for what level, look up the fields in the
     * classes matching the specified level.
     * 
     * @see com.devonfw.tools.solicitor.model.masterdata.Engagement
     * @see com.devonfw.tools.solicitor.model.masterdata.Application
     * @see com.devonfw.tools.solicitor.model.inventory.ApplicationComponent
     * @see com.devonfw.tools.solicitor.model.inventory.NormalizedLicense
     * @param level @see com.devonfw.tools.solicitor.writer.ResultTableFactory.Level
     * @return DataTableImpl
     */
    public void createDatabaseTable(Level level) {

        List<DataRowSource> dataRowSources = new ArrayList<>();
        // add content to dataRowSources depending on the level
        switch (level) {
        case ENGAGEMENT:
            saveToDatabase(engagement);
            engagementMap.put(engagement.getId(), engagement);
            break;
        case APPLICATION:
            for (Application a : engagement.getApplications()) {
                saveToDatabase(a);
                applicationMap.put(a.getId(), a);
            }
            break;
        case APPLICATIONCOMPONENT:
            for (Application a : engagement.getApplications()) {
                for (ApplicationComponent ac : a.getApplicationComponents()) {
                    saveToDatabase(ac);
                    applicationComponentMap.put(ac.getId(), ac);
                }
            }
            break;
        case LICENSE:
            for (Application a : engagement.getApplications()) {
                for (ApplicationComponent ac : a.getApplicationComponents()) {
                    for (NormalizedLicense l : ac.getNormalizedLicenses()) {
                        saveToDatabase(l);
                        licenseMap.put(l.getId(), l);
                    }
                }
            }
            break;
        default:
            throw new SolicitorRuntimeException("Unmatched Case");
        }
    }

}
