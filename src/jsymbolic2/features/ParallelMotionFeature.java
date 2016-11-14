package jsymbolic2.features;

import java.util.*;
import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.NoteInfo;
import jsymbolic2.featureutils.CollectedNoteInfo;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the fraction of all notes that move together in the same direction within
 * 10% of the duration of the shorter note. If multiple notes are sounding within a single channel (e.g. a
 * piano chord), only the highest pitched note in this channel is considered for the purposes of this feature.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class ParallelMotionFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public ParallelMotionFeature()
	{
		code = "T-19";
		String name = "Parallel Motion";
		String description = "Fraction of all notes that move together in the same direction within 10% of the duration of the shorter note. If multiple notes are sounding within a single channel (e.g. a piano chord), only the highest pitched note in this channel is considered for the purposes of this feature.";
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
			// The total number of notes that move in parallel with other notes
			double parallel_notes = 0;

			// Set up the appropriate tick note maps
			List<NoteInfo> all_notes = sequence_info.all_notes.getNoteList();
			all_notes.sort((n1, n2) -> ((Integer) n1.getStartTick()).compareTo(n2.getStartTick()));
			Map<Integer, List<NoteInfo>> tickNoteMap = sequence_info.all_notes.getStartTickNoteMap();
			Integer[] ticks = tickNoteMap.keySet().toArray(new Integer[0]);
			Arrays.sort(ticks);

			// For each tick we check the note range in other voices
			for (int current_tick_index = 0; current_tick_index < ticks.length - 1; current_tick_index++)
			{
				// Store the motion of the voices in each channel
				List<NoteMotion> tick_motion = new ArrayList<>(16);

				// Get shortest note duration for this tick
				int current_tick = ticks[current_tick_index];
				NoteInfo short_note = getShortestNoteInChannelAtTick(sequence_info.all_notes, current_tick);
				int short_duration = short_note.getDuration();

				// Compare the voice motion in each voice(channel) w.r.t. the shortest duration
				for (int channel = 0; channel < 16; channel++)
				{
					if (channel == 10 - 1)  // Skip over the percussion channel
						continue;

					List<NoteInfo> channel_notes = sequence_info.all_notes.getNotesOnChannel(channel);
					Map<Integer, List<NoteInfo>> current_tick_notes = sequence_info.all_notes.noteListToStartTickNoteMap(channel_notes);
					Set<Integer> channel_ticks = current_tick_notes.keySet();
					List<Integer> channel_ticks_list = new ArrayList<>(channel_ticks);
					channel_ticks_list.sort((i1, i2) -> i1.compareTo(i2));

					// Search for close voice within the given short duration
					int close_voice = -1;
					int close_voice_index = 0;
					for (int channel_tick = current_tick; channel_tick < current_tick + 0.1 * short_duration; channel_tick++)
					{
						if (channel_ticks.contains(channel_tick))
						{
							close_voice = channel_tick;
							close_voice_index = channel_ticks_list.indexOf(close_voice);
							break; // jump out once we have found the closest note
						}
					}
					int next_tick_index = close_voice_index + 1;
					
					// If no close voice was found or out of array index bounds then continue
					if (close_voice < 0 || next_tick_index >= channel_ticks_list.size())
					{
						tick_motion.add(NoteMotion.NO_MOTION); // means no similar motion is occurring
						continue; // In case there are no nearby notes in this channel
					}

					// Get the closest note and then the next note to find this voices current motion
					List<NoteInfo> close_tick_notes = current_tick_notes.get(close_voice);
					int next_channel_tick = channel_ticks_list.get(next_tick_index);
					List<NoteInfo> next_tick_notes = current_tick_notes.get(next_channel_tick);
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
				parallel_notes += computeNumberVoicesMovingInParallelAtThisTick(tick_motion);
			}
			value = parallel_notes / all_notes.size();
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
	

	/* PRIVATE METHODS **************************************************************************************/
	
	
	/**
	 * Convenience enum to deal with different types of possible note motion.
	 */
	private enum NoteMotion
	{
		UP,
		DOWN,
		SAME,
		NO_MOTION
	}

	
	/**
	 * Get the shortest note in any channel from the highest pitch in each channel at the specified tick.
	 *
	 * @param all_note_info	All the note data of the given piece of music.
	 * @param current_tick	The tick at which to check the channels from.
	 * @return				The shortest note with the highest pitch of any channel at the given current tick.
	 */
	private NoteInfo getShortestNoteInChannelAtTick(CollectedNoteInfo all_note_info, int current_tick)
	{
		int shortest_duration = Integer.MAX_VALUE;
		NoteInfo shortest_note = all_note_info.getNoteList().get(0); // to initialize to non-null value
		for (int channel = 0; channel < 16; channel++)
		{
			if (channel == 10 - 1) // Ignore notes in channel 10
				continue;

			List<NoteInfo> channel_notes = all_note_info.getNotesOnChannel(channel);
			Map<Integer, List<NoteInfo>> channel_tick_notes = all_note_info.noteListToStartTickNoteMap(channel_notes);
			if (channel_tick_notes.containsKey(current_tick))
			{
				List<NoteInfo> current_tick_notes = channel_tick_notes.get(current_tick);
				NoteInfo high_note = getHighestNote(current_tick_notes);
				int high_duration = high_note.getDuration();
				if (high_duration < shortest_duration)
				{
					shortest_duration = high_duration;
					shortest_note = high_note;
				}
			}
		}
		return shortest_note;
	}

	
	/**
	 * Compute how many voices are moving in parallel at a given MIDI tick.
	 *
	 * @param tick_motion	The direction of motion for each channel at the given MIDI tick.
	 * @return				The number of voices moving in parallel at the given MIDI tick.
	 * @throws Exception	An Exception is thrown if 16 channels are not present in tick_motion.
	 */
	private double computeNumberVoicesMovingInParallelAtThisTick(List<NoteMotion> tick_motion)
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
	 * Get the note with the highest pitch within the given list of notes.
	 *
	 * @param notes	The list of notes to examine.
	 * @return		The note with the highest pitch in the specified list of notes.
	 */
	private NoteInfo getHighestNote(List<NoteInfo> notes)
	{
		if (notes == null)
			return null;

		int max_pitch = 0;
		NoteInfo note_with_max_pitch = notes.get(0);
		for (NoteInfo note : notes)
		{
			int this_pitch = note.getPitch();
			if (this_pitch > max_pitch)
			{
				max_pitch = this_pitch;
				note_with_max_pitch = note;
			}
		}
		return note_with_max_pitch;
	}

	
	/**
	 * Get the note with the shortest duration within the given list of notes.
	 *
	 * @param notes	The list of notes to examine.
	 * @return		The note with the shortest duration in the specified list of notes.
	 */
	private NoteInfo getShortestNote(List<NoteInfo> notes)
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