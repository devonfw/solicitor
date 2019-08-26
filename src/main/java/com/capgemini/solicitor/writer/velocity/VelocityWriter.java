/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.capgemini.solicitor.writer.velocity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.tools.generic.EscapeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.capgemini.solicitor.SolicitorRuntimeException;
import com.capgemini.solicitor.common.DataTable;
import com.capgemini.solicitor.common.IOHelper;
import com.capgemini.solicitor.common.InputStreamFactory;
import com.capgemini.solicitor.writer.Writer;

//import lombok.extern.slf4j.Slf4j;

@Component
public class VelocityWriter implements Writer {

    @Autowired
    private InputStreamFactory inputStreamFactory;

    @Override
    public boolean accept(String type) {

        return "velo".equals(type);
    }

    /**
     * This function will generate a report based on the given velocity
     * template.
     * 
     * @param templateSource full path to the template (given in the config
     *        file)
     * @param target file the report will be written to (given in the config
     *        file)
     * @param dataTables
     */
    @Override
    public void writeReport(String templateSource, String target,
            Map<String, DataTable> dataTables) {

        // initialize velocity runtime engine
        Velocity.init();

        // set up the context
        VelocityContext context = new VelocityContext();
        for (Entry<String, DataTable> entry : dataTables.entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }
        context.put("esc", new EscapeTool());

        // read the template as a string (including the template as a property
        // when initializing the velocity engine doesn't work anymore with
        // springboot)

        String templateString;
        try (InputStream inp =
                inputStreamFactory.createInputStreamFor(templateSource)) {
            templateString = IOHelper.readStringFromInputStream(inp);

        } catch (IOException e) {
            throw new SolicitorRuntimeException(
                    "Reading of template for Velocity report failed", e);
        }

        // write output
        File file = new File(target);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            Velocity.evaluate(context, writer, "solicitor report velocity",
                    templateString);
            writer.flush();
        } catch (IOException e) {
            throw new SolicitorRuntimeException(
                    "Processing of velocity report failed", e);
        }

    }

}
