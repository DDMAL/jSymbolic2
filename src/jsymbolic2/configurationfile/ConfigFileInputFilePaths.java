package jsymbolic2.configurationfile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * An object of this class holds a list of valid and invalid input file paths associated with a jSymbolic
 * configuration file, as well as a list of invalid input files associated with the same configuration file.
 * 
 * @author Tristano Tenaglia and Cory McKay
 */
public class ConfigFileInputFilePaths
{
	/* FIELDS ***********************************************************************************************/

	
	/**
	 * Valid input files associated with a jSymbolic configuration file.
	 */
	private final List<File> valid_files;


	/**
	 * Invalid input files associated with a jSymbolic configuration file.
	 */
	private final List<File> invalid_files;

	
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Instantiate an object of this class with empty lists of valid and invalid input files.
	 */
	public ConfigFileInputFilePaths()
	{
		this.valid_files = new ArrayList<>();
		this.invalid_files = new ArrayList<>();
	}

	
	/* METHODS **********************************************************************************************/


	/**
	 * @return	The valid input files added to this object so far.
	 */
	public List<File> getValidFiles()
	{
		return valid_files;
	}


	/**
	 * @return	The invalid input files added to this object so far.
	 */
	public List<File> getInvalidFiles()
	{
		return invalid_files;
	}

	
	/**
	 * Add a valid input file to this object.
	 * 
	 * @param file	A valid file.
	 */
	public void addValidFile(File file)
	{
		valid_files.add(file);
	}

	/**
	 * Add an invalid input  file to this object.
	 * 
	 * @param file	An invalid file.
	 */
	public void addInvalidFile(File file)
	{
		invalid_files.add(file);
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

		ConfigFileInputFilePaths that = (ConfigFileInputFilePaths) object_to_test;
		if (valid_files != null ? !valid_files.equals(that.valid_files) : that.valid_files != null)
			return false;
		return invalid_files != null ? invalid_files.equals(that.invalid_files) : that.invalid_files == null;
	}

	
	/**
	 * A hash code for this object.
	 * 
	 * @return	The hash code for this object.
	 */
	@Override
	public int hashCode()
	{
		int result = valid_files != null ? valid_files.hashCode() : 0;
		result = 31 * result + (invalid_files != null ? invalid_files.hashCode() : 0);
		return result;
	}
}