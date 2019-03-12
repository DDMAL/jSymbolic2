package jsymbolic2.configurationfile;

/**
 * An object of this class holds output file paths associated with a jSymbolic configuration file.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class ConfigFileOutputFilePaths
{
	/* FIELD ************************************************************************************************/

	
	/**
	 * The path to save feature values to in the form of an ACE XML feature values file.
	 */
	private final String feature_values_save_path;

	
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Instantiate an object holding the specified feature_values_save_path.
	 * 
	 * @param feature_values_save_path	The path to save feature values to in the form of an ACE XML feature
	 *									values file.
	 */
	public ConfigFileOutputFilePaths(String feature_values_save_path)
	{
		this.feature_values_save_path = feature_values_save_path;
	}

	
	/* METHODS **********************************************************************************************/


	/**
	 * Get the stored path to save feature values to in the form of an ACE XML feature values file.
	 * 
	 * @return The stored path to save feature values to in the form of an ACE XML feature values file.
	 */
	public String getFeatureValuesSavePath()
	{
		return feature_values_save_path;
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

		ConfigFileOutputFilePaths that = (ConfigFileOutputFilePaths) object_to_test;
		return feature_values_save_path != null ? feature_values_save_path.equals(that.feature_values_save_path) : that.feature_values_save_path == null;
	}

	
	/**
	 * A hash code for this object.
	 * 
	 * @return	The hash code for this object.
	 */
	@Override
	public int hashCode()
	{
		int result = feature_values_save_path != null ? feature_values_save_path.hashCode() : 0;
		return 31 * result;
	}
}