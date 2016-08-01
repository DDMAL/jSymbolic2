package jsymbolic2.featureutils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dinamix on 7/6/16.
 */
public enum ChordTypesEnum {
    PARTIAL_CHORD(ChordTypesValidation.PARTIAL_CHORD_VALIDATION, 0),
    MAJOR_TRIAD(ChordTypesValidation.MAJOR_TRIAD_VALIDATION, 1),
    MINOR_TRIAD(ChordTypesValidation.MINOR_TRIAD_VALIDATION, 2),
    AUGMENTED_TRIAD(ChordTypesValidation.AUGMENTED_TRIAD_VALIDATION, 3),
    DIMINISHED_TRIAD(ChordTypesValidation.DIMINISHED_TRIAD_VALIDATION, 4),
    OTHER_TRIAD(ChordTypesValidation.OTHER_TRIAD_VALIDATION, 5),
    DOMINANT_SEVENTH(ChordTypesValidation.DOMINANT_SEVENTH_VALIDATION, 6),
    MAJOR_SEVENTH(ChordTypesValidation.MAJOR_SEVENTH_VALIDATION, 7),
    MINOR_SEVENTH(ChordTypesValidation.MINOR_SEVENTH_VALIDATION, 8),
    OTHER_FOUR_CHORD(ChordTypesValidation.OTHER_FOUR_VALIDATION, 9),
    COMPLEX_CHORD(ChordTypesValidation.COMPLEX_CHORD_VALIDATION, 10);

    private final ChordTypesValidation validator;
    private final int chord_number;
    ChordTypesEnum(ChordTypesValidation validator, int chord_number) {
        this.validator = validator;
        this.chord_number = chord_number;
    }

    /**
     * Obtain the corresponding enum chord type given a 12-interval integer array.
     * @param chord A 12-interval integer array which corresponds to the twelve main musical tones.
     *              The chord type will be computed based on the distance between the indices
     *              of values in the array that are > 0. For example, a C-major chord can be represented
     *              as [10, 0, 0, 0, 20, 0, 0, 64, 0, 0, 0, 0].
     * @return The corresponding chord type if this is indeed a known chord of size 3 or more. Otherwise, this method
     * will return null if it does not know the given chord type. For example a chord array with only 2 notes
     * in it will make this function return a null.
     * @throws Exception Thrown if the chord integer array is invalid.
     */
    public static ChordTypesEnum getChordType(int[] chord) throws Exception {
        if(chord == null || chord.length != 12) {
            throw new Exception("Need to pass in a 12 interval single chord array to validate chord.\n");
        }
        for(ChordTypesEnum chordType : ChordTypesEnum.values()) {
            if(chordType.validator.validate(chord)) {
                return chordType;
            }
        }
        return null; //if no chord type is found
    }

    public int getChord_number() {
        return chord_number;
    }

    private enum ChordTypesValidation {
        PARTIAL_CHORD_VALIDATION {
            @Override
            boolean validate(int[] chord) {
                return check_chord_length(chord, 2);
            }
        },
        MAJOR_TRIAD_VALIDATION {
            @Override
            public boolean validate(int[] chord) {
                int major_triad_length = 3;
                int[] root_position = {4,3};
                int[] first_inversion = {3,5};
                int[] second_inversion = {5,4};
                int[][] major_triad_intervals = {root_position, first_inversion, second_inversion};
                return generic_chord_with_inversion_validate(chord, major_triad_length, major_triad_intervals);
            }
        },
        MINOR_TRIAD_VALIDATION {
            @Override
            boolean validate(int[] chord) {
                int minor_triad_length = 3;
                int[] root_position = {3,4};
                int[] first_inversion = {4,5};
                int[] second_inversion = {5,3};
                int[][] minor_triad_intervals = {root_position, first_inversion, second_inversion};
                return generic_chord_with_inversion_validate(chord, minor_triad_length, minor_triad_intervals);
            }
        },
        AUGMENTED_TRIAD_VALIDATION {
            @Override
            boolean validate(int[] chord) {
                int augmented_triad_length = 3;
                int[] root_position = {4,4};
                int[] first_inversion = {4,4};
                int[] second_inversion = {4,4};
                int[][] augmented_triad_intervals = {root_position, first_inversion, second_inversion};
                return generic_chord_with_inversion_validate(chord, augmented_triad_length, augmented_triad_intervals);
            }
        },
        OTHER_TRIAD_VALIDATION {
            @Override
            boolean validate(int[] chord) {
                return check_chord_length(chord, 3);
            }
        },
        DIMINISHED_TRIAD_VALIDATION {
            @Override
            boolean validate(int[] chord) {
                int diminished_triad_length = 3;
                int[] root_position = {3,3};
                int[] first_inversion = {3,6};
                int[] second_inversion = {6,3};
                int[][] diminished_triad_intervals = {root_position, first_inversion, second_inversion};
                return generic_chord_with_inversion_validate(chord, diminished_triad_length, diminished_triad_intervals);
            }
        },
        DOMINANT_SEVENTH_VALIDATION {
            @Override
            boolean validate(int[] chord) {
                int dominant_seventh_length = 4;
                int[] root_position = {4,3,3};
                int[] first_inversion = {3,3,2};
                int[] second_inversion = {3,2,4};
                int[] third_inversion = {2,4,3};
                int[][] dominant_seventh_intervals = {root_position,first_inversion,second_inversion,third_inversion};
                return generic_chord_with_inversion_validate(chord, dominant_seventh_length, dominant_seventh_intervals);
            }
        },
        MAJOR_SEVENTH_VALIDATION {
            @Override
            boolean validate(int[] chord) {
                int major_seventh_length = 4;
                int[] root_position = {4,3,4};
                int[] first_inversion = {3,4,1};
                int[] second_inversion = {4,1,4};
                int[] third_inversion = {1,4,3};
                int[][] major_seventh_intervals = {root_position,first_inversion,second_inversion,third_inversion};
                return generic_chord_with_inversion_validate(chord, major_seventh_length, major_seventh_intervals);
            }
        },
        MINOR_SEVENTH_VALIDATION {
            @Override
            boolean validate(int[] chord) {
                int minor_seventh_length = 4;
                int[] root_position = {3,4,3};
                int[] first_inversion = {4,3,2};
                int[] second_inversion = {3,2,3};
                int[] third_inversion = {2,3,4};
                int[][] minor_seventh_intervals = {root_position,first_inversion,second_inversion,third_inversion};
                return generic_chord_with_inversion_validate(chord, minor_seventh_length, minor_seventh_intervals);
            }
        },
        OTHER_FOUR_VALIDATION {
            @Override
            boolean validate(int[] chord) {
                return check_chord_length(chord, 4);
            }
        },
        COMPLEX_CHORD_VALIDATION {
            @Override
            boolean validate(int[] chord) {
                for(int chord_length = 5; chord_length < chord.length; chord_length++) {
                    if(check_chord_length(chord, chord_length)) {
                        return true;
                    }
                }
                return false;
            }
        };

