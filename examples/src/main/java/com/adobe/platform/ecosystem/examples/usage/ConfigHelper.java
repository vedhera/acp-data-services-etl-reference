package com.adobe.platform.ecosystem.examples.usage;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigHelper {

    private static Options options = new Options();

    private static final String IMS_OPTION = "imsEndpoint";
    private static final String DATA_ACCESS_OPTION = "dataAccessEndpoint";
    private static final String CATALOG_OPTION = "catlaogEndpoint";
    private static final String IMS_ORG_OPTION = "imsOrg";
    private static final String CLIENT_ID_OPTION = "clientId";
    private static final String CLIENT_KEY_OPTION = "clientKey";
    private static final String PRIVATE_KEY_PATH_OPTION = "privateKeyPath";
    private static final String TECHNICAL_ACCOUNT_OPTION = "technicalAccount";
    private static final String OUTPUT_PATH_OPTION = "outputPath";
    private static final String DATASET_ID_OPTION = "dataSetId";
    private static final String LIMIT_OPTION = "limit";
    private static final String HELP_OPTION = "help";

    private static final Logger logger = LoggerFactory.getLogger(ConfigHelper.class);
    private static final String STACK_TRACE = "Stack Trace: {}";

    public static Configuration getConfig(String[] args) {

        registerOptions();

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args, false);
        } catch (ParseException e) {
            logger.debug(STACK_TRACE, e);
            logger.error("Unable to parse command line");
            formatter.printHelp("EventHub CLI options", options);
            System.exit(1);
        }

        Configuration configuration = parseOptions(cmd);
        return configuration;
    }

    private static void registerOptions() {
        Option imsEndpoint = new Option("ims", IMS_OPTION, true, "");
        options.addOption(imsEndpoint);

        Option dataAccessEndpoint = new Option("dataAccess", DATA_ACCESS_OPTION, true, "");
        options.addOption(dataAccessEndpoint);

        Option catalogEndpoint = new Option("catalog", CATALOG_OPTION, true, "");
        options.addOption(catalogEndpoint);

        Option imsOrg = new Option(IMS_ORG_OPTION, IMS_ORG_OPTION, true, "");
        options.addOption(imsOrg);

        Option clientId = new Option(CLIENT_ID_OPTION, CLIENT_ID_OPTION, true, "");
        options.addOption(clientId);

        Option clientKey = new Option(CLIENT_KEY_OPTION, CLIENT_KEY_OPTION, true, "");
        options.addOption(clientKey);

        Option privateKeyPath = new Option(PRIVATE_KEY_PATH_OPTION, PRIVATE_KEY_PATH_OPTION, true, "");
        options.addOption(privateKeyPath);

        Option technicalAccount = new Option(TECHNICAL_ACCOUNT_OPTION, TECHNICAL_ACCOUNT_OPTION, true, "");
        options.addOption(technicalAccount);

        Option dataSetId = new Option(DATASET_ID_OPTION, DATASET_ID_OPTION, true, "");
        options.addOption(dataSetId);

        Option limit = new Option(LIMIT_OPTION, LIMIT_OPTION, true, "");
        options.addOption(limit);

        Option outputPath = new Option(OUTPUT_PATH_OPTION, OUTPUT_PATH_OPTION, true, "");
        options.addOption(outputPath);

        Option helpOption = new Option("h", HELP_OPTION, false,
            "Outputs the HELP menu.");
        options.addOption(helpOption);
    }

    private static Configuration parseOptions(CommandLine cmd) {
        Configuration result = new Configuration();

        if (cmd.hasOption(IMS_OPTION)) {
            result.setImsEndpoint(cmd.getOptionValue(IMS_OPTION));
        }

        if (cmd.hasOption(DATA_ACCESS_OPTION)) {
            result.setDataAccessEndpoint(cmd.getOptionValue(DATA_ACCESS_OPTION));
        }

        if (cmd.hasOption(CATALOG_OPTION)) {
            result.setCatalogEndpoint(cmd.getOptionValue(CATALOG_OPTION));
        }

        if (cmd.hasOption(IMS_ORG_OPTION)) {
            result.setImsOrg(cmd.getOptionValue(IMS_ORG_OPTION));
        }

        if (cmd.hasOption(CLIENT_ID_OPTION)) {
            result.setClientId(cmd.getOptionValue(CLIENT_ID_OPTION));
        }

        if (cmd.hasOption(CLIENT_KEY_OPTION)) {
            result.setClientSecretKey(cmd.getOptionValue(CLIENT_KEY_OPTION));
        }

        if (cmd.hasOption(PRIVATE_KEY_PATH_OPTION)) {
            result.setPrivateKeyPath(cmd.getOptionValue(PRIVATE_KEY_PATH_OPTION));
        }

        if (cmd.hasOption(TECHNICAL_ACCOUNT_OPTION)) {
            result.setTechnicalAccountId(cmd.getOptionValue(TECHNICAL_ACCOUNT_OPTION));
        }

        if (cmd.hasOption(OUTPUT_PATH_OPTION)) {
            result.setOutputFilePath(cmd.getOptionValue(OUTPUT_PATH_OPTION));
        }

        if (cmd.hasOption(DATASET_ID_OPTION)) {
            result.setDataSetId(cmd.getOptionValue(DATASET_ID_OPTION));
        }

        if (cmd.hasOption(LIMIT_OPTION)) {
            result.setLimit(getLimit(cmd.getOptionValue(LIMIT_OPTION)));
        }
        return result;
    }

    private static int getLimit(String argument) {
        if (argument.contains("limit=")) {
            argument = argument.replace("limit=", "");
        } else {
            logger.error("Must provide the expected format for setting the limit. Example: limit=10");
            System.exit(1);
        }
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            logger.error(
                "Invalid number format. Must provide the expected format for setting the limit. Example: limit=10");
            System.exit(1);
        }
        return 0;
    }
}
