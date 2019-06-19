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
 * track and channel.
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
	 * @param	lookahead_only				Governs which algorithm is used to break the music into note onset 
	 *										slices. If this is true, then a new slice is started whenever a
	 *										note is encountered outside an existing slice, and then notes 
	 *										falling in the note_simultaneity_threshold after it are merged
	 *										into its slice. If this is false, then perform both a look-ahead
	 *										and a look-behind.
	 * @param	remove_notes_held_too_long	Whether to exclude held notes whose offsets are slightly past the 
	 *										current tick from the onset slice being created.
	 * @param	offset_error_margin			The minimum rhythmic distance, measured as a fraction of a
	 *										quarter note, that a held note must sound from a given tick to be
	 *										included in the onset slice being created at that tick, when
	 *										remove_notes_held_too_long is true.
	 */
	public NoteOnsetSliceContainer( Sequence midi_sequence, 
									Track[] tracks_from_sequence,
									CollectedNoteInfo all_notes_from_sequence,
									double note_simultaneity_threshold,
									boolean lookahead_only,
									boolean remove_notes_held_too_long,
									double offset_error_margin)
	{
		// The smallest number of MIDI ticks that can separate two note onsets for them to be treated as
		// occurring in two separate onset slices. Notes separated by a smaller number of ticks are merged 
		// into the same slice.
		int lookahead_ticks = (int) ((int) midi_sequence.getResolution() * note_simultaneity_threshold);
		// System.out.println("The resolution is " + midi_sequence.getResolution() + ", the note simultaneity threshold is " + lookahead_ticks + "\n");
		
		// Held notes whose offsets are within this value in ticks from the current tick will not be included
		// in the onset slice being built, for being erroneously too long.
		int offset_error_ticks = (int) ((int) midi_sequence.getResolution() * offset_error_margin);
		// if (remove_notes_held_too_long)
		// 	System.out.println("The offset_error_ticks value is " + offset_error_ticks + "\n");
		// else
		// 	System.out.println("No held note offset correction \n");
		
		// Initialize the note_onset_slices, note_onset_slices_by_track_and_channel, 
		// note_onset_slices_only_new_onsets, and note_onset_slices_by_track_and_channel_only_new_onsets 
		// fields.
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

		// Iterate through ticks one-by-one
		Map<Integer, List<NoteInfo>> note_tick_map = all_notes_from_sequence.getStartTickNoteMap();
		int slice = 0; // Index of note onset slice being built
		// all_pitched_notes_encountered = new LinkedList<>();
		// List<NoteInfo> all_notes_captured
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
				
				// Populate the slice if there is a pitched note
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
						List<NoteInfo> to_remove = new ArrayList();
						for (NoteInfo note_sounding : notes_sounding)
						{
							if (note_sounding.getEndTick() <= tick)
							{
								to_remove.add(note_sounding);
								
								// Check if the ended note belongs to the melody in its track and channel,
								// remove it if so
								if (melodic_notes[note_sounding.getTrack()][note_sounding.getChannel()] == note_sounding)
									melodic_notes[note_sounding.getTrack()][note_sounding.getChannel()] = null;
							}
							else if (remove_notes_held_too_long) 
							{
								// Notes whose end ticks are within the offset_error_ticks value of the
								// current tick are considered to be erroneously held too long, and are not 
								// included in the onset slice being constructed.
								if (note_sounding.getEndTick() > tick + offset_error_ticks)
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
						notes_sounding.removeAll(to_remove);	
					}

					// Add notes starting on the current tick to the onset slices (except 
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

					// Perform look-ahead and jump to any tick within range that contains a note onset, and
					// add pitches to sices as encountered 
					// (exept to note_onset_slices_by_track_and_channel_only_melodic_lines)
					if (lookahead_only)
					{
						for (int i = tick + 1; i < original_tick + lookahead_ticks; i++)
						{
							List<NoteInfo> nearby_ticks = note_tick_map.get(i);
							if (nearby_ticks != null)
							{
								for (NoteInfo note: nearby_ticks)
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
					// note_onset_slices_by_track_and_channel_only_melodic_lines)
					else
					{
						int quantized_tick = (int) Math.floor(tick + (lookahead_ticks - (tick % lookahead_ticks)));
						for (int i = quantized_tick - lookahead_ticks; i < quantized_tick + lookahead_ticks; i++)
						{
							List<NoteInfo> nearby_ticks = note_tick_map.get(i);
							if (nearby_ticks != null)
							{
								// Check that there is at least one pitched note for which an onset slice should be created.
								boolean pitched_note_on_nearby_tick = false;
								for (NoteInfo note: notes_starting_on_tick)
									if (note.getChannel() != 10 - 1) pitched_note_on_nearby_tick = true;

								if (pitched_note_on_nearby_tick)
								{
									// if (i < quantized_tick) System.out.println("--- Looking behind from quantized tick " + quantized_tick + ":\n");
									// if (i == quantized_tick) System.out.println("--- On quantized tick " + quantized_tick + ":\n");
									// if (i > quantized_tick) System.out.println("--- Looking ahead from quantized tick " + quantized_tick + ":\n");
									for (NoteInfo note: nearby_ticks)
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

									// Jump to the start tick of the nearby note to avoid duplication upon next
									// outer loop iteration
									if (i > original_tick) tick = i;
								}
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
		int index_of_highest_pitch = note_onset_slices_by_track_and_channel[track][channel].get(slice_index).size() - 1;
		int highest_pitch = note_onset_slices_by_track_and_channel[track][channel].get(slice_index).get(index_of_highest_pitch);
		return 	note_onset_slices_by_track_and_channel_only_new_onsets[track][channel].get(slice_index).contains(highest_pitch);
	}
}