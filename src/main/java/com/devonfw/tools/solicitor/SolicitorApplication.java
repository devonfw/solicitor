/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.devonfw.tools.solicitor.SolicitorCliProcessor.CommandLineOptions;

/**
 * The main class. Triggers command line processing and bootstraps the Spring
 * application context. It delegates to class {@link Solicitor} for further
 * processing.
 */
@SpringBootApplication
public class SolicitorApplication {

    /**
     * The standard java main method.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {

        SolicitorCliProcessor scp = new SolicitorCliProcessor();
        CommandLineOptions clo = scp.parse(args);

        // only continue processing if help option was not selected
        if (!clo.help) {
            ApplicationContext context = SpringApplication.run(SolicitorApplication.class, args);
            Solicitor solicitor = context.getBean(Solicitor.class);
            solicitor.run(clo);
        }

    }

}
