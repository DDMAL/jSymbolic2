package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.processing.MIDIIntermediateRepresentations;
import org.ddmal.jmei2midi.meielements.meispecific.MeiSpecificStorage;

import javax.sound.midi.Sequence;

/**
 * Obtain all of the grace notes of a particular MEI piece through the {@link MeiSpecificStorage} class from the
 * See <a href="https://github.com/DDMAL/jMei2Midi">jMei2Midi</a> dependency software.
 */
public class NumberOfGraceNotesFeature extends MEIFeatureExtractor {

    /**
     * Constructor for the GraceNotEnumerationFeature.
     */
    public NumberOfGraceNotesFeature() {
        String name = "Number of Grace Notes";
        String description = "The total number of grace notes in a piece (i.e. the number of notes indicated"
				+ " as grace notes in the MEI encoding).";
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
     * Extract the number of grace notes of the specified mei music.
	 * 
     * @param meiSpecificStorage The storage that holds all accessible mei specific data.
     * @return The number of grace notes from the specified piece. This can be found in the
     * first and only index of the returned double array.
     */
    @Override
    public double[] extractMEIFeature(MeiSpecificStorage meiSpecificStorage,
                                      Sequence sequence,
                                      MIDIIntermediateRepresentations sequence_info,
                                      double[][] other_feature_values)
    {
        double[] numberOfGraceNotes = {(double)meiSpecificStorage.getGraceNoteList().size()};
        return numberOfGraceNotes;
    }
}
