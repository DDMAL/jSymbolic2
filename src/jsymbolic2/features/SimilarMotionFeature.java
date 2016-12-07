package jsymbolic2.features;

import java.util.*;
import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.NoteInfo;
import jsymbolic2.featureutils.CollectedNoteInfo;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the fraction of all pitched notes that move together in the same direction
 * within 10% of the duration of the shorter note. If multiple notes are sounding within a single channel
 * (e.g. a piano chord), only the highest pitched note in each such channel is considered for the purposes of
 * this feature. Set to 0 if there are no pitched notes.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class SimilarMotionFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public SimilarMotionFeature()
	{
		code = "T-17";
		String name = "Similar Motion";
		String description = "Fraction of all pitched notes that move together in the same direction within 10% of the duration of the shorter note. If multiple notes are sounding within a single channel (e.g. a piano chord), only the highest pitched note in each such channel is considered for the purposes of this feature. Set to 0 if there are no pitched notes.";
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
			/*
			// A map indicating all notes starting on any given MIDI tick, where the start tick value serves
			// as the map key and the map value is a list of all notes starting on the specified tick, on any 
			// track and on any channel. The particular ordering of notes in this List value is not 
			// necessarily meaningful.
			Map<Integer, List<NoteInfo>> start_tick_note_map = sequence_info.all_notes.getStartTickNoteMap();
			
			// The total number of ticks in the music under consideration
			int total_ticks = sequence_info.duration_of_ticks_in_seconds.length;
			
			// The last qualifying note that was played in each channel. (A note only qualifies if it is the
			// sole note on its channel that starts on a given tick or, if more than one note starts on
			// the same tick on the same channel, it is the note amongst these with the highest pitch.)
			NoteInfo[] last_note_by_channel = new NoteInfo[16];
			
			double total_number_qualifying_notes_moving_in_same_direction = 0.0;
			
			for (int tick = 0; tick < total_ticks; tick++)
			{
				if (start_tick_note_map.containsKey(tick))
				{
					
				}
			}
			*/
			
			// The total number of notes that move in parallel with other notes
			double total_number_parallel_notes = 0;

			// Set up ticks_with_note_ons_sorted, an array indicating all ticks on which one or more note ons
			// occur, sorted from first to last
			Map<Integer, List<NoteInfo>> start_tick_note_map = sequence_info.all_notes.getStartTickNoteMap();
			Integer[] ticks_with_note_ons = start_tick_note_map.keySet().toArray(new Integer[0]);
			Arrays.sort(ticks_with_note_ons);

			// Go through each tick on which one or more note ons occur
			for (int current_tick_index = 0; current_tick_index < ticks_with_note_ons.length - 1; current_tick_index++)
			{
				// The MIDI tick corresponding to this iteration
				int current_tick = ticks_with_note_ons[current_tick_index];

				// Will store the motion of the notes starting on this tick in each channel
				List<NoteMotion> tick_motion = new ArrayList<>(16);

				// Find the shortest note amongst the highest notes in pitch starting on this tick in all 
				// channels but channel 10
				NoteInfo shortest_qualifying_note = findShortestQualifyingNoteStartingAtTick(sequence_info.all_notes, current_tick);
				
				if (shortest_qualifying_note != null)
				{
					// The number of MIDI ticks permitted to separate qualifying notes
					int permitted_offset_in_ticks = (int) (0.1 * (double) shortest_qualifying_note.getDuration());
					
					// Compare the voice motion in each channel wrt the shortest duration
					for (int channel = 0; channel < 16; channel++)
					{
						if (channel == 10 - 1)  // Skip over the unpitched percussion channel 10
							continue;

						// Collect information about notes in this channel
						List<NoteInfo> notes_on_this_channel = sequence_info.all_notes.getNotesOnChannel(channel);
						Map<Integer, List<NoteInfo>> start_tick_note_map_this_channel = CollectedNoteInfo.noteListToStartTickNoteMap(notes_on_this_channel);
						Set<Integer> ticks_with_note_ons_this_channel = start_tick_note_map_this_channel.keySet();
						List<Integer> sorted_ticks_with_note_ons_this_channel = new ArrayList<>(ticks_with_note_ons_this_channel);
						sorted_ticks_with_note_ons_this_channel.sort((i1, i2) -> i1.compareTo(i2));

						// Search for close voice within the given short duration
						int close_voice = -1;
						int close_voice_index = 0;
						for (int channel_tick = current_tick; channel_tick < (current_tick + permitted_offset_in_ticks); channel_tick++)
						{
							if (ticks_with_note_ons_this_channel.contains(channel_tick))
							{
								close_voice = channel_tick;
								close_voice_index = sorted_ticks_with_note_ons_this_channel.indexOf(close_voice);
								break; // jump out once we have found the closest note
							}
						}
						int next_tick_index = close_voice_index + 1;

						// If no close voice was found or out of array index bounds then continue
						if (close_voice < 0 || next_tick_index >= sorted_ticks_with_note_ons_this_channel.size())
						{
							tick_motion.add(NoteMotion.NO_MOTION); // means no similar motion is occurring
							continue; // In case there are no nearby notes in this channel
						}

						// Get the closest note and then the next note to find this voices current motion
						List<NoteInfo> close_tick_notes = start_tick_note_map_this_channel.get(close_voice);
						int next_channel_tick = sorted_ticks_with_note_ons_this_channel.get(next_tick_index);
						List<NoteInfo> next_tick_notes = start_tick_note_map_this_channel.get(next_channel_tick);
						NoteInfo close_note = getHighestNote(close_tick_notes);
						NoteInfo next_note = getHighestNote(next_tick_notes);
						if (close_note.getPitch() < next_note.getPitch())
							tick_motion.add(NoteMotion.UP);
						else
						{
							if (close_note.getPitch() > next_note.getPitch())
								tick_motion.add(NoteMotion.DOWN);
							else
								tick_motion.add(NoteMotion.SAME);
						}
					}
					total_number_parallel_notes += computeNumberVoicesMovingInParallelAtThisTick(tick_motion);
				}
			}
			
			// Calculate final feature value
			if (sequence_info.total_number_pitched_note_ons == 0)
				value = 0;
			else
				value = total_number_parallel_notes / sequence_info.total_number_pitched_note_ons;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
	

	/* PRIVATE DATA STRUCTURES ******************************************************************************/
	
	
	/**
	 * Convenience enum to deal with the different directions of motion a note can take.
	 */
	private enum NoteMotion
	{
		UP,
		DOWN,
		SAME,
		NO_MOTION
	}

	
	/* PRIVATE STATIC METHODS *******************************************************************************/
	
	
	/**
	 * Find all notes starting at the given tick. Then, find the highest note in pitch starting on this tick
	 * in each channel (except for channel 10, which is ignored). Then, amongst these highest notes in pitch
	 * in each channel starting at this tick, return the one with the shortest duration. Returns null if there
	 * are no notes starting in at this tick that are not in channel 10.
	 *
	 * @param all_notes	All the notes in the music under consideration.
	 * @param tick		The tick at which to find the shortest qualifying note.
	 * @return			The shortest note amongst the highest notes in pitch starting on this tick in all 
	 *					channels but channel 10. Returns null if there are no notes starting in at this tick
	 *					that are not in channel 10.
	 */
	private static NoteInfo findShortestQualifyingNoteStartingAtTick(CollectedNoteInfo all_notes, int tick)
	{
		NoteInfo shortest_note = null;
		int shortest_duration = Integer.MAX_VALUE;
		
		for (int channel = 0; channel < 16; channel++)
		{
			if (channel == 10 - 1) // Ignore notes in channel 10
				continue;

			List<NoteInfo> channel_notes = all_notes.getNotesOnChannel(channel);
			Map<Integer, List<NoteInfo>> channel_start_tick_note_map = CollectedNoteInfo.noteListToStartTickNoteMap(channel_notes);
			if (channel_start_tick_note_map.containsKey(tick))
			{
				List<NoteInfo> current_tick_notes = channel_start_tick_note_map.get(tick);
				NoteInfo high_note = getHighestNote(current_tick_notes);
				int high_duration = high_note.getDuration();
				if (high_duration < shortest_duration)
				{
					shortest_note = high_note;
					shortest_duration = high_duration;
				}
			}
		}
		
		return shortest_note;
	}

	
	/**
	 * Return the note with the highest pitch within the given list of notes.
	 *
	 * @param notes	The list of notes to examine.
	 * @return		The note with the highest pitch in the specified list of notes.
	 */
	private static NoteInfo getHighestNote(List<NoteInfo> notes)
	{
		if (notes == null)
			return null;

		NoteInfo note_with_max_pitch = notes.get(0);
		int max_pitch = notes.get(0).getPitch();
		for (NoteInfo note : notes)
		{
			if (note.getPitch() > max_pitch)
			{
				max_pitch = note.getPitch();
				note_with_max_pitch = note;
			}
		}
		return note_with_max_pitch;
	}

	
	/**
	 * Compute how many voices are moving in parallel at a given MIDI tick.
	 *
	 * @param tick_motion	The direction of motion for each channel at the given MIDI tick.
	 * @return				The number of voices moving in parallel at the given MIDI tick.
	 * @throws Exception	An Exception is thrown if 16 channels are not present in tick_motion.
	 */
	private static double computeNumberVoicesMovingInParallelAtThisTick(List<NoteMotion> tick_motion)
		throws Exception
	{
		if (tick_motion.size() != 16 - 1)
			throw new Exception("There must 16 channels in order to compare directions of motion.");

		double parallel_voices = 0;
		int up_motion = 0;
		int down_motion = 0;
		int same_motion = 0;
		for (NoteMotion motion : tick_motion)
		{
			switch (motion)
			{
				case UP:
					up_motion++;
					break;
				case DOWN:
					down_motion++;
					break;
				case SAME:
					same_motion++;
					break;
				case NO_MOTION:
					break;
			}
		}
		
		if (up_motion > 1)
			parallel_voices += up_motion;
		if (down_motion > 1)
			parallel_voices += down_motion;
		if (same_motion > 1)
			parallel_voices += same_motion;

		return parallel_voices;
	}


	
	/**
	 * Get the note with the shortest duration within the given list of notes.
	 *
	 * @param notes	The list of notes to examine.
	 * @return		The note with the shortest duration in the specified list of notes.
	 */
	private static NoteInfo getShortestNote(List<NoteInfo> notes)
	{
		if (notes == null)
			return null;

		int short_duration = Integer.MAX_VALUE;
		NoteInfo note_with_shortest_duration = notes.get(0);
		for (NoteInfo note : notes)
		{
			int this_duration = note.getDuration();
			if (this_duration < short_duration)
			{
				short_duration = this_duration;
				note_with_shortest_duration = note;
			}
		}
		return note_with_shortest_duration;
	}
}