package jsymbolic2.api.deprecated;

import java.io.File;
import java.io.PrintStream;
import ace.datatypes.DataBoard;
import org.ddmal.jmei2midi.meielements.meispecific.MeiSpecificStorage;
import jsymbolic2.processing.UserFeedbackGenerator;

/**
 * An object holding information associated with the results of a feature extraction job, typically performed
 * through the JsymbolicProcessorDeprecated API. Although this code should still work, it is not associated
 * with the newer unified FeatureExtractionJobProcessor and UserFeedbackGenerator infrastructure, and has
 * therefore been deprecated. Also, this code is no longer maintained, so it is possible that errors might
 * occur.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class JsymbolicData
{
	/* PRIVATE FIELDS ***************************************************************************************/
	
	
	/**
	 * An ACE DataBoard object holding the extracted feature values and feature definition metadata
	 * (accessible via the getFeatureVectors() and getFeatureDefinitions() methods, respectively).
	 */
	private DataBoard feature_values_and_definitions;

	/**
	 * A jMei2Midi MeiSpecificStorage object holding information extracted from MEI files processed (if any)
	 * that could not be converted to MIDI.
	 */
	private MeiSpecificStorage mei_specific_data;

	/**
	 * An ACE XML feature values file containing the feature values that were extracted.
	 */
	private File saved_ace_xml_feature_values_file;

	/**
	 * An ACE XML feature definitions file containing metadata about all the features that were saved.
	 */
	private File saved_ace_xml_feature_definitions_file;

	/**
	 * A Weka ARFF file holding all extracted feature values.
	 */
	private File saved_weka_arff_file;

	/**
	 * A CSV file holding all extracted feature values.
	 */
	private File saved_csv_file;

	/**
	 * A stream to print error messages to if necessary.
	 */
	private final PrintStream error_print_stream;
	
	
	/* CONSTRUCTOR ******************************************************************************************/
	
	
	/**
	 * Instantiate an object of this class holding the results of a jSymbolic feature extraction, typically
	 * performed through the jSymbolic API. Parses and stores the contents of
	 * saved_ace_xml_feature_values_file and saved_ace_xml_feature_definitions_file (writes an error message
	 * to error_print_stream and throws an Exception if this does not work).
	 *
	 * @param	mei_specific_data						A jMei2Midi MeiSpecificStorage object holding 
	 *													information extracted from MEI files processed (if 
	 *													any) that could not be converted to MIDI.
	 * @param	saved_ace_xml_feature_values_file		An ACE XML feature values file containing the feature 
	 *													values that were extracted.
	 * @param	saved_ace_xml_feature_definitions_file	An ACE XML feature definitions file containing 
	 *													metadata about all the features that were saved.
	 * @param	saved_weka_arff_file					A Weka ARFF file holding all extracted feature values.
	 * @param	saved_csv_file							A CSV file holding all extracted feature values.
	 * @param	error_print_stream						A stream to print error messages to if necessary.
	 * @throws	Exception								An exception is thrown if the feature values could
	 *													not be parsed from the two specified ACE XML files.
	 */
	public JsymbolicData( MeiSpecificStorage mei_specific_data,
	                      File saved_ace_xml_feature_values_file,
	                      File saved_ace_xml_feature_definitions_file,
	                      File saved_weka_arff_file,
	                      File saved_csv_file,
	                      PrintStream error_print_stream )
		throws Exception
	{
		// Store arguments in fields of this object
		this.mei_specific_data = mei_specific_data;
		this.saved_ace_xml_feature_values_file = saved_ace_xml_feature_values_file;
		this.saved_ace_xml_feature_definitions_file = saved_ace_xml_feature_definitions_file;
		this.saved_weka_arff_file = saved_weka_arff_file;
		this.saved_csv_file = saved_csv_file;
		this.error_print_stream = error_print_stream;

		// Parse the ACE XML files in order to get feature_values_and_definitions
		try
		{
			String ace_xml_feature_definitions_path = saved_ace_xml_feature_definitions_file.getAbsolutePath();
			String[] ace_xml_feature_values_paths = new String[1];
			ace_xml_feature_values_paths[0] = saved_ace_xml_feature_values_file.getAbsolutePath();
			feature_values_and_definitions = new ace.datatypes.DataBoard( null,
			                                                              ace_xml_feature_definitions_path,
			                                                              ace_xml_feature_values_paths,
			                                                              null );
		}
		catch (Exception e)
		{
			String error_message = "Could not succesfully parse one or both of the saved feature files: " + 
			                       saved_ace_xml_feature_values_file.getAbsolutePath()+ " and " + 
			                       saved_ace_xml_feature_definitions_file.getAbsolutePath() + ".";
			UserFeedbackGenerator.printErrorMessage(error_print_stream, error_message);
			throw e;
		}
	}
	
	
	/* METHODS **********************************************************************************************/

	
	/**
	 * @return	An ACE DataBoard object holding the extracted feature values and feature definition metadata
	 *			(accessible via the getFeatureVectors() and getFeatureDefinitions() methods, respectively).
	 */
	public DataBoard getFeatureValuesAndDefinitions()
	{
		return feature_values_and_definitions;
	}

	
	/**
	 * @return	A jMei2Midi MeiSpecificStorage object holding information extracted from MEI files processed
	 *			(if any) that could not be converted to MIDI.
	 */
	public MeiSpecificStorage getMeiSpecificData()
	{
		return mei_specific_data;
	}

	
	/**
	 * @return	An ACE XML feature values file containing the feature values that were extracted.
	 */
	public File getSavedXmlFeatureValues()
	{
		return saved_ace_xml_feature_values_file;
	}

	
	/**
	 * @return	An ACE XML feature definitions file containing metadata about all the features that were
	 *			saved.
	 */
	public File getSavedAceXmlFeatureDefinitionsFile()
	{
		return saved_ace_xml_feature_definitions_file;
	}

	
	/**
	 * @return	A Weka ARFF file holding all extracted feature values.
	 */
	public File getSavedWekaArffFile()
	{
		return saved_weka_arff_file;
	}

	
	/**
	 * @return	A CSV file holding all extracted feature values.
	 */
	public File getSavedCsvFile()
	{
		return saved_csv_file;
	}

	
	/**
	 * Set the stored Weka ARFF File stored in this object.
	 *
	 * @param saved_weka_arff_file	A Weka ARFF file holding all extracted feature values.
	 */
	public void setSavedWekaArffFile(File saved_weka_arff_file)
	{
		this.saved_weka_arff_file = saved_weka_arff_file;
	}
	
	
	/**
	 * Set the stored CSV File stored in this object.
	 *
	 * @param saved_csv_file	A CSV file holding all extracted feature values.
	 */
	public void setSavedCsvFile(File saved_csv_file)
	{
		this.saved_csv_file = saved_csv_file;
	}
}