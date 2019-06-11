package jsymbolic2.features.melodicintervals;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the absolute value of the difference (in semitones) between the most common 
 * and second most common rising wrapped melodic intervals in the piece.
 *
 * @author radamian
 */
public class DistanceBetweenMostPrevalentRisingWrappedMelodicIntervalsFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public DistanceBetweenMostPrevalentRisingWrappedMelodicIntervalsFeature()
	{
		String name = "Distance Between Most Prevalent Rising Wrapped Melodic Intervals";
		String code = "M-27";
		String description = "Absolute value of the difference (in semitones) between the most common and second most common rising wrapped melodic intervals in the piece.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[1];
		dependencies[0] = "Wrapped Melodic Interval Histogram - Rising Intervals Only";
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
			// Get wrapped histogram
			double[] wrapped_melodic_interval_histogram = other_feature_values[0];
			
			// Find the bin with the highest magnitude
			int max_index = mckay.utilities.staticlibraries.MathAndStatsMethods.getIndexOfLargest(wrapped_melodic_interval_histogram);

			// Find the bin with the second highest magnitude
			int second_max_index = mckay.utilities.staticlibraries.MathAndStatsMethods.getIndexOfSecondLargest(wrapped_melodic_interval_histogram);

			// Calculate the value
			int difference = Math.abs(max_index - second_max_index);
			value = (double) difference;
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
    }
}