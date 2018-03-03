package jsymbolic2.api;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import ace.datatypes.DataBoard;
import ace.datatypes.DataSet;
import jsymbolic2.configuration.ConfigFileHeaderEnum;
import jsymbolic2.configuration.ConfigurationFileData;
import jsymbolic2.configuration.txtimplementation.ConfigurationFileValidatorTxtImpl;
import jsymbolic2.featureutils.FeatureExtractorAccess;
import jsymbolic2.processing.*;

/**
 * This class provides an API for programmatic access to jSymbolic's functionality. The constructor allows the
 * programmer to specify feature extraction settings, after which the methods of this class can be called to
 * extract features. Extracted feature values and associated metadata are saved to files, and may also be
 * returned in the form of a JsymbolicData object, depending on the particular method called. Separate
 * independent processing can also be performed via the static methods of this class.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class JsymbolicProcessor
{
	/* PRIVATE FIELDS ***************************************************************************************/
	
	
	/**
	 * The path of the ACE XML Feature Values file to save extracted feature values to.
	 */
	private final String feature_values_save_path;

	/**
	 * The path of the ACE XML Feature Definitions file to save feature metadata about all extracted features
	 * to.
	 */
	private final String feature_definitions_save_path;

	/**
	 * Whether or not to save extracted feature values in a Weka ARFF file. If this is set to true, the saved
	 * file will have the same save path as that specified in feature_values_save_path, but with a different
	 * file extension.
	 */
	private final boolean save_arff_file;

	/**
	 * Whether or not to save extracted feature values in a CSV file. If this is set to true, the saved file
	 * will have the same save path as that specified in feature_values_save_path, but with a different file
	 * extension.
	 */
	private final boolean save_csv_file;

	/**
	 * Which features to extract, as returned by a call to the FeatureExtractorAccess.findSpecifiedFeatures
	 * method.
	 */
	private final boolean[] features_to_extract;

	/**
	 * Whether or not to extract features over each input file as a whole (i.e. without breaking each input
	 * file into analysis windows. Note that if this is set to true, then save_features_for_each_window must
	 * be set to false.
	 */
	private final boolean save_features_for_overall_pieces;

	/**
	 * Whether or not to break each input file into smaller analysis windows, and then extracting features
	 * from these windows individually, rather than from pieces as a whole. Note that if this is set to true,
	 * then save_features_for_overall_pieces must be set to false.
	 */
	private final boolean save_features_for_each_window;

	/**
	 * The duration of each analysis window, in seconds. Value is irrelevant if save_features_for_each_window
	 * is set to false.
	 */
	private final double analysis_window_size;

	/**
	 * The fractional (0 to 1) overlap between consecutive analysis windows. Value is irrelevant if
	 * save_features_for_each_window is set to false.
	 */
	private final double analysis_window_overlap;

	/**
	 * A stream to print processing status messages to as processing continues.
	 */
	private final PrintStream status_print_stream;

	/**
	 * A stream to print error messages to as processing continues.
	 */
	private final PrintStream error_print_stream;

	
	/* CONSTRUCTORS *****************************************************************************************/
	
	
	/**
	 * Instantiate a JsymbolicProcessor object with manually specified feature extraction settings. Note that
	 * calling this constructor does NOT begin feature extraction from any input files; a separate method call
	 * must be made after instantiation in order to do this.
	 *
	 * @param feature_values_save_path			The path of the ACE XML Feature Values file to save extracted
	 *											feature values to.
	 * @param feature_definitions_save_path		The path of the ACE XML Feature Definitions file to save
	 *											feature metadata about all extracted features to.
	 * @param save_arff_file					Whether or not to save extracted feature values in a Weka
	 *											ARFF file. If this is set to true, the saved file will have 
	 *											the same save path as that specified in
	 *											feature_values_save_path, but with a different file extension.
	 * @param save_csv_file						Whether or not to save extracted feature values in a CSV file.
	 *											If this is set to true, the saved file will have the same save
	 *											path as that specified in feature_values_save_path, but with a
	 *											different file extension.
	 * @param names_of_features_to_extract		A list of feature names to extract. Note that the name of each
	 *											feature must exactly match the name of the feature specified
	 *											in its constructor and stored in the name field of its
	 *											FeatureDefinition object.
	 * @param save_features_for_overall_pieces	Whether or not to extract features over each input file 
	 *											as a whole (i.e. without breaking each input file into
	 *											analysis windows. Note that if this is set to true, then
	 *											save_features_for_each_window must be set to false.
	 * @param save_features_for_each_window		Whether or not to break each input file into smaller analysis
	 *											windows, and then extracting features from these windows
	 *											individually, rather than from pieces as a whole. Note that if
	 *											this is set to true, then save_features_for_overall_pieces
	 *											must be set to false.
	 * @param analysis_window_size				The duration of each analysis window, in seconds. Value is
	 *											irrelevant if save_features_for_each_window is set to false.
	 * @param analysis_window_overlap			The fractional (0 to 1) overlap between consecutive analysis
	 *											windows. Value is irrelevant if save_features_for_each_window 
	 *											is set to false.
	 * @param status_print_stream				A stream to print processing status messages to as processing
	 *											continues.
	 * @param error_print_stream				A stream to print error messages to as processing continues.
	 * @throws Exception						Throws an exception if a particular feature name in the
	 *											names_of_features_to_extract list does not correspond to a
	 *											feature implemented in jSymbolic.
	 */
	public JsymbolicProcessor( String feature_values_save_path,
	                           String feature_definitions_save_path,
	                           boolean save_arff_file,
	                           boolean save_csv_file,
	                           List<String> names_of_features_to_extract,
	                           boolean save_features_for_overall_pieces,
	                           boolean save_features_for_each_window,
	                           double analysis_window_size,
	                           double analysis_window_overlap,
	                           PrintStream status_print_stream,
	                           PrintStream error_print_stream )
		throws Exception
	{
		this.feature_values_save_path = feature_values_save_path;
		this.feature_definitions_save_path = feature_definitions_save_path;
		this.save_arff_file = save_arff_file;
		this.save_csv_file = save_csv_file;
		this.features_to_extract = FeatureExtractorAccess.findSpecifiedFeatures(names_of_features_to_extract);
		this.save_features_for_overall_pieces = save_features_for_overall_pieces;
		this.save_features_for_each_window = save_features_for_each_window;
		this.analysis_window_size = analysis_window_size;
		this.analysis_window_overlap = analysis_window_overlap;
		this.status_print_stream = status_print_stream;
		this.error_print_stream = error_print_stream;
	}

	
	/**
	 * Instantiate a JsymbolicProcessor object with manually specified feature extraction settings, and with
	 * default features selected to be extracted (as specified by the
	 * FeatureExtractorAccess.getNamesOfDefaultFeaturesToSave() method). Note that calling this constructor
	 * does NOT begin feature extraction from any input files; a separate method call must be made after
	 * instantiation in order to do this.
	 *
	 * @param feature_values_save_path			The path of the ACE XML Feature Values file to save extracted
	 *											feature values to.
	 * @param feature_definitions_save_path		The path of the ACE XML Feature Definitions file to save
	 *											feature metadata about all extracted features to.
	 * @param save_arff_file					Whether or not to save extracted feature values in a Weka
	 *											ARFF file. If this is set to true, the saved file will have 
	 *											the same save path as that specified in
	 *											feature_values_save_path, but with a different file extension.
	 * @param save_csv_file						Whether or not to save extracted feature values in a CSV file.
	 *											If this is set to true, the saved file will have the same save
	 *											path as that specified in feature_values_save_path, but with a
	 *											different file extension.
	 * @param save_features_for_overall_pieces	Whether or not to extract features over each input file 
	 *											as a whole (i.e. without breaking each input file into
	 *											analysis windows. Note that if this is set to true, then
	 *											save_features_for_each_window must be set to false.
	 * @param save_features_for_each_window		Whether or not to break each input file into smaller analysis
	 *											windows, and then extracting features from these windows
	 *											individually, rather than from pieces as a whole. Note that if
	 *											this is set to true, then save_features_for_overall_pieces
	 *											must be set to false.
	 * @param analysis_window_size				The duration of each analysis window, in seconds. Value is
	 *											irrelevant if save_features_for_each_window is set to false.
	 * @param analysis_window_overlap			The fractional (0 to 1) overlap between consecutive analysis
	 *											windows. Value is irrelevant if save_features_for_each_window 
	 *											is set to false.
	 * @param status_print_stream				A stream to print processing status messages to as processing
	 *											continues.
	 * @param error_print_stream				A stream to print error messages to as processing continues.
	 * @throws Exception						Throws an exception if a particular feature name in the
	 *											names_of_features_to_extract list does not correspond to a
	 *											feature implemented in jSymbolic.
	 */
	public JsymbolicProcessor( String feature_values_save_path,
	                           String feature_definitions_save_path,
	                           boolean save_arff_file,
	                           boolean save_csv_file,
	                           boolean save_features_for_overall_pieces,
	                           boolean save_features_for_each_window,
	                           double analysis_window_size,
	                           double analysis_window_overlap,
	                           PrintStream status_print_stream,
	                           PrintStream error_print_stream )
		throws Exception
	{
		this( feature_values_save_path,
	          feature_definitions_save_path,
	          save_arff_file,
	          save_csv_file,
	          FeatureExtractorAccess.getNamesOfDefaultFeaturesToSave(),
	          save_features_for_overall_pieces,
	          save_features_for_each_window,
	          analysis_window_size,
	          analysis_window_overlap,
	          status_print_stream,
	          error_print_stream );
	}

	
	/**
	 * Instantiate a JsymbolicProcessor object with feature extraction settings specified in a jSymbolic
	 * configuration settings file. An invalid configuration file will result in an error being output to the
	 * provided error_print_stream and an exception being thrown. Note that calling this constructor does NOT
	 * begin feature extraction from any input files that may (or may not) be specified in the configuration
	 * settings file; a separate method call must be made after instantiation in order to do this. Also note
	 * that this constructor does not cause the paths to any input files that may be stored in the
	 * configuration settings file to be stored in the newly instantiated object.
	 *
	 * @param configuration_file_path	The path to a jSymbolic configuration settings file. This 
	 *									configuration file must be valid, and must contain extraction
	 *									settings, output paths and features to extract. It does not, however,
	 *									need to specify input files (these are ignored if present).
	 * @param status_print_stream		A stream to print processing status messages to as processing 
	 *									continues.
	 * @param error_print_stream		A stream to print error messages to as processing continues.
	 * @throws Exception				Throws an exception if the configuration_file_path does not refer to a
	 *									valid configuration file .
	 */
	public JsymbolicProcessor( String configuration_file_path,
	                           PrintStream status_print_stream,
	                           PrintStream error_print_stream )
		throws Exception
	{
		ConfigurationFileData config_data;
		try
		{
			UserFeedbackGenerator.printParsingConfigFileMessage(status_print_stream, configuration_file_path);
			List<ConfigFileHeaderEnum> config_file_headers_to_check = Arrays.asList( ConfigFileHeaderEnum.FEATURE_HEADER,
			                                                                         ConfigFileHeaderEnum.OPTION_HEADER,
			                                                                         ConfigFileHeaderEnum.OUTPUT_FILE_HEADER );
			config_data = new ConfigurationFileValidatorTxtImpl().parseConfigFile( configuration_file_path,
			                                                                       config_file_headers_to_check,
			                                                                       error_print_stream );
		}
		catch (Exception e)
		{
			UserFeedbackGenerator.printExceptionErrorMessage(System.err, e);
			throw e;
		}

		feature_values_save_path = config_data.getFeatureValueSavePath();
		feature_definitions_save_path = config_data.getFeatureDefinitionSavePath();
		save_arff_file = config_data.convertToArff();
		save_csv_file = config_data.convertToCsv();
		features_to_extract = config_data.getFeaturesToSaveBoolean();
		save_features_for_overall_pieces = config_data.saveOverall();
		save_features_for_each_window = config_data.saveWindow();
		analysis_window_size = config_data.getWindowSize();
		analysis_window_overlap = config_data.getWindowOverlap();
		this.status_print_stream = status_print_stream;
		this.error_print_stream = error_print_stream;
	}

	
	/* PUBLIC METHODS ***************************************************************************************/
	
	
	/**
	 * Extracts and saves features from the specified path_of_file_or_folder_to_parse. Carries out these
	 * operations using the settings with which this JsymbolicProcessor object was instantiated. If
	 * path_of_file_or_folder_to_parse refers to a folder rather than a file, then all qualifying files (i.e.
	 * MIDI or MEI) in it have their features extracted. Provides status updates as processing continues. Any
	 * errors occurring during processing are reported on error_print_stream, and are also collected for
	 * summarization at the end of processing. Processing continues even if errors are encountered, with three
	 * exceptions: if the JVM runs out of memory, if an MEI-specific feature is set to be extracted from a
	 * non-MEI file, or if MIDIFeatureProcessor cannot be initialized. In the latter three cases, execution is
	 * terminated immediately.
	 *
	 * @param path_of_file_or_folder_to_parse	The path of a file to extract features from, or of a directory
	 *											holding files to extract features from.
	 * @return									A list of errors that may have occurred during processing.
	 *											Will be empty if no errors occurred. Note that this often 
	 *											simply duplicates what is written to error_print_stream.
	 */	
	public List<String> extractAndSaveFeaturesFromFileOrDirectory(String path_of_file_or_folder_to_parse)
	{
		return FeatureExtractionJobProcessor.extractAndSaveSpecificFeatures( path_of_file_or_folder_to_parse,
		                                                                     feature_values_save_path,
		                                                                     feature_definitions_save_path,
		                                                                     features_to_extract,
		                                                                     save_features_for_each_window,
		                                                                     save_features_for_overall_pieces,
		                                                                     analysis_window_size,
		                                                                     analysis_window_overlap,
		                                                                     save_arff_file,
		                                                                     save_csv_file,
		                                                                     status_print_stream,
		                                                                     error_print_stream,
		                                                                     false );
	}
	
	
	/**
	 * Extracts and saves features from the specified path_of_file_or_folder_to_parse. Carries out these
	 * operations using the settings with which this JsymbolicProcessor object instantiated. If
	 * path_of_file_or_folder_to_parse refers to a folder rather than a file, then all qualifying files (i.e.
	 * MIDI or MEI) in it have their features extracted. Provides status updates as processing continues. Any
	 * errors occurring during processing are reported on error_print_stream, and are also collected for
	 * summarization at the end of processing. Processing continues even if errors are encountered, with three
	 * exceptions: if the JVM runs out of memory, if an MEI-specific feature is set to be extracted from a
	 * non-MEI file, or if MIDIFeatureProcessor cannot be initialized. In the latter three cases, execution is
	 * terminated immediately.
	 *
	 * @param paths_of_files_or_folders_to_parse	A list of files and folders from which features should be 
	 *												extracted.
	 * @return										A list of errors that may have occurred during processing.
	 *												Will be empty if no errors occurred. Note that this often 
	 *												simply duplicates what is written to error_print_stream.
	 */	
	public List<String> extractAndSaveFeaturesFromFileOrDirectory(List<File> paths_of_files_or_folders_to_parse)
	{
		return FeatureExtractionJobProcessor.extractAndSaveSpecificFeatures( paths_of_files_or_folders_to_parse,
		                                                                     feature_values_save_path,
		                                                                     feature_definitions_save_path,
		                                                                     features_to_extract,
		                                                                     save_features_for_each_window,
		                                                                     save_features_for_overall_pieces,
		                                                                     analysis_window_size,
		                                                                     analysis_window_overlap,
		                                                                     save_arff_file,
		                                                                     save_csv_file,
		                                                                     status_print_stream,
		                                                                     error_print_stream,
		                                                                     false );
	}
	
	
	/**
	 * Extracts and saves features from the input files (MIDI or MEI) specified in the provided configuration
	 * settings file. Carries out these operations using the settings with which this JsymbolicProcessor
	 * object was instantiated, NOT with any contrasting settings that may (or may not) be in the
	 * configuration file referred to by configuration_file_path (only input files are parsed from this file).
	 * Provides status updates as processing continues. Any errors occurring during processing are reported on
	 * error_print_stream, and are also collected for summarization at the end of processing. Processing
	 * continues even if errors are encountered, with three exceptions: if the JVM runs out of memory, if an
	 * MEI-specific feature is set to be extracted from a non-MEI file, or if MIDIFeatureProcessor cannot be
	 * initialized. In the latter three cases, execution is terminated immediately.
	 *
	 * @param configuration_file_path	The path of a configuration settings file holding paths to the files
	 *									from which features are to be extracted.
	 * @return							A list of errors that may have occurred during processing.	Will be
	 *									empty if no errors occurred. Note that this often  simply duplicates
	 *									what is written to error_print_stream. Will be null if no features
	 *									could be successfully extracted
	 */		
	public List<String> extractAndSaveFeaturesFromConfigFile(String configuration_file_path)
	{
		List<File> input_file_list = null;
		try
		{
			UserFeedbackGenerator.printParsingConfigFileMessage(status_print_stream, configuration_file_path);
			List<ConfigFileHeaderEnum> config_file_headers_to_check = Arrays.asList(ConfigFileHeaderEnum.INPUT_FILE_HEADER);
			ConfigurationFileData config_file_data = new ConfigurationFileValidatorTxtImpl().parseConfigFile( configuration_file_path,
																											  config_file_headers_to_check,
																											  error_print_stream );
			input_file_list = config_file_data.getInputFileList().getValidFiles();
		}
		catch (Exception e)
		{
			UserFeedbackGenerator.printExceptionErrorMessage(System.err, e);
		}

		if (input_file_list != null)
			return FeatureExtractionJobProcessor.extractAndSaveSpecificFeatures( input_file_list,
																				 feature_values_save_path,
																				 feature_definitions_save_path,
																				 features_to_extract,
																				 save_features_for_each_window,
																				 save_features_for_overall_pieces,
																				 analysis_window_size,
																				 analysis_window_overlap,
																				 save_arff_file,
																				 save_csv_file,
																				 status_print_stream,
																				 error_print_stream,
		                                                                         false );
		else return null;
	}
	
	
	/**
	 * Parse saved extracted feature values and associated metadata (e.g. feature definitions) and return them
	 * in the form of an ace.datatypes.DataBoard object. Note that this method should only be called after
	 * features have been successfully extracted and saved as an ACE XML feature values file at the
	 * feature_values_save_path specified when the constructor of this object was called. Such a feature
	 * extraction would typically have been performed using one of this object's extractAndSaveFeatures
	 * methods. Note that problems encountered are written to error_print_stream and also result in a thrown
	 * exception.
	 *
	 * @return				An ace.datatypes.DataBoard object holding the extracted feature values as well
	 *						as associated feature definitions (which are parsed from the ACE XML feature
	 *						definitions file saved at the path specified by this object's
	 *						feature_definitions_save_path field). See the ACE project's documentation for more
	 *						details.
	 * @throws Exception	An informative exception is thrown if a valid ACE XML file holding extracted 
	 *						feature values cannot be found at the path specified by this object's
	 *						feature_values_save_path field.
	 */	
	public DataBoard getCompleteExtractedFeatureInformation()
		throws Exception
	{
		// Verify that a file exists at feature_values_save_path. Throw an exception if it does not.
		if (feature_values_save_path == null)
		{
			String error_message = "No save path for extracted feature valuse has been specified, so cannot access saved extracted feature values.";
			UserFeedbackGenerator.printErrorMessage(error_print_stream, error_message);
			throw new Exception(error_message);
		}
		try 
		{
			mckay.utilities.staticlibraries.FileMethods.validateFile(new File(feature_values_save_path), true, false);
		}
		catch (Exception e)
		{
			String error_message = "Cannot access the ACE XML file containing extracted features: " + feature_values_save_path + ". Perhaps features have not been extracted yet?";
			UserFeedbackGenerator.printErrorMessage(error_print_stream, error_message);
			throw new Exception(error_message);
		}
		
		// Parse the ACE XML feature values and feature definitions file.
		String[] feature_values_paths = {feature_values_save_path};
		DataBoard feature_values_and_definitions = new DataBoard( null,
		                                                          feature_definitions_save_path,
		                                                          feature_values_paths,
		                                                          null );
		
		// Return the results
		return feature_values_and_definitions;
	}
	
	
	/**
	 * Parse saved extracted feature values and return them in the form of an array of ace.datatypes.DataSet
	 * objects. Note that this method should only be called after features have been successfully extracted
	 * and saved as an ACE XML feature values file at the feature_values_save_path specified when the
	 * constructor of this object was called. Such a feature extraction would typically have been performed
	 * using one of this object's extractAndSaveFeatures methods. Note that problems encountered are written
	 * to error_print_stream and also result in a thrown exception.
	 * 
	 * @return				An array of ace.datatypes.DataSet objects holding the extracted feature values.		
	 *						Each DataSet object corresponds to a different piece of music from which features
	 *						were extracted. If windowed extraction was performed, then each DataSet object in
	 *						the returned array will also hold its own array of DataSet objects, one for each
	 *						extracted window. Feature values are contained in the feature_values field of
	 *						each DataSet object (both single and multi-dimensional features), feature names
	 *						are contained in the feature_names field, and an identifier for each instance
	 *						is stored in the identifier field. See the ACE project's documentation for more
	 *						details.
	 * @throws Exception	An informative exception is thrown if a valid ACE XML file holding extracted 
	 *						feature values cannot be found at the path specified by this object's
	 *						feature_values_save_path field.
	 */
	public DataSet[] getExtractedFeatureValues()
		throws Exception
	{
		DataBoard feature_values_and_definitions = getCompleteExtractedFeatureInformation();
		return feature_values_and_definitions.getFeatureVectors();
	}	

	
	/* PUBLIC STATIC METHODS ********************************************************************************/
	
	
	/**
	 * Parse the MIDI and/or MEI files referred to in file_or_directory_path (either a single file or a
	 * directory holding MIDI and/or MEI files) and return formatted ordered reports on whether or not certain
	 * musical characteristics are consistent both across the files as a group, and internally within each
	 * file. In the case of directories, processing is recursive, and only files with qualifying MIDI or MEI
	 * file extensions are included. Problems that are encountered can result in an exception being thrown
	 * and/or a message being printed to standard error.
	 *
	 * @param file_or_directory_path	Either a single file or a directory holding MIDI and/or MEI files.
	 *									Generate the report based on these files.
	 * @return							The formatted report.
	 * @throws Exception				An informative exception is thrown if there is a problem with the
	 *									specified file_or_directory_path.
	 */
	public static String getConsistencyReport(String file_or_directory_path)
		throws Exception
	{
		// Prepare the set of files (after recursive directory parsing and extension filtering, if
		// appropriate), to report on
		File[] midi_or_mei_file_list = SymbolicMusicFileUtilities.getRecursiveListOfFiles( file_or_directory_path,
																						   new MusicFilter(),
																						   System.err,
																						   new ArrayList<>() );					

		// Prepare and return the report
		if (midi_or_mei_file_list != null)
			return MIDIReporter.prepareConsistencyReports(midi_or_mei_file_list, true, true, true);
		else
			throw new Exception("Null file or directory path provided.");
	}
	
	
	/**
	 * Parse the MIDI and/or MEI files referred to in file_or_directory_path (either a single file or a
	 * directory holding MIDI and/or MEI files) and return formatted ordered reports on the MIDI messages
	 * contained in these files. This report indicates, separately for each file, a structured transcription
	 * of all the relevant MIDI messages that the given file contains. If a given file is an MEI rather than a
	 * MIDI file, then it is converted to MIDI before reporting. In the case of directories, processing is
	 * recursive, and only files with qualifying MIDI or MEI file extensions are included. Problems that are
	 * encountered can result in an exception being thrown and/or a message being printed to standard error.
	 *
	 * @param file_or_directory_path	Either a single file or a directory holding MIDI and/or MEI files.
	 *									Generate the report based on these files.
	 * @return							The formatted report.
	 * @throws Exception				An informative exception is thrown if there is a problem with the
	 *									specified file_or_directory_path.
	 */
	public static String getMidiContentsReport(String file_or_directory_path)
		throws Exception
	{
		// Prepare the set of files (after recursive directory parsing and extension filtering, if
		// appropriate), to report on
		File[] midi_or_mei_file_list = SymbolicMusicFileUtilities.getRecursiveListOfFiles( file_or_directory_path,
																						   new MusicFilter(),
																						   System.err,
																						   new ArrayList<>() );					

		// Prepare and output the reports
		if (midi_or_mei_file_list != null)
		{					
			// The report to display
			StringBuilder report = new StringBuilder();
			
			// Report on each file
			for (int i = 0; i < midi_or_mei_file_list.length; i++)
			{
				// Parse and check the MIDI file
				MIDIReporter midi_debugger = new MIDIReporter(midi_or_mei_file_list[i]);

				// Generate the report
				report.append("\n============ MIDI MESSAGES REPORT FOR FILE " + (i+1) + " / " + midi_or_mei_file_list.length + " ============\n");
				report.append(midi_debugger.prepareHeaderReport());
				report.append(midi_debugger.prepareMetaMessageReport(true, true, true, true, true, true));
				report.append(midi_debugger.prepareProgramChangeAndUnpitchedInstrumentsReport());
				report.append(midi_debugger.prepareControllerMessageReport());
				report.append(midi_debugger.prepareNoteReport(false, true));
			}
			
			// Return the report
			return report.toString();
		}
		else throw new Exception("Null file or directory path provided.");
	}	
}