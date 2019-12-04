package jsymbolic2.ngrams;

import java.util.LinkedList;

/**
 * An n-gram contains information extracted from a contiguous sequence of either n or n+1 note onset slices, 
 * such that the n-gram encodes n musical events or transitions in this sequence. An n-gram will require n 
 * slices if it encodes events, and n+1 slices if it encodes transitions. N-grams can encode a variety of
 * meaningful information. The types of n-grams that jSymbolic currently supports are melodic interval,
 * vertical interval (complete, lowest and highest lines), and rhythmic value n-grams.
 * 
 * Objects of this class are instantiated by an NGramGenerator object for a given value of n and subset of 
 * voices, and are aggregated in objects of the NGramAggregate class, to facilitate easy usage and analysis by 
 * feature calculators. Objects of this class have an additional String field that also functions as an 
 * identifier.
 * 
 * @author radamian
 */
public class NGram
{
	/**
	 * The identifier that defines this n-gram. There are n-value number of entries in this list for n
	 * sequential musical events or transitions (e.g. a 3-gram will have three double arrays). Each array 
	 * contains double values for these moments (vertical interval, melodic interval, or rhythmic value). If 
	 * this is a melodic interval or a rhythmic value n-gram, then each array will be of size 1. If this is a 
	 * vertical interval n-gram, each array will be of variable size, depending on the number of unique
	 * vertical intervals at each moment. 
	 */
	private final LinkedList<double[]> id;
	
	/**
	 * This n-gram's identifier in String form. Each double value in this n-gram's identifier is converted 
	 * directly to String and concatenated, with spaces delimiting the values of the variable n moments of
	 * the identifier.
	 */
	private final String string_id;
	
	
	/* CONSTRUCTOR ******************************************************************************************/


	/**
	 * Encode the given sequence of values into a one-dimensional n-gram.
	 * 
	 * @param	sequence	The sequence of values to be encoded in this n-gram. 
	 */
	public NGram(LinkedList<double[]> sequence)
	{
		id = new LinkedList<>();
		for (int i = 0; i < sequence.size(); i++)
		{
			double[] copy = new double[sequence.get(i).length];
			for (int value = 0; value < sequence.get(i).length; value++)
				copy[value] = sequence.get(i)[value];
			
			id.add(copy);
		}
		
		string_id = identifierToString(id);
	}


	/* PUBLIC METHODS ***************************************************************************************/

	
	/**
	 * @return	The identifier of this n-gram.
	 */
	public LinkedList<double[]> getIdentifier()
	{
		return id;
	}
	
	
	/**
	 * @return	The string identifier of this n-gram.
	 */
	public String getStringIdentifier()
	{
		return string_id;
	}

	
	/**
	 * @return	The n-value of this n-gram.
	 */
	public int getNValue()
	{
		return id.size();
	}
	
	
	/* PUBLIC STATIC METHODS ********************************************************************************/
	
	
	/**
	 * Returns the given identifier as a string. The values of identifier are converted directly from double
	 * to String, and the values for each moment are delimited by spaces.
	 * 
	 * @param	id		The identifier to convert to String form.
	 * @return			A string representation of the given identifier.
	 */
	public static String identifierToString(LinkedList<double[]> id
)	{
		String s = "";
		
		for (int i = 0; i < id.size(); i++)
		{
			for (int j = 0; j < id.get(i).length; j++)
				s += id.get(i)[j];
			
			if (i < id.size() - 1) s += " ";
		}
		
		return s;
	}
}
