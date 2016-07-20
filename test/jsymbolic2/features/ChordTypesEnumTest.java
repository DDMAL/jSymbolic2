package jsymbolic2.features;

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
        ChordTypesEnum actual_two_pitch = ChordTypesEnum.getChordType(two_pitch);
        ChordTypesEnum expected_two_pitch = ChordTypesEnum.PARTIAL_CHORD;
        assertEquals(expected_two_pitch, actual_two_pitch);

        // Major triad test
        int[] major_triad_root = new int[number_of_intervals];
        major_triad_root[2] = 64;
        major_triad_root[6] = 64;
        major_triad_root[9] = 64;
        ChordTypesEnum actual_major_triad_root = ChordTypesEnum.getChordType(major_triad_root);
        ChordTypesEnum expected_major_triad_root = ChordTypesEnum.MAJOR_TRIAD;
        assertEquals(expected_major_triad_root, actual_major_triad_root);
        
        // Minor triad test
        int[] minor_triad_root = new int[number_of_intervals];
        minor_triad_root[2] = 64;
        minor_triad_root[6] = 64;
        minor_triad_root[11] = 64;
        ChordTypesEnum actual_minor_triad_root = ChordTypesEnum.getChordType(minor_triad_root);
        ChordTypesEnum expected_minor_triad_root = ChordTypesEnum.MINOR_TRIAD;
        assertEquals(expected_minor_triad_root, actual_minor_triad_root);
        
        // Augmented triad test
        int[] augmented_triad = new int[number_of_intervals];
        augmented_triad[2] = 64;
        augmented_triad[6] = 64;
        augmented_triad[10] = 64;
        ChordTypesEnum actual_augmented_triad = ChordTypesEnum.getChordType(augmented_triad);
        ChordTypesEnum expected_augmented_triad = ChordTypesEnum.AUGMENTED_TRIAD;
        assertEquals(expected_augmented_triad, actual_augmented_triad);

        // Diminished triad test
        int[] diminished_triad = new int[number_of_intervals];
        diminished_triad[1] = 64;
        diminished_triad[4] = 64;
        diminished_triad[10] = 64;
        ChordTypesEnum actual_diminished_triad = ChordTypesEnum.getChordType(diminished_triad);
        ChordTypesEnum expected_diminished_triad = ChordTypesEnum.DIMINISHED_TRIAD;
        assertEquals(expected_diminished_triad, actual_diminished_triad);

        // Dominant seventh chord test
        int[] dominant_seventh = new int[number_of_intervals];
        dominant_seventh[0] = 128;
        dominant_seventh[4] = 128;
        dominant_seventh[7] = 128;
        dominant_seventh[10] = 64;
        ChordTypesEnum actual_dominant_seventh = ChordTypesEnum.getChordType(dominant_seventh);
        ChordTypesEnum expected_dominant_seventh = ChordTypesEnum.DOMINANT_SEVENTH;
        assertEquals(expected_dominant_seventh, actual_dominant_seventh);

        // Major seventh chord test
        int[] major_seventh = new int[number_of_intervals];
        major_seventh[0] = 128;
        major_seventh[4] = 128;
        major_seventh[7] = 128;
        major_seventh[11] = 64;
        ChordTypesEnum actual_major_seventh = ChordTypesEnum.getChordType(major_seventh);
        ChordTypesEnum expected_major_seventh = ChordTypesEnum.MAJOR_SEVENTH;
        assertEquals(expected_major_seventh, actual_major_seventh);

        // Minor seventh chord test
        int[] minor_seventh = new int[number_of_intervals];
        minor_seventh[0] = 128;
        minor_seventh[3] = 128;
        minor_seventh[7] = 128;
        minor_seventh[10] = 64;
        ChordTypesEnum actual_minor_seventh = ChordTypesEnum.getChordType(minor_seventh);
        ChordTypesEnum expected_minor_seventh = ChordTypesEnum.MINOR_SEVENTH;
        assertEquals(expected_minor_seventh, actual_minor_seventh);
        
        // Complex chord test
        int[] complex_chord = new int[number_of_intervals];
        complex_chord[0] = 128;
        complex_chord[3] = 128;
        complex_chord[7] = 128;
        complex_chord[10] = 64;
        complex_chord[11] = 70;
        ChordTypesEnum actual_complex_chord = ChordTypesEnum.getChordType(complex_chord);
        ChordTypesEnum expected_complex_chord = ChordTypesEnum.COMPLEX_CHORD;
        assertEquals(expected_complex_chord, actual_complex_chord);
    }

}