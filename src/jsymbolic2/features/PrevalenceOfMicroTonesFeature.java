package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;
import java.util.LinkedList;

/**
 * A feature extractor that extracts the number of Note Ons that are preceded by
 * isolated MIDI Pitch Bend messages as a fraction of the total number of Note Ons.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class PrevalenceOfMicroTonesFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public PrevalenceOfMicroTonesFeature() {
        String name = "Prevalence of Micro-Tones";
        String description = "Number of Note Ons that are preceded by\n" +
                "isolated MIDI Pitch Bend messages as a fraction of the total number of Note Ons.";
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
        LinkedList pitch_bend = sequence_info.pitch_bends_list;
        // To check for the case of no pitch bends in midi
        if(pitch_bend.size() == 0) {
            return new double[]{0};
        }

        double number_note_ons = 0;
        for(Object pitch_bend_note : pitch_bend) {
            LinkedList bend_list = (LinkedList) pitch_bend_note;
            // 1 for an isolated pitch bend
            // i.e. not near any other pitch bends
            if(bend_list.size() == 1) {
                number_note_ons++;
            }
        }
        double prevalence = number_note_ons / sequence_info.total_number_notes;
        return new double[]{prevalence};
    }
}
