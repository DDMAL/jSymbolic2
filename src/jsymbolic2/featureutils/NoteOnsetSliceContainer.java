package jsymbolic2.featureutils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import mckay.utilities.staticlibraries.ArrayMethods;
import mckay.utilities.staticlibraries.MathAndStatsMethods;

/**
 * A specialized class that contains the data structures representing the note onset slices of a piece, and 
 * the processing for building them. There is a data structure for all note onset slices, note onset 
 * slices divided into track and channel, and versions of each containing exclusively pitches of new note 
 * onsets. These structures are created by iterating on the ticks of a sequence. An onset slice is represented 
 * by a list of pitches, in increasing order, created each time a pitched note is encountered. It includes the 
 * pitches of all notes encountered at that point, as well as those that are still sounding at that time. A 
 * lookahead value (in ticks) is calculated to include slightly desynchronized notes (e.g. if a MIDI file is a 
 * transcription of a human performance) in the proper onset slice. The minimum lookahead value is the 
 * duration of a thirty-second note in ticks.
 * 
 * An object of this class is instantiated with a MIDI sequence, array of tracks, the information 
 * associated with all notes in the piece, and the feature value rhythmic_value_of_each_note_in_quarter_notes. 
 * These parameters are generated in MIDIIntermediateRepresentations. The private method 
 * GenerateNoteOnsetSliceLists() is used to build the lists representing all note onset slices, and the public 
 * methods are used to more easily access the specific information required by feature calculations having to
 * do with melodic and vertical intervals.
 * 
 * @author radamian
 */
public class NoteOnsetSliceContainer 
{
	/* PUBLIC FIELDS ****************************************************************************************/
	
	
	/**
	 * Information on the pitch, start tick, end tick, track and channel of every note (including Channel 10
	 * unpitched notes). The notes can be accessed simply as a List of notes, or a query can be made (using
	 * the getNotesStartingOnTick method) to find all notes starting on a particular MIDI tick. Additional
	 * information is also available, such as a list of all notes on a particular channel (via the
	 * getNotesOnChannel method).
	 */
	public CollectedNoteInfo all_notes;
	
	/**
	 * A list of lists, where each entry of the root list represents a note onset slice and each inner list 
	 * contains the pitches of the notes in that slice, in increasing order. This list has the same number of 
	 * slices and the same rhythmic synchronizations as note_onset_slices_by_track_and_channel.
	 */
	public LinkedList<LinkedList<Integer>> note_onset_slices;
	
	/**
	 * A table of lists of lists, where The first index partitions the note onset slice by tracks in which the 
	 * note occurred, and the second partitions it into channels. Each entry in the table is a list of lists, 
	 * each inner list representing a note onset slice for a particular track and channel, containing the 
	 * pitches of that slice in increasing order. This list has the same number of slices and the same 
	 * rhythmic synchronizations as note_onset_slices. An onset slice is created for all tracks and channels 
	 * whenever a note is encountered. If there is no note on a particular track and channel when an onset 
	 * slice is created, then the entry for that track and channel is an empty list. 
	 */
	public LinkedList<LinkedList<Integer>>[][] note_onset_slices_by_track_and_channel;
	
	/**
	 * A list of lists, where each entry of the root list represents a note onset slice containing only notes 
	 * with new onsets at that rhythmic synchronization. Each inner list contains the pitches of the notes in 
	 * that slice, in increasing order. An onset slice is created each time a pitched note is encountered. It 
	 * includes notes that are still sounding. This list has the same number of slices  and the same rhythmic 
	 * synchronizations as note_onset_slices_by_track_and_channel_only_new_onsets.
	 */
	public LinkedList<LinkedList<Integer>> note_onset_slices_only_new_onsets;
	
	/**
	 * A table of lists of lists, where The first index partitions the note onset slice by tracks in which the 
	 * note occurred, and the second partitions it into channels. Each entry in this table is a list of lists, 
	 * each inner list representing a note onset slice for a particular track and channel, containing the 
	 * pitches of that slice in increasing order. This list has the same number of slices and the same 
	 * rhythmic synchronizations as note_onset_slices_only_new_onsets. An onset slice is created for all 
	 * tracks and channels whenever a note is encountered. If there is no note on a particular track and 
	 * channel when an onset slice is created, then the entry for that track and channel is an empty list. 
	 */
	public LinkedList<LinkedList<Integer>>[][] note_onset_slices_by_track_and_channel_only_new_onsets;

	
	/* PRIVATE FIELDS ***************************************************************************************/

	
	/**
	 * The MIDI sequence which the data used by this object comes from.
	 */
	private final Sequence sequence;

