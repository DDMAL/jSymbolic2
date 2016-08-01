package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.ChordTypesEnum;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;

/**
 * A feature extractor that extracts Fraction of all simultaneously sounding pitches
 * that consist of more than two pitch class chords and are not major or minor triads
 * or seventh chords.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class NonStandardChordsFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public NonStandardChordsFeature() {
        String name = "Non-Standard Chords";
        String description = "Fraction of all simultaneously sounding pitches\n" +
                "that consist of more than two pitch class chords and are not major or minor triads\n" +
                "or seventh chords.";
        boolean is_sequential = true;
        int dimensions = 1;
        definition = new FeatureDefinition( name,
                description,
                is_sequential,
                dimensions );

        dependencies = new String[]{"Chord Types Histogram"};
        offsets = null;
    }

    /**
     * Extracts this feature from the given MIDI sequence given the other
     * feature values.
     *
     * <p>In the case of this feature, the other_feature_values parameters
     * are the ChordTypeFeature output in other_feature_values[0].
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
        int number_non_standard_chords = ChordTypesEnum.values().length - 6;
        int[] non_standard_numbers = new int[number_non_standard_chords];
        for(ChordTypesEnum chordType : ChordTypesEnum.values()) {
            int index = 0;
            if(!chordType.equals(ChordTypesEnum.PARTIAL_CHORD) &&
                    !chordType.equals(ChordTypesEnum.MAJOR_TRIAD) &&
                    !chordType.equals(ChordTypesEnum.MINOR_TRIAD) &&
                    !chordType.equals(ChordTypesEnum.DOMINANT_SEVENTH) &&
                    !chordType.equals(ChordTypesEnum.MAJOR_SEVENTH) &&
                    !chordType.equals(ChordTypesEnum.MINOR_SEVENTH))
            {
                non_standard_numbers[index] = chordType.getChord_number();
                index++;
            }
        }
        double non_standard_fraction = 0;
        for(int number : non_standard_numbers) {
            non_standard_fraction += chord_type_values[number];
        }
        return new double[]{non_standard_fraction};
    }
}
