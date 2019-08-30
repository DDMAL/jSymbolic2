package jsymbolic2.ngrams;

import java.util.LinkedList;

/**
 * An n-gram, defined as an identifier that numerically represents a sequence of variable n musical moments 
 * associated with one or more voices (here, a voice is represented by a pair of MIDI track and channel). The 
 * musical moments that are currently supported are vertical intervals, melodic intervals, and rhythmic 
 * values. A vertical interval or rhythmic value n-gram can be generated for every variable n note onsets, and 
 * a melodic interval n-gram can be created for every n + 1 note onsets. Intervals are in number of semitones 
 * or their generic interval value, and rhythmic values are in a quantized fraction of a quarter note (e.g. a 
 * value of 0.5 corresponds to the duration of an eighth note, possible values being 0.125, 0.25, 0.5, 0.75, 
 * 1.0, 2.0, 3.0, 4.0, 6.0, 8.0, 10.0, 12.0). Here, n-grams only capture musical moments in the melodic line 
 * of each voice; this follows jSymbolic's convention that the highest note sounding at a given time is that 
 * belonging to the melodic line. Note that objects of this class do not retain what kind of moment their
 * identifier represents (however, this can be deduced from the values therein).
 * 
 * Objects of this class are instantiated by an NGramGenerator object for a given value of n and subset of 
 * voices, and are aggregated by objects of the NGramAggregate class to facilitate easy usage and analysis by 
 * feature calculators. Objects of this class have an additional String field that also functions as an 
 * identifier.
 * 
 * @author radamian
 */
public class NGram
{
	/**
	 * A list of arrays of doubles that identifies this n-gram. Each array in this list contains values for
	 * a musical moment (vertical interval, melodic interval, rhythmic value), in the order that they occur
	 * in the piece for a specific voice. If this is a melodic interval or a rhythmic value n-gram, then each 
	 * array will be of size 1. In the case of vertical interval n-grams, a single moment may involve multiple
	 * vertical intervals, so the size of each array may vary. The size of this list is the n-value of the 
	 * n-gram.
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
