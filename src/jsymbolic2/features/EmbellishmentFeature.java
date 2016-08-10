package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.NoteInfo;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A feature extractor that extracts the fraction of notes that are surrounded on both sides by
 * Note Ons on the same MIDI channel that have durations at least three times as
 * long as the central note.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class EmbellishmentFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public EmbellishmentFeature() {
        String name = "Embellishment";
        String description = "Fraction of notes that are surrounded on both sides by\n" +
                "Note Ons on the same MIDI channel that have durations at least three times as\n" +
                "long as the central note.";
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
        double embellish_notes = 0;
        // For each channel get all the notes in the channel
        for(int channel = 0; channel < 16; channel++) {
            List<NoteInfo> channel_notes = sequence_info.all_note_info.getChannelNotes(channel);
            channel_notes.sort((n1, n2) -> ((Integer)n1.getStart_tick()).compareTo(n2.getStart_tick()));
            Map<Integer, List<NoteInfo>> channelTickNotes = sequence_info.all_note_info.channelListToTickMap(channel_notes);
            Integer[] channelTicks = channelTickNotes.keySet().toArray(new Integer[0]);
            Arrays.sort(channelTicks);
            // For each note in this channel, compare with all other notes up to 16 notes away
            for(NoteInfo current_note : channel_notes) {
                int current_tick = current_note.getStart_tick();
                int current_tick_index = 0;
                for(int tick_index = 0; tick_index < channelTicks.length; tick_index++) {
                    if(channelTicks[tick_index] == current_tick) {
                        current_tick_index = tick_index;
                        break;
                    }
                }
                if(current_tick_index < channelTicks.length - 1 &&
                        current_tick_index > 0)
                {
                    int next_tick = channelTicks[current_tick_index + 1];
                    int previous_tick = channelTicks[current_tick_index - 1];
                    List<NoteInfo> next_notes = channelTickNotes.get(next_tick);
                    List<NoteInfo> previous_notes = channelTickNotes.get(previous_tick);
                    boolean next_check = false;
                    for(NoteInfo next : next_notes) {
                        if(next.getDuration() >= 3 * current_note.getDuration()) {
                            next_check = true;
                        }
                    }
                    boolean previous_check = false;
                    for(NoteInfo previous : previous_notes) {
                        if(previous.getDuration() >= 3 * current_note.getDuration()) {
                            previous_check = true;
                        }
                    }
                    if(next_check && previous_check) {
                        embellish_notes++;
                    }
                }
            }
        }
        double embellish_fraction = embellish_notes / sequence_info.all_note_info.getAllNotes().size();
        return new double[]{embellish_fraction};
    }
}
