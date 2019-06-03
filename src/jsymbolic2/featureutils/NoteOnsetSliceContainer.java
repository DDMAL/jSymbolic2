package jsymbolic2.featureutils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

/**
 * Objects of this class parse the music they are given at instantiation into note onset slices, and store the
 * results. A note onset slice, in its most basic form, is defined here as a set of pitched notes that start
 * simultaneously, or nearly simultaneously. A new onset slice is created whenever a new pitched note (in any
 * voice) occurs with sufficient rhythmic separation from previous pitched notes. A variety of different slice
 * types are calculated, including ones where notes are grouped from all MIDI tracks and channels, and ones
 * where they are kept segregated, as well as ones where notes being held from previous slices are included in
 * subsequent slices during which they are still sounding, and ones where they are not.
 *
 * The threshold governing whether non-simultaneous notes are grouped into the same onset slice is based on a
 * specified minimum fraction of a quarter note. Such grouping is necessary to account for MIDI files where
 * notes meant to be simultaneous are in fact time coded such that they are not (due to manual encoding or due
 * to encoding software settings), as well as to account for very short notes (such as grace notes) that are
 * not significant enough to merit their own slice.
 * 
 * @author radamian
 */
public class NoteOnsetSliceContainer 
{
	/* PRIVATE FIELDS ***************************************************************************************/

	
	/**
	 * The note onset slices for the music passed to this object at instantiation, listed in the order that
	 * the slices occur temporally. The outer list index specifies the slice, and the inner list index
	 * specifies the MIDI pitches in that slice, sorted from lowest pitch to highest pitch, and with duplicate
	 * pitches (in the same octave) removed. Pitches occurring very close to one another rhythmically are
	 * merged into the same slice, despite not being simultaneous. Only pitched notes are included (which is
	 * to say that Channel 10 unpitched instrument notes are excluded). Each slice of this type includes not
	 * only the notes starting in it, but also notes being sustained from previous slices. Notes from all MIDI
	 * tracks and channels are grouped together. This list has the same number of slices and the same slice
	 * synchronization as note_onset_slices_only_new_onsets, note_onset_slices_by_track_and_channel, and
	 * note_onset_slices_by_track_and_channel_only_new_onsets.
	 */
	private final LinkedList<LinkedList<Integer>> note_onset_slices;
	
	/**
	 * The note onset slices for the music passed to this object at instantiation, listed in the order that
	 * the slices occur temporally. The outer list index specifies the slice, and the inner list index
	 * specifies the MIDI pitches in that slice, sorted from lowest pitch to highest pitch, and with duplicate
	 * pitches (in the same octave) removed. Pitches occurring very close to one another rhythmically are
	 * merged into the same slice, despite not being simultaneous. Only pitched notes are included (which is
	 * to say that Channel 10 unpitched instrument notes are excluded). Each slice of this type includes ONLY
	 * the notes starting in that slice, and does NOT include notes sustained from previous slices. Notes from
	 * all MIDI tracks and channels are grouped together. This list has the same number of slices and the same
	 * slice synchronization as note_onset_slices, note_onset_slices_by_track_and_channel, and
	 * note_onset_slices_by_track_and_channel_only_new_onsets.
	 */
	private final LinkedList<LinkedList<Integer>> note_onset_slices_only_new_onsets;
	
	/**
	 * The note onset slices for the music passed to this object at instantiation, separated out by track
	 * (first array index) and by channel (second array index). The outer list index specifies the slice (the
	 * slices are listed in temporal order), and the inner list index specifies the MIDI pitches in that slice
	 * on that track and channel, sorted from lowest pitch to highest slice, and with duplicate pitches (in
	 * the same octave) in the same track and channel removed. Pitches occurring very close to one another
	 * rhythmically are merged into the same slice, despite not being simultaneous. Only pitched notes are
	 * included (which is to say that Channel 10 unpitched instrument notes are excluded). Each slice of this
	 * type includes not only the notes starting in it, but also notes being sustained from previous slices in
	 * the same track and channel. If a note occurs in any track and channel, a matching (but potentially
	 * empty) slice will be created for every other track and channel. The list for every track and channel
	 * has the same number of slices and the same slice synchronization as note_onset_slices,
	 * note_onset_slices_only_new_onsets, and note_onset_slices_by_track_and_channel_only_new_onsets.
	 */
	private final LinkedList<LinkedList<Integer>>[][] note_onset_slices_by_track_and_channel;
	
