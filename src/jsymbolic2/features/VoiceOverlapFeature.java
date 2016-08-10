package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;
import java.util.List;

/**
 * A feature extractor that extracts the number of notes played within the range of another voice
 * divided by total number of notes in the piece overall.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class VoiceOverlapFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public VoiceOverlapFeature() {
        String name = "Voice Overlap";
        String description = "Number of notes played within the range of another voice\n" +
                "divided by total number of notes in the piece overall.";
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
        int[][] channel_stats = sequence_info.channel_statistics;
        List<List<Integer>> channel_pitch = sequence_info.channel_pitches;

        // Find notes that are inside the range of any other channel
        int notes_inside_range = 0;
        for(int channel = 0; channel < channel_pitch.size(); channel++) {
            List<Integer> channel_notes = channel_pitch.get(channel);
            for(Integer current_pitch : channel_notes) {
                // Compare note to the range of every other channel
                for(int other_channel = 0; other_channel < channel_stats.length; other_channel++) {
                    if(channel == other_channel) continue;
                    int high_pitch = channel_stats[other_channel][5];
                    int low_pitch = channel_stats[other_channel][4];
                    if(current_pitch <= high_pitch && current_pitch >= low_pitch) {
                        notes_inside_range++;
                        break; // only count this note once
                    }
                }
            }
        }

        double total_notes = sequence_info.total_number_notes;
        double voice_overlap = notes_inside_range / total_notes;
        return new double[]{voice_overlap};
    }
}
