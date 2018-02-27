package jsymbolic2.processing;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;
import ace.datatypes.DataBoard;
import jsymbolic2.configuration.ConfigurationFileData;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.FeatureExtractorAccess;
import mckay.utilities.staticlibraries.FileMethods;

/**
 * Static methods for performing outer layer feature extraction jobs.
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public final class FeatureExtractionJobProcessor
{
	/* PUBLIC STATIC METHODS ********************************************************************************/
	
	
	/**
	 * Extracts features from all the files in the specified files_and_folders_to_parse list and saves them in
	 * an ACE XML feature values file and (if specified in the passed arguments) as Weka ARFF and/or CSV
	 * files. Qualifying files (i.e. MIDI or MEI) in directories specified in this list are also processed.
	 * Also saves an ACE XML feature definitions file. Only the features specified in features_to_extract will
	 * be extracted. Provides status updates as processing continues. Any errors occurring during processing
	 * are reported on error_print_stream, and are also collected for summarization at the end of processing.
	 * Processing continues even if errors are encountered, with three exceptions: if the JVM runs out of
	 * memory, if an MEI-specific feature is set to be extracted from a non-MEI file, or if
	 * MIDIFeatureProcessor cannot be initialized. In the latter three cases, execution is terminated
	 * immediately.
	 *
	 * @param paths_of_files_or_folders_to_parse	A list of files and folders from which features should be 
	 *												extracted.
	 * @param feature_values_save_path				The path to save the extracted features to in the form of
	 *												an ACE XML feature values file.
	 * @param feature_definitions_save_path			The path to save the feature definitions of all features 
	 *												to be extracted, as an ACE XML feature definitions file.
	 * @param features_to_extract					An array indicating which features are to be saved. This
	 *												array is ordered to match the array returned by the 
	 *												FeatureExtractorAccess.findSpecifiedFeatures.
	 * @param save_features_for_each_window			Whether or not features should be saved for individual
	 *												windows arrived at by dividing up input files.
	 * @param save_overall_recording_features		Whether or not features should be saved for overall 
	 *												recordings as a whole (as opposed to windows).
	 * @param window_size							The duration of each window, in seconds.
	 * @param window_overlap						The fractional overlap between consecutive windows.
	 * @param save_arff_file						Whether or not to save the feature values as a Weka ARFF 
	 *												file.
	 * @param save_csv_file							Whether or not to save the feature values as a CSV file.
	 * @param status_print_stream					A stream to print processing progress to.
	 * @param error_print_stream					A stream to print processing errors to as they happen, and
	 *												at the end of processing.
	 * @param gui_processing						True if this method is being called by a GUI, false 
	 *												otherwise. If it is true, then error summaries will only
	 *												be partially printed, and out of memory errors will result
	 *												in an error window being displayed and a direct printing
	 *												of the associated error message to standard error.
	 * @return										A list of errors that may have occurred during processing.
	 *												Will be empty if no errors occurred. Note that this often 
	 *												simply duplicates what is written to error_print_stream.
	 */
	public static List<String> extractAndSaveSpecificFeatures( List<File> paths_of_files_or_folders_to_parse,
	                                                           String feature_values_save_path,
	                                                           String feature_definitions_save_path,
	                                                           boolean[] features_to_extract,
	                                                           boolean save_features_for_each_window,
	                                                           boolean save_overall_recording_features,
	                                                           double window_size,
	                                                           double window_overlap,
	                                                           boolean save_arff_file,
	                                                           boolean save_csv_file,
	                                                           PrintStream status_print_stream,
	                                                           PrintStream error_print_stream,
	                                                           boolean gui_processing )
	{
		// Prepare the feature extractor
		MIDIFeatureProcessor processor = null;
		try
		{
			processor = new MIDIFeatureProcessor( window_size,
			                                      window_overlap,
					                              FeatureExtractorAccess.getAllImplementedFeatureExtractors(),
					                              features_to_extract,
					                              save_features_for_each_window,
					                              save_overall_recording_features,
					                              feature_values_save_path,
					                              feature_definitions_save_path );
		}
		catch (Exception e)
		{
			UserFeedbackGenerator.printExceptionErrorMessage(error_print_stream, e);
			System.exit(-1);
		}

		// To hold reports of errors that may occur. Note that this often simply duplicates what is written to
		// error_print_stream.
		List<String> error_log = new ArrayList<>();

		// Extract features and save the feature values and definitions as ACE XML files
		extractFeatures( paths_of_files_or_folders_to_parse,
		                 processor,
						 feature_values_save_path,
						 feature_definitions_save_path,
		                 status_print_stream,
		                 error_print_stream,
		                 error_log,
		                 gui_processing );

		// Convert the ACE XML feature values file to a Weka ARFF and/or a CSV file, if appropriate
		saveWekaArffAndCsvFiles( feature_values_save_path,
	                             feature_definitions_save_path,
	                             save_arff_file,
	                             save_csv_file,
	                             status_print_stream,
	                             error_print_stream );
		
		// Indicate that processing is done
		UserFeedbackGenerator.printExecutionFinished(status_print_stream);
		
		// Return any errors that may have occurred
		return error_log;
	}
	
	
	/**
	 * Extracts features from the specified path_of_file_or_folder_to_parse and saves feature values in an ACE
	 * XML feature values file and (if specified in the passed arguments) as Weka ARFF and/or CSV files. Also
	 * saves an ACE XML feature definitions file. Only the features specified in features_to_extract will be
	 * extracted. If path_of_file_or_folder_to_parse refers to a folder rather than a file, then all
	 * qualifying files (i.e. MIDI or MEI) in it have their features extracted. Provides status updates as
	 * processing continues. Any errors occurring during processing are reported on error_print_stream, and
	 * are also collected for summarization at the end of processing. Processing continues even if errors are
	 * encountered, with three exceptions: if the JVM runs out of memory, if an MEI-specific feature is set to
	 * be extracted from a non-MEI file, or if MIDIFeatureProcessor cannot be initialized. In the latter three
	 * cases, execution is terminated immediately.
	 *
	 * @param path_of_file_or_folder_to_parse	The path of a file to extract features from, or of a directory
	 *											holding files to extract features from.
	 * @param feature_values_save_path			The path to save the extracted features to in the form of an
	 *											ACE XML feature values file.
	 * @param feature_definitions_save_path		The path to save the feature definitions of all features to be
	 *											extracted, as an ACE XML feature definitions file.
	 * @param features_to_extract				An array indicating which features are to be saved. This array
	 *											is ordered to match the array returned by the 
	 *											FeatureExtractorAccess.findSpecifiedFeatures.
	 * @param save_features_for_each_window		Whether or not features should be saved for individual
	 *											windows arrived at by dividing up input files.
	 * @param save_overall_recording_features	Whether or not features should be saved for overall recordings
	 *											as a whole (as opposed to windows).
	 * @param window_size						The duration of each window, in seconds.
	 * @param window_overlap					The fractional overlap between consecutive windows.
	 * @param save_arff_file					Whether or not to save the feature values as a Weka ARFF file.
	 * @param save_csv_file						Whether or not to save the feature values as a CSV file.
	 * @param status_print_stream				A stream to print processing progress to.
	 * @param error_print_stream				A stream to print processing errors to as they happen, and at 
	 *											the end of processing.
	 * @param gui_processing					True if this method is being called by a GUI, false 
	 *											otherwise. If it is true, then error summaries will only
	 *											be partially printed, and out of memory errors will result
	 *											in an error window being displayed and a direct printing
	 *											of the associated error message to standard error.
	 * @return									A list of errors that may have occurred during processing.
	 *											Will be empty if no errors occurred. Note that this often 
	 *											simply duplicates what is written to error_print_stream.
	 */
	public static List<String> extractAndSaveSpecificFeatures( String path_of_file_or_folder_to_parse,
	                                                           String feature_values_save_path,
	                                                           String feature_definitions_save_path,
	                                                           boolean[] features_to_extract,
	                                                           boolean save_features_for_each_window,
	                                                           boolean save_overall_recording_features,
	                                                           double window_size,
	                                                           double window_overlap,
	                                                           boolean save_arff_file,
	                                                           boolean save_csv_file,
	                                                           PrintStream status_print_stream,
	                                                           PrintStream error_print_stream,
	                                                           boolean gui_processing )
	{
		return extractAndSaveSpecificFeatures( Arrays.asList(new File(path_of_file_or_folder_to_parse)),
	                                           feature_values_save_path,
	                                           feature_definitions_save_path,
	                                           features_to_extract,
										       save_features_for_each_window,
	                                           save_overall_recording_features,
	                                           window_size,
	                                           window_overlap,
										       save_arff_file,
										       save_csv_file,
	                                           status_print_stream,
	                                           error_print_stream,
											   gui_processing );
	}
	
	
	/**
	 * Extracts features from all the files in the specified files_and_folders_to_parse list and saves them in
	 * an ACE XML feature values file and (if specified in the passed arguments) as Weka ARFF and/or CSV
	 * files. Qualifying files (i.e. MIDI or MEI) in directories specified in this list are also processed.
	 * Also saves an ACE XML feature definitions file. The default jSymbolic features will be extracted, which
	 * will be less than the total number of implemented features. Provides status updates as processing
	 * continues. Any errors occurring during processing are reported on error_print_stream, and are also
	 * collected for summarization at the end of processing. Processing continues even if errors are
	 * encountered, with three exceptions: if the JVM runs out of memory, if an MEI-specific feature is set to
	 * be extracted from a non-MEI file, or if MIDIFeatureProcessor cannot be initialized. In the latter three
	 * cases, execution is terminated immediately.
	 *
	 * @param paths_of_files_or_folders_to_parse	A list of files and folders from which features should be 
	 *												extracted.
	 * @param feature_values_save_path				The path to save the extracted features to in the form of
	 *												an ACE XML feature values file.
	 * @param feature_definitions_save_path			The path to save the feature definitions of all features 
	 *												to be extracted, as an ACE XML feature definitions file.
	 * @param save_features_for_each_window			Whether or not features should be saved for individual
	 *												windows arrived at by dividing up input files.
	 * @param save_overall_recording_features		Whether or not features should be saved for overall 
	 *												recordings as a whole (as opposed to windows).
	 * @param window_size							The duration of each window, in seconds.
	 * @param window_overlap						The fractional overlap between consecutive windows.
	 * @param save_arff_file						Whether or not to save the feature values as a Weka ARFF 
	 *												file.
	 * @param save_csv_file							Whether or not to save the feature values as a CSV file.
	 * @param status_print_stream					A stream to print processing progress to.
	 * @param error_print_stream					A stream to print processing errors to as they happen, and
	 *												at the end of processing.
	 * @param gui_processing						True if this method is being called by a GUI, false 
	 *												otherwise. If it is true, then error summaries will only
	 *												be partially printed, and out of memory errors will result
	 *												in an error window being displayed and a direct printing
	 *												of the associated error message to standard error.
	 * @return										A list of errors that may have occurred during processing.
	 *												Will be empty if no errors occurred. Note that this often 
	 *												simply duplicates what is written to error_print_stream.
	 */
	public static List<String> extractAndSaveDefaultFeatures( List<File> paths_of_files_or_folders_to_parse,
	                                                          String feature_values_save_path,
	                                                          String feature_definitions_save_path,
	                                                          boolean save_features_for_each_window,
	                                                          boolean save_overall_recording_features,
	                                                          double window_size,
	                                                          double window_overlap,
	                                                          boolean save_arff_file,
	                                                          boolean save_csv_file,
	                                                          PrintStream status_print_stream,
	                                                          PrintStream error_print_stream,
	                                                          boolean gui_processing )
	{
		return extractAndSaveSpecificFeatures( paths_of_files_or_folders_to_parse,
	                                           feature_values_save_path,
	                                           feature_definitions_save_path,
	                                           FeatureExtractorAccess.getDefaultFeaturesToSave(),
										       save_features_for_each_window,
	                                           save_overall_recording_features,
	                                           window_size,
	                                           window_overlap,
										       save_arff_file,
										       save_csv_file,
	                                           status_print_stream,
	                                           error_print_stream,
											   gui_processing );
	}
	
	
	/**
	 * Extracts features from the specified path_of_file_or_folder_to_parse and saves feature values in an ACE
	 * XML feature values file and (if specified in the passed arguments) as Weka ARFF and/or CSV files. Also
	 * saves an ACE XML feature definitions file. The default jSymbolic features will be extracted, which will
	 * be less than the total number of implemented features. If path_of_file_or_folder_to_parse refers to a
	 * folder rather than a file, then all qualifying files (i.e. MIDI or MEI) in it have their features
	 * extracted. Provides status updates as processing continues. Any errors occurring during processing are
	 * reported on error_print_stream, and are also collected for summarization at the end of processing.
	 * Processing continues even if errors are encountered, with three exceptions: if the JVM runs out of
	 * memory, if an MEI-specific feature is set to be extracted from a non-MEI file, or if
	 * MIDIFeatureProcessor cannot be initialized. In the latter three cases, execution is terminated
	 * immediately.
	 *
	 * @param path_of_file_or_folder_to_parse	The path of a file to extract features from, or of a directory
	 *											holding files to extract features from.
	 * @param feature_values_save_path			The path to save the extracted features to in the form of an
	 *											ACE XML feature values file.
	 * @param feature_definitions_save_path		The path to save the feature definitions of all features to be
	 *											extracted, as an ACE XML feature definitions file.
	 * @param save_features_for_each_window		Whether or not features should be saved for individual
	 *											windows arrived at by dividing up input files.
	 * @param save_overall_recording_features	Whether or not features should be saved for overall recordings
	 *											as a whole (as opposed to windows).
	 * @param window_size						The duration of each window, in seconds.
	 * @param window_overlap					The fractional overlap between consecutive windows.
	 * @param save_arff_file					Whether or not to save the feature values as a Weka ARFF file.
	 * @param save_csv_file						Whether or not to save the feature values as a CSV file.
	 * @param status_print_stream				A stream to print processing progress to.
	 * @param error_print_stream				A stream to print processing errors to as they happen, and at 
	 *											the end of processing.
	 * @param gui_processing					True if this method is being called by a GUI, false 
	 *											otherwise. If it is true, then error summaries will only
	 *											be partially printed, and out of memory errors will result
	 *											in an error window being displayed and a direct printing
	 *											of the associated error message to standard error.
	 * @return									A list of errors that may have occurred during processing.
	 *											Will be empty if no errors occurred. Note that this often 
	 *											simply duplicates what is written to error_print_stream.
	 */
	public static List<String> extractAndSaveDefaultFeatures( String path_of_file_or_folder_to_parse,
	                                                          String feature_values_save_path,
	                                                          String feature_definitions_save_path,
	                                                          boolean save_features_for_each_window,
	                                                          boolean save_overall_recording_features,
	                                                          double window_size,
	                                                          double window_overlap,
	                                                          boolean save_arff_file,
	                                                          boolean save_csv_file,
	                                                          PrintStream status_print_stream,
	                                                          PrintStream error_print_stream,
	                                                          boolean gui_processing )
	{
		return extractAndSaveSpecificFeatures( Arrays.asList(new File(path_of_file_or_folder_to_parse)),
	                                           feature_values_save_path,
	                                           feature_definitions_save_path,
	                                           FeatureExtractorAccess.getDefaultFeaturesToSave(),
										       save_features_for_each_window,
	                                           save_overall_recording_features,
	                                           window_size,
	                                           window_overlap,
										       save_arff_file,
										       save_csv_file,
	                                           status_print_stream,
	                                           error_print_stream,
											   gui_processing );
	}
	
	
	/**
	 * Extracts features from all the files in the specified paths_of_files_or_folders_to_parse list and saves
	 * them in an ACE XML feature values file and (if specified in the config_file_data) as Weka ARFF and/or
	 * CSV files. Qualifying files (i.e. MIDI or MEI) in directories specified in this
	 * paths_of_files_or_folders_to_parse list are also processed. Also saves an ACE XML feature definitions
	 * file. Extraction settings are based on the config_file_data, except for the output save paths, which
	 * are specified in feature_values_save_path and feature_definitions_save_path, and the input files, which
	 * are specified in paths_of_files_or_folders_to_parse. Provides status updates as processing continues.
	 * Any errors occurring during processing are reported on error_print_stream, and are also collected for
	 * summarization at the end of processing. Processing continues even if errors are encountered, with three
	 * exceptions: if the JVM runs out of memory, if an MEI-specific feature is set to be extracted from a
	 * non-MEI file, or if MIDIFeatureProcessor cannot be initialized. In the latter three cases, execution is
	 * terminated immediately.
	 *
	 * @param paths_of_files_or_folders_to_parse	A list of files and folders from which features should be 
	 *												extracted.
	 * @param config_file_data						Extraction settings parsed from a jSymbolic configuration
	 *												settings file.
	 * @param feature_values_save_path				The path to save the extracted features to (in the form of 
	 *												an ACE XML feature values file).
	 * @param feature_definitions_save_path			The path to save the feature definitions of all features 
	 *												to be extracted (in the form of an ACE XML feature
	 *												definitions file).
	 * @param status_print_stream					A stream to print processing progress to.
	 * @param error_print_stream					A stream to print processing errors to as they happen, and
	 *												at the	end of processing.
	 * @param gui_processing						True if this method is being called by a GUI, false 
	 *												otherwise. If it is true, then error summaries will only
	 *												be partially printed, and out of memory errors will result
	 *												in an error window being displayed and a direct printing
	 *												of the associated error message to standard error.
	 * @return										A list of errors that may have occurred during processing. 
	 *												Will be empty if no errors occurred. Note that this often 
	 *												simply duplicates what is written to error_print_stream.
	 */
	public static List<String> extractAndSaveFeaturesConfigFileSettings( List<File> paths_of_files_or_folders_to_parse,
	                                                                     ConfigurationFileData config_file_data,
	                                                                     String feature_values_save_path,
	                                                                     String feature_definitions_save_path,
	                                                                     PrintStream status_print_stream,
	                                                                     PrintStream error_print_stream,
	                                                                     boolean gui_processing )
	{
		return extractAndSaveSpecificFeatures( paths_of_files_or_folders_to_parse,
	                                           feature_values_save_path,
	                                           feature_definitions_save_path,
	                                           config_file_data.getFeaturesToSaveBoolean(),
										       config_file_data.saveWindow(),
	                                           config_file_data.saveOverall(),
	                                           config_file_data.getWindowSize(),
	                                           config_file_data.getWindowOverlap(),
										       config_file_data.convertToArff(),
										       config_file_data.convertToCsv(),
	                                           status_print_stream,
	                                           error_print_stream,
											   gui_processing );
	}
	
	
	/* PRIVATE STATIC METHODS *******************************************************************************/
	
	
	/**
	 * Extracts features from all the files in the specified files_and_folders_to_parse list and saves them in
	 * an ACE XML feature values file. Qualifying files (i.e. MIDI or MEI) in directories specified in this
	 * list are also processed. Provides status updates as processing continues. Any errors occurring during
	 * processing are reported on error_print_stream, and are also collected for summarization at the end of
	 * processing. Processing continues even if errors are encountered, with two exceptions: if the JVM runs
	 * out of memory or if an MEI-specific feature is set to be extracted from a non-MEI file. In the latter
	 * two cases, execution is terminated immediately. Also saves the feature definitions of the features
	 * selected for extraction in an ACE XML feature definitions file.
	 *
	 * @param files_and_folders_to_parse	A list of files and folders from which features should be 
	 *										extracted.
	 * @param processor						The MIDIFeatureProcessor holding feature extraction settings.
	 * @param feature_values_save_path		The path to save the extracted features to in the form of an ACE 
	 *										XML feature values file.
	 * @param feature_definitions_save_path	The path to save the feature definitions of all features to be
	 *										extracted, as an ACE XML feature definitions file.
	 * @param status_print_stream			A stream to print processing progress to.
	 * @param error_print_stream			A stream to print processing errors to as they happen, and at the
	 *										end of processing.
	 * @param error_log						A list of errors encountered so far. Errors are added to it if 
	 *										encountered. This will be printed to error_print_stream at the end
	 *										of processing.
	 * @param gui_processing				True if this method is being called by a GUI, false otherwise. If
	 *										it is true, then error summaries will only be partially printed, 
	 *										and out of memory errors will result in an error window being 
	 *										displayed and a direct printing	of the associated error message to
	 *										standard error.
	 */
	private static void extractFeatures( List<File> files_and_folders_to_parse,
	                                     MIDIFeatureProcessor processor,
										 String feature_values_save_path,
										 String feature_definitions_save_path,
	                                     PrintStream status_print_stream,
	                                     PrintStream error_print_stream,
	                                     List<String> error_log,
	                                     boolean gui_processing )
	{
		// Traverse any subdirectories in files_and_folders_to_parse to find qualifying files there
		ArrayList<File> files_to_parse = SymbolicMusicFileUtilities.getFilteredFilesRecursiveTraversal( files_and_folders_to_parse,
		                                                                                                false,
		                                                                                                new MusicFilter(),
		                                                                                                error_print_stream,
		                                                                                                error_log );
		
		// Remove all files from files_to_parse that are not valid MIDI or MEI files. Print error messages
		// indicating any that are not. End execution if no valid files remain.
		files_to_parse = SymbolicMusicFileUtilities.validateAndGetMidiAndMeiFiles( files_to_parse,
		                                                                           status_print_stream,
		                                                                           error_print_stream,
		                                                                           error_log );
		
		// Verify that, if MEI-specific features have been chosen to be extracted, then none of the files
		// chosen to be parsed are non-MEI files. End execution if some are.
		verifyNoMeiFeaturesAndNonMeiFiles(files_to_parse, processor, error_print_stream);

		// Extract features from each file
		UserFeedbackGenerator.printGeneratingAceXmlFeatureDefinitionsFile(status_print_stream, feature_definitions_save_path);
		UserFeedbackGenerator.printFeatureExtractionStartingMessage(status_print_stream, files_to_parse.size());
		for (int i = 0; i < files_to_parse.size(); i++)
			extractFeatures( files_to_parse.get(i).getPath(),
			                 processor,
			                 i+1,
			                 files_to_parse.size(),
			                 status_print_stream,
			                 error_print_stream,
			                 error_log,
			                 gui_processing );

		// Finalize the saving of the feature values ACE XML file
		UserFeedbackGenerator.printGeneratingAceXmlFeatureValuesFile(status_print_stream, feature_values_save_path);
		try { processor.finalizeFeatureValuesFile(); } 
		catch (Exception e)
		{
			UserFeedbackGenerator.printExceptionErrorMessage(error_print_stream, e);
			error_log.add(e + ": " + e.getMessage());
		}

		// Indicate that feature extraction is done, and provide a summary of results
		UserFeedbackGenerator.printFeatureExtractionCompleteMessage( status_print_stream,
		                                                             feature_values_save_path,
		                                                             files_to_parse.size() );
		
		// Print the error log summary 
		UserFeedbackGenerator.printErrorSummary(error_print_stream, error_log, gui_processing );
	}


	/**
	 * Extracts all available features from a single MIDI file. Any errors encountered are printed to standard
	 * error. Save the features as they are extracted to an ACE XML feature values file, and save the feature
	 * definitions in an ACE XML feature definitions file.
	 *
	 * @param input_file_path			The path of the file to extract features from.
	 * @param processor					The MIDIFeatureProcessor to extract features with.
	 * @param current_extraction_index	Indicates the number of this particular input file in the overall
	 *									extraction order.
	 * @param total_files_to_process	The total number of input files that are being processed.
	 * @param status_print_stream		A stream to print processing progress to.
	 * @param error_print_stream		A stream to print processing errors to.
	 * @param error_log					A list of errors encountered so far. Errors are added to it if 
	 *									encountered.
	 * @param gui_processing			True if this method is being called by a GUI, false otherwise. If
	 *									it is true, then out of memory errors will result in an error window 
	 *									being displayed and a direct printing of the associated error 
	 *									message to standard error.
	 */
	private static void extractFeatures( String input_file_path,
	                                     MIDIFeatureProcessor processor,
	                                     int current_extraction_index,
	                                     int total_files_to_process,
	                                     PrintStream status_print_stream,
	                                     PrintStream error_print_stream,
	                                     List<String> error_log,
	                                     boolean gui_processing )  
	{
		try
		{
			// Validate the input file
			UserFeedbackGenerator.printFeatureExtractionFileTestProgressMessage(status_print_stream, input_file_path, current_extraction_index, total_files_to_process);
			File input_MIDI_file = new File(input_file_path);
			FileMethods.validateFile(input_MIDI_file, true, false);

			// Extract features from input_file_path and save them in an ACE XML feature values file
			UserFeedbackGenerator.printFeatureExtractionProgressMessage(status_print_stream, input_file_path, current_extraction_index, total_files_to_process);
			processor.extractFeatures(input_MIDI_file, error_log);
			UserFeedbackGenerator.printFeatureExtractionDoneAFileProgressMessage(status_print_stream, input_file_path, current_extraction_index, total_files_to_process);
		}
		catch (OutOfMemoryError e) // Terminate execution if this happens
		{
			String error_message = "The Java Runtime ran out of memory while processing:\n" +
			                       "     " + input_file_path + "\n" +
			                       "Please rerun jSymbolic with more memory assigned to the Java runtime heap.\n\n";
			UserFeedbackGenerator.printErrorMessage(error_print_stream, error_message);
			if (gui_processing)
			{
				System.err.println(error_message);
				java.awt.Toolkit.getDefaultToolkit().beep();
				JOptionPane.showMessageDialog( null,
				                               error_message,
				                               "Error",
				                               JOptionPane.ERROR_MESSAGE );				
			}
			System.exit(-1);
		}
		catch (Exception e)
		{
			String error_message = "Problem extracting features from " + input_file_path + "."+
			                       "\n\tDetailed error message: " + e + ": " + e.getMessage();
			UserFeedbackGenerator.printErrorMessage(error_print_stream, error_message);
			error_log.add(error_message);
			e.printStackTrace(error_print_stream);
		}
	}


	/**
	 * Verify that no MEI-specific features are scheduled to be extracted from non-MEI files. If one or more
	 * MEI-specific features are scheduled to be extracted and if one or more non-MEI files is on th elist of
	 * files to be processed, then print an error message and terminate execution.
	 *
	 * @param file_list	Files to check (and from which features will ultimately be extracted).
	 * @param processor	The MIDIFeatureProcessor holding feature extraction settings.
	 *
	 */
	private static void verifyNoMeiFeaturesAndNonMeiFiles( List<File> file_list, 
	                                                       MIDIFeatureProcessor processor,
	                                                       PrintStream error_print_stream)
	{
		MIDIFeatureExtractor[] features_chosen_to_be_extracted = processor.getFinalFeaturesToBeExtracted();
		List<String> all_mei_specific_features = FeatureExtractorAccess.getNamesOfMeiSpecificFeatures();
		
		// Check if any MEI-specific features are chosen to be selected
		boolean contains_mei_specific_features = false;
		List<String> list_of_mei_specific_features_chosen_to_be_extracted = new ArrayList<>();
		for (MIDIFeatureExtractor feature : features_chosen_to_be_extracted)
		{
			if (all_mei_specific_features.contains(feature.getFeatureDefinition().name))
			{
				contains_mei_specific_features = true;
				list_of_mei_specific_features_chosen_to_be_extracted.add(feature.getFeatureDefinition().name);
			}
		}

		// Check if any non-MEI files are set to have features extracted from them
		boolean non_mei_files_present = false;
		List<File> list_of_non_mei_files_present = new ArrayList<>();
		for (File file : file_list)
		{
			if (SymbolicMusicFileUtilities.isValidMidiFile(file))
			{
				non_mei_files_present = true;
				list_of_non_mei_files_present.add(file);
			}
		}
		
		// Generate an error message and end execution if MEI-specific features are set to be extracted and
		// non-MEI files are present
		if (contains_mei_specific_features && non_mei_files_present)
		{
			String error_message = "Cannot extract MEI-specific features from non-MEI files. The currently scheduled extraction includes the following MEI-specific features: ";
			for (int i = 0; i < list_of_mei_specific_features_chosen_to_be_extracted.size(); i++)
			{
				error_message += list_of_mei_specific_features_chosen_to_be_extracted.get(i);
				if (i == list_of_mei_specific_features_chosen_to_be_extracted.size() - 1)
					error_message += ". ";
				else error_message += ", ";
			}
			error_message += "The currently sechedule extraction also includes the following non-MEI files: ";
			for (int i = 0; i < list_of_non_mei_files_present.size(); i++)
			{
				error_message += list_of_non_mei_files_present.get(i);
				if (i == list_of_non_mei_files_present.size() - 1)
					error_message += ". ";
				else error_message += ", ";
			}
			error_message += "Please only include MEI-specific features if features will be extracted from MEI files exclusively.\n";
			UserFeedbackGenerator.printErrorMessage(error_print_stream, error_message);
			System.exit(-1);
		}
	}
	
	
	/**
	 * Convert the given ACE XML Feature Values file to a Weka ARFF and/or to a CSV file. Each file will have
	 * the same file name as the ACE XML Feature Values file, but with an appropriately modified extension.
	 * Does nothing if ace_xml_feature_values_file_path is null or empty, or if both save_ARFF and save_CSV
	 * are false (null is returned in any of these cases).
	 *
	 * <p>The ARFF file will have a relation name of Converted_from_ACE_XML.</p
	 *
	 * <p> The first row of the CSV file will list the feature names (multi-dimensional features will have a
	 * feature index number appended to the end of their feature name). Each other row will consist of, first,
	 * the instance identifier, followed by the value of each feature.</p>
	 *
	 * @param feature_values_save_path			The path of the ACE XML feature values file to convert.
	 * @param feature_definitions_save_path		The path to save the ACE XML feature definitions file
	 *											matching the feature_values_save_path file.
	 * @param save_arff_file					Whether or not to save the feature values as a Weka ARFF file.
	 * @param save_csv_file						Whether or not to save the feature values as a CSV file.
	 * @param status_print_stream				A stream to print processing progress to.
	 * @param error_print_stream				A stream to print processing errors to as they happen, and at 
	 *											the end of processing.
	 */
	private static void saveWekaArffAndCsvFiles( String feature_values_save_path,
	                                             String feature_definitions_save_path,
	                                             boolean save_arff_file,
	                                             boolean save_csv_file,
	                                             PrintStream status_print_stream,
	                                             PrintStream error_print_stream )
	{
		if ( save_arff_file || save_csv_file )
		{
			try
			{
				// Check to see if a valid ACE XML feature files was saved and holds extracted features for at
				// least one instance.
				int number_succesfully_extracted_files = 0;
				try
				{
					String[] input_files = { feature_values_save_path };
					DataBoard instance_data = new DataBoard(null, null, input_files, null);
					number_succesfully_extracted_files = instance_data.getNumOverall();
				}
				catch (Exception e)
				{
					String warning = "Saving of Weka ARFF and/or CSV files was aborted. " + e.getMessage();
					UserFeedbackGenerator.printWarningMessage(error_print_stream, warning);
				}			

				// Onlly try the onversion if features for at least one instance are available
				if (number_succesfully_extracted_files > 0)
				{
					AceXmlConverter.saveAsArffOrCsvFiles( feature_values_save_path,
														  feature_definitions_save_path,
														  save_arff_file,
														  save_csv_file,
														  status_print_stream );
				}
			} 
			catch (Exception e)
			{
				UserFeedbackGenerator.printExceptionErrorMessage(error_print_stream, e);
			}
		}
	}
}