package jsymbolic2.features.ngrams;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.LinkedList;
import jsymbolic2.ngrams.*;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature vector of size 3 consisting of the melodic interval 3-gram that occurs the most frequently in the 
 * MIDI track and channel pairing with the lowest average pitch in the music. Each of the 3 component values 
 * of the 3-gram specifies a melodic interval in number of semitones, appearing in the order it occurs (e.g. 
 * the first value is the first melodic interval in the 3-gram). Only pitched notes are included in these 
 * 3-grams. Set to [0,0,0] if there are no melodic interval 3-grams in the music (e.g. music with only MIDI 
 * Channel 10 unpitched notes).
 *
 * @author radamian
 */
public class MostCommonMelodicInterval3GramTypeInLowestLineFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MostCommonMelodicInterval3GramTypeInLowestLineFeature()
	{
		String name = "Most Common Melodic Interval 3-gram Type in Lowest Line";
		String code = "NM-27";
		String description = "A feature vector of size 3 consisting of the melodic interval 3-gram that occurs the most frequently in the MIDI track and channel pairing with the lowest average pitch in the music. Each of the 3 component values of the 3-gram specifies a melodic interval in number of semitones, appearing in the order it occurs (e.g. the first value is the first melodic interval in the 3-gram). Only pitched notes are included in these 3-grams. Set to [0,0,0] if there are no melodic interval 3-grams in the music (e.g. music with only MIDI Channel 10 unpitched notes).";
		boolean is_sequential = true;
		int dimensions = 3;
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
		double[] result = null;
		if (sequence_info != null)
		{
            // The aggregate of melodic interval 3-grams in the lowest line
			NGramAggregate melodic_interval_3gram_in_lowest_line_aggregate = sequence_info.melodic_interval_3gram_in_lowest_line_aggregate;
            
            // Verify there is at least one melodic interval 3-gram
            if (!melodic_interval_3gram_in_lowest_line_aggregate.noNGrams())
            {
                // Get the most common melodic interval 3-gram
                LinkedList<double[]> most_common_melodic_interval_3gram = melodic_interval_3gram_in_lowest_line_aggregate.getMostCommonIdentifier();

                // Copy the 3-gram to the result vector
                result = new double[most_common_melodic_interval_3gram.size()];
                for (int i = 0; i < result.length; i++)
                    result[i] = most_common_melodic_interval_3gram.get(i)[0];
            } else
                result = new double[] { 0, 0, 0};
		}

		return result;
	}
}