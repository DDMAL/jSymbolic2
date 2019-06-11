package jsymbolic2.features.melodicintervals;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the number of different rising melodic intervals that each account 
 * individually for at least 9% of all rising wrapped melodic intervals.
 *
 * @author radamian
 */
public class NumberOfCommonRisingWrappedMelodicIntervalsFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public NumberOfCommonRisingWrappedMelodicIntervalsFeature()
	{
		String name = "Number of Common Rising Wrapped Melodic Intervals";
		String code = "M-19";
		String description = "Number of different rising melodic intervals that each account individually for at least 9% of all rising wrapped melodic intervals.";
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
			
			// Count number of intervals that each account individually for at least 9% of all wrapped rising
			// melodic intervals.
			int number_of_intervals = 0;
			for (int bin = 0; bin < wrapped_melodic_interval_histogram.length; bin++)
				if (wrapped_melodic_interval_histogram[bin] >= 0.09) number_of_intervals++;
			
			value = (double) number_of_intervals;
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}