package jsymbolic2.features.melodicintervals;

import java.util.LinkedList;
import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the average melodic interval (in semitones) separating the top note of 
 * melodic peaks and the bottom note of adjacent melodic troughs in the MIDI channel with the highest average 
 * pitch. Similar assumptions are made in the calculation of this feature as for the Melodic Interval 
 * Histogram.
 *
 * @author radamian and Cory McKay
 */
public class AverageIntervalSpannedByMelodicHalfArcsInHighestLineFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public AverageIntervalSpannedByMelodicHalfArcsInHighestLineFeature()
	{
		String name = "Average Interval Spanned by Melodic Half-Arcs in Highest Line";
		String code = "M-114";
		String description = "Average melodic interval (in semitones) separating the top note of melodic peaks and the bottom note of adjacent melodic troughs in the MIDI channel with the highest average pitch. Similar assumptions are made in the calculation of this feature as for the Melodic Interval Histogram.";
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
			
			int total_intervals = 0;
			int number_intervals = 0;
		
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
							total_intervals += interval_so_far;
							number_intervals++;
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
							total_intervals += interval_so_far;
							number_intervals++;
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
						{
							total_intervals += interval_so_far;
							number_intervals++;
						}
				}
			}

			// Calculate the value
			if (number_intervals == 0)
				value = 0.0;
			else
				value = (double) total_intervals / (double) number_intervals;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}