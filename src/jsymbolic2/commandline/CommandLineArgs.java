/*
 * CommandLine.java
 * Version 2.0
 *
 * Last modified on June 24, 2010.
 * McGill University and the University of Waikato
 */
package jsymbolic2.commandline;

import java.util.ArrayList;
import java.util.List;

import jsymbolic2.processing.AceXmlConverter;

/**
 * Allows jSymbolic's functionality to be accessed from the command line.
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public class CommandLineArgs {

    private boolean ARFFcheck = false;
    private boolean CSVcheck = false;

    /**
     * Interprets the command line arguments and begins feature extraction.
     *
     * @param args arguments passed in by user through command line
     */
    public CommandLineArgs(String[] args) {
        String window_size_pattern = "\\d*.?\\d*";
        String window_offset_pattern = "0?.\\d*";
        String ace_xml_feature_values_file = "";
		String ace_xml_feature_definitions_file = "";

        String[] tempArgs = checkFileTypes(args);
        args = (tempArgs == null) ? args : tempArgs;

        // If there are a proper number of command line arguments
        if (args.length == 3)
		{
            checkArgParameters(args, 3);
            ace_xml_feature_values_file = args[1];
			ace_xml_feature_definitions_file = args[2];
        }
		else if (args.length == 6 && args[0].equals("-window") &&
                 args[4].matches(window_size_pattern) &&
                 args[5].matches(window_offset_pattern) )
		{
            checkArgParameters(args, 6);
            ace_xml_feature_values_file = args[2];
			ace_xml_feature_definitions_file = args[3];
        }
		else // if invalid command line arguments are used
            CommandLineUtils.printMessageAndTerminate(CommandLineUtils.getUsageMessage(), -1);

        try
		{
            AceXmlConverter.saveAsArffOrCsvFiles(ace_xml_feature_values_file, ace_xml_feature_definitions_file, ARFFcheck, CSVcheck);
        }
		catch (Exception ex)
		{
            String errorMessage = "Error converting " + ace_xml_feature_values_file + " to ARFF/CSV.\n";
            CommandLineUtils.printMessageAndTerminate(errorMessage, -1);
        }
    }

    /**
     * Check for ARFF or CSV output.
     *
     * @param args args with -csv or -arff switches
     * @return new args[] without -csv or -arff switches or else return null
     */
    private String[] checkFileTypes(String[] args) {
        String ARFF = "-arff";
        String CSV = "-csv";

        if (args.length > 2 &&
                (args[0].equalsIgnoreCase(CSV) || args[0].equalsIgnoreCase(ARFF)
                        || args[1].equalsIgnoreCase(CSV) || args[1].equalsIgnoreCase(ARFF))) {
            if (args[0].equalsIgnoreCase(CSV)
                    || args[1].equalsIgnoreCase(CSV)) {
                CSVcheck = true;
            }
            if (args[0].equalsIgnoreCase(ARFF)
                    || args[1].equalsIgnoreCase(ARFF)) {
                ARFFcheck = true;
            }

            List<String> tempArgs = new ArrayList<>();
            for (String arg : args) {
                if (!arg.equalsIgnoreCase(CSV)
                        && !arg.equalsIgnoreCase(ARFF)) {
                    tempArgs.add(arg);
                }
            }
            return tempArgs.toArray(new String[0]);
        }
        return null;
    }

    /**
     * Check if the number of args is correct and set them appropriately for
     * the creation of the MIDIFeatureProcessor object.
     *
     * @param args       arguments passed in by used without -csv or -arff switches
     * @param argslength equivalent to args.length
     */
    private static void checkArgParameters(String[] args,
                                           int argslength) {
        String input_file_path;
        String feature_values_save_path;
        String feature_descriptions_save_path;
        boolean save_features_for_each_window;
        boolean save_overall_recording_features;
        double window_size;
        double window_overlap;

        if (argslength == 3) {
            input_file_path = args[0];
            feature_values_save_path = args[1];
            feature_descriptions_save_path = args[2];
            save_features_for_each_window = false;
            save_overall_recording_features = true;
            window_size = 1.0;
            window_overlap = 0.0;
        } else //argslength == 6
        {
            input_file_path = args[1];
            feature_values_save_path = args[2];
            feature_descriptions_save_path = args[3];
            save_features_for_each_window = true;
            save_overall_recording_features = false;
            window_size = Double.parseDouble(args[4]);
            window_overlap = Double.parseDouble(args[5]);

        }

        CommandLineFeatureExtraction.setupFeatureProcessorAndExtractFeatures(input_file_path,
                feature_values_save_path,
                feature_descriptions_save_path,
                save_features_for_each_window,
                save_overall_recording_features,
                window_size,
                window_overlap);
    }
}
