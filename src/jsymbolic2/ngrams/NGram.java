package jsymbolic2.ngrams;

import java.util.LinkedList;

/**
 * A musical n-gram. Here, an n-gram is defined as a sequence of n elements associated with one or more 
 * voices, where the value(s) of each element can represent melodic intervals, rhythmic values, or vertical 
 * intervals. Objects of this class are instantiated by the NGramGenerator object using already calculated 
 * lists of all such values.
 * 
 * The child class of this object, VerticalAndMelodicIntervalNGram, convolves sequences of both vertical and
 * melodic intervals and contains a list of melodic intervals of size n - 1.
 * 
 * @author radamian
 */
public class NGram
{
	/**
	 * A 2-dimensional array of doubles containing the identifier of this n-gram. The row (first index) 
	 * indicates the element of n-gram, and the column (second index) indicates the voice that the value at 
	 * that index is associated with. The value of each entry may represent a melodic interval, vertical 
	 * interval, or rhythmic value. The length of the identifier is the n-value of this n-gram. If this is a 
	 * melodic interval or rhythmic value n-gram, the column will have a length of one for the single voice 
	 * whose of melodic intervals or rhythmic values the identifier stores. If this is a vertical interval 
	 * n-gram, the column will have an entry for each voice (that is not the base voice) in the list of voices 
	 * passed to the method returning the aggregate of vertical interval n-grams.
	 */
	private final double[][] id;
	

	/* CONSTRUCTOR ******************************************************************************************/


	public NGram(	LinkedList<double[]> data,
					int number_of_voices)
	{
		id = new double[data.size()][number_of_voices];
		for (int i = 0; i < id.length; i++)
			for (int voice = 0; voice < id[i].length; voice++)
				id[i][voice] = data.get(i)[voice];
	}


	/* PUBLIC METHODS ***************************************************************************************/

	
	/**
	 * @return	The identifier of this n-gram
	 */
	public double[][] getIdentifier()
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
	 * Returns this n-gram as a string. Each element of the n-gram is enclosed in parentheses. If this n-gram
	 * is a vertical interval n-gram, then the vertical intervals between each voice at each intervallic
	 * moment are listed between commas.
	 * 
	 * @return	A string representation of this n-gram.
	 */
	public String nGramToString()
	{
		String s = "";
		
		for (int i = 0; i < id.length; i++)
		{
			s += "(";
			for (int j = 0; j < id[i].length; j++)
			{
				s += id[i][j];
				if (j < id[i].length - 1) s += ", ";
			}
			s += ") ";
		}
		
		return s;
	}
}
