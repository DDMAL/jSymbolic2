package jsymbolic2.ngrams;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * An aggregate of n-grams. This object is instantiated by the NGramGenerator object with a specified n-value
 * and other parameters, depending on the type of n-grams generated. Each instance is instantiated with a list 
 * of n-grams that have the same n-value and are of the same type of n-gram (jSymbolic currently supports
 * melodic interval, vertical interval, and rhythmic value n-grams).
 * 
 * Objects of this class have utility fields to facilitate easy usage by feature calculators. It has a hash 
 * map mapping the string identifier of a unique n-gram to the normalized frequency at which that n-gram 
 * occurs. It also has two arrays: one array containing each unique n-gram, and a corresponding array of equal 
 * length containing the normalized frequency at which the n-gram at the same index of the first array occurs.
 * 
 * @author radamian
 */
public class NGramAggregate 
{
	/**
	 * The n-grams this object aggregates. Each n-gram is of the same type (vertical interval, melodic 
	 * interval, rhythmic value) and has the same n-value.
	 */
	protected LinkedList<NGram> ngrams_ll;
	
	/**
	 * A HashMap mapping the string identifier of a unique n-gram to that n-gram's normalized frequency among 
	 * all aggregated n-grams.
	 */
	protected HashMap<String, Double> string_id_to_frequency_map;
	
	/**
	 * An array of unique n-grams in the ngrams_ll field. Each n-gram in this array corresponds to the entry
	 * found at the same index in the frequencies_of_unique_ngrams field, the normalized frequency of that 
	 * n-gram among all aggregated n-grams.
	 */
	protected NGram[] unique_ngrams;
	
	/**
	 * A normalized histogram containing the frequencies at which each unique n-grams occurs among all 
	 * aggregated n-grams. Each value in this array corresponds to the entry found at the same index in the 
	 * unique_ngrams field, the unique n-gram that occurs with the normalized frequency of that value.
	 */
	protected double[] frequencies_of_unique_ngrams;
	
	
	/* CONSTRUCTOR ******************************************************************************************/
	
	
	/**
	 * Create an aggregate of n-grams.
	 * 
	 * @param	ngram_list		The list of n-grams that this object aggregates.
	 */
	public NGramAggregate(LinkedList<NGram> ngram_list)
	{
		ngrams_ll = ngram_list;
		string_id_to_frequency_map = new HashMap<>();
		
		LinkedList<NGram> unique_ngrams_ll = new LinkedList<>();
		
		// Iterate through the given n-grams and add pairs of unique n-grams and their frequency count to the
		// string_id_to_frequency_map field
		for (NGram ngram: ngrams_ll)
		{
			String string_id = ngram.getStringIdentifier();
			
			if (string_id_to_frequency_map.get(string_id) == null)
			{
				string_id_to_frequency_map.put(string_id, 1.0);
				unique_ngrams_ll.add(ngram);
			}
			else
			{
				double old_frequency = string_id_to_frequency_map.get(string_id);
				string_id_to_frequency_map.put(string_id, old_frequency + 1);
			}
		}
		
		/*
		// Filter out n-grams that occur at a rate less than a specified percentage theshold.
		double filtering_threshold = .01;
		
		// Iterate through the list of n-grams, filtering out those that account for less than 1% of all
		// n-grams
		for (NGram ngram: ngrams)
		{
			String string_id = ngram.getStringIdentifier();
			
			if (string_id_to_frequency_map.get(string_id) != null)
			{
				// If the unique n-gram accounts for less than the filtering threshold percentage value of 
				// all n-grams by unique identifiers, then its string identifier is removed from the  
				// string_id_to_frequency_map field, and the n-gram is removed from the unique_ngrams_ll
				// field.
				if (string_id_to_frequency_map.get(string_id) < ngrams.size() * filtering_threshold)
				{
					System.out.println("N-gram " + ngram.getStringIdentifier() + " with frequency " + (string_id_to_frequency_map.get(string_id) / ngrams.size()) + " removed");
					string_id_to_frequency_map.remove(string_id);
					unique_ngrams_ll.remove(ngram);
				}
			}
		}
		*/
		
		// Initialize the ngrams_by_unique_id field
		unique_ngrams = new NGram[unique_ngrams_ll.size()];
		for (int ngram = 0; ngram < unique_ngrams.length; ngram++)
			unique_ngrams[ngram] = unique_ngrams_ll.get(ngram);
		
		// Initialize the frequencies_of_unique_ngrams field
		frequencies_of_unique_ngrams = new double[unique_ngrams.length];
		for (int i = 0; i < frequencies_of_unique_ngrams.length; i++)
			frequencies_of_unique_ngrams[i] = string_id_to_frequency_map.get(unique_ngrams[i].getStringIdentifier());
		
		// Normalize the frequencies_of_unique_ngrams field
		frequencies_of_unique_ngrams = mckay.utilities.staticlibraries.MathAndStatsMethods.normalize(frequencies_of_unique_ngrams);
		
		// Update the values in the ngram_by_unique_id_to_frequency_map field to the normalized frequencies 
		// of each unique n-gram
		for (int i = 0; i < unique_ngrams.length; i++)
			string_id_to_frequency_map.put(unique_ngrams[i].getStringIdentifier(), frequencies_of_unique_ngrams[i]);
		
		/*
		// See all n-grams and their frequencies
        double single_occurrence_frequency = (double) 1 / ngrams_ll.size();
        System.out.println("\n\n\nSingle occurrence frequency is: " + single_occurrence_frequency);
		System.out.println("\nN-grams and their frequencies: ");
		for (int i = 0; i < unique_ngrams.length; i++)
        {
			System.out.print(unique_ngrams[i].getStringIdentifier() + " with frequency " + frequencies_of_unique_ngrams[i]);
            if (frequencies_of_unique_ngrams[i] ==  single_occurrence_frequency) System.out.print(" (OCCURS ONLY ONCE)");
            System.out.print("\n");
        }
		
        System.out.println("\nTop 10 n-grams: ");
        for (int i = 0; i < getTopTenMostCommonStringIdentifiers().size(); i++)
            System.out.println((i + 1) + ": " + getTopTenMostCommonStringIdentifiers().get(i));
        */
	}
	
	
	/* PUBLIC METHODS ***************************************************************************************/
		
	
	/**
	 * @return	The map between the string identifier of a unique n-gram to that n-gram's normalized frequency 
	 *			among all aggregated n-grams.
	 */
	public HashMap<String, Double> getStringIdToFrequencyMap()
	{
		return string_id_to_frequency_map;
	}

	
    /**
	 * @return	The normalized histogram containing the frequencies at which each unique n-grams occurs among 
	 *			all aggregated n-grams.
	 */
	public double[] getFrequenciesOfUniqueNGrams()
	{
		return frequencies_of_unique_ngrams;
	}
    
    
	/**
	 * @return	The array of unique n-grams in the ngrams_ll field.
	 */
	public NGram[] getUniqueNGrams()
	{
		return unique_ngrams;
	}
	
	
    /**
     * @return  Whether there are no aggregated n-grams.
     */
    public boolean noNGrams()
    {
        return ngrams_ll.isEmpty();
    }
    
