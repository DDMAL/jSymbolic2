package jsymbolic2.features.melodicintervals;

import java.util.LinkedList;
import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the average melodic interval (in semitones) separating the top note of 
 * melodic peaks and the top note of the next peak, as well as the bottom note of melodic troughs and the 
 * bottom note of the next trough in the MIDI channel with the highest average pitch. Similar assumptions are 
 * made in the calculation of this feature as for the Melodic Interval Histogram.
 *
 * @author radamian and Cory McKay
 */
public class AverageIntervalSpannedByMelodicArcsInHighestLineFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public AverageIntervalSpannedByMelodicArcsInHighestLineFeature()
	{
		String name = "Average Interval Spanned by Melodic Arcs in Highest Line";
		String code = "M-113";
		String description = "Average melodic interval (in semitones) separating the top note of melodic peaks and the top note of the next peak, as well as the bottom note of melodic troughs and the bottom note of the next trough in the MIDI channel with the highest average pitch. Similar assumptions are made in the calculation of this feature as for the Melodic Interval Histogram.";
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
			int track_with_highest_average_pitch = sequence_info.track_and_channel_with_highest_average_pitch[0];
			int channel_with_highest_average_pitch = sequence_info.track_and_channel_with_highest_average_pitch[1];
			
			int total_intervals = 0;
			int number_arcs = 0;
			
			int direction = 0;
			// The interval separating a peak from a trough. It is reset to 0 once the next peak 
			// is met and the interval from the last peak to the new peak is calculated.
			int interval_of_descent = 0;
			// The interval separating a trough from a peak. It is reset to 0 once the next trough 
			// is met and the distance from the last trough to the new trough is calculated.
			int interval_of_ascent = 0;

			// Find the interval spanned by each melodic arc
			LinkedList<Integer> intervals = sequence_info.melodic_intervals_by_track_and_channel.get(track_with_highest_average_pitch)[channel_with_highest_average_pitch];
			for (int i = 0; i < intervals.size(); i++)
			{
				// If arc is currently descending
				if (direction == -1)
				{
					if (intervals.get(i) < 0)
						interval_of_descent += intervals.get(i);
					else if (intervals.get(i) > 0)
					{
						if (interval_of_ascent != 0)
						{
							total_intervals += Math.abs(interval_of_descent + interval_of_ascent);
							number_arcs++;
							interval_of_ascent = 0;
						}
						direction = 1;
						interval_of_ascent += intervals.get(i);
					}
				}

				// If arc is currently ascending
				else if (direction == 1)
				{
					if (intervals.get(i) > 0)
						interval_of_ascent += intervals.get(i);
					else if (intervals.get(i) < 0)
					{
						if (interval_of_descent != 0)
						{
							total_intervals += Math.abs(interval_of_descent + interval_of_ascent);
							number_arcs++;
							interval_of_descent = 0;
						}
						direction = -1;
						interval_of_descent += intervals.get(i);
					}
				}

				// Handle the first interval
				else if (direction == 0)
				{
					if (intervals.get(i) > 0)
					{
						direction = 1;
						interval_of_ascent += intervals.get(i);
					}
					if (intervals.get(i) < 0)
					{
						direction = -1;
						interval_of_descent += intervals.get(i);
					}
				}

				// Handle case when last interval is encountered
				if (i == intervals.size() - 1)
					if (interval_of_descent != 0 && interval_of_ascent != 0)
					{
						total_intervals += Math.abs(interval_of_descent + interval_of_ascent);
						number_arcs++;
					}
			}
			
			// Calculate the value
			if (number_arcs == 0)
				value = 0.0;
			else
				value = (double) total_intervals / (double) number_arcs;
		} 
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}