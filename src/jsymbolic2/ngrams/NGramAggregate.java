package jsymbolic2.ngrams;

import java.util.HashMap;
import java.util.LinkedList;
import mckay.utilities.staticlibraries.MathAndStatsMethods;

/**
 * Objects of this class take a list of n-grams of the same type at instantiation (usually from an
 * NGramGenerator object) and break them into unique n-grams. The normalized frequency of each unique n-gram
 * present is also calculated. Unique n-grams and their corresponding frequencies may be accessed through
 * matching arrays or via a hashmap. Infrequently occurring n-grams may also be filtered out from the set of
 * unique n-grams, if desired. Objects of this class can be used as intermediate data structures for
 * calculating a variety of n-gram global features based on n-grams.
 * 
 * @author radamian and Cory McKay
 */
public class NGramAggregate 
{
	/**
	 * A reference to the complete ordered set of n-grams provided to an object of this class at
	 * instantiation, and which it aggregates into its unique_ngrams field (without changing the original
	 * list). Each NGram object should have the same value of n and should represent information of the same
	 * type (melodic interval, rhythmic value, complete vertical interval or lowest and highest lines vertical
	 * interval n-gram). Any n-grams filtered out by the constructor due to low frequency will NOT be filtered
	 * out from all_ngrams.
	 */
	protected LinkedList<NGram> all_ngrams;
	
	/**
	 * Contains each unique n-gram that is present in the all_ngrams field. Duplicates are not included, so
	 * each unique n-gram appears once and only once in this array. Index values of this array match the index
	 * values of the normalized_frequencies_of_unique_ngrams array, so the relative frequency (in all_ngrams)
	 * of each n-gram found in unique_ngrams can be found by looking at the entry of
	 * normalized_frequencies_of_unique_ngrams with the matching index value. Any n-grams filtered out by the
	 * constructor due to low frequency WILL be filtered out from unique_ngrams.
	 */
	protected NGram[] unique_ngrams;
	
	/**
	 * A normalized histogram indicating the relative frequencies (in all_ngrams) of each of the n-grams
	 * contained in the unique_ngrams array. This array and the unique_ngrams array have matching index
	 * values, so the relative frequency of each n-gram in unique_ngrams can be found by looking at the entry
	 * of normalized_frequencies_of_unique_ngrams with the matching index value.Any n-grams filtered out by
	 * the constructor due to low frequency WILL be filtered out from normalized_frequencies_of_unique_ngrams,
	 * and will be treated in the remaining frequency calculations as if they never existed.
	 */
	protected double[] normalized_frequencies_of_unique_ngrams;
	
	/**
	 * A HashMap mapping an n-gram's string identifier (as returned by the NGram object's getStringIdentifier
	 * method) to its normalized frequency in all_ngrams. There is one map for each unique n-gram contained in
	 * the unique_ngrams field.
	 */
	protected HashMap<String, Double> string_id_to_normalized_frequency_map;
	

