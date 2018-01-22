package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the bin index of the higher (in terms of bin index) of the two beat
 * histogram peaks with the highest magnitude, divided by the index of the lower (in terms of bin index) of
 * the two bins.
 *
 * @author Cory McKay
 */
public class HarmonicityOfTwoStrongestRhythmicPulsesFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public HarmonicityOfTwoStrongestRhythmicPulsesFeature()
	{
		code = "RT-22";
		String name = "Harmonicity of Two Strongest Rhythmic Pulses";
		String description = "Bin index of the higher (in terms of bin index) of the two beat histogram peaks with the highest magnitude, divided by the index of the lower (in terms of bin index) of the two bins.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, description, is_sequential, dimensions);
		dependencies = null;
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
			// Find the bin with the highest magnitude
			double max = 0.0;
			int max_index = 1; //changed from 0 -> 1 for / by zero error
			for (int bin = 0; bin < sequence_info.beat_histogram.length; bin++)
			{
				if (sequence_info.beat_histogram[bin] > max)
				{
					max = sequence_info.beat_histogram[bin];
					max_index = bin;
				}
			}

			// Find the bin with the second highest magnitude
			double second_highest_bin_magnitude = 0.0;
			int second_hidgest_bin_index = 1; // changed from 0 -> 1 to avoid divide by zero error
			for (int bin = 0; bin < sequence_info.beat_histogram_thresholded_table.length; bin++)
			{
				if ( sequence_info.beat_histogram_thresholded_table[bin][1] > second_highest_bin_magnitude && 
				     bin != max_index )
				{
					second_highest_bin_magnitude = sequence_info.beat_histogram_thresholded_table[bin][1];
					second_hidgest_bin_index = bin;
				}
			}

			// Calculate the featurevalue
			if (second_hidgest_bin_index == 0 || max_index == 0)
				value = 0.0;
			else if (max_index > second_hidgest_bin_index)
				value = (double) max_index / (double) second_hidgest_bin_index;
			else
				value = (double) second_hidgest_bin_index / (double) max_index;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}