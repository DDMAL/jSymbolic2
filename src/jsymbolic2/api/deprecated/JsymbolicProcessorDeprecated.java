package jsymbolic2.api.deprecated;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jsymbolic2.configuration.ConfigFileHeaderEnum;
import jsymbolic2.configuration.ConfigurationFileData;
import jsymbolic2.configuration.txtimplementation.ConfigurationFileValidatorTxtImpl;
import jsymbolic2.featureutils.FeatureExtractorAccess;
import jsymbolic2.processing.AceXmlConverter;
import jsymbolic2.processing.MIDIFeatureProcessor;
import jsymbolic2.processing.MusicFilter;
import jsymbolic2.processing.UserFeedbackGenerator;
import mckay.utilities.staticlibraries.FileMethods;

/**
 * This class contains methods formerly in the jsymbolic2.api.JsymbolicProcessor class. These methods can be
 * used as an API for doing feature extraction jobs. Although these methods should still work, they do not use
 * the newer unified FeatureExtractionJobProcessor and UserFeedbackGenerator infrastructure, and have
 * therefore been deprecated. Also, this code is no longer maintained, so it is possible that errors might
 * occur.
 *
 * <p> The constructor allows the programmer to specify feature extraction settings, after which the methods
 * of this class can be called to extract features. Extracted feature values and associated metadata are saved
 * to files, and may also be returned in the form of a JsymbolicData object, depending on the particular 
 * method called.</p>
* 
 * @author Tristano Tenaglia and Cory McKay
 */
public class JsymbolicProcessorDeprecated
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
	public JsymbolicProcessorDeprecated( String feature_values_save_path,
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
	public JsymbolicProcessorDeprecated( String feature_values_save_path,
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
	public JsymbolicProcessorDeprecated( String configuration_file_path,
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
			                                                                         ConfigFileHeaderEnum.OUTPUT_FILE_HEADER,
																					 ConfigFileHeaderEnum.INPUT_FILE_HEADER);
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
	 * Extract features from a specified input file.
	 *
	 * @param	input_file	The symbolic music file to extract features from.
	 * @param	error_log	An empty list. Will be populated during processing with an entry for each error
	 *						that may occur during processing.
	 * @return				The extracted feature values and related metadata.
	 * @throws Exception	An informative exception is thrown if invalid arguments are provided, or if an
	 *						error occurs during processing.
	 */
	public JsymbolicData extractReturnAndSaveFeaturesFromFile( File input_file,
	                                                           List<String> error_log)
		throws Exception
	{
		if (input_file == null)
			throw new Exception("Cannot extract features from null file.");
		if (input_file.exists())
		{
			MIDIFeatureProcessor processor = buildNewProcessor();
			JsymbolicData extracted_data = processor.extractAndReturnFeatures(input_file, error_log, error_print_stream);
			createFileConversions(extracted_data);
			
			//Print out all errors to console, they will also be returned inherently by the error log
			error_print_stream.println(Arrays.toString(error_log.toArray()));
			
			return extracted_data;
		}
		else throw new Exception(" Input file " + input_file + "  does not exist");
	}

	
	/**
	 * Obtain a JsymbolicData objects given a directory of music files from which features are to be
	 * extracted. This will also check subdirectories.
	 *
	 * @param directory		The directory containing files to extract features from.
	 * @param errorLog		An error log noting any processing problems encountered.
	 * @return				A Map with a File as a key and jSymbolicData as a value. The specific File keys 
	 *						can be obtained using the keySet() method of the Map object. This is recommended 
	 *						as then the appropriate jSymbolicData object can be obtained to get the
	 *						corresponding data.
	 * @throws Exception	Thrown if the directory is not valid.
	 */
	public Map<File, JsymbolicData> extractAndReturnSavedFeaturesFromDirectory( File directory,
	                                                                            List<String> errorLog)
		throws Exception
	{
		if (directory == null)
			throw new Exception("The directory specified for for feature extraction is null.");
		else if (!directory.isDirectory())
			throw new Exception(directory.getName() + " is not an existing directory.");

		Map<File, JsymbolicData> featureMap = new HashMap<>();
		File[] allFile = FileMethods.getAllFilesInDirectory(directory, true, new MusicFilter(), null);
		for (File file : allFile)
		{
			MIDIFeatureProcessor processor = buildNewProcessor();
			try
			{
				JsymbolicData featureState = processor.extractAndReturnFeatures(file, errorLog, error_print_stream);
				featureMap.put(file, featureState);
				createFileConversions(featureState);
			}
			catch (Exception e)
			{
				errorLog.add("Error found in file : " + file.getName() + ". Error Message : " + e.getMessage() + ".");
			}
		}

		//Print out all errors to console, they will also be returned inherently by the error log
		status_print_stream.println(Arrays.toString(errorLog.toArray()));
		return featureMap;
	}
	
	
	/* PRIVATE METHODS **************************************************************************************/
	
	
	/**
	 * This is necessary as each time new data is obtained a new processor must be made to avoid IOExceptions.
	 *
	 * @return				A new MIDIFeatureProcessor with the original data passed into the constructor.
	 * @throws Exception	An exception is thrown when the MIDIFeatureProcessor is not well formed.
	 */
	private MIDIFeatureProcessor buildNewProcessor() throws Exception
	{
		return new MIDIFeatureProcessor( analysis_window_size,
		                                 analysis_window_overlap,
		                                 FeatureExtractorAccess.getAllImplementedFeatureExtractors(),
		                                 features_to_extract,
		                                 save_features_for_each_window,
		                                 save_features_for_overall_pieces,
		                                 feature_values_save_path,
		                                 feature_definitions_save_path );
	}

	
	/**
	 * Convert files if it is required by the corresponding constructor input parameters.
	 *
	 * @param featureState	The state of the feature data that may need to be changed.
	 * @throws Exception	An exception is thrown if there are errors that occur during the conversion.
	 */
	private void createFileConversions(JsymbolicData featureState) throws Exception
	{
		AceXmlConverter.AceConversionPaths conversionPaths = AceXmlConverter.saveAsArffOrCsvFiles(feature_values_save_path, feature_definitions_save_path, save_arff_file, save_csv_file, status_print_stream);
		if(save_arff_file) {
			File arffFile = new File(conversionPaths.getArffFilePath());
			featureState.setSavedWekaArffFile(arffFile);
		}
		if(save_csv_file) {
			File csvFile = new File(conversionPaths.getCsvFilePath());
			featureState.setSavedCsvFile(csvFile);
		}
	}
}