    /**
     * @return  The number of aggregated n-grams.
     */
    public int getNumberOfNGrams()
    {
        return ngrams_ll.size();
    }
    
    
	/**
	 * Returns a boolean indicating whether two given identifiers are the same.
	 * 
	 * @param	id1			The first identifier.
	 * @param	id2			The second identifier.
	 * @return				Whether the two identifiers are the same.
	 */
	public boolean equivalentIdentifiers(	LinkedList<double[]> id1, 
											LinkedList<double[]> id2)
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
	
	
	/**
	 * Return the normalized frequency of the n-gram with the given identifier among all aggregated n-grams. 
	 * If no n-gram has the given identifier, then a value of -1.0 is returned.
	 * 
	 * @param	id			The identifier of the n-gram.
	 * @return				The frequency of the n-gram with the given identifier among all n-grams. -1.0 if 
	 *						there is no n-gram with the given identifier.
	 */
	public double getFrequencyOfIdentifier(LinkedList<double[]> id)
	{
		String string_id = NGram.identifierToString(id);
		
		return string_id_to_frequency_map.get(string_id);
	}
	
	
	/**
	 * Return the identifier of the most common n-gram.
	 * 
	 * @return	The identifier of the most common n-gram.
	 */
	public LinkedList<double[]> getMostCommonIdentifier()
	{
		int index_of_highest_frequency = mckay.utilities.staticlibraries.MathAndStatsMethods.getIndexOfLargest(frequencies_of_unique_ngrams);
		LinkedList<double[]> most_common_identifier = unique_ngrams[index_of_highest_frequency].getIdentifier();
		
		return most_common_identifier;
	}
	
	
	/**
	 * Return the identifier of the second most common n-gram.
	 * 
	 * @return	The identifier of the second most common n-gram.
	 */
	public LinkedList<double[]> getSecondMostCommonIdentifier()
	{
		int index_of_second_highest_frequency = mckay.utilities.staticlibraries.MathAndStatsMethods.getIndexOfSecondLargest(frequencies_of_unique_ngrams);
		LinkedList<double[]> second_most_common_identifier = unique_ngrams[index_of_second_highest_frequency].getIdentifier();
		
		return second_most_common_identifier;
	}
	
	
	/**
	 * Return a list of the string identifiers of the top ten most common n-grams, ordered from most to least
	 * common.
	 * 
	 * @return	The list of string identifiers of the top ten most common n-grams, ordered from most to least
	 *			common.
	 */
	public LinkedList<String> getTopTenMostCommonStringIdentifiers()
	{
		LinkedList<String> top_ten_most_common_string_ids = new LinkedList<>();
		
		while (top_ten_most_common_string_ids.size() < 10 && top_ten_most_common_string_ids.size() != frequencies_of_unique_ngrams.length)
		{
			String string_id = "";
			double highest_frequency = 0;
			
			for (int i = 0; i < frequencies_of_unique_ngrams.length; i++)
			{
				if (frequencies_of_unique_ngrams[i] > highest_frequency && !top_ten_most_common_string_ids.contains(unique_ngrams[i].getStringIdentifier()))
				{
					highest_frequency = frequencies_of_unique_ngrams[i];
					string_id = unique_ngrams[i].getStringIdentifier();
				}
			}
			
			top_ten_most_common_string_ids.add(string_id);
		}
		
		return top_ten_most_common_string_ids;
	}
}
