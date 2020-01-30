package jsymbolic2.features.ngrams;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.ngrams.*;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the fraction of all rhythmic value 3-grams that correspond to the rhythmic 
 * value 3-gram type with the median prevalence. Set to 0 if there are no rhythmic value 3-grams in the music 
 * (e.g. music with only MIDI Channel 10 unpitched notes).
 *
 * @author radamian
 */
public class PrevalenceOfMedianRhythmicValue3GramTypeFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public PrevalenceOfMedianRhythmicValue3GramTypeFeature()
	{
		String name = "Prevalence of Median Rhythmic Value 3-gram Type";
		String code = "NR-5";
		String description = "Fraction of all rhythmic value 3-grams that correspond to the rhythmic value 3-gram type with the median prevalence. Set to 0 if there are no rhythmic value 3-grams in the music (e.g. music with only MIDI Channel 10 unpitched notes).";
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
            // The aggregate of rhythmic value 3-grams in the piece
			NGramAggregate rhythmic_value_3gram_aggregate = sequence_info.rhythmic_value_3gram_aggregate;
            
            // Verify there is at least one rhythmic value 3-gram
            if (!rhythmic_value_3gram_aggregate.noNGrams())
            {
                double[] frequencies_of_unique_3grams = rhythmic_value_3gram_aggregate.getFrequenciesOfUniqueNGrams();
            
                int index_of_median = mckay.utilities.staticlibraries.MathAndStatsMethods.getIndexOfMedian(frequencies_of_unique_3grams);
                value = frequencies_of_unique_3grams[index_of_median];
            }
		}

        double[] result = new double[1];
        result[0] = value;
		return result;
	}
}