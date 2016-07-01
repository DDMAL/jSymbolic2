package jsymbolic2.commandline;

import jsymbolic2.configuration.txtimplementation.ConfigurationFileValidatorTxtImpl;
import jsymbolic2.configuration.HeaderEnum;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Deals with the command line switches and how to parse and direct the appropriate action.
 * This is done through the use of the enum strategy pattern.
 * </p>
 * <p>
 * Each new enum (i.e. new switch) is required to have both a {@link SwitchCommand} and
 * a corresponding {@link String} name. The {@link SwitchCommand} has an abstract function which needs to be
 * implemented and thus, the function is required to be implemented for
 * every new switch that is added. The corresponding {@link String} name is the actual
 * switch required in the command line arguments. This also allows for easy reuse of a particular command.
 * This could help if ever the switches need to be parsed more thoroughly.
 * </p>
 *
 * @author Tristano Tenaglia
 */
public enum CommandLineSwitchEnum {
    /**
     * Switch to run the GUI by default.
     */
    GUI(SwitchCommand.PLAIN_GUI, ""),

    /**
     * Switch to run with no switches and only input/output file paths.
     */
    NOSWITCH(SwitchCommand.COMMAND_LINE, ""),

    /**
     * Switch to use the window mode through the command line.
     */
    WINDOW(SwitchCommand.COMMAND_LINE, "-window"),

    /**
     * Switch to obtain the arff format through the command line.
     */
    ARFF(SwitchCommand.COMMAND_LINE, "-arff"),

    /**
     * Switch to obtain the csv format through the command line.
     */
    CSV(SwitchCommand.COMMAND_LINE, "-csv"),

    /**
     * Switch to run the GUI using the data from a specified configuration file.
     */
    CONFIGURATION_GUI(SwitchCommand.CONFIG_GUI, "-configgui"),

    /**
     * Switch to run the command line using the data from a specified configuration file.
     */
    CONFIGURATION_RUN(SwitchCommand.CONFIG_RUN, "-configrun"),

    /**
     * Check to see if configuration file is valid for all headers.
     */
    VALIDATE_CONFIGURATION_ALL(SwitchCommand.VALIDATE_CONFIG_ALL_HEADERS, "-validateconfigallheaders"),

    /**
     * Check to see if configuration is valid with only feature and option headers.
     */
    VALIDATE_CONFIGURATION_FEATURE_OPTION(SwitchCommand.VALIDATE_CONFIG_FEATURE_OPTION, "-validateconfigfeatureoption"),

    /**
     * Switch to print out usage when requested.
     */
    HELP(SwitchCommand.HELP, "-help");

    /**
     * The action of a particular switch.
     */
    private final SwitchCommand switchAction;

    /**
     * The command line argument format of a particular switch.
     */
    private final String switchString;

    /**
     * Constructor
     *
     * @param switchAction
     * @param switchString
     */
    CommandLineSwitchEnum(SwitchCommand switchAction, String switchString) {
        this.switchAction = switchAction;
        this.switchString = switchString;
    }

    /**
     * Run the command based on the args input.
     *
     * @param args The command line arguments.
     * @throws Exception Thrown if the switch command in the args input is invalid.
     */
    public static void runCommand(String[] args) throws Exception {
        CommandLineSwitchEnum switchToRun;
        String switchString;
        if (args == null || args.length <= 0) {
            //Nothing at all in command line arguments
            switchToRun = GUI;
        } else {
            //TODO could change here to parse entire switches to make multi-switch command line arguments easier to use
            switchString = args[0];
            if (exists(switchString)) {
                //If there is in fact a switch in command line arguments
                switchToRun = stringToSwitch(switchString);
            } else {
                //If there is no switch but there is input and output file paths
                //Otherwise NOSWITCH will print usage on error if input args invalid
                switchToRun = NOSWITCH;
            }
        }
        switchToRun.switchAction.command(args);
    }

