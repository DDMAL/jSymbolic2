package jsymbolic2.features.ngrams;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.LinkedList;
import jsymbolic2.ngrams.*;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature vector of size 3 consisting of the lowest and highest lines vertical interval 3-gram that occurs 
 * the second most frequently in the music. Only vertical intervals between the MIDI track/channel 
 * combinations with the lowest and highest average pitches are considered here, and, in the case of multiple 
 * simultaneous notes in either of these lines, only the lowest and highest notes (respectively) sounding in a 
 * given note onset slice are used in this feature calculation. Each value of the feature vector is a vertical 
 * interval specified in number of semitones (a negative value indicating voice crossing). The three values 
 * are specified in the order that they occur. Set to [0,0,0] if there are no lowest and highest lines 
 * vertical interval 3-grams in the music (e.g. music consisting only of a single melody, or with only MIDI 
 * Channel 10 unpitched notes).
 *
 * @author radamian
 */
public class SecondMostCommonVerticalInterval3GramTypeBetweenLowestAndHighestLinesFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public SecondMostCommonVerticalInterval3GramTypeBetweenLowestAndHighestLinesFeature()
	{
		String name = "Second Most Common Vertical Interval 3-gram Type - Between Lowest and Highest Lines";
		String code = "NC-2";
		String description = "A feature vector of size 3 consisting of the lowest and highest lines vertical interval 3-gram that occurs the second most frequently in the music. Only vertical intervals between the MIDI track/channel combinations with the lowest and highest average pitches are considered here, and, in the case of multiple simultaneous notes in either of these lines, only the lowest and highest notes (respectively) sounding in a given note onset slice are used in this feature calculation. Each value of the feature vector is a vertical interval specified in number of semitones (a negative value indicating voice crossing). The three values are specified in the order that they occur. Set to [0,0,0] if there are no lowest and highest lines vertical interval 3-grams in the music (e.g. music consisting only of a single melody, or with only MIDI Channel 10 unpitched notes).";
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
            // The aggregate of lowest and highest lines vertical interval 3-grams
			NGramAggregate lowest_and_highest_lines_vertical_interval_3gram_aggregate = sequence_info.lowest_and_highest_lines_vertical_interval_3gram_aggregate;
            
            // Verify there is at least one lowest and highest lines vertical interval 3-gram
            if (!lowest_and_highest_lines_vertical_interval_3gram_aggregate.noNGrams())
            {
                // Get the second most common lowest and highest lines vertical interval 3-gram
                LinkedList<double[]> second_most_common_vertical_interval_3gram = lowest_and_highest_lines_vertical_interval_3gram_aggregate.getSecondMostCommonNGramIdentifier();
            
                // Copy the 3-gram to the result vector
                result = new double[second_most_common_vertical_interval_3gram.size()];
                for (int i = 0; i < result.length; i++)
                    result[i] = second_most_common_vertical_interval_3gram.get(i)[0];
            }
            else
                result = new double[] { 0, 0, 0};
		}

		return result;
	}
}