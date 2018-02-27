package jsymbolic2.commandline;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jsymbolic2.configuration.txtimplementation.ConfigurationFileValidatorTxtImpl;
import jsymbolic2.configuration.ConfigFileHeaderEnum;
import jsymbolic2.configuration.ConfigurationFileData;
import jsymbolic2.processing.FeatureExtractionJobProcessor;
import jsymbolic2.processing.MIDIReporter;
import jsymbolic2.processing.MusicFilter;
import jsymbolic2.processing.SymbolicMusicFileUtilities;
import jsymbolic2.processing.UserFeedbackGenerator;

/**
 * An enumerator for parsing command line arguments and directing execution appropriately. A given enum of the
 * CommandLineSwitchEnum type corresponds to a different type of processing specified at the command line.
 * More particularly, a CommandLineSwitchEnum is initiated by the first command line argument specified at the
 * command line. The switch_string field of a CommandLineSwitchEnum identifies the first command line argument
 * that initiated it, and the switch_actions field identifies the kind of processing that will correspondingly
 * be carried out (via the runProcessing method of the particular SwitchCommandEnum that is pointed to by that
 * field).
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public enum CommandLineSwitchEnum
{
	/* CONSTANTS ********************************************************************************************/

	
	/**
	 * To run the GUI under default settings.
	 */
	GUI(SwitchCommandEnum.PLAIN_GUI, ""),
	
	/**
	 * To run via the command line with no switches and only input and output file paths specified. This is
	 * also used a temporary mapping for invalid command line arguments.
	 */
	NOSWITCH(SwitchCommandEnum.COMMAND_LINE, ""),
	
	/**
	 * A command line switch for running windowed extraction.
	 */
	WINDOW(SwitchCommandEnum.COMMAND_LINE, "-window"),
	
	/**
	 * A command line switch for saving extracted feature values in a Weka ARFF file.
	 */
	ARFF(SwitchCommandEnum.COMMAND_LINE, "-arff"),
	
	/**
	 * A command line switch for saving extracted feature values in a CSV file.
	 */
	CSV(SwitchCommandEnum.COMMAND_LINE, "-csv"),
	
	/**
	 * A command line switch for running the GUI using data parsed from a specified configuration file.
	 */
	CONFIGURATION_GUI(SwitchCommandEnum.CONFIG_GUI, "-configgui"),
	
	/**
	 * A command line switch for running command line processing using data parsed from a specified 
	 * configuration file.
	 */
	CONFIGURATION_RUN(SwitchCommandEnum.CONFIG_RUN, "-configrun"),
	
	/**
	 * A command line switch for checking if the specified configuration file is valid with all data 
	 * specified, including both input and output save paths.
	 */
	VALIDATE_CONFIGURATION_ALL(SwitchCommandEnum.VALIDATE_CONFIG_ALL_HEADERS, "-validateconfigallheaders"),
	
	/**
	 * A command line switch for checking if the specified configuration file is valid with all data 
	 * specified, excluding input and output save paths.
	 */
	VALIDATE_CONFIGURATION_FEATURE_OPTION(SwitchCommandEnum.VALIDATE_CONFIG_FEATURE_OPTION, "-validateconfigfeatureoption"),
	
	/**
	 * A command line switch for checking if certain musical characteristics are consistent both across a set
	 * of MIDI or MEI files, and internally within each file.
	 */
	CONSISTENCY_CHECK(SwitchCommandEnum.CONSISTENCY_CHECK, "-consistencycheck"),
	
	/**
	 * A command line switch for printing out reports on the contents of a MIDI (or converted) MEI files.
	 */
	MIDI_DUMP(SwitchCommandEnum.MIDI_DUMP, "-mididump"),
	
	/**
	 * A command line switch for printing out valid command line usage instructions.
	 */
	HELP(SwitchCommandEnum.HELP, "-help");


	/* STATIC FINAL FIELDS **********************************************************************************/

	
	/**
	 * The path where the default configuration file is stored.
	 */
	public static final String default_config_file_path = "./jSymbolicDefaultConfigs.txt";
	

	/* FIELDS ***********************************************************************************************/

	
	/**
	 * The actions associated with a particular command line argument switch.
	 */
	private final SwitchCommandEnum switch_actions;

	
	/**
	 * The command line text key corresponding to a particular command line argument switch.
	 */
	private final String switch_string;


	/* CONSTRUCTOR ******************************************************************************************/


	/**
	 * Instantiate a CommandLineSwitchEnum corresponding to a certain command line argument / switch.
	 *
	 * @param switch_action	An enum specifying actions associated with a particular command line switch.
	 * @param switchString	The command line text corresponding to a particular switch.
	 */
	CommandLineSwitchEnum(SwitchCommandEnum switch_action, String switch_string)
	{
		this.switch_actions = switch_action;
		this.switch_string = switch_string;
	}


	/* PUBLIC STATIC METHODS ********************************************************************************/


	/**
	 * Execute processing based on the given command line arguments.
	 *
	 * @param args	Arguments with which jSymbolic was run at the command line.
	 */
	public static void runCommandLine(String[] args)
	{
		// Will hold appropriate processing for the specified args
		CommandLineSwitchEnum this_switch_to_run;
		
		// If there are no command line arguments
		if (args == null || args.length <= 0)
			this_switch_to_run = GUI;
		
		// If there are command line arguments
		else
		{
			// If the first command line argument is known
			if (matchesKnownConstant(args[0]))
				this_switch_to_run = stringToSwitch(args[0]);

			// If the first command line argument is not a known switch. This could mean that invalid command
			// line arguments were specified, or it could mean that there is no switch, but three valid I/O
			// file paths were specified.
			else this_switch_to_run = NOSWITCH;
		}
		
		// Run processing via the SwitchCommandEnum internal enum
		this_switch_to_run.switch_actions.runProcessing(args);
	}


	/* PRIVATE STATIC METHODS *******************************************************************************/


	/**
	 * Check to see if the given arg matches the switch_string of a known CommandLineSwitchEnum.
	 *
	 * @param arg	A command line argument to check to see if it matches.
	 * @return		True if arg matches, false if it does not.
	 */
	private static boolean matchesKnownConstant(String arg)
	{
		for (CommandLineSwitchEnum e : CommandLineSwitchEnum.values())
			if (e.switch_string.equals(arg))
				return true;
		return false;
	}
	

	/**
	 * Get a CommandLineSwitchEnum with a switch_string matching the given arg.
	 *
	 * @param arg	A command line argument to match.
	 * @return		The CommandLineSwitchEnum with a switch_string matching arg.
	 */
	private static CommandLineSwitchEnum stringToSwitch(String arg)
	{
		for (CommandLineSwitchEnum e : CommandLineSwitchEnum.values())
			if (e.switch_string.equals(arg))
				return e;
		return null;
	}
	
	
	/* INTERNAL ENUM ****************************************************************************************/

	
	/**
	 * An internal enum that allows the outer CommandLineSwitchEnum enum to specify a particular type of 
	 * processing to occur based on parsed command line arguments.
	 */
	private enum SwitchCommandEnum
	{
		/* CONSTANTS ****************************************************************************************/


		COMMAND_LINE
		{
			/**
			 * Runs command line feature extraction under settings where there is no configuration file 
			 * specified by the user, and where the GUI is not used. If there is a configuration at the 
			 * default path, then settings are loaded from it. If such a file is invalid or does not exist,
			 * then jSybmolic command line feature extraction is performed under standard settings. Note that
			 * this method also serves as a catchall for generally erroneous command line arguments. If 
			 * extraction is successful, then save ACE XML feature values and feature definitions files as
			 * well as, if appropriate, Weka ARFF and CSV files.
			 *
			 * @param args	Arguments with which jSymbolic was run at the command line.
			 */
			@Override
			public void runProcessing(String[] args)
			{
				if ( args.length == 3 &&  Files.exists(Paths.get(default_config_file_path)) )
				{
					// Try loading configuration file at default path
					try
					{
						List<File> input_file_list = Arrays.asList(new File(args[0]));
						String feature_values_save_path = args[1];
						String feature_definitions_save_path = args[2];
						List<ConfigFileHeaderEnum> config_file_headers_to_check = Arrays.asList(ConfigFileHeaderEnum.FEATURE_HEADER, ConfigFileHeaderEnum.OPTION_HEADER);
						UserFeedbackGenerator.printParsingConfigFileMessage(System.out, default_config_file_path);
						ConfigurationFileData config_file_data = new ConfigurationFileValidatorTxtImpl().parseConfigFile(default_config_file_path, config_file_headers_to_check, System.err);
						FeatureExtractionJobProcessor.extractAndSaveFeaturesConfigFileSettings( input_file_list,
						                                                                        config_file_data,
						                                                                        feature_values_save_path,
						                                                                        feature_definitions_save_path,
						                                                                        System.out,
						                                                                        System.err,
						                                                                        false );
					}
					
					// Run without configuration file if does not exist at default path
					catch (Exception ex)
					{
						UserFeedbackGenerator.simplePrintln(System.out, "NON-CRITICAL WARNING: Could not find a configurations file called " + default_config_file_path + "  in the jSymbolic home directory that is valid under current settings. As a result, processing will continue using standard settings (unless specified manually). Although a default configurations file is by no means necessary to use jSymbolic, it is often convenient. You can save one at anytime either manually or using the jSymbolic GUI, if you wish (see the manual for more details).\n");
						CommandLineUtilities.parseNoConfigFileCommandLineAndExtractAndSaveFeatures(args);
					}
				}
				
				// Run without configuration file if does not exist at default path
				else if (args.length == 3)
				{
					UserFeedbackGenerator.simplePrintln(System.out, "NON-CRITICAL WARNING: Could not find a configurations file called " + default_config_file_path + " in the jSymbolic home directory that is valid under current settings. As a result, processing will continue using standard settings (unless specified manually). Although a default configurations file is by no means necessary to use jSymbolic, it is often convenient. You can save one at anytime either manually or using the jSymbolic GUI, if you wish (see the manual for more details).\n");
					CommandLineUtilities.parseNoConfigFileCommandLineAndExtractAndSaveFeatures(args);
				}
				
				// Run without configuration file if does not exist at default path if three are some other
				// number of command line arguments than 3
				else CommandLineUtilities.parseNoConfigFileCommandLineAndExtractAndSaveFeatures(args);
			}
		},
		
		PLAIN_GUI
		{
			/**
			 * Runs the GUI. If a file named jSymbolicDefaultConfigs.txt is in the same directory as the
			 * jSymbolic2.jar at runtime, then the GUI will be pre-loaded with the settings specified in that
			 * file at runtime. If this default configuration file does not exist or is invalid, then the
			 * jSymbolic GUI is run with default settings (and a message is output indicating that the
			 * default configuration file was absent).
			 *
			 * @param args	Arguments with which jSymbolic was run at the command line.
			 */
			@Override
			public void runProcessing(String[] args)
			{
				// Try parsing default configuration file if it exists
				if (Files.exists(Paths.get(default_config_file_path)))
				{
					try
					{
						UserFeedbackGenerator.printParsingConfigFileMessage(System.out, default_config_file_path);
						ConfigurationFileData config_file_data = new ConfigurationFileValidatorTxtImpl().parseConfigFileTwoThreeOrFour(default_config_file_path, System.err);
						new jsymbolic2.gui.OuterFrame(config_file_data);
					}
					catch (Exception e)
					{
						new jsymbolic2.gui.OuterFrame(null);
					}
				}
				else
				{
					new jsymbolic2.gui.OuterFrame(null);
				}
			}
		},
		
		CONFIG_GUI
		{
			/**
			 * Runs the GUI with settings pre-loaded from the configuration file specified in args.
			 *
			 * @param args	Arguments with which jSymbolic was run at the command line.
			 */
			@Override
			public void runProcessing(String[] args)
			{
				if (args.length != 2)
					UserFeedbackGenerator.indicateIncorrectCommandLineArgumentsAndEndExecution(System.err, args);
				
				String config_file_path = args[1];
				try
				{
					UserFeedbackGenerator.printParsingConfigFileMessage(System.out, config_file_path);
					ConfigurationFileData config_file_data = new ConfigurationFileValidatorTxtImpl().parseConfigFileTwoThreeOrFour(config_file_path, System.err);
					new jsymbolic2.gui.OuterFrame(config_file_data);
				}
				catch (Exception e) { UserFeedbackGenerator.printExceptionErrorMessage(System.err, e); }
			}
		},
		
		CONFIG_RUN
		{
			/**
			 * Parse the command line specified in args and carry out feature extraction based on its contents
			 * and, potentially, additional information specified in args. Output any errors that occur to
			 * System.err. If extraction is successful, then save ACE XML feature values and feature 
			 * definitions files as well as, if appropriate, Weka ARFF and CSV files.
			 *
			 * @param args Arguments with which jSymbolic was run at the command line.
			 */
			@Override
			public void runProcessing(String[] args)
			{
				// The path of the configuration file
				String config_file_path = args[1];
				
				// If only configuration file path is specified in args 
				if (args.length == 2)
				{
					try
					{
						UserFeedbackGenerator.printParsingConfigFileMessage(System.out, config_file_path);
						ConfigurationFileData config_file_data = new ConfigurationFileValidatorTxtImpl().parseConfigFileAllHeaders(config_file_path, System.err);
						List<File> input_file_list = config_file_data.getInputFileList().getValidFiles();
						String feature_values_save_path = config_file_data.getFeatureValueSavePath();
						String feature_definitions_save_path = config_file_data.getFeatureDefinitionSavePath();
						FeatureExtractionJobProcessor.extractAndSaveFeaturesConfigFileSettings( input_file_list,
																								config_file_data,
																								feature_values_save_path,
																								feature_definitions_save_path,
																								System.out,
																								System.err,
						                                                                        false );
					}
					catch (Exception e) { UserFeedbackGenerator.printExceptionErrorMessage(System.err, e); }
				}
					
				// If input and output file paths are specified in args, in addition to the configuration 
				// file path
				else
				{
					// Must be either 2 or 5 command line arguments for CONFIG_RUN option
					if (args.length != 5)
						UserFeedbackGenerator.indicateIncorrectCommandLineArgumentsAndEndExecution(System.err, args);
					
					// Parse args
					String input_file_path = args[2];
					String feature_values_save_path = args[3];
					String feature_definitions_save_path = args[4];
					List<ConfigFileHeaderEnum> config_file_headers_to_check = Arrays.asList(ConfigFileHeaderEnum.FEATURE_HEADER, ConfigFileHeaderEnum.OPTION_HEADER);
					
					// Parse configuration file and extract features based on its contents and the supplied
					// command line arguments
					try
					{
						UserFeedbackGenerator.printParsingConfigFileMessage(System.out, config_file_path);
						ConfigurationFileData config_file_data = new ConfigurationFileValidatorTxtImpl().parseConfigFile(config_file_path, config_file_headers_to_check, System.err);
						List<File> input_file_list = Arrays.asList(new File(input_file_path));
						FeatureExtractionJobProcessor.extractAndSaveFeaturesConfigFileSettings( input_file_list,
																								config_file_data,
																								feature_values_save_path,
																								feature_definitions_save_path,
																								System.out,
																								System.err,
						                                                                        false );
					}
					catch (Exception e)
					{
						UserFeedbackGenerator.printExceptionErrorMessage(System.err, e);
					} 
				}
			}
		},
		
		VALIDATE_CONFIG_ALL_HEADERS
		{
			/**
			 * Print out whether the configuration file referred to in args is valid. In this case, it is 
			 * considered valid only if feature settings, option settings, input file paths and output file
			 * paths are all specified. in the configuration file.
			 *
			 * @param args			Arguments with which jSymbolic was run at the command line.
			 */
			@Override
			public void runProcessing(String[] args)
			{
				// Check valid number of command line arguments
				if (args.length != 2)
					UserFeedbackGenerator.indicateIncorrectCommandLineArgumentsAndEndExecution(System.err, args);

				String config_file_path = args[1];
				try
				{
					// Try parsing configuration file to see if it is valid
					UserFeedbackGenerator.printParsingConfigFileMessage(System.out, config_file_path);
					new ConfigurationFileValidatorTxtImpl().parseConfigFileAllHeaders(config_file_path, System.err);
					
					// If the configuration file is valid as defined by this method
					UserFeedbackGenerator.simplePrintln(System.out, "\n" + config_file_path + " is a valid configuration file that specifies features to be extracted, extraction options, input file paths and output file paths.\n");
				}
				
				// If the configuration file is not valid as defined by this method
				catch (Exception e) { UserFeedbackGenerator.simplePrintln(System.out, "\n" + e.getMessage() + "\n"); }
			}
		},
		
		VALIDATE_CONFIG_FEATURE_OPTION
		{
			/**
			 * Print out whether the configuration file referred to in args is valid. In this case, it is 
			 * considered valid only if feature and option settings are both specified, but input and output
			 * file paths are NOT specified in the configuration file.
			 * 
			 * @param args	Arguments with which jSymbolic was run at the command line.
			 */
			@Override
			public void runProcessing(String[] args)
			{
				// Check valid number of command line arguments
				if (args.length != 2)
					UserFeedbackGenerator.indicateIncorrectCommandLineArgumentsAndEndExecution(System.err, args);

				String config_file_path = args[1];
				List<ConfigFileHeaderEnum> config_headers_to_check = Arrays.asList( ConfigFileHeaderEnum.FEATURE_HEADER,
				                                                                    ConfigFileHeaderEnum.OPTION_HEADER );
				try
				{
					// Try parsing configuration file to see if it is valid
					UserFeedbackGenerator.printParsingConfigFileMessage(System.out, config_file_path);
					new ConfigurationFileValidatorTxtImpl().parseConfigFile(config_file_path, config_headers_to_check, System.err);
					
					// If the configuration file is valid as defined by this method
					UserFeedbackGenerator.simplePrintln(System.out, "\n" + config_file_path + " is a valid configuration file that specifies features to be extracted and extraction options, but does not specify input or output files.\n");
				}
				
				// If the configuration file is not valid as defined by this method
				catch (Exception e) { UserFeedbackGenerator.simplePrintln(System.out, "\n" + e.getMessage() + "\n"); }
			}
		},
			
		CONSISTENCY_CHECK
		{
			/**
			 * Parses the MIDI and/or MEI files referred to in args (either a single file or a directory 
			 * holding MIDI and/or MEI files) and prints out ordered reports to standard out on whether or not
			 * certain musical characteristics are consistent both across the files as a group, and internally
			 * within each file. In the case of directories, processing is recursive, and only files with
			 * qualifying MIDI or MEI file extensions are included.
			 * 
			 * @param args	Arguments with which jSymbolic was run at the command line.
			 */
			@Override
			public void runProcessing(String[] args)
			{
				// Check valid number of command line arguments
				if (args.length != 2)
					UserFeedbackGenerator.indicateIncorrectCommandLineArgumentsAndEndExecution(System.err, args);

				try
				{
					// Prepare the set of files (after recursive directory parsing and extension filtering, if
					// appropriate), to report on
					File[] midi_or_mei_file_list = SymbolicMusicFileUtilities.getRecursiveListOfFiles( args[1],
					                                                                                   new MusicFilter(),
					                                                                                   System.err,
					                                                                                   new ArrayList<>() );					
					
					// Prepare and output the reports
					if (midi_or_mei_file_list != null)
					{					
						String report = MIDIReporter.prepareConsistencyReports( midi_or_mei_file_list,
						                                                        true,
						                                                        true,
						                                                        true );
						UserFeedbackGenerator.simplePrint(System.out, report);
					}
				}			
				// If the MIDI file is not valid
				catch (Exception e) { UserFeedbackGenerator.printExceptionErrorMessage(System.err, e); }
			}
		},

		MIDI_DUMP
		{
			/**
			 * Parse the MIDI and/or MEI files referred to in args (either a single file or a directory 
			 * holding MIDI and/or MEI files) and print out ordered reports on the file(s) and the MIDI
			 * messages they contain to standard out. If it is an MEI file, it is converted to MIDI before
			 * reporting. In the case of directories, processing is recursive, and only files with qualifying
			 * MIDI or MEI file extensions are included.
			 * 
			 * @param args	Arguments with which jSymbolic was run at the command line.
			 */
			@Override
			public void runProcessing(String[] args)
			{
				// Check valid number of command line arguments
				if (args.length != 2)
					UserFeedbackGenerator.indicateIncorrectCommandLineArgumentsAndEndExecution(System.err, args);

				try
				{
					// Prepare the set of files (after recursive directory parsing and extension filtering, if
					// appropriate), to report on
					File[] midi_or_mei_file_list = SymbolicMusicFileUtilities.getRecursiveListOfFiles( args[1],
					                                                                                   new MusicFilter(),
					                                                                                   System.err,
					                                                                                   new ArrayList<>() );					
					
					// Prepare and output the reports
					if (midi_or_mei_file_list != null)
					{					
						// Report on each file
						for (int i = 0; i < midi_or_mei_file_list.length; i++)
						{
							// Note progress
							UserFeedbackGenerator.simplePrint(System.out, "\n============ MIDI MESSAGES REPORT FOR FILE " + (i+1) + " / " + midi_or_mei_file_list.length + " ============\n");

							// Parse and check the MIDI file
							MIDIReporter midi_debugger = new MIDIReporter(midi_or_mei_file_list[i]);

							// Output the reports
							UserFeedbackGenerator.simplePrint(System.out, midi_debugger.prepareHeaderReport());
							UserFeedbackGenerator.simplePrint(System.out, midi_debugger.prepareMetaMessageReport(true, true, true, true, true, true));
							UserFeedbackGenerator.simplePrint(System.out, midi_debugger.prepareProgramChangeAndUnpitchedInstrumentsReport());
							UserFeedbackGenerator.simplePrint(System.out, midi_debugger.prepareControllerMessageReport());
							UserFeedbackGenerator.simplePrintln(System.out, midi_debugger.prepareNoteReport(false, true));
						}
					}
				}			
				// If the MIDI file is not valid
				catch (Exception e) { UserFeedbackGenerator.printExceptionErrorMessage(System.err, e); }
			}
		},
		
		HELP
		{
			/**
			 * Print general jSymbolic command line usage to standard out.
			 *
			 * @param args	Arguments with which jSymbolic was run at the command line.
			 */
			@Override
			public void runProcessing(String[] args)
			{
				UserFeedbackGenerator.simplePrintln(System.out, CommandLineUtilities.getCommandLineCorrectUsage());
			}
		};

		
		/* ABSTRACT METHOD **********************************************************************************/

		
		/**
		 * Execute processing appropriate for the the given command line arguments, as specified by this
		 * particular SwitchCommandEnum.
		 *
		 * @param args			Arguments with which jSymbolic was run at the command line.
		 */
		public abstract void runProcessing(String[] args);
	}
}