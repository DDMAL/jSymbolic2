package jsymbolic2.commandline;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import jsymbolic2.processing.FeatureExtractionJobProcessor;
import jsymbolic2.processing.UserFeedbackGenerator;

/**
 * Holder class for static methods related to command line processing.
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public final class CommandLineUtilities
{
	/* PUBLIC STATIC METHODS ********************************************************************************/
	

	/**
	 * Parses command line arguments that do not require accessing a configuration settings file. Carries
	 * out feature extraction and saving of output files based on the specified arguments. Prints status
	 * messages during processing to standard out, and error messages to standard err. Outputs an appropriate
	 * error message if invalid arguments are specified.
	 * 
	 * @param args	Arguments with which jSymbolic was run at the command line. Note that this method is only
	 *				equipped to deal with arguments that do not involve accessing a configuration settings
	 *				file, and do not access GUI; any arguments that do in fact indicate either of these things
	 *				this will be treated as invalid by this method.
	 */
	public static void parseNoConfigFileCommandLineAndExtractAndSaveFeatures(String[] args)
	{
		// To hold feature extraction settings
		boolean convert_to_arff = true;
		boolean convert_to_csv = true;
		PrintStream status_print_stream = System.out;
		PrintStream error_print_stream = System.err;

		// Define search patterns for parsing command line arguments
		final String window_size_pattern = "\\d*.?\\d*";
		final String window_offset_pattern = "0?.\\d*";
		
		// Define flags
		final String arff_flag = "-arff";
		final String csv_flag = "-csv";
		final String window_flag = "-window";
		
		// Find out if CSV and/or ARFF files should be generated. Create reduced_args to hold the command line
		// arguments with CSV or ARFF flags, if any, removed.
		String[] reduced_args = new String[args.length];
		for (int i = 0; i < reduced_args.length; i++)
			reduced_args[i] = args[i];
		if (args.length > 2)
		{
			if ( args[0].equalsIgnoreCase(csv_flag) ||
			     args[0].equalsIgnoreCase(arff_flag) ||
			     args[1].equalsIgnoreCase(csv_flag) ||
			     args[1].equalsIgnoreCase(arff_flag)) 
			{
				if (args[0].equalsIgnoreCase(csv_flag) || args[1].equalsIgnoreCase(csv_flag))
					convert_to_csv = true;
				if (args[0].equalsIgnoreCase(arff_flag) || args[1].equalsIgnoreCase(arff_flag))
					convert_to_arff = true;

				List<String> reduced_args_list = new ArrayList<>();
				for (String arg : args)
					if (!arg.equalsIgnoreCase(csv_flag) && !arg.equalsIgnoreCase(arff_flag))
						reduced_args_list.add(arg);
				reduced_args = reduced_args_list.toArray(new String[0]);
			}
		}

		// If there are a proper number of command line arguments, assuming no windowing
		if (reduced_args.length == 3)
		{
			String input_file_path = reduced_args[0];
			String ace_xml_feature_values_file_path = reduced_args[1];
			String ace_xml_feature_definitions_file_path = reduced_args[2];
			boolean save_features_for_each_window = false;
			boolean save_overall_recording_features = true;
			double window_size = 0.0;
			double window_overlap = 0.0;
			
			FeatureExtractionJobProcessor.extractAndSaveDefaultFeatures( input_file_path,
			                                                             ace_xml_feature_values_file_path,
			                                                             ace_xml_feature_definitions_file_path,
			                                                             save_features_for_each_window,
			                                                             save_overall_recording_features,
			                                                             window_size,
			                                                             window_overlap,
			                                                             convert_to_arff,
			                                                             convert_to_csv,
			                                                             status_print_stream,
			                                                             error_print_stream,
						                                                 false );
		}
		else
		{
			// If proper command line arguments are present for windowing
			if ( reduced_args.length == 6 && reduced_args[0].equals(window_flag) &&
			     reduced_args[4].matches(window_size_pattern) &&
			     reduced_args[5].matches(window_offset_pattern) )
			{
				String input_file_path = reduced_args[1];
				String ace_xml_feature_values_file_path = reduced_args[2];
				String ace_xml_feature_definitions_file_path = reduced_args[3];
				boolean save_features_for_each_window = true;
				boolean save_overall_recording_features = false;
				double window_size = Double.parseDouble(reduced_args[4]);
				double window_overlap = Double.parseDouble(reduced_args[5]);
				
				FeatureExtractionJobProcessor.extractAndSaveDefaultFeatures( input_file_path,
				                                                             ace_xml_feature_values_file_path,
				                                                             ace_xml_feature_definitions_file_path,
				                                                             save_features_for_each_window,
				                                                             save_overall_recording_features,
				                                                             window_size,
				                                                             window_overlap,
				                                                             convert_to_arff,
				                                                             convert_to_csv,
				                                                             status_print_stream,
				                                                             error_print_stream,
						                                                     false );
			} 
			
			// Indicate invalid choice of command line arguments
			else
				UserFeedbackGenerator.indicateIncorrectCommandLineArgumentsAndEndExecution(System.err, args);
		}
	}


	/**
	 * Returns instructions on how to properly format command line arguments for execution of jSymbolic,
	 * including all available switches and options.
	 * 
	 * @return	Usage instructions for the jSymbolic command line interface.
	 */
	public static String getCommandLineCorrectUsage()
	{
		return "\nProper usage of jSymbolic via the command line requires one of the following command line argument configurations (see the manual for more details):\n\n"
				+ "1) No arguments (automaticallys run the GUI under default settings)\n"
				+ "2) <SymbolicMusicFileOrDirectoryInputPath> <AceXmlFeatureValuesOutputPath> <AceXmlFeatureDefinitionsOutputPath>\n"
				+ "\t-arff and/or -csv can be optionally be added before the above arguments.\n"
				+ "3) -window <SymbolicMusicFileInputPath> <AceXmlFeatureValuesOutputPath> <AceXmlFeatureDefinitionsOutputPath> <WindowLength> <WindowOverlapFraction>\n"
				+ "\t-arff and/or -csv can be optionally be added before the above arguments.\n"
				+ "4) -configrun <ConfigurationFilePath>\n"
				+ "5) -configrun <ConfigurationFilePath> <SymbolicMusicFileInputPath> <AceXmlFeatureValuesOutputPath> <AceXmlFeatureDefinitionsOutputPath>\n"
				+ "6) -configgui <ConfigurationFilePath>\n"
				+ "7) -validateconfigallheaders <ConfigurationFilePath>\n"
				+ "8) -validateconfigfeatureoption <ConfigurationFilePath>\n"
				+ "9) -consistencycheck <MidiFileOrMeiFileOrDirectoryPath>\n"
				+ "10) -mididump <MidiFileOrMeiFileOrDirectoryPath>\n"
				+ "11) -help\n\n"
				+ "Command line variable descriptions:\n"
				+ "* SymbolicMusicFileOrDirectoryInputPath: The file path of the MIDI or MEI file from which features are to be extracted. May alternatively be a directory holding one or more such files (sub-folders are searched recursively, and files must have qualifying MIDI or MEI extensions to be included).\n"
				+ "* AceXmlFeatureValuesOutputPath: The path of the ACE XML file to which extracted feature values will be saved.\n"
				+ "* AceXmlFeatureDefinitionsOutputPath: The path of the ACE XML file to to which metadata descriptions of the extracted features will be saved.\n"
				+ "* WindowLength: The duration in seconds of windows to be used during windowed feature extraction.\n"
				+ "* WindowOverlapFraction: A value between 0 and 1 specifying the fractional overlap between consecutive windows.\n"
				+ "* ConfigurationFilePath: The path of a configuration file to load jSymbolic settings from.\n"
				+ "* MidiOrMeiOrDirectoryPath: The path of a MIDI or MEI file to parse and report on. May alternatively be a directory holding one or more such files (sub-folders are searched recursively, and files must have qualifying MIDI or MEI extensions to be included). MEI files are converted to MIDI as part of this process.\n\n"
				+ "NOTE: All specified file paths must either be absolute or relative to the directory holding jSymbolic2.jar.\n\n";
	}
}