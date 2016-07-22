package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;

/**
 * A feature extractor that extracts the fraction of all chords that are dominant seventh, major
 * seventh or minor seventh chords.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class SeventhChordsFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public SeventhChordsFeature() {
        String name = "Dominant Seventh Chords";
        String description = "Fraction of all chords that are dominant seventh, major\n" +
                "seventh or minor seventh chords.";
        boolean is_sequential = true;
        int dimensions = 1;
        definition = new FeatureDefinition( name,
                description,
                is_sequential,
                dimensions );

        dependencies = new String[]{"Chord Types"};
        offsets = null;
    }

    /**
     * Extracts this feature from the given MIDI sequence given the other
     * feature values.
     *
     * <p>In the case of this feature, the other_feature_values parameters
     * are the ChordTypes output in other_feature_values[0].
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
        double[] chord_type_values = other_feature_values[0];
        int dominant_seventh = ChordTypesEnum.DOMINANT_SEVENTH.getChord_number();
        int major_seventh = ChordTypesEnum.MAJOR_SEVENTH.getChord_number();
        int minor_seventh = ChordTypesEnum.MINOR_SEVENTH.getChord_number();
        double seventh_fraction = chord_type_values[dominant_seventh] + chord_type_values[major_seventh]
                + chord_type_values[minor_seventh];
        return new double[]{seventh_fraction};
    }
}
