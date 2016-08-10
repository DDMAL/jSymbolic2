package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.NoteInfo;
import jsymbolic2.featureutils.NoteInfoList;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;
import java.util.*;

/**
 * A feature extractor computes the fraction of all notes in different voices
 * that move together in the same direction within 10% of the duration of the shortest note.
 * If multiple notes are sounding within a single voice (e.g. a piano chord),
 * only the highest pitched note in this voice is considered for the purposes of this feature.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class ParallelMotionFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public ParallelMotionFeature() {
        String name = "Parallel Motion";
        String description = "Fraction of all notes in different voices that move together in the same direction " +
                "within 10% of the duration of the shortest note. " +
                "If multiple notes are sounding within a single voice (e.g. a piano chord), " +
                "only the highest pitched note in this voice is considered for the purposes of this feature.";
        boolean is_sequential = true;
        int dimensions = 1;
        definition = new FeatureDefinition( name,
                description,
                is_sequential,
                dimensions );

        dependencies = null;
        offsets = null;
    }

    /**
     * Extracts this feature from the given MIDI sequence given the other
     * feature values.
     *
     * <p>In the case of this feature, the other_feature_values parameters
     * are ignored.
     *
     * @param sequence			The MIDI sequence to extract the feature
     *                                 from.
     * @param sequence_info		Additional data about the MIDI sequence.
     * @param other_feature_values	The values of other features that are
     *					needed to calculate this value. The
     *					order and offsets of these features
     *					must be the same as those returned by
     *					this class's getDependencies and
     *					getDependencyOffsets methods
     *                                 respectively. The first indice indicates
     *                                 the feature/window and the second
     *                                 indicates the value.
     * @return				The extracted feature value(s).
     * @throws Exception		Throws an informative exception if the
     *					feature cannot be calculated.
     */
    @Override
    public double[] extractFeature(Sequence sequence,
                                   MIDIIntermediateRepresentations sequence_info,
                                   double[][] other_feature_values)
            throws Exception
    {
        double parallel_notes = 0;

        // Setup the appropriate tick note maps
        List<NoteInfo> all_notes = sequence_info.all_note_info.getAllNotes();
        all_notes.sort((n1, n2) -> ((Integer)n1.getStart_tick()).compareTo(n2.getStart_tick()));
        Map<Integer, List<NoteInfo>> tickNoteMap = sequence_info.all_note_info.getTickNoteMap();
        Integer[] ticks = tickNoteMap.keySet().toArray(new Integer[0]);
        Arrays.sort(ticks);

        // For each tick we check the note range in other voices
        for(int current_tick_index = 0; current_tick_index < ticks.length - 1; current_tick_index++) {

            // Store the motion of the voices in each channel
            List<NoteMotion> tick_motion = new ArrayList<>(16);

            // Get shortest note duration for this tick
            int current_tick = ticks[current_tick_index];
            NoteInfo short_note = getShortNoteInChannelAtTick(sequence_info.all_note_info, current_tick);
            int short_duration = short_note.getDuration();

            // Compare the voice motion in each voice(channel) w.r.t. the shortest duration
            for(int channel = 0; channel < 16; channel++) {
                if(channel == 10 - 1) continue; // skip over percussion channel
                List<NoteInfo> channel_notes = sequence_info.all_note_info.getChannelNotes(channel);
                Map<Integer, List<NoteInfo>> current_tick_notes = sequence_info.all_note_info.channelListToTickMap(channel_notes);
                Set<Integer> channel_ticks = current_tick_notes.keySet();
                List<Integer> channel_ticks_list = new ArrayList<>(channel_ticks);
                channel_ticks_list.sort((i1, i2) -> i1.compareTo(i2));

                // Search for close voice within the given short duration
                int close_voice = -1;
                int close_voice_index = 0;
                for(int channel_tick = current_tick; channel_tick < current_tick + 0.1 * short_duration; channel_tick++) {
                    if(channel_ticks.contains(channel_tick)) {
                        close_voice = channel_tick;
                        close_voice_index = channel_ticks_list.indexOf(close_voice);
                        break; // jump out once we have found the closest note
                    }
                }
                int next_tick_index = close_voice_index + 1;
                // If no close voice was found or out of array index bounds then continue
                if(close_voice < 0 ||
                        next_tick_index >= channel_ticks_list.size())
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
                if(close_note.getPitch() < next_note.getPitch()) {
                    tick_motion.add(NoteMotion.UP);
                } else if (close_note.getPitch() > next_note.getPitch()) {
                    tick_motion.add(NoteMotion.DOWN);
                } else {
                    tick_motion.add(NoteMotion.SAME);
                }
            }
            parallel_notes += computeParallelMotionVoices(tick_motion);
        }
        double parallel_motion = parallel_notes / all_notes.size();
        return new double[]{parallel_motion};
    }

    /**
     * Convenience enum to deal with different types of possible note motion.
     */
    private enum NoteMotion {
        UP,
        DOWN,
        SAME,
        NO_MOTION
    }

    /**
     * Get the shortest note in any voice from the highest pitch in each channel at the given current tick.
     * @param all_note_info All of the note info required.
     * @param current_tick The tick at which to check the voices from.
     * @return The shortest note with the highest pitch of any channel at the given current tick.
     */
    private NoteInfo getShortNoteInChannelAtTick(NoteInfoList all_note_info, int current_tick) {
        int shortest_duration = Integer.MAX_VALUE;
        NoteInfo shortest_note = all_note_info.getAllNotes().get(0); // to initialize to non-null value
        for(int channel = 0; channel < 16; channel++) {
            if(channel == 10 - 1) continue;
            List<NoteInfo> channel_notes = all_note_info.getChannelNotes(channel);
            Map<Integer, List<NoteInfo>> channel_tick_notes = all_note_info.channelListToTickMap(channel_notes);
            if(channel_tick_notes.containsKey(current_tick)) {
                List<NoteInfo> current_tick_notes = channel_tick_notes.get(current_tick);
                NoteInfo high_note = getHighestNote(current_tick_notes);
                int high_duration = high_note.getDuration();
                if(high_duration < shortest_duration) {
                    shortest_duration = high_duration;
                    shortest_note = high_note;
                }
            }
        }
        return shortest_note;
    }

    /**
     * Given the motion of each voice, compute how many voices are moving in parallel.
     * @param tick_motion The motion of each voice.
     * @return The number of voices moving in parallel.
     * @throws Exception
     */
    private double computeParallelMotionVoices(List<NoteMotion> tick_motion) throws Exception {
        // Exception here added for error checking
        if(tick_motion.size() != 16 - 1) throw new Exception("There needs to be 16 channels to compare motions");
        double parallel_voices = 0;
        int up_motion = 0;
        int down_motion = 0;
        int same_motion = 0;
        for(NoteMotion motion : tick_motion) {
            switch (motion) {
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
        if(up_motion > 1) {
            parallel_voices += up_motion;
        }
        if(down_motion > 1) {
            parallel_voices += down_motion;
        }
        if(same_motion > 1) {
            parallel_voices += same_motion;
        }
        return parallel_voices;
    }

    /**
     * Get the shortest note within the given note list.
     * @param notes The note list to be checked.
     * @return The note with the shortest duration from the notes list.
     */
    private NoteInfo getShortestNote(List<NoteInfo> notes) {
        if(notes == null) return null;
        int short_duration = Integer.MAX_VALUE;
        NoteInfo short_note = notes.get(0);
        for(NoteInfo note : notes) {
            int this_duration = note.getDuration();
            if(this_duration < short_duration) {
                short_duration = this_duration;
                short_note = note;
            }
        }
        return short_note;
    }

    /**
     * Get the highest pitch note within the given note list.
     * @param notes The note list to be checked.
     * @return The note with the highest pitch from the notes list.
     */
    private NoteInfo getHighestNote(List<NoteInfo> notes) {
        if(notes == null) return null;
        int max_pitch = 0;
        NoteInfo max_note = notes.get(0);
        for(NoteInfo note : notes) {
            int this_pitch = note.getPitch();
            if(this_pitch > max_pitch) {
                max_pitch = this_pitch;
                max_note = note;
            }
        }
        return max_note;
    }
}
