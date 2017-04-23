package jsymbolic2.features;

import java.util.List;
import java.util.Map;
import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;
import jsymbolic2.featureutils.CollectedNoteInfo;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.NoteInfo;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the average number of notes that go by in a MIDI channel before a note's
 * pitch is repeated (including the repeated note itself). This is calculated across each channel individually
 * before being combined. Notes that occur simultaneously on the same MIDI tick are only counted as one note
 * for the purpose of this calculation. Notes that do not recur after 16 notes in the same channel are not
 * included in this calculation. Set to 0 if there are no qualifying repeated notes in the piece.
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public class MelodicPitchVarietyFeature
		extends MIDIFeatureExtractor
{

	/* CONSTRUCTOR ******************************************************************************************/

	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MelodicPitchVarietyFeature()
	{
		code = "M-25";
		String name = "Melodic Pitch Variety";
		String description = "Average number of notes that go by in a MIDI channel before a note's pitch is repeated (including the repeated note itself). This is calculated across each channel individually before being combined. Notes that occur simultaneously on the same MIDI tick are only counted as one note for the purpose of this calculation. Notes that do not recur after 16 notes in the same channel are not included in this calculation. Set to 0 if there are no qualifying repeated notes in the piece.";
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
			// The total number of notes for which a repeated note is found
			double number_of_repeated_notes_found = 0.0;

			// The total number of notes that go by before a note is repeated, all added together for all
			// note that are repeated within max_notes_that_can_go_by
			double summed_number_of_notes_before_pitch_repeated = 0.0;

			// Go through channel by channel
			for (int channel = 0; channel < 16; channel++)
			{
				if (channel != (10 - 1))  // Skip over the unpitched percussion channel
				{
					// The maximum number of notes that can go by before a note is discounted for the purposes
					// of this feature
					final int max_notes_that_can_go_by = 16;

					// Prepare a list of all notes on this channel sorted by start tick
					List<NoteInfo> all_notes_in_this_channel = sequence_info.all_notes.getNotesOnChannel(channel);
					all_notes_in_this_channel = CollectedNoteInfo.noteListToSortedNoteList(all_notes_in_this_channel);

					// Prepare a map indicating all notes starting on any given MIDI tick, where the start 
					// tick value serves as the map key and the map value is a list of all notes starting on 
					// the specified tick. The particular ordering of notes in this List value is not 
					// necessarily meaningful.
					Map<Integer, List<NoteInfo>> note_start_tick_map_this_channel = CollectedNoteInfo.noteListToStartTickNoteMap(all_notes_in_this_channel);

					// Prepare a sorted set of all ticks containing one or more note on messages
					Integer[] sorted_note_on_ticks_this_channel = note_start_tick_map_this_channel.keySet().toArray(new Integer[0]);
					Arrays.sort(sorted_note_on_ticks_this_channel);

					// For each note in this channel, compare with the pitches of all other notes up to 
					// max_notes_that_can_go_by notes after it
					for (NoteInfo current_note : all_notes_in_this_channel)
					{
						boolean found_repeated_pitch = false;
						int notes_gone_by_with_different_pitch = 0;
						int last_tick_examined = 0;
						for (Integer tick_following_note : sorted_note_on_ticks_this_channel)
						{
							if ( !found_repeated_pitch && 
								 tick_following_note > current_note.getStartTick() )
							{
								if (tick_following_note != last_tick_examined)
									notes_gone_by_with_different_pitch++;
								
								last_tick_examined = tick_following_note;

								List<NoteInfo> notes_starting_this_following_tick = note_start_tick_map_this_channel.get(tick_following_note);
								for (NoteInfo following_note : notes_starting_this_following_tick)
								{
									if ( current_note.getPitch() == following_note.getPitch() && 
										 !found_repeated_pitch &&
										 notes_gone_by_with_different_pitch <= max_notes_that_can_go_by )
									{
										found_repeated_pitch = true;
										number_of_repeated_notes_found++;
										summed_number_of_notes_before_pitch_repeated += notes_gone_by_with_different_pitch;
									}
								}
							}

							if ( found_repeated_pitch ||
								 notes_gone_by_with_different_pitch > max_notes_that_can_go_by)
								break;
						}
					}
				}
			}
			
			// To deal with music with no repeated notes
			if (number_of_repeated_notes_found == 0)
				value = 0.0;
			else
				value = summed_number_of_notes_before_pitch_repeated / number_of_repeated_notes_found;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}