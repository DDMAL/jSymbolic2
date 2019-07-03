package jsymbolic2.features.melodicintervals;

import java.util.LinkedList;
import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the standard deviation of the melodic interval (in semitones) separating 
 * the top note of melodic peaks and the bottom note of adjacent melodic troughs in the MIDI channel with the 
 * highest average pitch. Similar assumptions are made in the calculation of this feature as for the Melodic 
 * Interval Histogram.
 *
 * @author radamian and Cory McKay
 */
public class VariabilityOfIntervalSpannedByMelodicHalfArcsInHighestLineFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VariabilityOfIntervalSpannedByMelodicHalfArcsInHighestLineFeature()
	{
		String name = "Variability of Interval Spanned by Melodic Half-Arcs in Highest Line";
		String code = "M-116";
		String description = "Standard deviation of the melodic interval (in semitones) separating the top note of melodic peaks and the bottom note of adjacent melodic troughs in the MIDI channel with the highest average pitch. Similar assumptions are made in the calculation of this feature as for the Melodic Interval Histogram.";
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
			// Get channel with the highest average pitch
			int channel_with_highest_average_pitch = 0;
			for (int chan = 0; chan < 16; chan++)
				if (chan != 10 - 1) // Exclude Channel 10 (Percussion)
					if (sequence_info.channel_statistics[chan][6] > sequence_info.channel_statistics[channel_with_highest_average_pitch][6])
						channel_with_highest_average_pitch = chan;
			
			LinkedList<Integer> intervals_spanned_by_melodic_half_arcs = new LinkedList<>();
		
			for (int track = 0; track < sequence_info.melodic_intervals_by_track_and_channel.size(); track++)
			{
				int direction = 0;
				int interval_so_far = 0;

				// Find the interval spanned by each melodic half-arc
				LinkedList<Integer> intervals = sequence_info.melodic_intervals_by_track_and_channel.get(track)[channel_with_highest_average_pitch];
				for (int i = 0; i < intervals.size(); i++)
				{
					// If arc is currently descending
					if (direction == -1)
					{
						if (intervals.get(i) < 0)
							interval_so_far += Math.abs(intervals.get(i));
						else if (intervals.get(i) > 0)
						{
							intervals_spanned_by_melodic_half_arcs.add(interval_so_far);
							interval_so_far = Math.abs(intervals.get(i));
							direction = 1;
						}
					}

					// If arc is currently ascending
					else if (direction == 1)
					{
						if (intervals.get(i) > 0)
							interval_so_far += Math.abs(intervals.get(i));
						else if (intervals.get(i) < 0)
						{
							intervals_spanned_by_melodic_half_arcs.add(interval_so_far);
							interval_so_far = Math.abs(intervals.get(i));
							direction = -1;
						}
					}

					// Handle the first interval
					else if (direction == 0)
					{
						if (intervals.get(i) > 0)
						{
							direction = 1;
							interval_so_far += Math.abs(intervals.get(i));
						}
						if (intervals.get(i) < 0)
						{
							direction = -1;
							interval_so_far += Math.abs(intervals.get(i));
						}
					}

					// Handle case when last interval is encountered
					if (i == intervals.size() - 1)
						if (interval_so_far != 0)
							intervals_spanned_by_melodic_half_arcs.add(interval_so_far);
				}
			}

			// Prepare array for standard deviation calculation
			int[] intervals_spanned_by_melodic_half_arcs_in_array = new int[intervals_spanned_by_melodic_half_arcs.size()];
			for (int i = 0; i < intervals_spanned_by_melodic_half_arcs_in_array.length; i++)
				intervals_spanned_by_melodic_half_arcs_in_array[i] = intervals_spanned_by_melodic_half_arcs.get(i);
			
			// Calculate the value
			if (intervals_spanned_by_melodic_half_arcs_in_array.length == 0)
				value = 0.0;
			else
				value = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(intervals_spanned_by_melodic_half_arcs_in_array);
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}