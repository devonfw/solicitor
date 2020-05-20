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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.LogMessages;

/**
 * Provides a command line interface for configuring the generator.
 */
public class SolicitorCliProcessor {

    /**
     * Holds all settings parsed from the command line parameters.
     */
    public static class CommandLineOptions {
        public boolean help;

        public boolean extractUserGuide;

        public boolean wizard;

        public boolean extractConfig;

        public boolean save;

        public String pathForSave;

        public String configUrl;

        public boolean load;

        public String pathForLoad;

        public boolean diff;

        public String pathForDiff;

        public String targetDir;
    }

    private static final Logger LOG = LoggerFactory.getLogger(SolicitorCliProcessor.class);

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

        // option "eug" (extract user guide)
        builder = Option.builder("eug");
        builder.longOpt("extractUserGuide");
        description = "stores a copy of the user guide in the current directory (no main processing)";
        builder.desc(description);
        Option extractUserGuide = builder.build();
        options.addOption(extractUserGuide);

        // option "wiz" (wizard)
        builder = Option.builder("wiz");
        builder.longOpt("projectWizard");
        builder.hasArg();
        builder.argName("targetDir");
        description = "creates configuration for a new Solicitor project in the given directory (no main processing)";
        builder.desc(description);
        Option wizardConfig = builder.build();
        options.addOption(wizardConfig);

        // option "ec" (extract config)
        builder = Option.builder("ec");
        builder.longOpt("extractConfig");
        builder.hasArg();
        builder.argName("targetDir");
        description =
                "stores a copy of the base config file and all referenced configuration in the given directory (no main processing)";
        builder.desc(description);
        Option extractConfig = builder.build();
        options.addOption(extractConfig);

        // option "c" (config)
        builder = Option.builder("c");
        builder.longOpt("config");
        builder.hasArg();
        builder.argName("URL");
        description = "do main processing using the config referenced by the given URL";
        builder.desc(description);
        Option config = builder.build();
        options.addOption(config);

        // option "s" (save)
        builder = Option.builder("s");
        builder.longOpt("saveModel");
        builder.hasArg();
        builder.optionalArg(true);
        builder.argName("filename");
        description = "after rule evaluation save the internal data model to a file; "
                + "if no filename is given a filename will be automatically created";
        builder.desc(description);
        Option save = builder.build();
        options.addOption(save);

        // option "l" (load)
        builder = Option.builder("l");
        builder.longOpt("loadModel");
        builder.hasArg();
        builder.optionalArg(false);
        builder.argName("filename");
        description = "instead of reading raw license data and processing rules load the already "
                + "processed model from a previously saved file";
        builder.desc(description);
        Option load = builder.build();
        options.addOption(load);

        // option "d" (diff)
        builder = Option.builder("d");
        builder.longOpt("diff");
        builder.hasArg();
        builder.optionalArg(false);
        builder.argName("filename");
        description = "create a diff report to the already processed model given by this filename";
        builder.desc(description);
        Option diff = builder.build();
        options.addOption(diff);

        // evaluating the arguments
        CommandLineParser parser = new DefaultParser();
        CommandLine line;
        CommandLineOptions solClo = new CommandLineOptions();
        try {
            if (commandLineArgs == null || commandLineArgs.length == 0) {
                throw new IllegalArgumentException("No command line arguments given");
            }
            // parse the command line arguments
            line = parser.parse(options, commandLineArgs);

            if (line.hasOption("h")) {
                printHelp(options);
                solClo.help = true;
            }

            if (line.hasOption("eug")) {
                solClo.extractUserGuide = true;
            }

            if (line.hasOption("wiz")) {
                solClo.wizard = true;
                solClo.targetDir = line.getOptionValue("wiz");
            }

            if (line.hasOption("ec")) {
                solClo.extractConfig = true;
                solClo.targetDir = line.getOptionValue("ec");
            }

            if (line.hasOption("s")) {
                solClo.save = true;

                solClo.pathForSave = line.getOptionValue("s");
            }

            if (line.hasOption("l")) {
                solClo.load = true;

                solClo.pathForLoad = line.getOptionValue("l");
            }

            if (line.hasOption("d")) {
                solClo.diff = true;

                solClo.pathForDiff = line.getOptionValue("d");
            }

            if (line.hasOption("c")) {
                solClo.configUrl = line.getOptionValue("c");
            }

        } catch (Exception exp) {
            LOG.error(LogMessages.CLI_EXCEPTION.msg(), exp.getMessage());
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
                "DevonFW Solicitor - visit https://github.com/devonfw/solicitor", true);
    }

}
