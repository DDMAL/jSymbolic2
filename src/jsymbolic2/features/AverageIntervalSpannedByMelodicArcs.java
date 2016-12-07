package jsymbolic2.features;

import java.util.LinkedList;
import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the average melodic interval (in semitones) separating the top note of
 * melodic peaks and the bottom note of adjacent melodic troughs. Similar assumptions are made in the
 * calculation of this feature as for the Melodic Interval Histogram.
 *
 * @author Cory McKay
 */
public class AverageIntervalSpannedByMelodicArcs
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public AverageIntervalSpannedByMelodicArcs()
	{
		code = "M-24";
		String name = "Average Interval Spanned by Melodic Arcs";
		String description = "Average melodic interval (in semitones) separating the top note of melodic peaks and the bottom note of adjacent melodic troughs. Similar assumptions are made in the calculation of this feature as for the Melodic Interval Histogram.";
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
			int total_intervals = 0;
			int number_intervals = 0;
		
			for (int track = 0; track < sequence_info.melodic_intervals_by_track_and_channel.size(); track++)
			{
				LinkedList<Integer>[] melodic_intervals_by_channel = sequence_info.melodic_intervals_by_track_and_channel.get(track);
			
				for (int chan = 0; chan < melodic_intervals_by_channel.length; chan++)
				{
					if (chan != (10 - 1)) // Exclude unpitched Channel 10 notes
					{
						// Convert the list of melodic intervals in this channel to an array
						Object[] list_contents = melodic_intervals_by_channel[chan].toArray();
						int[] intervals = new int[list_contents.length];
						for (int i = 0; i < intervals.length; i++)
							intervals[i] = ((Integer) list_contents[i]).intValue();

						// Find the number of arcs
						int direction = 0;
						int interval_so_far = 0;
						for (int i = 0; i < intervals.length; i++)
						{
							// If arc is currently decending
							if (direction == -1)
							{
								if (intervals[i] < 0)
									interval_so_far += Math.abs(intervals[i]);
								else if (intervals[i] > 0)
								{
									total_intervals += interval_so_far;
									number_intervals++;
									interval_so_far = Math.abs(intervals[i]);
									direction = 1;
								}
							}

							// If arc is currently ascending
							else if (direction == 1)
							{
								if (intervals[i] > 0)
									interval_so_far += Math.abs(intervals[i]);
								else if (intervals[i] < 0)
								{
									total_intervals += interval_so_far;
									number_intervals++;
									interval_so_far = Math.abs(intervals[i]);
									direction = -1;
								}
							}

							// If arc is currently stationary
							else if (direction == 0)
							{
								if (intervals[i] > 0)
								{
									direction = 1;
									interval_so_far += Math.abs(intervals[i]);
								}
								if (intervals[i] < 0)
								{
									direction = -1;
									interval_so_far += Math.abs(intervals[i]);
								}
							}
						}
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