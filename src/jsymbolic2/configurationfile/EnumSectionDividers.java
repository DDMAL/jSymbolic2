package jsymbolic2.configurationfile;

import java.util.*;

/**
 * An enumeration of the section dividers (headers) used in jSymbolic configuration files.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public enum EnumSectionDividers
{
	/* CONSTANTS ********************************************************************************************/

	
	/**
	 * The features to extract.
	 */
	FEATURE_HEADER("<features_to_extract>"),
	
	/**
	 * Options associated with windowing and the saving of alternative file types.
	 */
	OPTIONS_HEADER("<jSymbolic_options>"),
	
	/**
	 * Paths to symbolic music files to extract features from.
	 */
	INPUT_FILES_HEADER("<input_files>"),
	
	/**
	 * Paths to save data extracted from the input files to.
	 */
	OUTPUT_FILES_HEADER("<output_files>");


	/* STATIC FINAL FIELDS **********************************************************************************/

	
	/**
	 * Set of all enumerator elements.
	 */
	private static final Set<String> header_names = new HashSet<>();


	/* FIELDS ***********************************************************************************************/
	
	
	/**
	 * The header text associated with a given enumerator element. 
	 */
	private final String text;


	/* CONSTRUCTOR ******************************************************************************************/


	/**
	 * Fully populate header_names.
	 */
	static
	{
		for (EnumSectionDividers value : EnumSet.allOf(EnumSectionDividers.class))
			header_names.add(value.toString());
	}


	/**
	 * Set the value of this entity's text field.
	 * 
	 * @param text	The text value.
	 */
	private EnumSectionDividers(final String text)
	{
		this.text = text;
	}


	/* PUBLIC STATIC METHODS ********************************************************************************/


	/**
	 * Check to see if this enumerator is associated with the specified header type.
	 *
	 * @param	name	Name of the option type to check for.
	 * @return			Whether or not the specified header type is associated with this enumerator.
	 */
	public static boolean contains(String name)
	{
		return header_names.contains(name);
	}

	
	/**
	 * @return	All the header entities permitted by this enumerator type.
	 */
	public static List<EnumSectionDividers> asList()
	{
		return Arrays.asList(values());
	}


	/* PUBLIC METHODS ***************************************************************************************/


	/**
	 * @return	The text label of this header type.
	 */
	@Override
	public String toString()
	{
		return text;
	}
}