	/**
	 * The note onset slices for the music passed to this object at instantiation, separated out by track
	 * (first array index) and by channel (second array index). The outer list index specifies the slice (the
	 * slices are listed in temporal order), and the inner list index specifies the MIDI pitches in that slice
	 * on that track and channel, sorted from lowest pitch to highest slice, and with duplicate pitches (in
	 * the same octave) in the same track and channel removed. Pitches occurring very close to one another
	 * rhythmically are merged into the same slice, despite not being simultaneous. Only pitched notes are
	 * included (which is to say that Channel 10 unpitched instrument notes are excluded). Each slice of this
	 * type includes ONLY the notes starting in it, and does NOT notes being sustained from previous slices.
	 * If a note occurs in any track and channel, a matching (but potentially empty) slice will be created for
	 * every other track and channel. The list for every track and channel has the same number of slices and
	 * the same slice synchronization as note_onset_slices, note_onset_slices_only_new_onsets, and
	 * note_onset_slices_by_track_and_channel_only_new_onsets.
	 */
	private final LinkedList<LinkedList<Integer>>[][] note_onset_slices_by_track_and_channel_only_new_onsets;

	
	/* CONSTRUCTOR ******************************************************************************************/
	
	
	/**
	 * Parse the specified MIDI sequence into onset slices stored in this object.
	 *
	 * @param	midi_sequence				The MIDI sequence to extract information from.
	 * @param	tracks_from_sequence		An array of tracks contained in midi_sequence, and already parsed
	 *										from it.
	 * @param	all_notes_from_sequence		Information on the pitch, start tick, end tick, track and channel 
	 *										of every note in midi_sequence (including Channel 10 unpitched 
	 *										notes).
	 * @param	rhythmic_value_notes_in_qns	An array with one entry for each note, where the value of each 
	 *										entry indicates the quantized duration of the note in quarter 
	 *										notes (e.g. a value of 0.5 corresponds to a duration of an eighth
	 *										note). This array can be generated in
	 *										MIDIIntermediateRepresentations, and is used to calculate the
	 *										lookahead value for merging notes into slices.
	 */
	public NoteOnsetSliceContainer(Sequence midi_sequence, 
									Track[] tracks_from_sequence,
									CollectedNoteInfo all_notes_from_sequence,
									double[] rhythmic_value_notes_in_qns)
	{
		// The smallest number of MIDI ticks that can separate two notes for them to be treated as being as
		// being in two separate onset slices. Notes separated by a smaller number of ticks are merged into 
		// the same slice.
		int lookahead_ticks;

		// Calculate lookahead_ticks
		int index_of_smallest_rhythmic_value = mckay.utilities.staticlibraries.MathAndStatsMethods.getIndexOfSmallest(rhythmic_value_notes_in_qns);
		double minimum_rhythmic_value = rhythmic_value_notes_in_qns[index_of_smallest_rhythmic_value];
		if (minimum_rhythmic_value < .125) // The shortest rhythmic value to merit its own onset slice is a 32nd note
			lookahead_ticks = (int) ((int) midi_sequence.getResolution() * .125); 
		else lookahead_ticks = (int) ((int) midi_sequence.getResolution() * minimum_rhythmic_value);
		
		// Initialize the note_onset_slices, note_onset_slices_by_track_and_channel, 
		// note_onset_slices_only_new_onsets, and note_onset_slices_by_track_and_channel_only_new_onsets 
		// fields.
		note_onset_slices = new LinkedList<>();
		note_onset_slices_by_track_and_channel = new LinkedList[tracks_from_sequence.length][16];
		note_onset_slices_only_new_onsets = new LinkedList<>();
		note_onset_slices_by_track_and_channel_only_new_onsets = new LinkedList[tracks_from_sequence.length][16];
		for (int n_track = 0; n_track < tracks_from_sequence.length; n_track++)
			for (int chan = 0; chan < 16; chan++)
			{
				note_onset_slices_by_track_and_channel[n_track][chan] = new LinkedList();
				note_onset_slices_by_track_and_channel_only_new_onsets[n_track][chan] = new LinkedList();
			}

		// A working list of notes sounding on a given MIDI tick, including both notes starting on this tick
		// and notes still sounding from earlier ticks.
		LinkedList<NoteInfo> notes_sounding = new LinkedList<>(); 

		// Iterate through ticks one-by-one
		Map<Integer, List<NoteInfo>> note_tick_map = all_notes_from_sequence.getStartTickNoteMap();
		int slice = 0; // Index of note onset slice being built
		for (int tick = 0; tick < midi_sequence.getTickLength(); tick++)
		{
			// Get the list of notes starting on given tick
			List<NoteInfo> notes_starting_on_tick = note_tick_map.get(tick);
			if (notes_starting_on_tick != null)
			{
				// Check that there is at least one pitched note for which an onset slice should be created.
				boolean pitched_note_on_tick = false;
				for (NoteInfo note: notes_starting_on_tick)
					if (note.getChannel() != 10 - 1) pitched_note_on_tick = true;
				
				// Populate the slice if ther eis a pitched note
				if (pitched_note_on_tick)
				{
					// Create new onset slices
					LinkedList<Integer> onset_slice = new LinkedList<>();
					LinkedList<Integer> onset_slice_only_new_onsets = new LinkedList<>();
					for (int n_track = 0; n_track < tracks_from_sequence.length; n_track++)
						for (int chan = 0; chan < 16; chan++)
						{
							note_onset_slices_by_track_and_channel[n_track][chan].add(new LinkedList<>());
							note_onset_slices_by_track_and_channel_only_new_onsets[n_track][chan].add(new LinkedList<>());
						}

					// Remove notes no longer sounding from notes_sounding, and add notes still sounding on
					// to onset_slide and note_onset_slices_by_track_and_channel.
					if (!(notes_sounding.isEmpty()))
					{
						List<NoteInfo> to_remove = new ArrayList();
						for (NoteInfo note_sounding: notes_sounding)
						{
							if (note_sounding.getEndTick() <= tick)
								to_remove.add(note_sounding);
							else
							{
								// System.out.println("Held note: " + note_sounding.getPitch() + " on tick " + tick);
								onset_slice.add(note_sounding.getPitch());
								note_onset_slices_by_track_and_channel[note_sounding.getTrack()][note_sounding.getChannel()].get(slice).add(note_sounding.getPitch());
							}
						}
						notes_sounding.removeAll(to_remove);
					}

					// Add notes starting on the current tick to the onset slices
					for (NoteInfo note: notes_starting_on_tick)
						if (note.getChannel() != 10 - 1) // Exclude Channel 10 (percussion)
						{
							// System.out.println("New note: " + note.getPitch() + " on tick " + tick);
							onset_slice.add(note.getPitch());
							note_onset_slices_by_track_and_channel[note.getTrack()][note.getChannel()].get(slice).add(note.getPitch());
							onset_slice_only_new_onsets.add(note.getPitch());
							note_onset_slices_by_track_and_channel_only_new_onsets[note.getTrack()][note.getChannel()].get(slice).add(note.getPitch());
							notes_sounding.add(note);
						}						

					// Perform lookahead and jump to any tick inspected with a note onset
					int original_tick = tick;
					for (int i = tick + 1; i < original_tick + lookahead_ticks; i++)
					{
						List<NoteInfo> nearby_ticks = note_tick_map.get(i);
						if (nearby_ticks != null)
						{
							for (NoteInfo note: nearby_ticks)
								if (note.getChannel() != 10 - 1) // Exclude Channel 10 (percussion)
								{
									// System.out.println("Note to merge: " + note.getPitch() + " on tick " + i);
									// System.out.println("Other notes already in this onset slice: ");
									// for (int pitch: onset_slice)
									//	System.out.print(pitch + ", ");
									// Sytem.out.print("\n");
									onset_slice.add(note.getPitch());
									note_onset_slices_by_track_and_channel[note.getTrack()][note.getChannel()].get(slice).add(note.getPitch());
									onset_slice_only_new_onsets.add(note.getPitch());
									note_onset_slices_by_track_and_channel_only_new_onsets[note.getTrack()][note.getChannel()].get(slice).add(note.getPitch());
								}
							tick = i; // Jump to start tick of nearby note to avoid duplication upon next outer loop iteration
						}			
					}

					// Sort onset slices by increasing pitch
					onset_slice.sort((s1, s2) -> s1.compareTo(s2));
					onset_slice_only_new_onsets.sort((s1, s2) -> s1.compareTo(s2));
					for (int n_track = 0; n_track < tracks_from_sequence.length; n_track++)
						for (int chan = 0; chan < 16; chan++)
						{
							note_onset_slices_by_track_and_channel[n_track][chan].get(slice).sort((s1, s2) -> s1.compareTo(s2));
							note_onset_slices_by_track_and_channel_only_new_onsets[n_track][chan].get(slice).sort((s1, s2) -> s1.compareTo(s2));
						}
					
					// System.out.println("Onset slice created at tick " + tick);
					// for (Integer pitch: onset_slice)
					//	System.out.println(pitch + ", ");
					// Sytem.out.print("\n");
					
					// Add onset slices not by track and channel to their respective fields
					note_onset_slices.add(onset_slice);
					note_onset_slices_only_new_onsets.add(onset_slice_only_new_onsets);
					slice++;
				}
			}
		}
	}

	
	/* PUBLIC METHODS **************************************************************************************/
	
	
	/**
	 * @return	The list of note onset slices in the MIDI sequence.
	 */
	public LinkedList<LinkedList<Integer>> getNoteOnsetSlices()
	{
		return note_onset_slices;
	}
	
	
	/**
	 * @return	The list of note onset slices in the MIDI sequence, sorted by track and channel. 
	 */
	public LinkedList<LinkedList<Integer>>[][] getNoteOnsetSlicesByTrackAndChannel()
	{
		return note_onset_slices_by_track_and_channel;
	}
	
	
	/**
	 * @return	The list of note onset slices in the MIDI sequence containing only new note onsets.
	 */
	public LinkedList<LinkedList<Integer>> getNoteOnsetSlicesOnlyNewOnsets()
	{
		return note_onset_slices_only_new_onsets;
	}
	
	
	/**
	 * @return	The list of note onset slices in the MIDI sequence containing only new note onsets, sorted by 
	 *			track and channel. 
	 */
	public LinkedList<LinkedList<Integer>>[][] getNoteOnsetSlicesByTrackAndChannelOnlyNewOnsets()
	{
		return note_onset_slices_by_track_and_channel_only_new_onsets;
	}
	
	
	/**
	 * Check to see if the highest pitch found in the given slice, and in the given track and channel, is
	 * newly occurring in that slice, or is being sustained from a previous slice for the same track and 
	 * channel.
	 * 
	 * @param	slice_index	The index of the onset slice to check.
	 * @param	track		The track of the onset slice to check.
	 * @param	channel		The channel of the onset slice to check.
	 * @return				True if the highest pitch in the specified slice is a new in that slice, false if
	 *						it is being sustained from a previous slice.
	 */
	public boolean isHighestPitchInSliceNewOnset(int slice_index, int track, int channel)
	{
		int index_of_highest_pitch = note_onset_slices_by_track_and_channel[track][channel].get(slice_index).size() - 1;
		int highest_pitch = note_onset_slices_by_track_and_channel[track][channel].get(slice_index).get(index_of_highest_pitch);
		return 	note_onset_slices_by_track_and_channel_only_new_onsets[track][channel].get(slice_index).contains(highest_pitch);
	}
}