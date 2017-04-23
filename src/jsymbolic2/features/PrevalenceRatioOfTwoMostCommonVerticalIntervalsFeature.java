package jsymbolic2.features;

import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the ratio between the fraction of notes corresponding to the second most
 * common vertical interval on the wrapped vertical interval histogram and the fraction of vertical intervals
 * corresponding to the most common vertical interval. Set to 0 if either of these prevalences are 0.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class PrevalenceRatioOfTwoMostCommonVerticalIntervalsFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public PrevalenceRatioOfTwoMostCommonVerticalIntervalsFeature()
	{
		code = "C-13";
		String name = "Prevalence Ratio of Two Most Common Vertical Intervals";
		String description = "Ratio between the fraction of notes corresponding to the second most common vertical interval on the wrapped vertical interval histogram and the fraction of vertical intervals corresponding to the most common vertical interval. Set to 0 if either of these prevalences are 0.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, description, is_sequential, dimensions);
		dependencies = new String[] { "Prevalence of Most Common Vertical Interval", "Prevalence of Second Most Common Vertical Interval" };
		offsets = null;
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
			double prevalence_of_most_common_vertical_interval = other_feature_values[0][0];
			double prevalence_of_second_common_vertical_interval = other_feature_values[1][0];
			if (prevalence_of_most_common_vertical_interval == 0.0)
				value = 0.0;
			else if (prevalence_of_second_common_vertical_interval == 0.0)
				value = 0.0;
			else
				value = prevalence_of_second_common_vertical_interval / prevalence_of_most_common_vertical_interval;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}