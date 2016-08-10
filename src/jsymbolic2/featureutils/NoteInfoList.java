package jsymbolic2.featureutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A convenience class to store all the note data of a given piece of music.
 */
public class NoteInfoList {

    /**
     * A list of all of the notes and their data.
     */
    private List<NoteInfo> note_info_list;

    /**
     * A map of all the notes playing at a given tick where
     * key = tick and value = list of all note data at a given tick.
     */
    private Map<Integer, List<NoteInfo>> tick_note_map;

    /**
     * Constructor
     */
    public NoteInfoList() {
        note_info_list = new ArrayList<>();
        tick_note_map = new HashMap<>();
    }

    /**
     * Add a note the list of note data.
     * @param noteInfo The note to be added.
     */
    public void addNote(NoteInfo noteInfo) {
        note_info_list.add(noteInfo);
        int tick = noteInfo.getStart_tick();
        if(tick_note_map.get(tick) == null) {
            List<NoteInfo> temp = new ArrayList<>();
            temp.add(noteInfo);
            tick_note_map.put(tick, temp);
        } else {
            tick_note_map.get(tick).add(noteInfo);
        }
    }

    /**
     * @return All of the note data as a list.
     */
    public List<NoteInfo> getAllNotes() {
        return note_info_list;
    }

    /**
     * Get all the note data in a particular given channel.
     * @param channel The channel to get the note data from.
     * @return All the note data from the given channel.
     */
    public List<NoteInfo> getChannelNotes(int channel) {
        return note_info_list.stream()
                .filter(n -> n.getChannel() == channel)
                .collect(Collectors.toList());
    }

    /**
     * @param tick The tick to be parsed at.
     * @return All of the notes at a particular tick.
     */
    public List<NoteInfo> getTickNoteList(int tick) {
        return tick_note_map.get(tick);
    }

    /**
     *
     * @return A map of all the note data, with ticks as keys and note data at the tick as values.
     */
    public Map<Integer, List<NoteInfo>> getTickNoteMap() {
        return tick_note_map;
    }

    /**
     * Convert a list of note data into a map which holds ticks as keys
     * and all corresponding playing notes at that given tick as values.
     * @param note_list The list of note data to be converted.
     * @return The map with ticks as keys and note data at the tick as values.
     */
    public Map<Integer, List<NoteInfo>> channelListToTickMap(List<NoteInfo> note_list) {
        Map<Integer, List<NoteInfo>> channelTickNotes = new HashMap<>();
        for(NoteInfo note : note_list) {
            int tick = note.getStart_tick();
            if(channelTickNotes.get(tick) == null) {
                List<NoteInfo> temp = new ArrayList<>();
                temp.add(note);
                channelTickNotes.put(tick, temp);
            } else {
                channelTickNotes.get(tick).add(note);
            }
        }
        return channelTickNotes;
    }

}
