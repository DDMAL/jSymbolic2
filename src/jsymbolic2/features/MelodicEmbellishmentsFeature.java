package jsymbolic2.features;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.CollectedNoteInfo;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.NoteInfo;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the fraction of all notes that are surrounded on both sides by MIDI Note
 * Ons on the same MIDI channel that have durations at least three times as long as the central note. Set to 0
 * if there are no notes in the piece.
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
		code = "M-21";
		String name = "Melodic Embellishments";
		String description = "Fraction of all notes that are surrounded on both sides by MIDI Note Ons on the same MIDI channel that have durations at least three times as long as the central note. Set to 0 if there are no notes in the piece.";
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
			// The total number of notes that are embellishemnets
			double number_embelleshing_notes = 0;
			
			// For each channel get all the notes in the channel
			for (int channel = 0; channel < 16; channel++)
			{
				if (channel != 10 - 1)  // Skip over the unpitched percussion channel
				{
					// Prepare a list of all notes on this channel sorted by start tick
					List<NoteInfo> all_notes_in_this_channel = sequence_info.all_notes.getNotesOnChannel(channel);
					all_notes_in_this_channel = CollectedNoteInfo.noteListToSortedNoteList(all_notes_in_this_channel);

					// Prepare a map indicating all notes starting on any given MIDI tick, where the start tick
					// value serves as the map key and the map value is a list of all notes starting on the
					// specified tick. The particular ordering of notes in this List value is not necessarily
					// meaningful.
					Map<Integer, List<NoteInfo>> note_start_tick_map_this_channel = CollectedNoteInfo.noteListToStartTickNoteMap(all_notes_in_this_channel);

					// Prepare a sorted set of all ticks containing one or more note on messages
					Integer[] sorted_note_on_ticks_this_channel = note_start_tick_map_this_channel.keySet().toArray(new Integer[0]);
					Arrays.sort(sorted_note_on_ticks_this_channel);

					// Compare the duration of each note in this channel with the duration of the notes on each
					// side of it on this channel
					for (NoteInfo current_note : all_notes_in_this_channel)
					{
						int current_tick_index = 0;
						int current_tick = current_note.getStartTick();
						for (int tick_index = 0; tick_index < sorted_note_on_ticks_this_channel.length; tick_index++)
						{
							if (sorted_note_on_ticks_this_channel[tick_index] == current_tick)
							{
								current_tick_index = tick_index;
								break;
							}
						}

						if (current_tick_index > 0 && current_tick_index < sorted_note_on_ticks_this_channel.length - 1)
						{
							int previous_tick_with_note = sorted_note_on_ticks_this_channel[current_tick_index - 1];
							int next_tick_with_note = sorted_note_on_ticks_this_channel[current_tick_index + 1];

							List<NoteInfo> previous_notes = note_start_tick_map_this_channel.get(previous_tick_with_note);
							List<NoteInfo> next_notes = note_start_tick_map_this_channel.get(next_tick_with_note);

							boolean previous_check = false;
							for (NoteInfo previous : previous_notes)
								if (previous.getDuration() >= 3 * current_note.getDuration())
									previous_check = true;
							boolean next_check = false;
							for (NoteInfo next : next_notes)
								if (next.getDuration() >= 3 * current_note.getDuration())
									next_check = true;

							if (next_check && previous_check)
								number_embelleshing_notes++;
						}
					}
				}
			}
			
			// Find the final feature value
			if (sequence_info.total_number_note_ons == 0)
				value = 0;
			else
				value = number_embelleshing_notes / sequence_info.total_number_note_ons;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}