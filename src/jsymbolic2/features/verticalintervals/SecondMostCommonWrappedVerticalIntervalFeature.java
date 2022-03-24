package jsymbolic2.features.verticalintervals;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;

/**
 * A feature calculator that finds the interval in semitones corresponding to the wrapped vertical interval
 * histogram bin with the second highest magnitude. Set to 0 if there are no vertical intervals.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class SecondMostCommonWrappedVerticalIntervalFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public SecondMostCommonWrappedVerticalIntervalFeature()
	{
		String name = "Second Most Common Wrapped Vertical Interval";
		String code = "C-25";
		String description = "The interval in semitones corresponding to the wrapped vertical interval histogram bin with the second highest magnitude. Set to 0 if there are no vertical intervals.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[] {"Wrapped Vertical Interval Histogram", "Number of Distinct Wrapped Vertical Intervals", "Most Common Wrapped Vertical Interval" };
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
			// Get relevant feature values
			double[] wrapped_vertical_interval_histogram = other_feature_values[0];
			double number_of_distinct_wrapped_vertical_intervals = other_feature_values[1][0];
			
			// Calculate the feature value
			if (number_of_distinct_wrapped_vertical_intervals <= 1)
				value = other_feature_values[2][0];
			else
				value = mckay.utilities.staticlibraries.MathAndStatsMethods.getIndexOfSecondLargest(wrapped_vertical_interval_histogram);
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}