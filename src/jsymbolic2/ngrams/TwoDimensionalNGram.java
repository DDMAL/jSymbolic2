package jsymbolic2.ngrams;

import java.util.LinkedList;

/**
 * A musical n-gram that contains two sequences of data, where the second sequence represents moments that 
 * occur between each moment represented by the first sequence of data. For example, a vertical and melodic 
 * interval n-gram represents intervallic moments involving vertical intervals in its first sequence, and 
 * melodic intervals that occur between those moments listed in the first sequence. This is possibly 
 * extensible to n-grams encoding vertical intervals and rhythmic values in the first and second sequence,
 * respectively.
 * 
 * In addition to an identifier (here referred to as a primary identifier given the addition of another 
 * dimension), a two-dimensional n-gram has both a secondary identifier and consequently a joint identifier 
 * composed of the two, by which it can be analyzed.
 * 
 * @author radamian
 */
public class TwoDimensionalNGram 
		extends NGram
{
	/**
	 * A 2-dimensional array of doubles containing the secondary identifier of this n_gram. 
	 */
	private final LinkedList<Double>[] secondary_id;
	
	
	/* CONSTRUCTOR ******************************************************************************************/
	
	
	/**
	 * Encode the given 
	 * 
	 * @param	primary_sequence	The primary data to be encoded in this n-gram. 
	 * @param	secondary_sequence	The secondary data to be encoded in this n-gram. There should be one less
	 *								element in this list than there is in primary_data.
	 * @param	number_of_voices	The number of voices for which data is encoded.
	 */
	public TwoDimensionalNGram( LinkedList<double[]> primary_sequence,
								LinkedList<double[]> secondary_sequence,
								int number_of_voices)
	{
		super(primary_sequence);
		
		secondary_id = new LinkedList[secondary_sequence.size()];
		for (int i = 0; i < secondary_id.length; i++)
		{
			secondary_id[i] = new LinkedList<>();
			for (int voice = 0; voice < secondary_sequence.get(i).length; voice++)
				secondary_id[i].add(secondary_sequence.get(i)[voice]);
		}
	}
	
	
	/* PUBLIC METHODS ***************************************************************************************/
	
	
	/**
	 * @return	The secondary identifier of this n-gram.
	 */
	public LinkedList<Double>[] getSecondaryIdentifier()
	{
		return secondary_id;
	}

	
	/**
	 * Returns this n-gram as a string, where each element of the primary identifier is enclosed in 
	 * parentheses, and each element of the secondary identifier is enclosed in brackets. This method assumes
	 * that the two-dimensional n-gram has encoded vertical and melodic intervals. If the value for an
	 * interval is recorded as 128 (i.e. there is no interval because a voice has a rest), then "Rest" is
	 * added to the string.
	 * 
	 * @return	A string representation of this n-gram.
	 */
	@Override
	public String nGramToString()
	{
		String s = "";
		LinkedList<Double>[] primary_id = getIdentifier();

		for (int i = 0; i < primary_id.length; i++)
		{
			s += "[";
			for (int element = 0; element < primary_id[i].size(); element++)
			{
				if (Math.abs(primary_id[i].get(element)) != 128)
					s += primary_id[i].get(element);
				else
					s += "Rest";
				
				if (element < primary_id[i].size() - 1) s += " ";
			}
			s += "] ";

			if (i < primary_id.length - 1)
			{
				s += "(";
				for (int element = 0; element < secondary_id[i].size(); element++)
				{
					if (Math.abs(secondary_id[i].get(element)) != 128)
						s += secondary_id[i].get(element);
					else
						s += "Rest";
					
					if (element < secondary_id[i].size() - 1) s += " ";
				}
				s += ") ";
			}
		}

		return s;
	}
}
