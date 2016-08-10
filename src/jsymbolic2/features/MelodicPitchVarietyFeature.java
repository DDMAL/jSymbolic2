package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.NoteInfo;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A feature extractor that extracts the average number of notes that go by in a channel
 * before a note is repeated. Notes that do not recur after sixteen notes are not counted.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class MelodicPitchVarietyFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public MelodicPitchVarietyFeature() {
        String name = "Melodic Pitch Variety";
        String description = "Average number of notes that go by in a channel\n" +
                "before a note is repeated. Notes that do not recur after sixteen notes are not\n" +
                "counted.";
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
        double cummulative_notes = 0;
        // For each channel get all the notes in the channel
        for(int channel = 0; channel < 16; channel++) {
            List<NoteInfo> channel_notes = sequence_info.all_note_info.getChannelNotes(channel);
            channel_notes.sort((n1, n2) -> ((Integer)n1.getStart_tick()).compareTo(n2.getStart_tick()));
            Map<Integer, List<NoteInfo>> channelTickNotes = sequence_info.all_note_info.channelListToTickMap(channel_notes);
            Set<Integer> channelTicks = channelTickNotes.keySet();
            double channel_notes_by = 0;
            double number_variety_notes = 0;
            // For each note in this channel, compare with all other notes up to 16 notes away
            for(NoteInfo current_note : channel_notes) {
                int current_tick = current_note.getStart_tick();
                int note_tick_count = 0;
                for(Integer tick : channelTicks) {
                    if(tick > current_tick) {
                        List<NoteInfo> notesAtTick = channelTickNotes.get(tick);
                        for(NoteInfo other_note : notesAtTick) {
                            if(current_note.getPitch() == other_note.getPitch() &&
                                    note_tick_count < 16)
                            {
                                // subtract to not include the repeated note
                                channel_notes_by += note_tick_count - 1;
                                number_variety_notes++;
                            }
                        }
                    }
                    note_tick_count++;
                }
            }
            // To avoid channels with no notes in them
            if(number_variety_notes != 0) {
                cummulative_notes += channel_notes_by / number_variety_notes;
            }
        }
        return new double[]{cummulative_notes};
    }
}
