package jsymbolic2.features.melodicintervals;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Number of different melodic intervals that each account individually for at least 20% of all rising wrapped 
 * melodic intervals.
 *
 * @author radamian
 */
public class NumberOfVeryCommonRisingWrappedMelodicIntervalsFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public NumberOfVeryCommonRisingWrappedMelodicIntervalsFeature()
	{
		String name = "Number of Very Common Rising Wrapped Melodic Intervals";
		String code = "M-23";
		String description = "Number of different melodic intervals that each account individually for at least 20% of all rising wrapped melodic intervals.";
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
		double value;
		if (sequence_info != null)
		{
			int number_of_intervals = 0;
			
			// Initialize wrapped histogram
			double[] wrapped_melodic_interval_histogram = new double[12];
			for (int bin = 0; bin < wrapped_melodic_interval_histogram.length; bin++)
				wrapped_melodic_interval_histogram[bin] = 0.0;
			
			// Fill wrapped histogram
			for (int bin = 0; bin < sequence_info.melodic_interval_histogram_rising_intervals_only.length; bin++)
				wrapped_melodic_interval_histogram[bin % 12] += sequence_info.melodic_interval_histogram_rising_intervals_only[bin];
			
			// Count number of intervals
			for (int bin = 0; bin < wrapped_melodic_interval_histogram.length; bin++)
				if (wrapped_melodic_interval_histogram[bin] >= 0.2) number_of_intervals++;
			
			value = (double) number_of_intervals;
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}