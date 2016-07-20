package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.processing.MIDIIntermediateRepresentations;
import org.ddmal.jmei2midi.meielements.meispecific.MeiSpecificStorage;

import javax.sound.midi.Sequence;

/**
 * Created by dinamix on 7/18/16.
 */
public class NumberOfSlurNotesFeature extends MEIFeatureExtractor {
    public NumberOfSlurNotesFeature() {
        String name = "Number of Slur Notes";
        String description = "The total number of slur notes in a piece (i.e. the number of notes indicated"
                + " as slur notes in the MEI encoding).";
        boolean is_sequential = true;
        int dimensions = 1;
        definition = new FeatureDefinition( name,
                description,
                is_sequential,
                dimensions );

        dependencies = null;

        offsets = null;
    }

    @Override
    public double[] extractMEIFeature(MeiSpecificStorage meiSpecificStorage,
                                      Sequence sequence,
                                      MIDIIntermediateRepresentations sequence_info,
                                      double[][] other_feature_values)
    {
        double[] numberOfSlurNotes = {(double)meiSpecificStorage.getSlurNoteList().size()};
        return numberOfSlurNotes;
    }
}
