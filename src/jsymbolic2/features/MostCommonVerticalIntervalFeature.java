package jsymbolic2.features;

import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the interval in semitones corresponding to the wrapped vertical interval
 * histogram bin with the highest magnitude.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class MostCommonVerticalIntervalFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MostCommonVerticalIntervalFeature()
	{
		code = "C-8";
		String name = "Most Common Vertical Interval";
		String description = "The interval in semitones corresponding to the wrapped vertical interval histogram bin with the highest magnitude.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, description, is_sequential, dimensions);
		dependencies = new String[1];
		dependencies[0] = "Wrapped Vertical Interval Histogram";
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
			double[] wrapped_vertical_interval_histogram = other_feature_values[0];
			value = 0.0;
			double max_magnitude = 0.0;
			for (int interval = 0; interval < wrapped_vertical_interval_histogram.length; interval++)
			{
				if (wrapped_vertical_interval_histogram[interval] > max_magnitude)
				{
					max_magnitude = wrapped_vertical_interval_histogram[interval];
					value = interval;
				}
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}