        /**
         * Validate a generic chord given inversions between the intervals.
         * @param chord The chord to be validated.
         * @param chord_length_check The chord length to be checked.
         * @param intervals The intervals to be checked with inversion. Therefore, this array should
         *                  be double the chord_length_check value. For example, a C-major triad will have the
         *                  array [4,3,3,5,5,4] where [4,3] corresponds to root position, [3,5] for first inversion
         *                  and [5,4] second inversion.
         * @return True if this chord does correspond to one set of the intervals described in the intervals
         * variable, otherwise returns false.
         */
        private static boolean generic_chord_with_inversion_validate(int[] chord,
                                                                     int chord_length_check,
                                                                     int[][] intervals)
        {
            // Check if we actually have a triad
            if(!check_chord_length(chord, chord_length_check)) return false;

            // Should be 2 intervals per note in the chord
            if(chord_length_check != intervals.length) return false;
            if(chord_length_check - 1 != intervals[0].length) return false;

            for(int interval = 0; interval < intervals.length; interval++) {
                int[] interval_position = intervals[interval];
                if(generic_chord_validate(chord, chord_length_check, interval_position)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Check if the given chord is actually of a certain length.
         * @param chord Chord to be checked.
         * @param length Length to be compared to.
         * @return True if the number of notes in the chord is equal to the given length, otherwise false.
         */
        private static boolean check_chord_length(int[] chord, int length) {
            int count = 0;
            for(int i = 0; i < chord.length; i++) {
                if(chord[i] > 0) {
                    count++;
                }
            }
            return count == length;
        }

        /**
         * Check if all passed in values are in fact true.
         * @param check_all_intervals The array to be checked.
         * @return True if all values in array are true, otherwise returns false.
         */
        private static boolean all_entries_true(boolean[] check_all_intervals) {
            for(int i = 0; i < check_all_intervals.length; i++) {
                if(check_all_intervals[i] == false) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Generic function to validate a chord w.r.t. a chord length and a set of given intervals.
         * This can be used for any sized chord with any number of intervals.
         * @param chord The chord to be validated.
         * @param chord_length_check The chord length to be checked.
         * @param intervals The intervals between each 2 notes that the chord should be validated to.
         * @return True if the given chord matches all the given intervals, otherwise false
         */
        private static boolean generic_chord_validate(int[] chord, int chord_length_check, int[] intervals) {
            // Check if we have a appropriate chord with corresponding intervals
            if(chord_length_check != intervals.length + 1) return false;

            // Get all sounding notes stored in a list
            List<Integer> notes = new ArrayList<>();
            for(int i = 0; i < chord.length; i++) {
                if(chord[i] > 0) {
                    notes.add(i);
                }
            }

            // Check to see if the given chord matches the given intervals to validate this chord
            boolean is_triad = false;
            int intervals_per_position = chord_length_check - 1;
            boolean[] check_all_intervals = new boolean[intervals_per_position];
            for(int note = 0; note < notes.size() - 1; note++) {
                int note_interval = Math.abs(notes.get(note) - notes.get(note + 1));
                int interval_check = intervals[note];
                if(note_interval == interval_check) {
                    check_all_intervals[note] = true;
                }
            }
            if(all_entries_true(check_all_intervals)) {
                is_triad = true;
            }
            return is_triad;
        }

        abstract boolean validate(int[] chord);
    }
}
