package jsymbolic2.features.verticalintervals;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the absolute value of the difference (in semitones) between the most common 
 * and second most common vertical intervals in the piece. Set to 0 if there are less than two distinct
 * vertical intervals.
 *
 * @author radamian
 */
public class DistanceBetweenTwoMostCommonVerticalIntervalsFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public DistanceBetweenTwoMostCommonVerticalIntervalsFeature()
	{
		String name = "Distance Between Two Most Common Vertical Intervals";
		String code = "C-26";
		String description = "Absolute value of the difference (in semitones) between the most common and second most common vertical intervals in the piece. Set to 0 if there are less than two distinct vertical intervals.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[] {"Most Common Vertical Interval", "Second Most Common Vertical Interval", "Number of Distinct Vertical Intervals"};
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
		double value;
		if (sequence_info != null)
		{
			// Get other needed feature values
			double most_common_vertical_interval = other_feature_values[0][0];
			double second_most_common_vertical_interval = other_feature_values[1][0];
			double number_of_distinct_vertical_intervals = other_feature_values[2][0];
			
			// Calculate the feature value
			if (number_of_distinct_vertical_intervals <= 1)
				value = 0.0;
			else
				value = (double) Math.abs(most_common_vertical_interval - second_most_common_vertical_interval);		
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
    }
}