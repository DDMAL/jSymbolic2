package jsymbolic2.ngrams;

import java.util.LinkedList;

/**
 * A musical n-gram, here defined as a sequence of n musical moments associated with one or more voices, where 
 * the sequence is represented by an identifier encoding the moments numerically. Moments that are currently 
 * supported are vertical intervals, melodic intervals, and rhythmic values. Intervals are represented by 
 * number of semitones or generic interval value, and rhythmic values are represented by fraction of a quarter
 * note. Objects of this class are instantiated by the NGramGenerator class, and are aggregated by the 
 * NGramAggregate class to facilitate analysis by feature calculators.
 * 
 * @author radamian
 */
public class NGram
{
	/**
	 * An array where each entry is a list of Doubles representing some musical moment (list of vertical 
	 * intervals, melodic interval, rhythmic value). The length of the identifier is the n-value of this 
	 * n-gram.
	 */
	private final LinkedList<Double>[] id;
	
	
	/* CONSTRUCTOR ******************************************************************************************/


	/**
	 * The constructor for this object.
	 * 
	 * @param	sequence	The sequence to be encoded in this n-gram. 
	 */
	public NGram(LinkedList<double[]> sequence)
	{
		id = new LinkedList[sequence.size()];
		for (int i = 0; i < id.length; i++)
		{
			id[i] = new LinkedList<>();
			for (int voice = 0; voice < sequence.get(i).length; voice++)
				id[i].add(sequence.get(i)[voice]);
		}
	}


	/* PUBLIC METHODS ***************************************************************************************/

	
	/**
	 * @return	The identifier of this n-gram.
	 */
	public LinkedList<Double>[] getIdentifier()
	{
		return id;
	}

	
	/**
	 * @return	The n-value of this n-gram.
	 */
	public int getNValue()
	{
		return id.length;
	}
	
	
	/**
	 * Returns this n-gram as a string, where each element of the n-gram is enclosed in parentheses.
	 * 
	 * @return	A string representation of this n-gram.
	 */
	public String nGramToString()
	{
		String s = "";
		
		for (int i = 0; i < id.length; i++)
		{
			s += "(";
			for (int j = 0; j < id[i].size(); j++)
			{
				s += id[i].get(j);
				if (j < id[i].size() - 1) s += ", ";
			}
			s += ") ";
		}
		
		return s;
	}
}