	/* CONSTRUCTOR ******************************************************************************************/
	
	
	/**
	 * Store a reference to the specified ngrams_to_aggregate, make note of all unique n-grams it contains
	 * and calculate the normalized frequency with which each unique n-gram occurs in ngrams_to_aggregate.
	 * N-grams with a relative frequency below a specified amount may optionally be filtered out.
	 * 
	 * @param	ngrams_to_aggregate	The complete ordered set of n-grams to be aggregated by this object. The
	 *								contents of the list itself will not be changed. Each NGram object in the
	 *								list should have the same value of n and should represent information of
	 *								the same type (melodic interval, rhythmic value, complete vertical
	 *								interval or lowest and highest lines vertical interval n-gram).
	 * @param	filtering_threshold	Filter out any unique n-grams with a normalized frequency below this value
	 *								from the aggregated unique n-grams. For example, a value of 0.01 would
	 *								mean that any n-grams that represent less than 1% of the n-grams in
	 *								ngrams_to_aggregate will be included neither in the list of aggregated
	 *								unique n-grams nor in the corresponding set of relative frequencies that
	 *								this object	constructs. The final set of relative frequencies for 
	 *								the remaining unique n-grams that occur above this threshold will be 
	 *								calculated as if the n-grams below the threshold had never been there.
	 *								No filtering will occur if this value is 0.0.
	 */
	public NGramAggregate( LinkedList<NGram>	ngrams_to_aggregate, 
						   double				filtering_threshold )
	{
		// Store a reference to the n-grams provided
		all_ngrams = ngrams_to_aggregate;
		
		// Iterate through the given n-grams, noting unique n-grams and their frequencies
		LinkedList<NGram> unique_ngrams_temp = new LinkedList<>(); // This copy is needed so that proper size is maintained after potential filtering
		string_id_to_normalized_frequency_map = new HashMap<>();
		for (NGram ng: all_ngrams)
		{
			String string_id = ng.getStringIdentifier();
			
			if (string_id_to_normalized_frequency_map.get(string_id) == null)
			{
				unique_ngrams_temp.add(ng);
				string_id_to_normalized_frequency_map.put(string_id, 1.0);
			}
			else
			{
				double old_frequency = string_id_to_normalized_frequency_map.get(string_id);
				string_id_to_normalized_frequency_map.put(string_id, old_frequency + 1);
			}
		}
		
		// Filter out n-grams that occur at a rate less than a specified percentage filtering_threshold
		if (filtering_threshold != 0.0)
		{
			for (NGram ng: all_ngrams)
			{
				String string_id = ng.getStringIdentifier();
				if (string_id_to_normalized_frequency_map.get(string_id) != null)
				{
					if ( string_id_to_normalized_frequency_map.get(string_id) < ( (double) all_ngrams.size() * filtering_threshold) )
					{
						unique_ngrams_temp.remove(ng);
						string_id_to_normalized_frequency_map.remove(string_id);
						// System.out.println("N-gram " + ng.getStringIdentifier() + " with frequency " + (string_id_to_frequency_map.get(string_id) / all_ngrams.size()) + " removed.");
					}
				}
			}
		}
	
		// Set the unique_ngrams field
		unique_ngrams = new NGram[unique_ngrams_temp.size()];
		for (int ng = 0; ng < unique_ngrams.length; ng++)
			unique_ngrams[ng] = unique_ngrams_temp.get(ng);
		
		// Set the normalized_frequencies_of_unique_ngrams field (BEFORE normalization)
		normalized_frequencies_of_unique_ngrams = new double[unique_ngrams.length];
		for (int i = 0; i < normalized_frequencies_of_unique_ngrams.length; i++)
			normalized_frequencies_of_unique_ngrams[i] = string_id_to_normalized_frequency_map.get(unique_ngrams[i].getStringIdentifier());
		
		// Normalize the normalized_frequencies_of_unique_ngrams field
		normalized_frequencies_of_unique_ngrams = MathAndStatsMethods.normalize(normalized_frequencies_of_unique_ngrams);
		
		// Normalize the string_id_to_normalized_frequency_map field
		for (int i = 0; i < unique_ngrams.length; i++)
			string_id_to_normalized_frequency_map.put(unique_ngrams[i].getStringIdentifier(), normalized_frequencies_of_unique_ngrams[i]);
		

		/* TESTING CODE: Display aggregated n-gram stats.
        double single_occurrence_frequency = 1.0 / (double) unique_ngrams.length;
		System.out.println("\n\n\n" + all_ngrams.size() + " total n-grams, " + unique_ngrams.length + " of which are unique.");
		System.out.println("\nSingle n-gram occurrence frequency is: " + single_occurrence_frequency);
		System.out.println("\nN-grams appearing once (with their frequencies):");
		for (int i = 0; i < unique_ngrams.length; i++)
            if (normalized_frequencies_of_unique_ngrams[i] <=  single_occurrence_frequency) 
				System.out.println("\t" + unique_ngrams[i].getStringIdentifier() + ": " + normalized_frequencies_of_unique_ngrams[i]);
		System.out.println("\nN-grams appearing more than once (with their frequencies):");
		for (int i = 0; i < unique_ngrams.length; i++)
            if (normalized_frequencies_of_unique_ngrams[i] >  single_occurrence_frequency) 
				System.out.println("\t" + unique_ngrams[i].getStringIdentifier() + ": " + normalized_frequencies_of_unique_ngrams[i]);
        System.out.println("\nTen n-grams occuring most frequently (with their frequencies):");
        for (int i = 0; i < getTopTenMostCommonStringIdentifiers().size(); i++)
		{
			String id = getTopTenMostCommonStringIdentifiers().get(i);
            System.out.println((i + 1) + ": " + getTopTenMostCommonStringIdentifiers().get(i) + ": " + string_id_to_normalized_frequency_map.get(id));
		}
		*/
	}
	
	
	/* PUBLIC METHODS ***************************************************************************************/
		
	
    /**
     * @return  The total number of n-grams that this object was instantiated with. N-grams that are 
	 *			duplicated ARE counted here.
     */
    public int getTotalNumberOfNGrams()
    {
        return all_ngrams.size();
    }
    
    
   /**
     * @return  True if this object was instantiated with an empty list of n-grams. False if one or more
	 *			n-grams were presnt.
     */
    public boolean noNGrams()
    {
        return all_ngrams.isEmpty();
    }
    
	
	/**
	 * @return	Each unique n-gram that was present in the list of n-grams this object was instantiated with. 
	 *			Duplicates are not included, so each unique n-gram appears once and only once in the returned
	 *			array. Infrequently occurring n-grams may have been filtered out by the constructor, depending
	 *			on the filtering threshold parameter passed to it. Index values of the returned array match 
	 *			the index values of the array returned by the getStringIdToNormalizedFrequencyMap method, so 
	 *			the relative frequency of each unique n-gram can be found by looking at the entry with the 
	 *			matching index value.
	 */
	public NGram[] getUniqueNGrams()
	{
		return unique_ngrams;
	}
	
	
    /**
	 * @return	A normalized histogram indicating the relative frequencies of each of the unique n-grams 
	 *			present in the array returned by the getUniqueNGrams method. The two arrays have matching 
	 *			index values. Infrequently occurring n-grams may have been filtered out by the constructor,
	 *			depending on the filtering threshold parameter passed to it, and frequencies are calculated as
	 *			if any such n-grams were never present.
	 */
	public double[] getNormalizedFrequenciesOfUniqueNGrams()
	{
		return normalized_frequencies_of_unique_ngrams;
	}
    
    
	/**
	 * @return	A HashMap mapping an n-gram's string identifier (as returned by the NGram object's 
	 *			getStringIdentifier method) to its normalized frequency in the list of arrays this object was 
	 *			instantiated with. Has a mapping for each unique n-gram returned by the getUniqueNGrams 
	 *			method. Infrequently occurring n-grams may have been filtered out by the constructor,
	 *			depending on the filtering threshold parameter passed to it, and frequencies are calculated as
	 *			if any such n-grams were never present.
	 */
	public HashMap<String, Double> getStringIdToNormalizedFrequencyMap()
	{
		return string_id_to_normalized_frequency_map;
	}

	
	/**
	 * Return the normalized relative frequency of the n-gram with the given identifier among all aggregated 
	 * n-grams.
	 * 
	 * @param	id	The identifier (as returned by the NGram object's getIdentifier method) of the n-gram 
	 *				whose normalized relative frequency is being requested.
	 * @return		The normalized relative frequency of the n-gram with the specified id in the list of
	 *				n-grams that this object was instantiated with. If no n-gram has the given identifier,
	 *				then a value of -1.0 is returned. Infrequently occurring n-grams may have been filtered
	 *				out by the constructor, depending on the filtering threshold parameter passed to it, and 
	 *				frequencies are calculated as if any such n-grams were never present.
	 */
	public double getNormalizedFrequency(LinkedList<double[]> id)
	{
		String string_id = NGram.identifierToString(id);
		if (string_id_to_normalized_frequency_map.get(string_id) == null)
			return -1.0;
		else return string_id_to_normalized_frequency_map.get(string_id);
	}
	
	
	/**
	 * Return the identifier of the most common n-gram in the list of n-grams that this object was 
	 * instantiated with.
	 * 
	 * @return	The identifier (as returned by the NGram object's getIdentifier method) of the most common
	 *			unique n-gram. The earliest occurrence is returned in the case	of a tie between unique 
	 *			n-grams with equal frequency.
	 */
	public LinkedList<double[]> getMostCommonNGramIdentifier()
	{
		int index_of_highest_frequency = MathAndStatsMethods.getIndexOfLargest(normalized_frequencies_of_unique_ngrams);
		return unique_ngrams[index_of_highest_frequency].getIdentifier();
	}
	
	
	/**
	 * Return the identifier of the second most common n-gram in the list of n-grams that this object was 
	 * instantiated with.
	 * 
	 * @return	The identifier (as returned by the NGram object's getIdentifier method) of the second most 
	 *			common unique n-gram. The second earliest occurrence is returned in the case of a tie between
	 *			unique  n-grams with equal frequency.
	 */
	public LinkedList<double[]> getSecondMostCommonNGramIdentifier()
	{
		int index_of_second_highest_frequency = MathAndStatsMethods.getIndexOfSecondLargest(normalized_frequencies_of_unique_ngrams);
		return unique_ngrams[index_of_second_highest_frequency].getIdentifier();
	}
	
	
	/**
	 * Return a list of the string identifiers of the top ten most common unique n-grams in the list of
	 * n-grams that this object was instantiated with, ordered from most to least common.
	 * 
	 * @return	The list of string identifiers (as returned by the NGram object's getStringIdentifier method)
	 *			of the top ten most common unique n-grams, ordered from most to least common. If fewer than 
	 *			ten unique n-grams are present, then the list will have one entry for each unique n-gram that
	 *			is present. Infrequently occurring n-grams may have been filtered out by the constructor,
	 *			depending on the filtering threshold parameter passed to it.
	 */
	public LinkedList<String> getTopTenMostCommonNGramStringIdentifiers()
	{
		// The n-gram string identifiers to return
		LinkedList<String> top_ten_most_common_string_ids = new LinkedList<>();

		// Iterate through n-gram frequencies until the ten most common ones have been found or all n-grams
		// have been tested.
		while (top_ten_most_common_string_ids.size() < 10 && top_ten_most_common_string_ids.size() != normalized_frequencies_of_unique_ngrams.length)
		{
			String string_id = "";
			double highest_frequency = 0.0;
			for (int i = 0; i < normalized_frequencies_of_unique_ngrams.length; i++)
			{
				if (normalized_frequencies_of_unique_ngrams[i] > highest_frequency && !top_ten_most_common_string_ids.contains(unique_ngrams[i].getStringIdentifier()))
				{
					highest_frequency = normalized_frequencies_of_unique_ngrams[i];
					string_id = unique_ngrams[i].getStringIdentifier();
				}
			}
			top_ten_most_common_string_ids.add(string_id);
		}

		// Return the list
		return top_ten_most_common_string_ids;
	}
}
