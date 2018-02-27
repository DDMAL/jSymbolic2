package jsymbolic2.featureutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 * An object of this class holds information on the pitch, start, end, track and channel of notes parsed from
 * a MIDI stream (including Channel 10 unpitched notes). Information on each note is stored in a separate
 * NoteInfo object, and these are made accessible based on temporal location as specified by the MIDI tick on
 * which a note started.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class CollectedNoteInfo
{
	/* PRIVATE FIELDS ***************************************************************************************/

	
	/**
	 * A list of notes from a MIDI stream. Temporal order is not necessarily guaranteed.
	 */
	private final List<NoteInfo> note_list;

	/**
	 * A map indicating all notes starting on any given MIDI tick, where the start tick value serves as the
	 * map key and the map value is a list of all notes starting on the specified tick, on any track and on
	 * any channel. The particular ordering of notes in this List value is not necessarily meaningful.
	 */
	private final Map<Integer, List<NoteInfo>> start_tick_note_map;
	
	
	/* CONSTRUCTORS *****************************************************************************************/
	
	
	/**
	 * Instantiate a CollectedNoteInfo object that does not yet hold any notes.
	 */
	public CollectedNoteInfo()
	{
		note_list = new ArrayList<>();
		start_tick_note_map = new HashMap<>();
	}
	
	
	/**
	 * Instantiate a CollectedNoteInfo object holding all notes in the provided tracks.
	 * 
	 * @param tracks	All the MIDI tracks loaded from the MIDI sequence.
	 */
	public CollectedNoteInfo(Track[] tracks)
	{
		// Initialize fields
		note_list = new ArrayList<>();
		start_tick_note_map = new HashMap<>();
		
		// Go through all tracks and all events in each track, looking for note ons and note offs, and adding
		// each note to the class fields
		for (int track_i = 0; track_i < tracks.length; track_i++)
		{
			Track this_track = tracks[track_i];
			for (int event_i = 0; event_i < this_track.size(); event_i++)
			{
				MidiEvent this_event = this_track.get(event_i);
				if (this_event.getMessage() instanceof ShortMessage)
				{
					ShortMessage start_message = (ShortMessage) this_event.getMessage();
					if (start_message.getCommand() == 0x90) // If a Note On message is encountered
					{
						if (start_message.getData2() != 0) // If not velocity 0
						{
							// Store the pitch and velocity of this note
							int pitch = start_message.getData1();
							int velocity = start_message.getData2();
							
							// Look ahead to find the corresponding Note Off for this Note On
							int event_start_tick = (int) this_event.getTick();
							for (int i = event_i + 1; i < this_track.size(); i++)
							{
								if (this_track.get(i).getMessage() instanceof ShortMessage)
								{
									ShortMessage end_message = (ShortMessage) this_track.get(i).getMessage();
									if (end_message.getChannel() == start_message.getChannel()) // Must be on same channel as Note On was
									{
										if (end_message.getCommand() == 0x80) // Note Off
										{
											if (end_message.getData1() == start_message.getData1()) // Must be same pitch as Note On
											{
												int event_end_tick = (int) this_track.get(i).getTick();
												NoteInfo this_note = new NoteInfo( pitch,
																				   velocity,
																				   event_start_tick,
																				   event_end_tick,
																				   track_i,
																				   end_message.getChannel() );
												addNote(this_note);
												i = this_track.size() + 1; // exit loop
											}
										}
										if (end_message.getCommand() == 0x90) // Note On (with velocity 0 is equivalent to note off)
										{
											if (end_message.getData2() == 0) // Velocity 0
											{
												if (end_message.getData1() == start_message.getData1()) // same pitch
												{
													int event_end_tick = (int) this_track.get(i).getTick();
													NoteInfo this_note = new NoteInfo( pitch,
																					   velocity,
																					   event_start_tick,
																					   event_end_tick,
																					   track_i,
																					   end_message.getChannel());
													addNote(this_note);
													i = this_track.size() + 1; // exit loop
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}	
	
	
	/* PUBLIC METHODS ***************************************************************************************/


	/**
	 * Add a note to be stored and indexed in this object.
	 *
	 * @param this_note	The note to be stored.
	 */
	public final void addNote(NoteInfo this_note)
	{
		note_list.add(this_note);
		
		int start_tick = this_note.getStartTick();
		if (start_tick_note_map.get(start_tick) == null)
		{
			List<NoteInfo> temp = new ArrayList<>();
			temp.add(this_note);
			start_tick_note_map.put(start_tick, temp);
		}
		else start_tick_note_map.get(start_tick).add(this_note);
	}

	
	/**
	 * @return	A list of notes added to this object from a MIDI stream so far. Temporal order is not
	 *			necessarily guaranteed.
	 */
	public List<NoteInfo> getNoteList()
	{
		return note_list;
	}

	
	/**
	 * @return	A map holding all notes parsed from a MIDI stream so far. It stores all notes starting on any
	 *			given MIDI tick, where the start tick value serves as the map key and the map value is a list
	 *			of all notes starting on the specified tick, on any track and on any channel. The particular
	 *			ordering of notes in this List value is not necessarily meaningful.
	 */
	public Map<Integer, List<NoteInfo>> getStartTickNoteMap()
	{
		return start_tick_note_map;
	}

	
	/**
	 * Get a list of notes in a given MIDI channel.
	 *
	 * @param	channel	The MIDI channel to find notes from.
	 * @return			A list of all notes added so far from a MIDI stream with the given channel number.
	 *					Temporal order is not necessarily guaranteed.
	 */
	public List<NoteInfo> getNotesOnChannel(int channel)
	{
		return note_list.stream().filter(n -> n.getChannel() == channel).collect(Collectors.toList());
	}
	

	/**
	 * Get a list of notes starting on the given MIDI start_tick.
	 * 
	 * @param tick	The MIDI start_tick to find notes starting on.
	 * @return		A list holding all notes parsed from a MIDI stream so far that start at the specified MIDI
					tick. The particular ordering of notes in this list is not necessarily meaningful.
	 */
	public List<NoteInfo> getNotesStartingOnTick(int tick)
	{
		return start_tick_note_map.get(tick);
	}
	
	
	/* PUBLIC STATIC METHODS ********************************************************************************/


	/**
	 * Convert a list of notes into a map indicating all notes starting on any given MIDI tick, where the
	 * start tick value serves as the map key and the map value is a list of all notes starting on the
	 * specified tick, on any track and on any channel. The particular ordering of notes in this List value is
	 * not necessarily meaningful.
	 *
	 * @param note_list	The list of notes to populate the returned object with.
	 * @return			The map linking MIDI ticks to the notes that start on those ticks.
	 */
	public static Map<Integer, List<NoteInfo>> noteListToStartTickNoteMap(List<NoteInfo> note_list)
	{
		Map<Integer, List<NoteInfo>> this_map = new HashMap<>();
		for (NoteInfo note : note_list)
		{
			int start_tick = note.getStartTick();
			if (this_map.get(start_tick) == null)
			{
				List<NoteInfo> temp = new ArrayList<>();
				temp.add(note);
				this_map.put(start_tick, temp);
			}
			else this_map.get(start_tick).add(note);
		}
		return this_map;
	}


	/**
	 * Return a copy of the the given list of notes sorted in increasing order by start tick. Note that the
	 * original list is not changed in any way.
	 *
	 * @param note_list	The list of notes to sort.
	 * @return			The sorted copy.
	 */
	public static List<NoteInfo> noteListToSortedNoteList(List<NoteInfo> note_list)
	{
		// Copy note_list
		List<NoteInfo> sorted_list = new ArrayList<>(note_list.size());
		for (NoteInfo note : note_list)
			sorted_list.add(note);
		
		// Sort and return the copy of note_list
		sorted_list.sort((s1, s2) -> ((Integer) s1.getStartTick()).compareTo(s2.getStartTick()));
		return sorted_list;
	}
}