package jsymbolic2.ngrams;

import java.util.LinkedList;

/**
 * An n-gram contains information extracted from a contiguous sequence of either n or n+1 note onset slices, 
 * such that the n-gram encodes n musical events or transitions in this sequence. An n-gram will require n 
 * slices if it encodes events, and n+1 slices if it encodes transitions. N-grams can encode a variety of
 * types of meaningful information. The types of n-grams that jSymbolic currently supports are melodic 
 * interval n-grams, rhythmic value n-grams and vertical interval n-grams (both complete and between the
 * lowest and highest lines only).
 * 
 * Objects of this class are instantiated (for example) by an NGramGenerator object for a given value of n and
 * subset of voices, and are aggregated in objects of the NGramAggregate class, to facilitate easy usage and
 * analysis by feature calculators.
 * 
 * @author radamian and Cory McKay
 */
public class NGram
{
	/* PRIVATE FIELDS ***************************************************************************************/

	
	/**
	 * The set of numbers characterizing this particular measured n-gram. This list will have n entries (e.g.
	 * there will be three entries for a 3-gram), and each entry may correspond to either a musical event or
	 * or a musical transition, depending on the type of n-gram. Each entry consists of an array of doubles.
	 * If this is a melodic interval n-gram or a rhythmic value n-gram, then each such array will be of size
	 * 1. If this is a vertical interval n-gram, then each array will be of variable size, depending on the
	 * number of unique vertical intervals in the corresponding note onset window.
	 */
	private final LinkedList<double[]> id;
	
	/**
	 * The same set of numbers characterizing this particular measured n-gram that is represented in the id
	 * field, but in this case formulated as a String. Each double value in this n-gram's identifier is 
	 * converted directly to a String and concatenated with no delimiter. Spaces are added in to separate 
	 * the n different entries in the list.
	 */
	private final String string_id;
	
	
	/* CONSTRUCTOR ******************************************************************************************/


	/**
	 * Copy the given sequence of values into this n-gram.
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
	 * @return	The series of numbers characterizing this n-gram.
	 */
	public LinkedList<double[]> getIdentifier()
	{
		return id;
	}
	
	
	/**
	 * @return	The series of numbers characterizing this n-gram formulated as a String. Each double value in
	 *			this n-gram's identifier is converted directly to a String and concatenated with no delimiter.
	 *			Spaces are added in to separate the n different entries in the list.
	 */
	public String getStringIdentifier()
	{
		return string_id;
	}

	
	/**
	 * @return	The n-value of this n-gram. How many entries there are in the id List.
	 */
	public int getNValue()
	{
		return id.size();
	}
	
	
	/* PUBLIC STATIC METHODS ********************************************************************************/
	
	
	/**
	 * Returns the given n-gram identifier formulated as a String. Each double value in this n-gram's
	 * identifier is converted directly to a String and concatenated with no delimiter. Spaces are added in to
	 * separate the n different entries in the list.
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
	
	
 	/**
	 * Returns whether two n-grams with the two specified identifiers are the same kind of n-gram.
	 * 
	 * @param	id1	The identifier (as returned by an NGram object's getIdentifier method) of the first 
	 *				n-gram to check.
	 * @param	id2	The identifier (as returned by an NGram object's getIdentifier method) of the second 
	 *				n-gram to check.
	 * @return		Whether the two identifiers refer to the same kind of n-gram (i.e. whether they contain
	 *				the same identifying sequence).
	 */
	public static boolean equivalentIdentifiers( LinkedList<double[]> id1, 
												 LinkedList<double[]> id2 )
	{
		boolean equivalent_id = true;
		for (int i = 0; i < id2.size(); i++)
		{
			if (id1.get(i).length != id2.get(i).length)
			{
				equivalent_id = false;
				break;
			}

			for (int j = 0; j < id2.get(i).length; j++)
				if (id1.get(i)[j] != id2.get(i)[j])
					equivalent_id = false;
		}
		return equivalent_id;
	}
}