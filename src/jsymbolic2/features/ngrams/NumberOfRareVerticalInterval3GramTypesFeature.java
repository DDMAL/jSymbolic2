package jsymbolic2.features.ngrams;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.ngrams.*;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the number of different complete vertical interval 3-gram types that each 
 * account individually for 0.5% or less of all complete vertical interval 3-grams that occur in the music.
 *
 * @author radamian
 */
public class NumberOfRareVerticalInterval3GramTypesFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public NumberOfRareVerticalInterval3GramTypesFeature()
	{
		String name = "Number of Rare Vertical Interval 3-gram Types";
		String code = "NC-18";
		String description = "The number of different complete vertical interval 3-gram types that each account individually for 0.5% or less of all complete vertical interval 3-grams that occur in the music.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = null;
		offsets = null;
		is_default = true;
		is_secure = true;
	}
	

	/* PUBLIC METHODS ***************************************************************************************/
	
	
	/**
	 * Extract this feature from the given sequence of MIDI data and its associated information.
	 *
	 * @param sequence				The MIDI data to extract the feature from.
	 * @param sequence_info			Additional data already extracted from the the MIDI sequence.
	 * @param other_feature_values	The values of other features that may be needed to calculate this feature. 
	 *								The order and offsets of these features must be the same as those returned
	 *								by this class' getDependencies and getDependencyOffsets methods, 
	 *								respectively. The first indice indicates the feature/window, and the 
	 *								second indicates the value.
	 * @return						The extracted feature value(s).
	 * @throws Exception			Throws an informative exception if the feature cannot be calculated.
	 */
	@Override
	public double[] extractFeature( Sequence sequence,
									MIDIIntermediateRepresentations sequence_info,
									double[][] other_feature_values )
	throws Exception
	{
		double value = 0.0;
		if (sequence_info != null)
		{
            // The aggregate of complete vertical interval 3-grams in the piece
			NGramAggregate complete_vertical_interval_3gram_aggregate = sequence_info.complete_vertical_interval_3gram_aggregate;
            
            // Verify there is at least one complete vertical interval 3-gram
            if (!complete_vertical_interval_3gram_aggregate.noNGrams())
            {
                double[] frequencies_of_unique_3grams = complete_vertical_interval_3gram_aggregate.getFrequenciesOfUniqueNGrams();
                
                // Count number of rare complete vertical interval 3-grams
                int number_of_rare_3grams = 0;
                for (int i = 0; i < frequencies_of_unique_3grams.length; i++)
                    if (frequencies_of_unique_3grams[i] <= 0.005)
                        number_of_rare_3grams++;
                
                value = (double) number_of_rare_3grams;
            }
		}

        double[] result = new double[1];
        result[0] = value;
		return result;
	}
}