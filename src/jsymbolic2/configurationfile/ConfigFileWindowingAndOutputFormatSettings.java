package jsymbolic2.configurationfile;

/**
 * The state of the windowing and output format settings associated with a jSymbolic configuration file.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class ConfigFileWindowingAndOutputFormatSettings
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * The duration in seconds of analysis windows to break input music into for analysis. This option is only
	 * meaningful if windowed selection is enabled via the save_features_for_each_window option.
	 */
	private final double window_size;

	/**
	 * This fractional value (which should have a value of 0 (for no overlap) or above and below 1) specifies
	 * the amount of overlap between consecutive windows. For example, for a window_size value of 10 and a
	 * window_overlap: value of 0.1, the windows will be from 0 sec to 10 sec, 9 sec to 19 sec, etc. This
	 * option is only meaningful if windowed selection is enabled via the 
	 * save_features_for_each_window option.
	 */
	private final double window_overlap;

	/**
	 * Whether or not to break each input file into analysis windows (of duration window_size and overlap
	 * window_overlap) and extract and save features separately for each such window.
	 */
	private final boolean save_features_for_each_window;

	/**
	 * Whether or not to save the feature for entire input files only (i.e. without windowing is used).
	 */
	private final boolean save_overall_recording_features;

	/**
	 * Whether or not to save extracted features in a Weka ARFF file, in addition to saving them as an ACE XML
	 * Features Values File.
	 */
	private final boolean convert_to_arff;

	/**
	 * Whether or not to save extracted features in a CSV text file, in addition to saving them as an ACE XML
	 * Features Values File.
	 */
	private final boolean convert_to_csv;

	
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Instantiate an object with the specified field values.
	 * 
	 * @param window_size						The duration in seconds of analysis windows to break input
	 *											music into for analysis. This option is only meaningful if 
	 *											windowed selection is enabled via the 
	 *											save_features_for_each_window option.
	 * @param window_overlap					This fractional value (which should have a value of 0 (for no
	 *											overlap) or above and below 1) specifies the amount of overlap
	 *											between consecutive windows. For example, for a window_size 
	 *											value of 10 and a window_overlap: value of 0.1, the windows 
	 *											will be from 0 sec to 10 sec, 9 sec to 19 sec, etc. This 
	 *											option is only meaningful if windowed selection is enabled via
	 *											the save_features_for_each_window option.
	 * @param save_features_for_each_window		Whether or not to break each input file into analysis windows 
	 *											(of duration window_size and overlap window_overlap) and 
	 *											extract and save features separately for each such window.
	 * @param save_overall_recording_features	Whether or not to save the feature for entire input files
	 *											only (i.e. no windowing is used).
	 * @param convert_to_arff					Whether or not to save extracted features in a Weka ARFF file,
	 *											in addition to saving them as an ACE XML Features Values File.
	 * @param convert_to_csv					Whether or not to save extracted features in a CSV text file,
	 *											in addition to saving them as an ACE XML Features Values File.
	 */
	public ConfigFileWindowingAndOutputFormatSettings( double window_size,
	                                 double window_overlap,
	                                 boolean save_features_for_each_window,
	                                 boolean save_overall_recording_features,
	                                 boolean convert_to_arff,
	                                 boolean convert_to_csv )
	{
		this.window_size = window_size;
		this.window_overlap = window_overlap;
		this.save_features_for_each_window = save_features_for_each_window;
		this.save_overall_recording_features = save_overall_recording_features;
		this.convert_to_arff = convert_to_arff;
		this.convert_to_csv = convert_to_csv;
	}

	
	/* METHODS **********************************************************************************************/


	/**
	 * @return	The duration in seconds of analysis windows to break input music into for analysis. This 
	 *			option is only meaningful if windowed selection is enabled via the 
	 *			save_features_for_each_window option.
	 */
	public double getWindowSize()
	{
		return window_size;
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
		return window_overlap;
	}

	
	/**
	 * @return	Whether or not to break each input file into analysis windows (of duration window_size and 
	 *			overlap window_overlap) and extract and save features separately for each such window.
	 */
	public boolean getSaveFeaturesForEachWindow()
	{
		return save_features_for_each_window;
	}

	
	/**
	 * @return	Whether or not to save the feature for entire input files only (i.e. no windowing is used).
	 */
	public boolean getSaveOverallRecordingFeatures()
	{
		return save_overall_recording_features;
	}

	
	/**
	 * @return	Whether or not to save extracted features in a Weka ARFF file, in addition to saving them as 
	 *			an ACE XML Features Values File.
	 */
	public boolean getConvertToArff()
	{
		return convert_to_arff;
	}

	
	/**
	 * @return	Whether or not to save extracted features in a CSV text file, in addition to saving them as an
	 *			ACE XML Features Values File.
	 */
	public boolean getConvertToCsv()
	{
		return convert_to_csv;
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

		ConfigFileWindowingAndOutputFormatSettings that = (ConfigFileWindowingAndOutputFormatSettings) object_to_test;

		if (Double.compare(that.window_size, window_size) != 0)
			return false;
		if (Double.compare(that.window_overlap, window_overlap) != 0)
			return false;
		if (save_features_for_each_window != that.save_features_for_each_window)
			return false;
		if (save_overall_recording_features != that.save_overall_recording_features)
			return false;
		if (convert_to_arff != that.convert_to_arff)
			return false;
		return convert_to_csv == that.convert_to_csv;
	}

	
	/**
	 * A hash code for this object.
	 * 
	 * @return	The hash code for this object.
	 */
	@Override
	public int hashCode()
	{
		int result;
		long temp;
		temp = Double.doubleToLongBits(window_size);
		result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(window_overlap);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + (save_features_for_each_window ? 1 : 0);
		result = 31 * result + (save_overall_recording_features ? 1 : 0);
		result = 31 * result + (convert_to_arff ? 1 : 0);
		result = 31 * result + (convert_to_csv ? 1 : 0);
		return result;
	}
}