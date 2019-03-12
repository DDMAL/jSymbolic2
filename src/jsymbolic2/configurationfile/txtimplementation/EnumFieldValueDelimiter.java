package jsymbolic2.configurationfile.txtimplementation;

/**
 * An enumerator specifying the string used to separate variable names from their values in the .txt
 * implementation of jSymbolic configuration files. Hardcoded to "=" in this case.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public enum EnumFieldValueDelimiter
{
	/* CONSTANTS ********************************************************************************************/


	/**
	 * The delimiter separating variable names from variable values.
	 */
	EQUAL("=");


	/* FIELD ************************************************************************************************/
	
	
	/**
	 * The delimiter separating variable names from variable values.
	 */
	private final String delimiter;


	/* CONSTRUCTOR ******************************************************************************************/


	/**
	 * Set the delimiter value for the EQUAL element.
	 * 
	 * @param delimiter The delimiter separating variable names from variable values.
	 */
	EnumFieldValueDelimiter(String delimiter)
	{
		this.delimiter = delimiter;
	}
	
	
	/* METHODS **********************************************************************************************/


	/**
	 * @return	The delimiter separating variable names from variable values.
	 */
	@Override
	public String toString()
	{
		return delimiter;
	}
}