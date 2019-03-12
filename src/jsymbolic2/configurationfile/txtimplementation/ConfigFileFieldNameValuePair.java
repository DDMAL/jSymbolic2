package jsymbolic2.configurationfile.txtimplementation;

import jsymbolic2.configurationfile.EnumWindowingAndOutputFormatSettings;

/**
 * An object of this class holds the name of a field and its corresponding value, as parsed from a jSymbolic 
 * configurations file.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class ConfigFileFieldNameValuePair
{
	/* FIELDS *************0**********************************************************************************/
	
	
	/**
	 * The name of a field parsed from a jSymbolic configurations file.
	 */
	private final EnumWindowingAndOutputFormatSettings field_name;

	
	/**
	 * The value corresponding to field_name, as parsed from a jSymbolic configurations file.
	 */
	private final String field_value;


	/* CONSTRUCTOR ******************************************************************************************/


	/**
	 * Instantiate an object of this class with the specified values.
	 * 
	 * @param field_name	The name of a field parsed from a jSymbolic configurations file.
	 * @param field_value	The value corresponding to field_name, as parsed from a jSymbolic configurations 
	 *						file.
	 */
	public ConfigFileFieldNameValuePair(EnumWindowingAndOutputFormatSettings field_name, String field_value)
	{
		this.field_name = field_name;
		this.field_value = field_value;
	}
	
	
	/* METHODS **********************************************************************************************/


	/**
	 * @return	The name of this field, as parsed from a jSymbolic configurations file.
	 */
	public EnumWindowingAndOutputFormatSettings getFieldName()
	{
		return field_name;
	}

	
	/**
	 * @return	The value of this field, as parsed from a jSymbolic configurations file.
	 */
	public String getFieldValue()
	{
		return field_value;
	}
}
