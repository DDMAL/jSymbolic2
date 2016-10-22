package jsymbolic2.features;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.NoteInfo;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the fraction of all notes that are surrounded on both sides by MIDI Note
 * Ons on the same MIDI channel that have durations at least three times as long as the central note.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class MelodicEmbellishmentsFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MelodicEmbellishmentsFeature()
	{
		code = "M-16";
		String name = "Melodic Embellishments";
		String description = "Fraction of all notes that are surrounded on both sides by MIDI Note Ons on the same MIDI channel that have durations at least three times as long as the central note.";
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
			double embellish_notes = 0;
			// For each channel get all the notes in the channel
			for (int channel = 0; channel < 16; channel++)
			{
				List<NoteInfo> all_notes_in_this_channel = sequence_info.all_note_info.getChannelNotes(channel);
				all_notes_in_this_channel.sort((n1, n2) -> ((Integer) n1.getStart_tick()).compareTo(n2.getStart_tick()));
				Map<Integer, List<NoteInfo>> channelTickNotes = sequence_info.all_note_info.channelListToTickMap(all_notes_in_this_channel);
				Integer[] channelTicks = channelTickNotes.keySet().toArray(new Integer[0]);
				Arrays.sort(channelTicks);
				
				// For each note in this channel, compare with all other notes up to 16 notes away
				for (NoteInfo current_note : all_notes_in_this_channel)
				{
					int current_tick = current_note.getStart_tick();
					int current_tick_index = 0;
					for (int tick_index = 0; tick_index < channelTicks.length; tick_index++)
					{
						if (channelTicks[tick_index] == current_tick)
						{
							current_tick_index = tick_index;
							break;
						}
					}
					
					if (current_tick_index < channelTicks.length - 1 && current_tick_index > 0)
					{
						int next_tick = channelTicks[current_tick_index + 1];
						int previous_tick = channelTicks[current_tick_index - 1];
						List<NoteInfo> next_notes = channelTickNotes.get(next_tick);
						List<NoteInfo> previous_notes = channelTickNotes.get(previous_tick);
						boolean next_check = false;
						for (NoteInfo next : next_notes)
							if (next.getDuration() >= 3 * current_note.getDuration())
								next_check = true;
						boolean previous_check = false;
						for (NoteInfo previous : previous_notes)
							if (previous.getDuration() >= 3 * current_note.getDuration())
								previous_check = true;
						if (next_check && previous_check)
							embellish_notes++;
					}
				}
			}
			
			value = embellish_notes / sequence_info.total_number_notes;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}