	/**
	 * All the MIDI tracks loaded from the MIDI sequence.
	 */
	private final Track[] tracks;
	
	/**
	 * The lookahead value in ticks. A note whose onset is on a tick within this value from the current tick
	 * is considered to be a desynchronized note (e.g. if a MIDI file is a transcription of a human 
	 * performance), and will be included in the onset slice being built on the rhythmic synchronization at 
	 * the current tick. The shortest rhythmic value that will merit its own onset slice is a 32nd note.
	 */
	private final int LOOKAHEAD_IN_TICKS;
	
	
	/* CONSTRUCTOR ******************************************************************************************/
	
	
	/**
	 * Parse the specified MIDI sequence and fill this object's fields with the appropriate information
	 * extracted from the sequence.
	 *
	 * @param	midi_sequence			The MIDI sequence to extract information from.
	 * @param	tracks_from_sequence	An array of tracks from the MIDI sequence, already parsed in
	 *									MIDIIntermediateRepresentations.
	 * @param	all_notes_from_sequence	Information on the pitch, start tick, end tick, track and channel of 
	 *									every note (including Channel 10 unpitched notes).
	 * @param	rhythmic_value_of_each_note_in_quarter_notes
	 *									An array with one entry for each note, where the value of each entry 
	 *									indicates the quantized duration of the note in quarter notes (e.g. 
	 *									a value of 0.5 corresponds to a duration of an eighth note). This
	 *									array is generated in MIDIIntermediateRepresentations and is used
	 *									to calculate the lookahead value.
	 */
	public NoteOnsetSliceContainer(Sequence midi_sequence, 
									Track[] tracks_from_sequence,
									CollectedNoteInfo all_notes_from_sequence,
									double[] rhythmic_value_of_each_note_in_quarter_notes)
	{
		sequence = midi_sequence;
		tracks = tracks_from_sequence;
		all_notes = all_notes_from_sequence;
		
		// Calculate lookahead value
		int index_of_smallest = mckay.utilities.staticlibraries.MathAndStatsMethods.getIndexOfSmallest(rhythmic_value_of_each_note_in_quarter_notes);
		double minimum_rhythmic_value = rhythmic_value_of_each_note_in_quarter_notes[index_of_smallest];
		if (minimum_rhythmic_value < .125) // The shortest rhythmic value to merit its own onset slice is a 32nd note
			LOOKAHEAD_IN_TICKS = (int) ((int) sequence.getResolution() * .125); 
		else LOOKAHEAD_IN_TICKS = (int) ((int) sequence.getResolution() * minimum_rhythmic_value);
		
		generateNoteOnsetSliceLists();
	}
	
	
	/* PRIVATE METHODS **************************************************************************************/
	
	
	/**
	* Generate the note_onset_slices, note_onset_slices_by_track_and_channel, 
	* note_onset_slices_only_new_onsets, and note_onset_slices_by_track_and_channel_only_new_onsets fields.
	*/
	private void generateNoteOnsetSliceLists() 
	{
		// Initialize the note_onset_slices, note_onset_slices_by_track_and_channel, 
		// note_onset_slices_only_new_onsets, and note_onset_slices_by_track_and_channel_only_new_onsets 
		// fields.
		note_onset_slices = new LinkedList<>();
		note_onset_slices_by_track_and_channel = new LinkedList[tracks.length][16];
		note_onset_slices_only_new_onsets = new LinkedList<>();
		note_onset_slices_by_track_and_channel_only_new_onsets = new LinkedList[tracks.length][16];
		for (int n_track = 0; n_track < tracks.length; n_track++)
			for (int chan = 0; chan < 16; chan++)
			{
				note_onset_slices_by_track_and_channel[n_track][chan] = new LinkedList();
				note_onset_slices_by_track_and_channel_only_new_onsets[n_track][chan] = new LinkedList();
			}

		// Iterate through ticks
		Map<Integer, List<NoteInfo>> note_tick_map = all_notes.getStartTickNoteMap();
		LinkedList<NoteInfo> notes_sounding = new LinkedList<>(); // A working list of notes still sounding
		int slice = 0; // Index of note onset slice being built
		for (int tick = 0; tick < sequence.getTickLength(); tick++)
		{
			// Get the list of notes starting on given tick
			List<NoteInfo> notes_starting_on_tick = note_tick_map.get(tick);
			if (notes_starting_on_tick != null)
			{
				// Check that there is at least one pitched note for which an onset slice should be created.
				boolean pitched_note_on_tick = false;
				for (NoteInfo note: notes_starting_on_tick)
					if (note.getChannel() != 10 - 1) pitched_note_on_tick = true;
				
				if (pitched_note_on_tick)
				{
					// Create new onset slices
					LinkedList<Integer> onset_slice = new LinkedList<>();
					LinkedList<Integer> onset_slice_only_new_onsets = new LinkedList<>();
					for (int n_track = 0; n_track < tracks.length; n_track++)
						for (int chan = 0; chan < 16; chan++)
						{
							note_onset_slices_by_track_and_channel[n_track][chan].add(new LinkedList<>());
							note_onset_slices_by_track_and_channel_only_new_onsets[n_track][chan].add(new LinkedList<>());
						}

					// Add notes that are still sounding to the onset slices, update list of notes still sounding
					if (!(notes_sounding.isEmpty()))
					{
						List<NoteInfo> to_remove = new ArrayList();
						for (NoteInfo note_sounding: notes_sounding)
						{
							if (note_sounding.getEndTick() <= tick)
								to_remove.add(note_sounding);
							else
							{
//								System.out.println("Held note: " + note_sounding.getPitch());
								onset_slice.add(note_sounding.getPitch());
								note_onset_slices_by_track_and_channel[note_sounding.getTrack()][note_sounding.getChannel()].get(slice).add(note_sounding.getPitch());
							}
						}
						notes_sounding.removeAll(to_remove);
					}

					// Add notes starting on given tick to the onset slices
					for (NoteInfo note: notes_starting_on_tick)
						if (note.getChannel() != 10 - 1) // Exclude Channel 10 (percussion)
						{
//							System.out.println("New note: " + note.getPitch());
							onset_slice.add(note.getPitch());
							note_onset_slices_by_track_and_channel[note.getTrack()][note.getChannel()].get(slice).add(note.getPitch());
							onset_slice_only_new_onsets.add(note.getPitch());
							note_onset_slices_by_track_and_channel_only_new_onsets[note.getTrack()][note.getChannel()].get(slice).add(note.getPitch());
							notes_sounding.add(note);
						}						

					// Perform lookahead and jump to any tick inspected with a note onset
					for (int i = tick + 1; i < tick + LOOKAHEAD_IN_TICKS; i++)
					{
						List<NoteInfo> nearby_ticks = note_tick_map.get(i);
						if (nearby_ticks != null)
						{
							for (NoteInfo note: nearby_ticks)
								if (note.getChannel() != 10 - 1) // Exclude Channel 10 (percussion)
								{
//									System.out.println("Desynchronized note: " + note.getPitch() + " at tick " + i);
//									System.out.println("Other notes in this onset slice: ");
//									for (int pitch: onset_slice)
//										System.out.print(pitch + ", ");
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
					for (int n_track = 0; n_track < tracks.length; n_track++)
						for (int chan = 0; chan < 16; chan++)
						{
							note_onset_slices_by_track_and_channel[n_track][chan].get(slice).sort((s1, s2) -> s1.compareTo(s2));
							note_onset_slices_by_track_and_channel_only_new_onsets[n_track][chan].get(slice).sort((s1, s2) -> s1.compareTo(s2));
						}
					
//					System.out.println("Onset slice created at tick " + tick);
//					for (Integer pitch: onset_slice)
//						System.out.println(pitch + ", ");
					
					// Add onset slices not by track and channel to respective fields
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
	 * @param	slice_index	The index of the onset slice
	 * @param	track		The track of the onset slice
	 * @param	channel		The channel of the onset slice
	 * @return	A boolean indicating if the highest note (that assumed to belong to a melodic line) in the
	 *			specified onset slice of the specified track and channel is a new onset, rather than a note
	 *			held over from a previous onset slice.
	 */
	public boolean isNewOnset(int slice_index, int track, int channel)
	{
		int index_of_highest_pitch = note_onset_slices_by_track_and_channel[track][channel].get(slice_index).size() - 1;
		int pitch = note_onset_slices_by_track_and_channel[track][channel].get(slice_index).get(index_of_highest_pitch);
		return 	note_onset_slices_by_track_and_channel_only_new_onsets[track][channel].get(slice_index).contains(pitch);
	}
}