    /**
     * Convenience method to see if a switchString exists.
     *
     * @param switchString Switch string ot check from command line arguments.
     * @return true if switch string does exist in this enum, otherwise false
     */
    private static boolean exists(String switchString) {
        for (CommandLineSwitchEnum swenum : CommandLineSwitchEnum.values()) {
            if (swenum.switchString.equals(switchString)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convenience method to get a switchString into enum form.
     *
     * @param switchString The switch string to be checked.
     * @return The command line switch enum if it does exists, otherwise this function returns null.
     */
    private static CommandLineSwitchEnum stringToSwitch(String switchString) {
        for (CommandLineSwitchEnum swenum : CommandLineSwitchEnum.values()) {
            if (swenum.switchString.equals(switchString)) {
                return swenum;
            }
        }
        return null;
    }

    /**
     * The enum strategy pattern which allows the outer enum to always specify
     * a particular command and then be able to run its function accordingly.
     */
    private enum SwitchCommand {
        COMMAND_LINE {
            /**
             * Runs the command line straight based on the arguments directly.
             * If the default configuration file is invalid or does not exist,
             * then jSymbolic command line is run normally and the exception message is written to standard output.
             * @param args The command line arguments.
             */
            @Override
            void command(String[] args) {
                //File path relative to jar here
                String defaultConfigName = "./jSymbolicDefaultConfigs.txt";
                if (Files.exists(Paths.get(defaultConfigName)) &&
                        args.length == 3)
                {
                    //Try out default behaviour for default configuration file
                    String inputFile = args[0];
                    String featureValueSavePath = args[1];
                    String featureDefinitionSavePath = args[2];
                    List<HeaderEnum> headersToCheck = Arrays.asList(HeaderEnum.FEATURE_HEADER, HeaderEnum.OPTION_HEADER);
                    try {
                        new CommandLineConfig(
                                new ConfigurationFileValidatorTxtImpl().parseConfigFile(defaultConfigName, headersToCheck),
                                Arrays.asList(new File(inputFile)),
                                featureValueSavePath,
                                featureDefinitionSavePath
                        );
                    } catch (Exception ex) {
                        System.out.println("WARNING JSYMBOLIC DEFAULT CONFIG FILE IGNORED: " + ex.getMessage());
                        new CommandLineArgs(args);
                    }
                } else {
                    System.out.println("WARNING JSYMBOLIC DEFAULT CONFIG FILE DOES NOT EXIST");
                    new CommandLineArgs(args);
                }
            }
        },
        PLAIN_GUI {
            /**
             * Runs the default GUI. If a file named jSymbolicDefaultConfigs.txt is in the
             * same directory as jSymbolic.jar at runtime, then it will use that default configuration
             * file to startup. If the default configuration file is invalid or does not exist,
             * then jSymbolic GUI is run normally and the exception message is written to standard output.
             * @param args The command line arguments.
             */
            @Override
            void command(String[] args) {
                //File path relative to jar here
                String defaultConfigName = "./jSymbolicDefaultConfigs.txt";
                //Try out default behaviour for default configuration file
                if(Files.exists(Paths.get(defaultConfigName))) {
                    try {
                        new jsymbolic2.gui.OuterFrame(
                                new ConfigurationFileValidatorTxtImpl().parseConfigFileAllWays(defaultConfigName)
                        );
                    } catch (Exception ex) {
                        //If configuration file is invalid, then run jSymbolic GUI normally and output message
                        System.out.println("WARNING JSYMBOLIC DEFAULT CONFIG FILE IGNORED: " + ex.getMessage());
                        new jsymbolic2.gui.OuterFrame(null);
                    }
                } else {
                    System.out.println("WARNING JSYMBOLIC DEFAULT CONFIG FILE DOES NOT EXIST");
                    new jsymbolic2.gui.OuterFrame(null);
                }
            }
        },
        CONFIG_GUI {
            /**
             * Runs the GUI with the passed in configuration file.
             * @param args The command line arguments.
             */
            @Override
            void command(String[] args) {
                String configFileName = args[1];
                try {
                    new jsymbolic2.gui.OuterFrame(
                            new ConfigurationFileValidatorTxtImpl().parseConfigFile(configFileName)
                    );
                } catch (Exception ex) {
                    CommandLineUtils.printMessageAndTerminate(ex.getMessage(), 1);
                }
            }
        },
        CONFIG_RUN {
            /**
             * Run the command line using the data from the specified configuration file.
             * @param args The command line arguments.
             * @throws Exception
             */
            @Override
            void command(String[] args) throws Exception {
                //All data is specified in configuration file
                if (args.length == 2) {
                    String configFileName = args[1];
                    new CommandLineConfig(
                            new ConfigurationFileValidatorTxtImpl().parseConfigFile(configFileName)
                    );
                } else {
                    //Input files and save paths are specified at command line
                    if (args.length != 5) {
                        throw new Exception("In jSymbolic, -configrun switch needs either 2 or 5 arguments.");
                    }
                    String configFileName = args[1];
                    String inputFile = args[2];
                    String featureValueSavePath = args[3];
                    String featureDefinitionSavePath = args[4];
                    List<HeaderEnum> headersToCheck = Arrays.asList(HeaderEnum.FEATURE_HEADER, HeaderEnum.OPTION_HEADER);
                    new CommandLineConfig(
                            new ConfigurationFileValidatorTxtImpl().parseConfigFile(configFileName, headersToCheck),
                            Arrays.asList(new File(inputFile)),
                            featureValueSavePath,
                            featureDefinitionSavePath
                    );
                }
            }
        },
        VALIDATE_CONFIG_ALL_HEADERS {
            /**
             * Print out success to standard out if specified configuration file is valid.
             * Otherwise the appropriate exception will be thrown and shown to the user.
             * @param args The command line arguments.
             * @throws Exception The exception thrown from the invalid configuration file.
             */
            @Override
            void command(String[] args) throws Exception {
                if (args.length != 2) {
                    throw new Exception(CommandLineUtils.getUsageMessage());
                }
                String configFileName = args[1];
                //Will throw exception here if configuration file is not valid
                new ConfigurationFileValidatorTxtImpl().parseConfigFile(configFileName);
                System.out.println(configFileName + " is a valid configuration file for all headers!");
            }
        },
        VALIDATE_CONFIG_FEATURE_OPTION {
            /**
             * Print out success to standard out if specified configuration file is valid.
             * In this case, valid only if feature and option headers are the only valid ones specified.
             * Otherwise the appropriate exception will be thrown and shown to the user.
             * @param args The command line arguments.
             * @throws Exception The exception thrown from the invalid configuration file.
             */
            @Override
            void command(String[] args) throws Exception {
                if (args.length != 2) {
                    throw new Exception(CommandLineUtils.getUsageMessage());
                }
                String configFileName = args[1];
                List<HeaderEnum> headersToCheck = Arrays.asList(HeaderEnum.FEATURE_HEADER, HeaderEnum.OPTION_HEADER);
                //Will throw exception here if configuration file is not valid
                new ConfigurationFileValidatorTxtImpl().parseConfigFile(configFileName, headersToCheck);
                System.out.println(configFileName + " is a valid configuration file for feature and option headers only!");
            }
        },
        HELP {
            /**
             * Print out the general usage of jSymbolic to output.
             * @param args The command line arguments.
             * @throws Exception
             */
            @Override
            void command(String[] args) throws Exception {
                System.out.println(CommandLineUtils.getUsageMessage());
            }
        };

        /**
         * The command that needs to be implemented by each switch.
         *
         * @param args The command line arguments.
         * @throws Exception
         */
        abstract void command(String[] args) throws Exception;
    }
}
