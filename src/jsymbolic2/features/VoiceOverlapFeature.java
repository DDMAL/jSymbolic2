package jsymbolic2.features;

import java.util.List;
import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the number of notes played within the range of another channel, divided by
 * the total number of notes in the piece as a whole.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class VoiceOverlapFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VoiceOverlapFeature()
	{
		code = "T-16";
		String name = "Voice Overlap";
		String description = "Number of notes played within the range of another channel, divided by the total number of notes in the piece as a whole.";
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
			// Access pre-calculated information in sequence_info
			int[][] channel_stats = sequence_info.channel_statistics;
			List<List<Integer>> channel_pitch = sequence_info.list_of_note_on_pitches_by_channel;

			// Find notes that are within the range of any other channel
			int notes_inside_range = 0;
			for (int channel = 0; channel < channel_pitch.size(); channel++)
			{
				List<Integer> channel_notes = channel_pitch.get(channel);
				for (Integer current_pitch : channel_notes)
				{
					// Compare pitch of current_pitch to the range of every other channel
					for (int other_channel = 0; other_channel < channel_stats.length; other_channel++)
					{
						if (channel == other_channel)
							continue;

						int highest_pitch = channel_stats[other_channel][5];
						int lowest_pitch = channel_stats[other_channel][4];
						if (current_pitch <= highest_pitch && current_pitch >= lowest_pitch)
						{
							notes_inside_range++;
							break; // only current_pitch on channel once
						}
					}
				}
			}

			double total_notes = sequence_info.total_number_note_ons;
			if (total_notes == 0.0)
				value = 0.0;
			else 
				value = notes_inside_range / total_notes;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}