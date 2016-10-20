package jsymbolic2.features;

import javax.sound.midi.Sequence;
import java.util.List;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the number of notes in the channel with the highest average loudness (MIDI
 * velocity) that fall outside the range of any other channel, divided by the total number of notes in the
 * channel with the highest average loudness.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class RelativeRangeIsolationOfLoudestVoiceFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public RelativeRangeIsolationOfLoudestVoiceFeature()
	{
		code = "T-11";
		String name = "Relative Range Isolation of Loudest Voice";
		String description = "Number of notes in the channel with the highest average loudness (MIDI velocity) that fall outside the range of any other channel, divided by the total number of notes in the channel with the highest average loudness.";
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
			// Get information from sequence_info 
			int[][] channel_stats = sequence_info.channel_statistics;
			List<List<Integer>> channel_pitch = sequence_info.channel_pitches;

			// Find the loudest channel and its average loudness
			int index_of_loudest_channel = 0;
			int highest_average_loudness = 0;
			for (int channel = 0; channel < channel_stats.length; channel++)
			{
				int average_loudness = channel_stats[channel][2];
				if (average_loudness > highest_average_loudness)
				{
					highest_average_loudness = average_loudness;
					index_of_loudest_channel = channel;
				}
			}

			// Count all notes outside the range of the channel with the highest average loudness, and compare
			// these notes to the range of each channel
			int notes_outside_range = 0;
			List<Integer> highest_avg_notes = channel_pitch.get(index_of_loudest_channel);
			for (Integer current_pitch : highest_avg_notes)
			{
				for (int channel = 0; channel < channel_stats.length; channel++)
				{
					if (channel == index_of_loudest_channel)
						continue;

					int lowest_pitch = channel_stats[channel][4];
					int highest_pitch = channel_stats[channel][5];
					if ( current_pitch >= 0
						 && (current_pitch > highest_pitch  || current_pitch < lowest_pitch))
					{
						notes_outside_range++;
						break; //only need to count the note once
					}
				}
			}

			// Finalize results
			double total_avg_loud_notes = channel_stats[index_of_loudest_channel][0];
			value = notes_outside_range / total_avg_loud_notes;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}