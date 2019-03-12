package jsymbolic2.configurationfile;

import jsymbolic2.featureutils.FeatureExtractorAccess;

import java.util.Arrays;
import java.util.List;

/**
 * An object of this class holds all the data specified in a given jSymbolic configuration file.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class ConfigFileCompleteData
{
	/* FIELDS ***********************************************************************************************/


	/**
	 * The path of the configuration file corresponding to the information stored in this object.
	 */
	private final String this_configuration_file_path;

	/**
	 * (Unique) names of the features to be extracted and saved.
	 */
	private final List<String> features_to_save;

	/**
	 * A boolean array indicating the features to be extracted and saved. Ordering matches that specified in
	 * the FeatureExtractorAccess class.
	 */
	private final boolean[] features_to_save_boolean;

	/**
	 * Settings associated with windowing and output file types to save.
	 */
	private final ConfigFileWindowingAndOutputFormatSettings windowing_and_output_format_settings;

	/**
	 * Paths of symbolic music files to extract features from.
	 */
	private final ConfigFileInputFilePaths input_file_paths;

	/**
	 * Paths to save extracted feature information to.
	 */
	private final ConfigFileOutputFilePaths output_file_paths;

	
	/* CONSTRUCTOR ******************************************************************************************/
	
	
	/**
	 * Instantiate an object of this class holding the specified data corresponding to a configuration file.
	 *
	 * @param features_to_save						(Unique) names of the features to be extracted and saved.
	 * @param windowing_and_output_format_settings	Settings corresponding to windowing and output file types 
	 *												to save.
	 * @param output_file_paths						Paths to save extracted feature information to.
	 * @param this_configuration_file_path			The path of the configuration file corresponding to the
	 *												information stored in this object.
	 * @param input_file_paths						Paths of symbolic music files to extract features from.
	 * @throws Exception							An informative Exception is thrown if one of the 
	 *												features_to_save does not correspond to the name of an 
	 *												implemented feature.
	 */
	public ConfigFileCompleteData( List<String> features_to_save,
	                              ConfigFileWindowingAndOutputFormatSettings windowing_and_output_format_settings,
	                              ConfigFileOutputFilePaths output_file_paths,
	                              String this_configuration_file_path,
	                              ConfigFileInputFilePaths input_file_paths )
		throws Exception
	{
		// Store specified data directly
		this.features_to_save = features_to_save;
		this.windowing_and_output_format_settings = windowing_and_output_format_settings;
		this.output_file_paths = output_file_paths;
		this.this_configuration_file_path = this_configuration_file_path;
		this.input_file_paths = input_file_paths;
		
		// Calculate features_to_save_boolean
		this.features_to_save_boolean = FeatureExtractorAccess.findSpecifiedFeatures(features_to_save);
	}
	
	
	/* PUBLIC METHODS ***************************************************************************************/

	
	/**
	 * @return	The path of the configuration file corresponding to the information stored in this object.
	 */
	public String getConfigurationFilePath()
	{
		return this_configuration_file_path;
	}

	
	/**
	 * @return	(Unique) names of the features to be extracted and saved.
	 */
	public List<String> getFeaturesToSave()
	{
		return features_to_save;
	}

	
	/**
	 * @return	A boolean array indicating the features to be extracted and saved. Ordering matches that 
	 *			specified in the FeatureExtractorAccess class.
	 */
	public boolean[] getFeaturesToSaveBoolean()
	{
		return features_to_save_boolean;
	}


	/**
	 * @return	Settings associated with windowing and output file types to save.
	 */
	public ConfigFileWindowingAndOutputFormatSettings getWindowingAndOutputFormatSettings()
	{
		return windowing_and_output_format_settings;
	}

	
	/**
	 * @return	The duration in seconds of analysis windows to break input music into for analysis. This 
	 *			option is only meaningful if windowed selection is enabled via the 
	 *			save_features_for_each_window option.
	 */
	public double getWindowSize()
	{
		return windowing_and_output_format_settings.getWindowSize();
	}

	
	/**
	 * @return	This fractional value (which should have a value of 0 (for no overlap) or above and below 1) 
	 *			specifies the amount of overlap between consecutive windows. For example, for a window_size 
	 *			value of 10 and a window_overlap: value of 0.1, the windows will be from 0 sec to 10 sec, 9
	 *			sec to 19 sec, etc. This option is only meaningful if windowed selection is enabled via the 
	 *			save_features_for_each_window option.
	 */
	public double getWindowOverlap()
	{
		return windowing_and_output_format_settings.getWindowOverlap();
	}

	
	/**
	 * @return	Whether or not to break each input file into analysis windows (of duration window_size and 
	 *			overlap window_overlap) and extract and save features separately for each such window.
	 */
	public boolean getSaveFeaturesForEachWindow()
	{
		return windowing_and_output_format_settings.getSaveFeaturesForEachWindow();
	}

	
	/**
	 * @return	Whether or not to save the feature for entire input files only (i.e. no windowing is used).
	 */
	public boolean getSaveOverallRecordingFeatures()
	{
		return windowing_and_output_format_settings.getSaveOverallRecordingFeatures();
	}

	
	/**
	 * @return	Whether or not to save extracted features in a Weka ARFF file, in addition to saving them as 
	 *			an ACE XML Features Values File.
	 */
	public boolean getConvertToArff()
	{
		return windowing_and_output_format_settings.getConvertToArff();
	}

	
	/**
	 * @return	Whether or not to save extracted features in a CSV text file, in addition to saving them as an
	 *			ACE XML Features Values File.
	 */
	public boolean getConvertToCsv()
	{
		return windowing_and_output_format_settings.getConvertToCsv();
	}
	
	
	/**
	 * @return	Paths of symbolic music files to extract features from.
	 */
	public ConfigFileInputFilePaths getInputFilePaths()
	{
		return input_file_paths;
	}

	
	/**
	 * @return	Paths to save extracted feature information to.
	 */
	public ConfigFileOutputFilePaths getOutputFilePaths()
	{
		return output_file_paths;
	}


	/**
	 * @return	The path to save feature values to in the form of an ACE XML feature values file.
	 */
	public String getFeatureValueSavePath()
	{
		return output_file_paths.getFeatureValuesSavePath();
	}

	
	/**
	 * Test if this object is equal to the specified object_to_test.
	 * 
	 * @param	object_to_test	The object to compare this object to.
	 * @return					Whether or not this object equals object_to_test.
	 */
	@Override
	public boolean equals(Object object_to_test)
	{
		if (this == object_to_test)
			return true;
		if (object_to_test == null || getClass() != object_to_test.getClass())
			return false;

		ConfigFileCompleteData that = (ConfigFileCompleteData) object_to_test;

		if (features_to_save != null ? !features_to_save.equals(that.features_to_save) : that.features_to_save != null)
			return false;
		if (!Arrays.equals(features_to_save_boolean, that.features_to_save_boolean))
			return false;
		if (windowing_and_output_format_settings != null ? !windowing_and_output_format_settings.equals(that.windowing_and_output_format_settings) : that.windowing_and_output_format_settings != null)
			return false;
		if (output_file_paths != null ? !output_file_paths.equals(that.output_file_paths) : that.output_file_paths != null)
			return false;
		if (this_configuration_file_path != null ? !this_configuration_file_path.equals(that.this_configuration_file_path) : that.this_configuration_file_path != null)
			return false;
		return input_file_paths != null ? input_file_paths.equals(that.input_file_paths) : that.input_file_paths == null;
	}

	
	/**
	 * A hash code for this object.
	 * 
	 * @return	The hash code for this object.
	 */
	@Override
	public int hashCode()
	{
		int result = features_to_save != null ? features_to_save.hashCode() : 0;
		result = 31 * result + Arrays.hashCode(features_to_save_boolean);
		result = 31 * result + (windowing_and_output_format_settings != null ? windowing_and_output_format_settings.hashCode() : 0);
		result = 31 * result + (output_file_paths != null ? output_file_paths.hashCode() : 0);
		result = 31 * result + (this_configuration_file_path != null ? this_configuration_file_path.hashCode() : 0);
		result = 31 * result + (input_file_paths != null ? input_file_paths.hashCode() : 0);
		return result;
	}
}