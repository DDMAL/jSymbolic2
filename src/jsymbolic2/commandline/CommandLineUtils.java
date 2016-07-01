package jsymbolic2.commandline;

/**
 * Common utilities that can be used throughout the jSymbolic software.
 *
 * @author Tristano Tenaglia
 */
public final class CommandLineUtils {

    /**
     * @return The message of the proper usage for jSymbolic, including all of the different
     * command line switches and options.
     */
    public static String getUsageMessage() {
        return "\nProper command line usage of jSymbolic requires one of the following:\n\n" +
                "1) No arguments (runs the GUI)\n" +
                "2) <SymbolicMusicFileInputPath> <AceXmlFeatureValuesOutputPath> <AceXmlFeatureDefinitionsOutputPath>\n" +
                "\t-arff and/or -csv can be optionally be added before the above arguments.\n" +
                "3) -window <SymbolicMusicFileInputPath> <AceXmlFeatureValuesOutputPath> <AceXmlFeatureDefinitionsOutputPath> <WindowLength> <WindowOverlapFraction>\n" +
                "\t-arff and/or -csv can be optionally be added before the above arguments.\n" +
                "4) -configrun <ConfigurationFilePath>\n" +
                "5) -configrun <ConfigurationFilePath> <SymbolicMusicFileInputPath> <AceXmlFeatureValuesOutputPath> <AceXmlFeatureDefinitionsOutputPath>\n" +
                "6) -configgui <ConfigurationFilePath>\n" +
                "7) -validateconfigallheaders <ConfigurationFilePath>\n" +
                "8) -validateconfigfeatureoption <ConfigurationFilePath>\n" +
                "9) -help\n\n" +
                "Command line variable descriptions:\n" +
                "* SymbolicMusicFileInputPath: The file path of the file from which features are to be extracted. May alternatively be a directory of symbolic music files.\n" +
                "* AceXmlFeatureValuesOutputPath: The path of the ACE XML file to create holding extracted feature values.\n" +
                "* AceXmlFeatureDefinitionsOutputPath: The path of the ACE XML file to create holding metadata descriptions of the features extracted.\n" +
                "* WindowLength: The duration in seconds of windows to be used during windowed feature extraction.\n" +
                "* WindowOverlapFraction: A value between 0 and 1 specifying the fractional overlap between consecutive windows.\n" +
                "* ConfigurationFilePath: The path of the configuration file to load jSymbolic settings from.\n\n" +
                "NOTE: All specified file paths must be either absolute or relative to the directory holding jSymbolic.jar.\n\n";
    }

    /**
     * Prints the given message and then terminates based on the input status.
     * Anything other than a 0 status will result in an system error output printing.
     *
     * @param message The message that needs to be printed.
     * @param status  The status of the termination.
     */
    public static void printMessageAndTerminate(String message, int status) {
        if (status == 0) {
            System.out.println(message);
        } else {
            System.err.println(message);
        }
        System.exit(status);
    }
}
