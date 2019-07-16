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
 * subsequent slices during which they are still sounding, and ones where they are not. There is also a 
 * melodic slice type where all notes but the highest are filtered out in each given slice in each given MIDI
 * track and channel. All of these slice types are calculated when an object of this class is instantiated.
 * 
 * There are several different ways to determine whether two notes are sufficiently close enough to fall
 * within the same note onset slice. Although for perfectly rhythmically quantized music and for reasonably
 * long note durations one need only consider notes that are exactly simultaneous when grouping notes into
 * slices, in practice notes that are effectively simultaneous are not necessarily coded as precisely
 * simultaneous, especially in MIDI files. Grouping notes that occur with a slight rhythmic offset into the
 * same onset slice is therefore necessary when accounting for MIDI files where notes meant to be simultaneous
 * are in fact time coded such that they are not (due to manual encoding or due to encoding software settings
 * meant to incorporate "natural" rubato), as well as to account for very short notes (such as grace notes)
 * that are not significant enough to merit their own slice.
 * 
 * The details of how this grouping of near-simultaneous notes is to be performed is specified in the
 * constructor of this class. To begin with, a threshold is specified indicating the fraction of a quarter
 * note that is small enough to be considered "simultaneous." There are then two different algorithms provided
 * for forming note onset slices, each of which use this threshold in different ways, and either one of which
 * may be used: 1) A new slice is started whenever a new note is encountered outside an existing slice, and a
 * look-ahead is then performed to also include in this slice notes falling within the specified threshold
 * after the first note, OR 2) A new slice is once again initiated whenever a new note is encountered outside
 * an existing slice, but the new slice is centered at or after the start of this note, at the location of the
 * closest quantized (for the entire piece) rhythmic interval indicated by the specified threshold; a
 * look-ahead and a look-behind are then both performed, each of a width specified by the given threshold, and
 * any notes falling within these intervals are merged into the slice.
 * 
 * For those versions of the onset slices that include notes held from previous slices, it is also possible to
 * specify at the constructor an additional threshold to filter out notes that are held just slightly past the
 * point at which a new slice is initiated. This is, once again, to account for encoding issues associated
 * with erroneous non-simultaneity, as discussed above.
 *
 * @author radamian and Cory McKay
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
	 * synchronization as note_onset_slices_only_new_onsets, note_onset_slices_by_track_and_channel,
	 * note_onset_slices_by_track_and_channel_only_new_onsets and
	 * note_onset_slices_by_track_and_channel_only_melodic_lines.
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
	 * slice synchronization as note_onset_slices, note_onset_slices_by_track_and_channel,
	 * note_onset_slices_by_track_and_channel_only_new_onsets and
	 * note_onset_slices_by_track_and_channel_only_melodic_lines.
	 */
	private final LinkedList<LinkedList<Integer>> note_onset_slices_only_new_onsets;
	
	/**
	 * The note onset slices for the music passed to this object at instantiation, separated out by track
	 * (first array index) and by channel (second array index). The outer list index specifies the slice (the
	 * slices are listed in temporal order), and the inner list index specifies the MIDI pitches in that slice
	 * on that track and channel, sorted from lowest pitch to highest pitch, and with duplicate pitches (in
	 * the same octave) in the same track and channel removed. Pitches occurring very close to one another
	 * rhythmically are merged into the same slice, despite not being simultaneous. Only pitched notes are
	 * included (which is to say that Channel 10 unpitched instrument notes are excluded). Each slice of this
	 * type includes not only the notes starting in it, but also notes being sustained from previous slices in
	 * the same track and channel. If a note occurs in any track and channel, a matching (but potentially
	 * empty) slice will be created for every other track and channel. The list for every track and channel
	 * has the same number of slices and the same slice synchronization as note_onset_slices,
	 * note_onset_slices_only_new_onsets, note_onset_slices_by_track_and_channel_only_new_onsets and
	 * note_onset_slices_by_track_and_channel_only_melodic_lines.
	 */
	private final LinkedList<LinkedList<Integer>>[][] note_onset_slices_by_track_and_channel;
	
	/**
	 * The note onset slices for the music passed to this object at instantiation, separated out by track
	 * (first array index) and by channel (second array index). The outer list index specifies the slice (the
	 * slices are listed in temporal order), and the inner list index specifies the MIDI pitches in that slice
	 * on that track and channel, sorted from lowest pitch to highest pitch, and with duplicate pitches (in
	 * the same octave) in the same track and channel removed. Pitches occurring very close to one another
	 * rhythmically are merged into the same slice, despite not being simultaneous. Only pitched notes are
	 * included (which is to say that Channel 10 unpitched instrument notes are excluded). Each slice of this
	 * type includes ONLY the notes starting in it, and does NOT notes being sustained from previous slices.
	 * If a note occurs in any track and channel, a matching (but potentially empty) slice will be created for
	 * every other track and channel. The list for every track and channel has the same number of slices and
	 * the same slice synchronization as note_onset_slices, note_onset_slices_only_new_onsets, 
	 * note_onset_slices_by_track_and_channel and note_onset_slices_by_track_and_channel_only_new_onsets.
	 */
	private final LinkedList<LinkedList<Integer>>[][] note_onset_slices_by_track_and_channel_only_new_onsets;
	
	/**
	 * The note onset slices for the music passed to this object at instantiation, separated out by track
	 * (first array index) and by channel (second array index). The outer list index specifies the slice (the
	 * slices are listed in temporal order), and the inner list index specifies the MIDI pitches in that slice
	 * on that track and channel (in this case, the list of pitches will always be empty or hold only one
	 * pitch). Each slice of this type includes ONLY the note presumed to belong to the melody; if multiple
	 * notes occur simultaneously in the same slice (on the same MIDI track and channel), then all notes but
	 * the highest note in the track and channel's slice are removed from the list. Also, if the highest note
	 * is sustained from one note onset slice to the next, and is still the highest note in the second slice,
	 * then this is treated as if there is no change in melody, even if lower pitches in the same track and
	 * channel change (this will result in the second slice being left empty). Pitches occurring very close to
	 * one another rhythmically are merged into the same slice, despite not being simultaneous. Only pitched
	 * notes are included (which is to say that Channel 10 unpitched instrument notes are excluded). The list
	 * for every track and channel has the same number of slices and the same slice synchronization as
	 * note_onset_slices, note_onset_slices_only_new_onsets, note_onset_slices_by_track_and_channel and
	 * note_onset_slices_by_track_and_channel_only_new_onsets.
	 */
	private final LinkedList<LinkedList<Integer>>[][] note_onset_slices_by_track_and_channel_only_melodic_lines;
	
	/**
	 * The number of channels supported by the MIDI protocol.
	 */
	private final int NUMBER_OF_MIDI_CHANNELS = 16;

	
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
	 * @param	note_simultaneity_threshold	The largest rhythmic distance, measured as a fraction of a
	 *										quarter note, that can separate two note onsets for them to be
	 *										treated as effectively simultaneous by merging them into the same
	 *										note onset slice.
	 * @param	use_free_lookahead_only		Governs which algorithm is used to break the music into note onset 
	 *										slices. If this is true, then a new slice is started whenever a
	 *										new note is encountered outside an existing slice, and then notes 
	 *										falling within the note_simultaneity_threshold after it are merged
	 *										into this slice. If this is false, then a new slice is still
	 *										initiated whenever a new note is encountered outside an existing 
	 *										slice, but the new slice is centered at or after the start of this 
	 *										note, at the location of the closest quantized (for the entire 
	 *										piece) rhythmic interval specified by note_simultaneity_threshold; 
	 *										a look-ahead and a look-behind are then both performed, each of
	 *										width note_simultaneity_threshold, and any notes falling within
	 *										these intervals are merged into the slice.
	 * @param	remove_notes_held_too_long	Whether to exclude from new slices notes held from previous slices
	 *										whose offsets are slightly past the edge of the new onset slice 
	 *										being created.
	 * @param	held_note_edge_threshold	The minimum rhythmic distance, measured as a fraction of a
	 *										quarter note, that a held note must sound from a given tick to be
	 *										included in an onset slice being created at that tick, when
	 *										remove_notes_held_too_long is true. Ignored if it is false.
	 */
	public NoteOnsetSliceContainer( Sequence midi_sequence, 
									Track[] tracks_from_sequence,
									CollectedNoteInfo all_notes_from_sequence,
									double note_simultaneity_threshold,
									boolean use_free_lookahead_only,
									boolean remove_notes_held_too_long,
									double held_note_edge_threshold )
	{
		// The number of ticks defining the rhythmic region before and/or after the start of a new slice,
		// in which notes will be merged into that slice (to account for small rhythmic offsets). Notes 
		// separated by a larger number of ticks will be placed into separate slices.
		int lookahead_or_lookbehind_ticks = (int) ((int) midi_sequence.getResolution() * note_simultaneity_threshold);
		//  System.out.println("The resolution is " + midi_sequence.getResolution() + ", the note simultaneity threshold is " + lookahead_or_lookbehind_ticks + "\n");
		// if (use_free_lookahead_only) System.out.println("Only a lookahead is performed.");
		
		// A held note whose rhythmic offset is within this value in ticks from the the tick at which a new
		// onset slice is being created will not be included in the onset slice being built, since it is 
		// assumed the note is being erroneously held too long.
		int held_note_offset_error_ticks = (int) ((int) midi_sequence.getResolution() * held_note_edge_threshold);
		// if (remove_notes_held_too_long)
		// 	System.out.println("The held_note_offset_error_ticks value is " + held_note_offset_error_ticks + "\n");
		// else
		// 	System.out.println("No held note offset correction \n");
		
		// Initialize the note_onset_slices, note_onset_slices_by_track_and_channel, 
		// note_onset_slices_only_new_onsets, note_onset_slices_by_track_and_channel_only_new_onsets, 
		// and note_onset_slices_by_track_and_channel_only_melodic_lines fields.
		note_onset_slices = new LinkedList<>();
		note_onset_slices_by_track_and_channel = new LinkedList[tracks_from_sequence.length][NUMBER_OF_MIDI_CHANNELS];
		note_onset_slices_only_new_onsets = new LinkedList<>();
		note_onset_slices_by_track_and_channel_only_new_onsets = new LinkedList[tracks_from_sequence.length][NUMBER_OF_MIDI_CHANNELS];
		note_onset_slices_by_track_and_channel_only_melodic_lines = new LinkedList[tracks_from_sequence.length][NUMBER_OF_MIDI_CHANNELS];
		for (int n_track = 0; n_track < tracks_from_sequence.length; n_track++)
			for (int chan = 0; chan < NUMBER_OF_MIDI_CHANNELS; chan++)
			{
				note_onset_slices_by_track_and_channel[n_track][chan] = new LinkedList();
				note_onset_slices_by_track_and_channel_only_new_onsets[n_track][chan] = new LinkedList();
				note_onset_slices_by_track_and_channel_only_melodic_lines[n_track][chan] = new LinkedList();
			}

		// A working list of notes sounding on a given MIDI tick, including both notes starting on this tick
		// and notes still sounding from earlier ticks.
		LinkedList<NoteInfo> notes_sounding = new LinkedList<>();
		
		// A working table of the last note encountered in each track and channel that is considered to 
		// comprise that track and channel's melody (i.e. the highest note sounding in each slice for that
		// track and channel).
		NoteInfo[][] melodic_notes = new NoteInfo[tracks_from_sequence.length][NUMBER_OF_MIDI_CHANNELS];
		for (int n_track = 0; n_track < tracks_from_sequence.length; n_track++)
			for (int chan = 0; chan < NUMBER_OF_MIDI_CHANNELS; chan++)
				melodic_notes[n_track][chan] = null;

		// A map of all notes in the piece, indexed by starting tick
		Map<Integer, List<NoteInfo>> note_tick_map = all_notes_from_sequence.getStartTickNoteMap();
		
		// Index of note onset slice being built
		int slice = 0; 
		
		// all_pitched_notes_encountered = new LinkedList<>();
		// List<NoteInfo> all_notes_captured

		// Iterate through the piece tick-by-tick
		for (int tick = 0; tick < midi_sequence.getTickLength(); tick++)
		{
			// The list of notes starting on this tick
			List<NoteInfo> notes_starting_on_tick = note_tick_map.get(tick);
			
			// If there is at least one note starting on this tick
			if (notes_starting_on_tick != null)
			{
				// Check that there is at least one pitched note for which an onset slice should be created.
				boolean pitched_note_on_tick = false;
				for (NoteInfo note: notes_starting_on_tick)
					if (note.getChannel() != 10 - 1) pitched_note_on_tick = true;
				
				// Populate the slice if there is a pitched note starting on this tick
				if (pitched_note_on_tick)
				{
					// Create new onset slices
					// System.out.println("\n\nBUILDING SLICE " + slice + "\n");
					LinkedList<Integer> onset_slice = new LinkedList<>();
					LinkedList<Integer> onset_slice_only_new_onsets = new LinkedList<>();
					for (int n_track = 0; n_track < tracks_from_sequence.length; n_track++)
						for (int chan = 0; chan < NUMBER_OF_MIDI_CHANNELS; chan++)
						{
							note_onset_slices_by_track_and_channel[n_track][chan].add(new LinkedList<>());
							note_onset_slices_by_track_and_channel_only_new_onsets[n_track][chan].add(new LinkedList<>());
							note_onset_slices_by_track_and_channel_only_melodic_lines[n_track][chan].add(new LinkedList<>());	
						}

					// Remove notes no longer sounding from notes_sounding, and add notes still sounding
					// to onset_slide and note_onset_slices_by_track_and_channel. Update melodic_notes.
					if (!(notes_sounding.isEmpty()))
					{
						// Notes to remove from notes_sounding
						List<NoteInfo> to_remove = new ArrayList();
						
						// Go through all the notes currently in notes_sounding, remove or add them as
						// appropriate
						for (NoteInfo note_sounding : notes_sounding)
						{
							// Remove a note from notes_sounding if it ended before this tick
							if (note_sounding.getEndTick() < tick)
							{
								to_remove.add(note_sounding);
								
								// Check if the ended note belongs to the melody in its track and channel,
								// remove it if so
								if (melodic_notes[note_sounding.getTrack()][note_sounding.getChannel()] == note_sounding)
									melodic_notes[note_sounding.getTrack()][note_sounding.getChannel()] = null;
							}
							
							// If the remove_notes_held_too_long option is selected, then only add those held
							// notes whose end tick is past the start of this slice by a margin of
							// held_note_offset_error_ticks.
							else if (remove_notes_held_too_long) 
							{
								if (note_sounding.getEndTick() > tick + held_note_offset_error_ticks)
								{
									int pitch = note_sounding.getPitch();
									int track = note_sounding.getTrack();
									int channel = note_sounding.getChannel();

									// Check that pitch is not a duplicate
									if (!onset_slice.contains(pitch)) 
										onset_slice.add(pitch);
									if (!note_onset_slices_by_track_and_channel[track][channel].get(slice).contains(pitch))
										note_onset_slices_by_track_and_channel[track][channel].get(slice).add(pitch);
									
									// if (!all_pitched_notes_encountered.contains(note_sounding))
									// 	all_pitched_notes_encountered.add(note_sounding);
									// System.out.println("Held note: " + mckay.utilities.sound.midi.MIDIMethods.midiPitchToPitch(note_sounding.getPitch()) + " ending on " + note_sounding.getEndTick());
								}
							}
							
							// If the remove_notes_held_too_long option is NOT selected, then add all held
							// notes ending within this slice, even if their end tick is very soon after the
							// slice's beginning. 
							else
							{
								// System.out.println("Held note: " + note_sounding.getPitch() + " on tick " + tick);
								int pitch = note_sounding.getPitch();
								int track = note_sounding.getTrack();
								int channel = note_sounding.getChannel();
							
								// Check that pitch is not a duplicate
								if (!onset_slice.contains(pitch)) 
									onset_slice.add(pitch);
								if (!note_onset_slices_by_track_and_channel[track][channel].get(slice).contains(pitch))
									note_onset_slices_by_track_and_channel[track][channel].get(slice).add(pitch);
								
								// if (!all_pitched_notes_encountered.contains(note_sounding))
								// 	all_pitched_notes_encountered.add(note_sounding);
								// System.out.println("Held note: " + mckay.utilities.sound.midi.MIDIMethods.midiPitchToPitch(note_sounding.getPitch()) + " ending on " + note_sounding.getEndTick());
							}
						}
						
						// Remove from notes_sounding notes marked as no longer sounding
						notes_sounding.removeAll(to_remove);	
					}

					// Add notes starting on the current tick to the onset slice fields (except 
					// note_onset_slices_by_track_and_channel_only_melodic_lines)
					for (NoteInfo note: notes_starting_on_tick)
						if (note.getChannel() != 10 - 1) // Exclude Channel 10 (percussion)
						{
							// System.out.println("New note: " + note.getPitch() + " on tick " + tick);
							
							int pitch = note.getPitch();
							int track = note.getTrack();
							int channel = note.getChannel();
							
							// Check that pitch is not a duplicate
							if (!onset_slice.contains(pitch)) 
								onset_slice.add(pitch);
							if (!note_onset_slices_by_track_and_channel[track][channel].get(slice).contains(pitch))
								note_onset_slices_by_track_and_channel[track][channel].get(slice).add(pitch);
							if (!onset_slice_only_new_onsets.contains(pitch))
								onset_slice_only_new_onsets.add(pitch);
							if (!note_onset_slices_by_track_and_channel_only_new_onsets[track][channel].get(slice).contains(pitch))
								note_onset_slices_by_track_and_channel_only_new_onsets[track][channel].get(slice).add(pitch);
							if (melodic_notes[track][channel] == null || pitch > melodic_notes[track][channel].getPitch())
								melodic_notes[track][channel] = note;
							notes_sounding.add(note);
							
							// if (!all_pitched_notes_encountered.contains(note))
							// 	all_pitched_notes_encountered.add(note);
						}						

					// Note the tick at which the new onset window was initiated
					int original_tick = tick;

					// Perform a look-ahead just past the onset of this new slice, and add pitches to the 
					// slice if they fall within the lookahead_ticks margin. (Do not add notes to 
					// note_onset_slices_by_track_and_channel_only_melodic_lines yet, however).
					if (use_free_lookahead_only)
					{
						for (int i = tick + 1; i < original_tick + lookahead_or_lookbehind_ticks; i++)
						{
							// All notes starting on tick i
							List<NoteInfo> notes_starting_on_this_tick = note_tick_map.get(i);
							
							// If there is at least one note starting on tick i
							if (notes_starting_on_this_tick != null)
							{
								// Add all notes starting on this tick to the slice
								for (NoteInfo note: notes_starting_on_this_tick)
									if (note.getChannel() != 10 - 1) // Exclude Channel 10 (percussion)
									{
										// System.out.println("\nNote to merge: " + note.getPitch() + " on tick " + i);
										// System.out.println("Other notes already in this onset slice: ");
										// for (int pitch: onset_slice)
										//	System.out.print(pitch + ", ");
										// System.out.print("\n");
										
										int pitch = note.getPitch();
										int track = note.getTrack();
										int channel = note.getChannel();

										// Check that pitch is not a duplicate
										if (!onset_slice.contains(pitch)) 
											onset_slice.add(pitch);
										if (!note_onset_slices_by_track_and_channel[track][channel].get(slice).contains(pitch))
											note_onset_slices_by_track_and_channel[track][channel].get(slice).add(pitch);
										if (!onset_slice_only_new_onsets.contains(pitch))
											onset_slice_only_new_onsets.add(pitch);
										if (!note_onset_slices_by_track_and_channel_only_new_onsets[track][channel].get(slice).contains(pitch))
											note_onset_slices_by_track_and_channel_only_new_onsets[track][channel].get(slice).add(pitch);
										if (melodic_notes[track][channel] == null || pitch > melodic_notes[track][channel].getPitch())
											melodic_notes[track][channel] = note;
										if (!notes_sounding.contains(note))
											notes_sounding.add(note);
										// if (!all_pitched_notes_encountered.contains(note))
										// 	all_pitched_notes_encountered.add(note);
									}
								
								// Jump to the start tick of the nearby note to avoid duplication upon next
								// outer loop iteration
								tick = i;
							}
						}
					}
					
					// Perform lookahead and lookbehind from the next tick quantized to the division specified
					// by the note_simultaneity_threshold, and add pitches to slices as encountered (except to
					// note_onset_slices_by_track_and_channel_only_melodic_lines).
					else
					{
						// The closest tick following the initialization tick of this note onset slice, which
						// can only occur on a quantized, idealized sub-beat (as defined by the 
						// note_simultaneity_threshold). In other words, the tick corresponding to the center
						// of the note onset slice is snapped forward to the tick of the closest following
						// rhythmic division.
						int idealized_subeat_tick = (int) Math.floor(tick + (lookahead_or_lookbehind_ticks - (tick % lookahead_or_lookbehind_ticks)));
						
						// Go through all ticks in the slice, doing both a look-ahead and look-behind from
						// the rhythmically quantized centerpoint of this slice
						for (int i = idealized_subeat_tick - lookahead_or_lookbehind_ticks; i < idealized_subeat_tick + lookahead_or_lookbehind_ticks; i++)
						{
							// All notes starting on tick i
							List<NoteInfo> notes_starting_on_this_tick = note_tick_map.get(i);
							
							// If there is at least one note starting on tick i
							if (notes_starting_on_this_tick != null)
							{
								// if (i < quantized_tick) System.out.println("--- Looking behind from quantized tick " + quantized_tick + ":\n");
								// if (i == quantized_tick) System.out.println("--- On quantized tick " + quantized_tick + ":\n");
								// if (i > quantized_tick) System.out.println("--- Looking ahead from quantized tick " + quantized_tick + ":\n");

								// Add all notes starting on this tick to the slice
								for (NoteInfo note: notes_starting_on_this_tick)
									if (note.getChannel() != 10 - 1) // Exclude Channel 10 (percussion)
									{
										// System.out.println("Note to merge: " 
										// 	+ mckay.utilities.sound.midi.MIDIMethods.midiPitchToPitch(note.getPitch()) + " in measure ~" 
										// 	+ (i / (midi_sequence.getResolution() * 4) + 1) + " on tick " + i);
										// System.out.println("Other notes already in this onset slice: ");
										// for (int pitch: onset_slice)
										// 	System.out.print(mckay.utilities.sound.midi.MIDIMethods.midiPitchToPitch(pitch) + ", ");
										// System.out.print("\n");

										int pitch = note.getPitch();
										int track = note.getTrack();
										int channel = note.getChannel();

										// Check that pitch is not a duplicate
										if (!onset_slice.contains(pitch))
											onset_slice.add(pitch);
										if (!note_onset_slices_by_track_and_channel[track][channel].get(slice).contains(pitch))
											note_onset_slices_by_track_and_channel[track][channel].get(slice).add(pitch);
										if (!onset_slice_only_new_onsets.contains(pitch))
											onset_slice_only_new_onsets.add(pitch);
										if (!note_onset_slices_by_track_and_channel_only_new_onsets[track][channel].get(slice).contains(pitch))
											note_onset_slices_by_track_and_channel_only_new_onsets[track][channel].get(slice).add(pitch);
										if (melodic_notes[track][channel] == null || pitch > melodic_notes[track][channel].getPitch())
											melodic_notes[track][channel] = note;
										if (!notes_sounding.contains(note))
											notes_sounding.add(note);
										// if (!all_pitched_notes_encountered.contains(note))
										// 	all_pitched_notes_encountered.add(note);
									}

								// Jump to the start tick of the nearby note to avoid duplication upon 
								// next outer loop iteration.
								if (i > original_tick) tick = i;
							}
						}
					}
					
					// Add melody pitches to note_onset_slices_by_track_and_channel_only_melodic_lines and
					// note_onset_slices_by_channel_only_melodic_lines
					for (int n_track = 0; n_track < tracks_from_sequence.length; n_track++)
						for (int chan = 0; chan < NUMBER_OF_MIDI_CHANNELS; chan++)
						{
							NoteInfo melody_note = melodic_notes[n_track][chan];
							if (melody_note != null)
								// Check if the onset of the note belonging to the melody is between the
								// start and end of this particular slice (i.e. exclude notes held from 
								// previous slices)
								if (melody_note.getStartTick() >= original_tick && melody_note.getStartTick() <= tick) 
									note_onset_slices_by_track_and_channel_only_melodic_lines[n_track][chan].get(slice).add(melody_note.getPitch());
						}
					
					// Sort pitches in each slices to be in order of increasing pitch
					onset_slice.sort((s1, s2) -> s1.compareTo(s2));
					onset_slice_only_new_onsets.sort((s1, s2) -> s1.compareTo(s2));
					for (int n_track = 0; n_track < tracks_from_sequence.length; n_track++)
						for (int chan = 0; chan < NUMBER_OF_MIDI_CHANNELS; chan++)
						{
							note_onset_slices_by_track_and_channel[n_track][chan].get(slice).sort((s1, s2) -> s1.compareTo(s2));
							note_onset_slices_by_track_and_channel_only_new_onsets[n_track][chan].get(slice).sort((s1, s2) -> s1.compareTo(s2));
						}
					
//					 System.out.println("Onset slice created at tick " + tick);
//					 for (Integer pitch: onset_slice)
//						System.out.println(pitch + ", ");
//					 System.out.print("\n");
					
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
	 * Returns the note onset slices for the music passed to this object at instantiation, listed in the order 
	 * that the slices occur temporally. The outer list index specifies the slice, and the inner list index
	 * specifies the MIDI pitches in that slice, sorted from lowest pitch to highest pitch, and with duplicate
	 * pitches (in the same octave) removed. Pitches occurring very close to one another rhythmically are
	 * merged into the same slice, despite not being simultaneous. Only pitched notes are included (which is
	 * to say that Channel 10 unpitched instrument notes are excluded). Each slice of this type includes not
	 * only the notes starting in it, but also notes being sustained from previous slices. Notes from all MIDI
	 * tracks and channels are grouped together.  This list has the same number of slices and the same slice
	 * synchronization as the other slice data structures returned by the methods of this class.
	 * 
	 * @return	The list of note onset slices in the MIDI parsed by this object at instantiation.
	 */
	public LinkedList<LinkedList<Integer>> getNoteOnsetSlices()
	{
		return note_onset_slices;
	}
	
	
	/**
	 * Returns the note onset slices for the music passed to this object at instantiation, listed in the order 
	 * that the slices occur temporally. The outer list index specifies the slice, and the inner list index
	 * specifies the MIDI pitches in that slice, sorted from lowest pitch to highest pitch, and with duplicate
	 * pitches (in the same octave) removed. Pitches occurring very close to one another rhythmically are
	 * merged into the same slice, despite not being simultaneous. Only pitched notes are included (which is
	 * to say that Channel 10 unpitched instrument notes are excluded). Each slice of this type includes ONLY
	 * the notes starting in that slice, and does NOT include notes sustained from previous slices. Notes from
	 * all MIDI tracks and channels are grouped together. This list has the same number of slices and the same
	 * slice synchronization as the other slice data structures returned by the methods of this class.
	 * 
	 * @return	The list of note onset slices in the MIDI parsed by this object at instantiation. Only new 
	 *			note onsets are included (notes sustained from previous onset slices are excluded).
	 */
	public LinkedList<LinkedList<Integer>> getNoteOnsetSlicesOnlyNewOnsets()
	{
		return note_onset_slices_only_new_onsets;
	}
	
	
	/**
	 * Returns the note onset slices for the music passed to this object at instantiation, separated out by 
	 * track (first array index) and by channel (second array index). The outer list index specifies the slice 
	 * (the slices are listed in temporal order), and the inner list index specifies the MIDI pitches in that 
	 * slice on that track and channel, sorted from lowest pitch to highest pitch, and with duplicate pitches 
	 * (in the same octave) in the same track and channel removed. Pitches occurring very close to one another
	 * rhythmically are merged into the same slice, despite not being simultaneous. Only pitched notes are
	 * included (which is to say that Channel 10 unpitched instrument notes are excluded). Each slice of this
	 * type includes not only the notes starting in it, but also notes being sustained from previous slices in
	 * the same track and channel. If a note occurs in any track and channel, a matching (but potentially
	 * empty) slice will be created for every other track and channel. This list has the same number of slices
	 * and the same slice synchronization as the other slice data structures returned by the methods of this 
	 * class.
	 * 
	 * @return	The list of note onset slices in the MIDI parsed by this object at instantiation, segregated
	 *			by track and channel. 
	 */
	public LinkedList<LinkedList<Integer>>[][] getNoteOnsetSlicesByTrackAndChannel()
	{
		return note_onset_slices_by_track_and_channel;
	}
	
	
	/**
	 * Returns the note onset slices for the music passed to this object at instantiation, separated out by 
	 * track (first array index) and by channel (second array index). The outer list index specifies the slice 
	 * (the slices are listed in temporal order), and the inner list index specifies the MIDI pitches in that 
	 * slice on that track and channel, sorted from lowest pitch to highest pitch, and with duplicate pitches 
	 * (in the same octave) in the same track and channel removed. Pitches occurring very close to one another
	 * rhythmically are merged into the same slice, despite not being simultaneous. Only pitched notes are
	 * included (which is to say that Channel 10 unpitched instrument notes are excluded). Each slice of this
	 * type includes ONLY the notes starting in it, and does NOT notes being sustained from previous slices.
	 * If a note occurs in any track and channel, a matching (but potentially empty) slice will be created for
	 * every other track and channel. This list has the same number of slices and the same slice 
	 * synchronization as the other slice data structures returned by the methods of this class.
	 * 
	 * @return	The list of note onset slices in the MIDI parsed by this object at instantiation, segregated
	 *			by track and channel. Only new note onsets are included (notes sustained from previous onset
	 *			slices are excluded).
	 */
	public LinkedList<LinkedList<Integer>>[][] getNoteOnsetSlicesByTrackAndChannelOnlyNewOnsets()
	{
		return note_onset_slices_by_track_and_channel_only_new_onsets;
	}

	
	/**
	 * Returns note onset slices for the music passed to this object at instantiation, separated out by track
	 * (first array index) and by channel (second array index). The outer list index specifies the slice (the
	 * slices are listed in temporal order), and the inner list index specifies the MIDI pitches in that slice
	 * on that track and channel (in this case, the list of pitches will always be empty or hold only one
	 * pitch). Each slice of this type includes ONLY the note presumed to belong to the melody; if multiple
	 * notes occur simultaneously in the same slice (on the same MIDI track and channel), then all notes but
	 * the highest note in the track and channel's slice are removed from the list. Also, if the highest note
	 * is sustained from one note onset slice to the next, and is still the highest note in the second slice,
	 * then this is treated as if there is no change in melody, even if lower pitches in the same track and
	 * channel change (this will result in the second slice being left empty). Pitches occurring very close to
	 * one another rhythmically are merged into the same slice, despite not being simultaneous. Only pitched
	 * notes are included (which is to say that Channel 10 unpitched instrument notes are excluded). This list
	 * has the same number of slices and the same slice synchronization as the other slice data structures
	 * returned by the methods of this class.
	 * 
	 * @return	The list of note onset slices in the MIDI parsed by this object at instantiation, segregated
	 *			by track and channel. Only melodic notes are included, so thre will only be no or one note
	 *			per slice.
	 */
	public LinkedList<LinkedList<Integer>>[][] getNoteOnsetSlicesByTrackAndChannelMelodicLinesOnly()
	{
		return note_onset_slices_by_track_and_channel_only_melodic_lines;
	}
	
	
	/**
	 * Returns the note onset slices for the music passed to this object at instantiation, listed in the order
	 * that the slices occur temporally, where each list of MIDI pitches is converted to a list of MIDI pitch
	 * classes. The outer list index specifies the slice, and the inner list index specifies the pitch
	 * classes in that slice, sorted from lowest pitch class to highest pitch class, and with duplicate pitch
	 * classes removed. Pitches occurring very close to one another rhythmically are merged into the same
	 * slice, despite not being simultaneous. Only pitched notes are included (which is to say that Channel 10
	 * unpitched instrument notes are excluded). Each slice of this type includes not only the notes starting
	 * in it, but also notes being sustained from previous slices. Notes from all MIDI tracks and channels are
	 * grouped together. This list has the same number of slices and the same slice synchronization as
	 * the other slice data structures returned by the methods of this class.
	 * 
	 * @return	The list of note onset slices in the MIDI parsed by this object at instantiation. Values
	 *			indicate unique pitch class rather than MIDI pitch (a value of 0 corresponds to C, 1 to C#/Db,
	 *			etc.).
	 */
	public LinkedList<LinkedList<Integer>> getNoteOnsetSlicesInPitchClasses()
	{
		LinkedList<LinkedList<Integer>> result = new LinkedList<>();
		
		for (int slice = 0; slice < note_onset_slices.size(); slice++)
		{
			LinkedList<Integer> onset_slice = new LinkedList<>();
			result.add(onset_slice);
			for (int i = 0; i < note_onset_slices.get(slice).size(); i++)
			{
				int pitch_class = note_onset_slices.get(slice).get(i) % 12;
				if (!onset_slice.contains(pitch_class)) // Check pitch class added is not a duplicate
					onset_slice.add(pitch_class);
			}
			
			// Sort slice by pitch class
			onset_slice.sort((s1, s2) -> s1.compareTo(s2));
		}
		
		return result;
	}

	
	/**
	 * Returns the note onset slices for the music passed to this object at instantiation, listed in the order
	 * that the slices occur temporally, where each list of MIDI pitches is converted to a list of MIDI pitch
	 * classes. The outer list index specifies the slice, and the inner list index specifies the pitch classes
	 * in that slice, sorted from lowest pitch class to highest pitch class, and with duplicate pitch classes
	 * removed. Pitches occurring very close to one another rhythmically are merged into the same slice,
	 * despite not being simultaneous. Only pitched notes are included (which is to say that Channel 10
	 * unpitched instrument notes are excluded). Each slice of this type includes ONLY the notes starting in
	 * that slice, and does NOT include notes sustained from previous slices. Notes from all MIDI tracks and
	 * channels are grouped together. This list has the same number of slices and the same slice
	 * synchronization as the other slice data structures returned by the methods of this class.
	 * 
	 * @return	The list of note onset slices in the MIDI parsed by this object at instantiation. Only new 
	 *			note onsets are included (notes sustained from previous onset slices are excluded). Values
	 *			indicate unique pitch class rather than MIDI pitch (a value of 0 corresponds to C, 1 to C#/Db,
	 *			etc.).
	 */
	public LinkedList<LinkedList<Integer>> getNoteOnsetSlicesOnlyNewOnsetsInPitchClasses()
	{
		LinkedList<LinkedList<Integer>> result = new LinkedList<>();
		
		for (int slice = 0; slice < note_onset_slices_only_new_onsets.size(); slice++)
		{
			LinkedList<Integer> onset_slice = new LinkedList<>();
			result.add(onset_slice);
			for (int i = 0; i < note_onset_slices_only_new_onsets.get(slice).size(); i++)
			{
				int pitch_class = note_onset_slices_only_new_onsets.get(slice).get(i) % 12;
				if (!onset_slice.contains(pitch_class)) // Check pitch class added is not a duplicate
					onset_slice.add(pitch_class);
			}
			
			// Sort slice by pitch class
			onset_slice.sort((s1, s2) -> s1.compareTo(s2));
		}
		
		return result;
	}

	
	/**
	 * Returns the note onset slices for the music passed to this object at instantiation, separated out by
	 * track (first array index) and by channel (second array index), where each list of MIDI pitches is
	 * converted to a list of pitch classes. The outer list index specifies the slice (the slices are listed
	 * in temporal order), and the inner list index specifies the pitch classes in that slice on that track
	 * and channel, sorted from lowest pitch class to highest pitch class, and with duplicate pitch classes in
	 * the same track and channel removed. Pitches occurring very close to one another rhythmically are merged
	 * into the same slice, despite not being simultaneous. Only pitched notes are included (which is to say
	 * that Channel 10 unpitched instrument notes are excluded). Each slice of this type includes not only the
	 * notes starting in it, but also notes being sustained from previous slices in the same track and
	 * channel. If a note occurs in any track and channel, a matching (but potentially empty) slice will be
	 * created for every other track and channel. This list has the same number of slices and the same slice
	 * synchronization as the other slice data structures returned by the methods of this class.
	 * 
	 * @return	The list of note onset slices in the MIDI parsed by this object at instantiation, segregated
	 *			by track and channel. Values indicate unique pitch class rather than MIDI pitch (a value of 0
	 *			corresponds to C, 1 to C#/Db, etc.). 
	 */
	public LinkedList<LinkedList<Integer>>[][] getNoteOnsetSlicesByTrackAndChannelInPitchClasses()
	{
		LinkedList<LinkedList<Integer>>[][] result = new LinkedList[note_onset_slices_by_track_and_channel.length][NUMBER_OF_MIDI_CHANNELS];
		
		for (int n_track = 0; n_track < note_onset_slices_by_track_and_channel.length; n_track++)
			for (int chan = 0; chan < NUMBER_OF_MIDI_CHANNELS; chan++)
			{
				result[n_track][chan] = new LinkedList<>();
				for (int slice = 0; slice < note_onset_slices_by_track_and_channel[n_track][chan].size(); slice++)
				{
					LinkedList<Integer> onset_slice = new LinkedList<>();
					result[n_track][chan].add(onset_slice);
					for (int i = 0; i < note_onset_slices_by_track_and_channel[n_track][chan].get(slice).size(); i++)
					{
						int pitch_class = note_onset_slices_by_track_and_channel[n_track][chan].get(slice).get(i) % 12;
						if (!onset_slice.contains(pitch_class)) // Check pitch class added is not a duplicate
							onset_slice.add(pitch_class);
					}
					
					// Sort slice by pitch class
					onset_slice.sort((s1, s2) -> s1.compareTo(s2));
				}
			}	
		
		return result;
	}

	
	/**
	 * Returns the note onset slices for the music passed to this object at instantiation, separated out by
	 * track (first array index) and by channel (second array index), where each list of MIDI pitches is
	 * converted to a list of pitch classes. The outer list index specifies the slice (the slices are listed
	 * in temporal order), and the inner list index specifies the pitch classes in that slice on that track
	 * and channel, sorted from lowest pitch class to highest pitch class, and with duplicate pitch classes in
	 * the same track and channel removed. Pitches occurring very close to one another rhythmically are merged
	 * into the same slice, despite not being simultaneous. Only pitched notes are included (which is to say
	 * that Channel 10 unpitched instrument notes are excluded). Each slice of this type includes ONLY the
	 * notes starting in it, and does NOT notes being sustained from previous slices. If a note occurs in any
	 * track and channel, a matching (but potentially empty) slice will be created for every other track and
	 * channel. This list has the same number of slices and the same slice synchronization as the other slice
	 * data structures returned by the methods of this class.
	 * 
	 * @return	The list of note onset slices in the MIDI parsed by this object at instantiation, segregated
	 *			by track and channel. Only new note onsets are included (notes sustained from previous onset
	 *			slices are excluded). Values indicate unique pitch class rather than MIDI pitch (a value of 0
	 *			corresponds to C, 1 to C#/Db, etc.). 
	 */
	public LinkedList<LinkedList<Integer>>[][] getNoteOnsetSlicesByTrackAndChannelOnlyNewOnsetsInPitchClasses()
	{
		LinkedList<LinkedList<Integer>>[][] result = new LinkedList[note_onset_slices_by_track_and_channel_only_new_onsets.length][NUMBER_OF_MIDI_CHANNELS];
		
		for (int n_track = 0; n_track < note_onset_slices_by_track_and_channel_only_new_onsets.length; n_track++)
			for (int chan = 0; chan < NUMBER_OF_MIDI_CHANNELS; chan++)
			{
				result[n_track][chan] = new LinkedList<>();
				for (int slice = 0; slice < note_onset_slices_by_track_and_channel_only_new_onsets[n_track][chan].size(); slice++)
				{
					LinkedList<Integer> onset_slice = new LinkedList<>();
					result[n_track][chan].add(onset_slice);
					for (int i = 0; i < note_onset_slices_by_track_and_channel_only_new_onsets[n_track][chan].get(slice).size(); i++)
					{
						int pitch_class = note_onset_slices_by_track_and_channel_only_new_onsets[n_track][chan].get(slice).get(i) % 12;
						if (!onset_slice.contains(pitch_class)) // Check pitch class added is not a duplicate
							onset_slice.add(pitch_class);
					}
					
					// Sort slice by pitch class
					onset_slice.sort((s1, s2) -> s1.compareTo(s2));
				}
			}	
		
		return result;
	}
	
	
//	public List<NoteInfo> getAllPitchedNotesEncountered()
//	{
//		return all_pitched_notes_encountered;
//	}
	
	
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
		if (note_onset_slices_by_track_and_channel[track][channel].get(slice_index).isEmpty())
		{
			return false;
		}
		else
		{
			int index_of_highest_pitch = note_onset_slices_by_track_and_channel[track][channel].get(slice_index).size() - 1;
			int highest_pitch = note_onset_slices_by_track_and_channel[track][channel].get(slice_index).get(index_of_highest_pitch);
			return 	note_onset_slices_by_track_and_channel_only_new_onsets[track][channel].get(slice_index).contains(highest_pitch);
		}
	}
}