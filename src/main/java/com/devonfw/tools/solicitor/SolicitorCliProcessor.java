/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lombok.extern.slf4j.Slf4j;

/**
 * Provides a command line interface for configuring the generator.
 */
@Slf4j
public class SolicitorCliProcessor {

    public static class CommandLineOptions {
        public boolean help;

        public boolean externalizeUserguide;

        public boolean save;

        public String pathForSave;

        public String configUrl;
    }

    /**
     * Returns options given at the command line as {@link CommandLineOptions}
     * object.
     * 
     * @param commandLineArgs list of arguments as provided by the static main
     *        method
     * @return an object containing the command line options
     */
    public CommandLineOptions parse(String[] commandLineArgs) {

        Options options = new Options();

        // option "h" (help)
        Option.Builder builder = Option.builder("h");
        builder.longOpt("help");
        String description = "print this help (no main processing)";
        builder.desc(description);
        Option help = builder.build();

        options.addOption(help);

        // option "h" (help)
        builder = Option.builder("eu");
        builder.longOpt("externalizeUserguide");
        description =
                "stores a copy of the user guide in the current directory (no main processing)";
        builder.desc(description);
        Option externalizeDoc = builder.build();

        options.addOption(externalizeDoc);

        // option "c" (config)
        builder = Option.builder("c");
        builder.longOpt("config");
        builder.hasArg();
        builder.argName("URL");
        description =
                "do main processing using the config referenced by the given URL";
        builder.desc(description);
        Option config = builder.build();

        options.addOption(config);

        // option "s" (save)
        builder = Option.builder("s");
        builder.longOpt("saveModel");
        builder.hasArg();
        builder.optionalArg(true);
        builder.argName("filename");
        description =
                "after rule evaluation save the internal data model to a file";
        builder.desc(description);
        Option save = builder.build();

        options.addOption(save);

        // evaluating the arguments
        CommandLineParser parser = new DefaultParser();
        CommandLine line;
        CommandLineOptions solClo = new CommandLineOptions();
        try {
            // parse the command line arguments
            line = parser.parse(options, commandLineArgs);

            if (line.hasOption("h")) {
                printHelp(options);
                LOG.debug("Help option detected");
                solClo.help = true;
            }

            if (line.hasOption("eu")) {
                solClo.externalizeUserguide = true;
                LOG.debug("externalizeUserguide option detected");
            }

            if (line.hasOption("s")) {
                solClo.save = true;

                solClo.pathForSave = line.getOptionValue("s");
            }

            if (line.hasOption("c")) {
                solClo.configUrl = line.getOptionValue("c");
            }

        } catch (Exception exp) {
            LOG.error("Exception when processing command line arguments", exp);
            printHelp(options);
            return null;
        }
        return solClo;

    }

    /**
     * Prints help information to standard output.
     * 
     * @param options the {@link Options} object which describes the command
     *        line syntax
     */
    private void printHelp(Options options) {

        HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp(120, "java -jar solicitor.jar", "", options,
                "DevonFW Solicitor - visit https://github.com/devonfw-forge/solicitor",
                true);
    }

}
