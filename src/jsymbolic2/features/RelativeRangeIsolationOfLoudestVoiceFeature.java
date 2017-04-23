package jsymbolic2.features;

import javax.sound.midi.Sequence;
import java.util.List;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Number of pitched notes in the MIDI channel with the highest average loudness that fall outside the range
 * of any other pitched channel, divided by the total number of notes in the channel with the highest average
 * loudness. Set to 0 if there are only 0 or 1 channels containing pitched notes.
 *
 * @author Cory McKay and Tristano Tenaglia
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
		String description = "Number of pitched notes in the MIDI channel with the highest average loudness that fall outside the range of any other pitched channel, divided by the total number of notes in the channel with the highest average loudness. Set to 0 if there are only 0 or 1 channels containing pitched notes.";
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
			int[][] channel_statistics = sequence_info.channel_statistics;
			List<List<Integer>> list_of_note_on_pitches_by_channel = sequence_info.list_of_note_on_pitches_by_channel;

			// Find the loudest non-Channel 10 channel
			int index_of_loudest_channel = 0;
			int loudness_of_loudest_channel = 0;
			for (int channel = 0; channel < channel_statistics.length; channel++)
			{
				if (channel != 10 - 1) // not channel 10 (unpitched)
				{
					int average_loudness_this_channel = channel_statistics[channel][2];
					if (average_loudness_this_channel > loudness_of_loudest_channel)
					{
						loudness_of_loudest_channel = average_loudness_this_channel;
						index_of_loudest_channel = channel;
					}
				}
			}

			// Find the lowest and highest pitches in all channels other than 10 and the loudest channel
			boolean another_pitched_voice_exists = false;
			int lowest_pitch_in_non_loudest_voice = 127;
			int hightest_pitch_in_non_loudest_voice = 0;
			for (int channel = 0; channel < channel_statistics.length; channel++)
			{
				if (channel != index_of_loudest_channel && channel != 10 - 1)
				{
					if (channel_statistics[channel][0] != 0)
						another_pitched_voice_exists = true;

					int lowest_pitch_this_channel = channel_statistics[channel][4];
					int highest_pitch_this_channel = channel_statistics[channel][5];
					
					if (lowest_pitch_this_channel < lowest_pitch_in_non_loudest_voice)
						lowest_pitch_in_non_loudest_voice = lowest_pitch_this_channel;
					if (highest_pitch_this_channel > hightest_pitch_in_non_loudest_voice)
						hightest_pitch_in_non_loudest_voice = highest_pitch_this_channel;
				}
			}
			
			// Find number of notes in the loudest channel outside the range of any other channels
			double notes_in_loudest_channel_outside_range = 0.0;
			List<Integer> pitches_of_notes_in_loudest_channel = list_of_note_on_pitches_by_channel.get(index_of_loudest_channel);
			for (Integer this_pitch : pitches_of_notes_in_loudest_channel)
				if ( this_pitch > hightest_pitch_in_non_loudest_voice ||
				     this_pitch < lowest_pitch_in_non_loudest_voice)
					notes_in_loudest_channel_outside_range++;

			// Finalize results
			if (another_pitched_voice_exists)
			{
				double total_notes_in_loudest_voice = channel_statistics[index_of_loudest_channel][0];
				value = notes_in_loudest_channel_outside_range / total_notes_in_loudest_voice;
			}
			else
				value = 0.0;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}