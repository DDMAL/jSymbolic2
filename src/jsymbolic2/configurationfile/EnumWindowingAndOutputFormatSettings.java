package jsymbolic2.configurationfile;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An enumerator corresponding to windowing and output format settings associated with a jSymbolic
 * configuration file.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public enum EnumWindowingAndOutputFormatSettings
{
	/* CONSTANTS ********************************************************************************************/

	
	/**
	 * The duration in seconds of analysis windows to break input music into for analysis. This option is only
	 * meaningful if windowed selection is enabled via the save_features_for_each_window option.
	 */
	window_size,

	/**
	 * This fractional value (which should have a value of 0 (for no overlap) or above and below 1) specifies
	 * the amount of overlap between consecutive windows. For example, for a window_size value of 10 and a
	 * window_overlap: value of 0.1, the windows will be from 0 sec to 10 sec, 9 sec to 19 sec, etc. This
	 * option is only meaningful if windowed selection is enabled via the 
	 * save_features_for_each_window option.
	 */
	window_overlap,

	/**
	 * Whether or not to break each input file into analysis windows (of duration window_size and overlap
	 * window_overlap) and extract and save features separately for each such window.
	 */
	save_features_for_each_window,

	/**
	 * Whether or not to save the feature for entire input files only (i.e. without windowing is used).
	 */
	save_overall_recording_features,

	/**
	 * Whether or not to save extracted features in a Weka ARFF file, in addition to saving them as an ACE XML
	 * Features Values File.
	 */
	convert_to_arff,

	/**
	 * Whether or not to save extracted features in a CSV text file, in addition to saving them as an ACE XML
	 * Features Values File.
	 */
	convert_to_csv;


	/* FIELD ************************************************************************************************/
	
	
	/**
	 * Set storing all enumerator elements.
	 */
	private static final Set<String> hash_names = new HashSet<>();


	/* CONSTRUCTOR ******************************************************************************************/


	/**
	 * Initialize this enumerator to store all possible options.
	 */
	static
	{
		for (EnumWindowingAndOutputFormatSettings value : EnumSet.allOf(EnumWindowingAndOutputFormatSettings.class))
			hash_names.add(value.name());
	}

	
	/* STATIC METHODS ***************************************************************************************/


	/**
	 * Check to see if this enumerator is associated with the specified option type.
	 *
	 * @param	name	Name of the option type to check for.
	 * @return			Whether or not the specified option type is associated with this enumerator.
	 */
	public static boolean contains(String name)
	{
		return hash_names.contains(name);
	}

	
	/**
	 * Check to see if this enumerator is associated with all of the specified option types.
	 * 
	 * @param	names	Names of the option type to check for.
	 * @return			Whether or not the specified option type are all associated with this enumerator.
	 */
	public static boolean allOptionsExist(List<String> names)
	{
		for (String hash_name : hash_names)
			if (!names.contains(hash_name))
				return false;
		return true;
	}

	
	/**
	 * Check to see if value string is either "true" or "false".
	 * 
	 * @param	value	The string to check.
	 * @return			True if value is either "true" or "false", false if it is anything else.
	 */
	private static boolean isBoolean(String value)
	{
		return "true".equals(value) || "false".equals(value);
	}
	
	
	/* METHODS **********************************************************************************************/


	/**
	 * Verify that the specified value is of an acceptable type and (if appropriate) within an acceptable
	 * range.
	 *
	 * @param	value	The value to validate.
	 * @return			Whether or not value validates.
	 */
	public boolean checkValue(String value)
	{
		switch (this)
		{
			case window_size:
				//Greater than 0.1 as per the GUI checks
				return value.matches("\\d*\\.?\\d*") && (Double.parseDouble(value) >= 0.0);
			case window_overlap:
				//Between 0 and 1 (not inclusive) as per the GUI checks
				return value.matches("0?.\\d*") && (Double.parseDouble(value) >= 0) && (Double.parseDouble(value) < 1.0);
			case save_features_for_each_window:
				return isBoolean(value);
			case save_overall_recording_features:
				return isBoolean(value);
			case convert_to_arff:
				return isBoolean(value);
			case convert_to_csv:
				return isBoolean(value);
			default:
				return false;
		}
	}
}