package jsymbolic2.features;

import jsymbolic2.featureutils.ChordTypeEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dinamix on 7/8/16.
 */
public class ChordTypesEnumTest {
    private final int number_of_intervals = 12;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getChordType() throws Exception {
        // Two pitch test
        int[] two_pitch = new int[number_of_intervals];
        two_pitch[0] = 5;
        two_pitch[11] = 4;
        ChordTypeEnum actual_two_pitch = ChordTypeEnum.getChordType(two_pitch);
        ChordTypeEnum expected_two_pitch = ChordTypeEnum.PARTIAL_CHORD;
        assertEquals(expected_two_pitch, actual_two_pitch);

        // Major triad test
        int[] major_triad_root = new int[number_of_intervals];
        major_triad_root[2] = 64;
        major_triad_root[6] = 64;
        major_triad_root[9] = 64;
        ChordTypeEnum actual_major_triad_root = ChordTypeEnum.getChordType(major_triad_root);
        ChordTypeEnum expected_major_triad_root = ChordTypeEnum.MAJOR_TRIAD;
        assertEquals(expected_major_triad_root, actual_major_triad_root);
        
        // Minor triad test
        int[] minor_triad_root = new int[number_of_intervals];
        minor_triad_root[2] = 64;
        minor_triad_root[6] = 64;
        minor_triad_root[11] = 64;
        ChordTypeEnum actual_minor_triad_root = ChordTypeEnum.getChordType(minor_triad_root);
        ChordTypeEnum expected_minor_triad_root = ChordTypeEnum.MINOR_TRIAD;
        assertEquals(expected_minor_triad_root, actual_minor_triad_root);
        
        // Augmented triad test
        int[] augmented_triad = new int[number_of_intervals];
        augmented_triad[2] = 64;
        augmented_triad[6] = 64;
        augmented_triad[10] = 64;
        ChordTypeEnum actual_augmented_triad = ChordTypeEnum.getChordType(augmented_triad);
        ChordTypeEnum expected_augmented_triad = ChordTypeEnum.AUGMENTED_TRIAD;
        assertEquals(expected_augmented_triad, actual_augmented_triad);

        // Diminished triad test
        int[] diminished_triad = new int[number_of_intervals];
        diminished_triad[1] = 64;
        diminished_triad[4] = 64;
        diminished_triad[10] = 64;
        ChordTypeEnum actual_diminished_triad = ChordTypeEnum.getChordType(diminished_triad);
        ChordTypeEnum expected_diminished_triad = ChordTypeEnum.DIMINISHED_TRIAD;
        assertEquals(expected_diminished_triad, actual_diminished_triad);

        // Other triad test
        int[] other_triad = new int[number_of_intervals];
        other_triad[1] = 64;
        other_triad[4] = 64;
        other_triad[11] = 64;
        ChordTypeEnum actual_other_triad = ChordTypeEnum.getChordType(other_triad);
        ChordTypeEnum expected_other_triad = ChordTypeEnum.OTHER_TRIAD;
        assertEquals(expected_other_triad, actual_other_triad);

        // Dominant seventh chord test
        int[] dominant_seventh = new int[number_of_intervals];
        dominant_seventh[0] = 128;
        dominant_seventh[4] = 128;
        dominant_seventh[7] = 128;
        dominant_seventh[10] = 64;
        ChordTypeEnum actual_dominant_seventh = ChordTypeEnum.getChordType(dominant_seventh);
        ChordTypeEnum expected_dominant_seventh = ChordTypeEnum.DOMINANT_SEVENTH;
        assertEquals(expected_dominant_seventh, actual_dominant_seventh);

        // Major seventh chord test
        int[] major_seventh = new int[number_of_intervals];
        major_seventh[0] = 128;
        major_seventh[4] = 128;
        major_seventh[7] = 128;
        major_seventh[11] = 64;
        ChordTypeEnum actual_major_seventh = ChordTypeEnum.getChordType(major_seventh);
        ChordTypeEnum expected_major_seventh = ChordTypeEnum.MAJOR_SEVENTH;
        assertEquals(expected_major_seventh, actual_major_seventh);

        // Minor seventh chord test
        int[] minor_seventh = new int[number_of_intervals];
        minor_seventh[0] = 128;
        minor_seventh[3] = 128;
        minor_seventh[7] = 128;
        minor_seventh[10] = 64;
        ChordTypeEnum actual_minor_seventh = ChordTypeEnum.getChordType(minor_seventh);
        ChordTypeEnum expected_minor_seventh = ChordTypeEnum.MINOR_SEVENTH;
        assertEquals(expected_minor_seventh, actual_minor_seventh);

        // Other four chord test
        int[] other_four = new int[number_of_intervals];
        other_four[0] = 128;
        other_four[3] = 128;
        other_four[7] = 128;
        other_four[11] = 64;
        ChordTypeEnum actual_other_four = ChordTypeEnum.getChordType(other_four);
        ChordTypeEnum expected_other_four = ChordTypeEnum.OTHER_FOUR_NOTE_CHORD;
        assertEquals(expected_other_four, actual_other_four);
        
        // Complex chord test
        int[] complex_chord = new int[number_of_intervals];
        complex_chord[0] = 128;
        complex_chord[3] = 128;
        complex_chord[7] = 128;
        complex_chord[10] = 64;
        complex_chord[11] = 70;
        ChordTypeEnum actual_complex_chord = ChordTypeEnum.getChordType(complex_chord);
        ChordTypeEnum expected_complex_chord = ChordTypeEnum.COMPLEX_CHORD;
        assertEquals(expected_complex_chord, actual_complex_chord);
    }

}