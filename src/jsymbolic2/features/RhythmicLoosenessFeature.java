package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the average width of beat histogram peaks. The width of a peak is defined
 * here as the distance (in beats per minute) between the two points on the peak in question that have
 * magnitudes closest to 30% of the height of the peak. Only peaks with magnitudes at least 30% as high as the
 * highest peak are considered in this calculation.
 *
 * @author Cory McKay
 */
public class RhythmicLoosenessFeature
		extends MIDIFeatureExtractor
{

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public RhythmicLoosenessFeature()
	{
		code = "RT-28";
		String name = "Rhythmic Looseness";
		String description = "Average width of beat histogram peaks. The width of a peak is defined here as the distance (in beats per minute) between the two points on the peak in question that have magnitudes closest to 30% of the height of the peak. Only peaks with magnitudes at least 30% as high as the highest peak are considered in this calculation.";
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
			// Find the number of sufficiently large peaks
			int count = 0;
			for (int bin = 0; bin < sequence_info.beat_histogram_thresholded_table.length; bin++)
				if (sequence_info.beat_histogram_thresholded_table[bin][2] > 0.001)
					count++;

			// Store the peak bins
			int[] peak_bins = new int[count];
			int so_far = 0;
			for (int bin = 0; bin < sequence_info.beat_histogram_thresholded_table.length; bin++)
			{
				if (sequence_info.beat_histogram_thresholded_table[bin][2] > 0.001)
				{
					peak_bins[so_far] = bin;
					so_far++;
				}
			}

			// Find the width of each peak
			double[] widths = new double[peak_bins.length];
			for (int peak = 0; peak < peak_bins.length; peak++)
			{
				// 30% of this peak
				double limit_value = 0.3 * sequence_info.beat_histogram[peak_bins[peak]];

				// Find left limit
				int i = peak_bins[peak];
				int left_index = 0;
				while (i >= 0)
				{
					if (sequence_info.beat_histogram[i] < limit_value)
						i = -1;
					else
					{
						left_index = i;
						i--;
					}
				}

				// Find right limit
				i = peak_bins[peak];
				int right_index = 0;
				while (i < sequence_info.beat_histogram.length)
				{
					if (sequence_info.beat_histogram[i] < limit_value)
						i = sequence_info.beat_histogram.length + 1;
					else
					{
						right_index = i;
						i++;
					}
				}

				// Calculate width
				widths[peak] = (double) right_index - (double) left_index;
			}

			// Calculate the feature value
			if (widths == null || widths.length == 0)
				value = 0.0;
			else 
				value = mckay.utilities.staticlibraries.MathAndStatsMethods.getAverage(widths);
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}