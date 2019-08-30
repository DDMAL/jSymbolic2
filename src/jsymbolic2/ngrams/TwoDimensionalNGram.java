package jsymbolic2.ngrams;

import java.util.LinkedList;

/**
 * A two-dimensional n-gram, defined by a primary and a secondary identifier that numerically represent a
 * sequence of variable n musical moments and a sequence of n - 1 musical moments, respectively. Moments
 * represented by the secondary identifier are temporally linked to those at the same index and the next index
 * in the sequence represented by the primary identifier. For example, the only such moment currently 
 * supported is the melodic interval, which is measured between note onsets for which rhythmic values can be 
 * measured, or vertical intervals can be listed. For example, a vertical and melodic interval n-gram 
 * has a primary identifier representing vertical intervals and a secondary identifier representing melodic 
 * intervals that occur between the moments represented in the first sequence. A two-dimensional n-gram can be 
 * generated for every variable n note onsets. Intervals are in number of semitones or their generic interval 
 * value, and rhythmic values are in a quantized fraction of a quarter note (e.g. a value of 0.5 corresponds 
 * to the duration of an eighth note, possible values being 0.125, 0.25, 0.5, 0.75, 1.0, 2.0, 3.0, 4.0, 6.0, 
 * 8.0, 10.0, 12.0). Here, n-grams only capture musical moments in the melodic line of each voice; this 
 * follows jSymbolic's convention that the highest note sounding at a given time is that belonging to the 
 * melodic line. Note that objects of this class do not retain what kind of moment their identifier represents 
 * (however, this can be deduced from the values therein).
 * 
 * In addition to an identifier (inherited from the NGram parent class, and here referred to as a primary 
 * identifier given the addition of another dimension), a two-dimensional n-gram has both a secondary 
 * identifier, and consequently a joint identifier composed of the two, by which it can be analyzed. Objects 
 * of this class have two additional String fields representing its secondary and joint identifiers. 
 * 
 * @author radamian
 */
public class TwoDimensionalNGram 
		extends NGram
{
	/**
	 * A list of arrays of doubles that partially identifies this n-gram. Each array in this list contains 
	 * the interval, in number of semitones, between the melodic note sounding at the moment of the same index 
	 * in the primary identifier, and the melodic note sounding at the moment of the next index in the primary 
	 * identifier. The size of this list is one less than the n-value of the n-gram.
	 */
	private final LinkedList<double[]> secondary_id;

	/**
	 * This n-gram's secondary identifier in string form. Each double value in this n-gram's secondary 
	 * identifier is converted directly to String and concatenated, with spaces delimiting the values of the
	 * variable n - 1 moments. 
	 */
	private final String secondary_string_id; 
	
	/**
	 * This n-gram's joint identifier in string form. Each double value in this n-gram's primary and secondary 
	 * identifiers is converted directly to String and concatenated in the order that they occur, with spaces
	 * delimiting the values of each moment of each identifier.
	 */
	private final String joint_string_id;
	
	/* CONSTRUCTOR ******************************************************************************************/
	
	
	/**
	 * Encode the given sequences into a two-dimensional n-gram.
	 * 
	 * @param	primary_sequence	The primary data to be encoded in this n-gram. 
	 * @param	secondary_sequence	The secondary data to be encoded in this n-gram. There must be one less
	 *								element in this list than there is in primary_data.
	 * @throws	Exception			An informative exception is thrown if the size of the given secondary
	 *								sequence is not one less than that of the given primary sequence.
	 */
	public TwoDimensionalNGram( LinkedList<double[]> primary_sequence,
								LinkedList<double[]> secondary_sequence)
			throws Exception
	{
		super(primary_sequence);
		
		if (secondary_sequence.size() != primary_sequence.size() - 1)
			throw new Exception("The size of the specified secondary sequence is not one less than the size of the specified primary sequence.");
		
		secondary_id = new LinkedList<>();
		for (int i = 0; i < secondary_sequence.size(); i++)
		{
			double[] copy = new double[secondary_sequence.get(i).length];
			for (int value = 0; value < copy.length; value++)
				copy[value] = secondary_sequence.get(i)[value];
			
			secondary_id.add(copy);
		}
		
		secondary_string_id = identifierToString(secondary_sequence);
		joint_string_id = jointIdentifierToString(primary_sequence, secondary_sequence);
	}
	
	
	/* PUBLIC METHODS ***************************************************************************************/
	
	
	/**
	 * @return	The secondary identifier of this n-gram.
	 */
	public LinkedList<double[]> getSecondaryIdentifier()
	{
		return secondary_id;
	}
	
	
	/**
	 * @return	The secondary string identifier of this n-gram.
	 */
	public String getSecondaryStringIdentifier()
	{
		return secondary_string_id;
	}
	
	
	/**
	 * @return	The joint string identifier of this n-gram.
	 */
	public String getJointStringIdentifier()
	{
		return joint_string_id;
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
	public String nGramToString()
	{
		String s = "";
		LinkedList<double[]> primary_id = getIdentifier();

		for (int i = 0; i < primary_id.size(); i++)
		{
			s += "[";
			for (int value = 0; value < primary_id.get(i).length; value++)
			{
				if (Math.abs(primary_id.get(i)[value]) != 128)
					s += primary_id.get(i)[value];
				else
					s += "Rest";
				
				if (value < primary_id.get(i).length - 1) s += " ";
			}
			s += "] ";

			if (i < primary_id.size() - 1)
			{
				s += "(";
				for (int value = 0; value < secondary_id.get(i).length; value++)
				{
					if (Math.abs(secondary_id.get(i)[value]) != 128)
						s += secondary_id.get(i)[value];
					else
						s += "Rest";
					
					if (value < secondary_id.get(i).length - 1) s += " ";
				}
				s += ") ";
			}
		}

		return s;
	}
	
	
	/* PUBLIC STATIC METHODS ********************************************************************************/
	
	
	/**
	 * Returns the given joint identifier as a string. The values of each identifier are converted directly 
	 * from double to String, and the values for each moment of each identifier are delimited by spaces.
	 * 
	 * @param	primary_id		The primary identifier of the joint identifier.
	 * @param	secondary_id	The secondary identifier of the joint identifier.
	 * @return					A string representation of the given identifier.
	 */
	public static String jointIdentifierToString(	LinkedList<double[]> primary_id,
													LinkedList<double[]> secondary_id)
	{
		String s = "";
		
		for (int i = 0; i < primary_id.size(); i++)
		{
			for (int value = 0; value < primary_id.get(i).length; value++)
				s += primary_id.get(i)[value];

			if (i < primary_id.size() - 1)
			{
				s += " ";
				
				for (int value = 0; value < secondary_id.get(i).length; value++)
					s += secondary_id.get(i)[value];
				
				s += " ";
			}
		}

		return s;
	}
}
