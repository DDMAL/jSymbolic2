package jsymbolic2.featureutils;

import java.util.ArrayList;
import java.util.List;

/**
 * An enumerator that can be used to identify the type of chord a set of pitch classes represents using the
 * getChordType method. Each chord type is associated with a specific numerical code. The possible chord types
 * and their associated codes are as follows:
 * 
 * <ul>
 * <li><b>PARTIAL_CHORD (0)</b>: Any set of pitch classes consisting of exactly two different pitch classes.
 * <li><b>MINOR_TRIAD (1)</b>: A set of three pitch classes comprising a minor triad, in any inversion.
 * <li><b>MAJOR_TRIAD (2)</b>: A set of three pitch classes comprising a major triad, in any inversion.
 * <li><b>DIMINISHED_TRIAD (3)</b>: A set of three pitch classes comprising a diminished triad, in any inversion.
 * <li><b>AUGMENTED_TRIAD (4)</b>: A set of three pitch classes comprising an augmented triad, in any inversion.
 * <li><b>OTHER_TRIAD (5)</b>: Any set of notes consisting of exactly three different pitch classes. Note that, although this does technically include any triad, in practice it excludes minor, major, diminished and augmented triads when the getChordType method is called (because of order of processing).
 * <li><b>MINOR_SEVENTH (6)</b>: A set of four pitch classes comprising a minor seventh chord, in any inversion.
 * <li><b>DOMINANT_SEVENTH (7)</b>: A set of four pitch classes comprising a dominant seventh chord, in any inversion.
 * <li><b>MAJOR_SEVENTH (8)</b>: A set of four pitch classes comprising a major seventh chord, in any inversion.
 * <li><b>OTHER_FOUR_NOTE_CHORD (9)</b>: Any set of notes consisting of exactly four different pitch classes. Note that, although this does technically include any four note chord, in practice it excludes minor seventh, dominant seventh and major seventh chords when the getChordType method is called (because of order of processing). 
 * <li><b>COMPLEX_CHORD (10)</b>: Any set of notes consisting of more than four different pitch classes.
 * </ul>
 * 
 * @author Tristano Tenaglia and Cory McKay
 */
public enum ChordTypeEnum
{
	/* CONSTANTS ********************************************************************************************/

	
	// Call the constructor for each constant, specifying each associated validator and chord_type_code
	PARTIAL_CHORD(ChordTypeValidationEnum.PARTIAL_CHORD_VALIDATION, 0),
	MINOR_TRIAD(ChordTypeValidationEnum.MINOR_TRIAD_VALIDATION, 1),
	MAJOR_TRIAD(ChordTypeValidationEnum.MAJOR_TRIAD_VALIDATION, 2),
	DIMINISHED_TRIAD(ChordTypeValidationEnum.DIMINISHED_TRIAD_VALIDATION, 3),
	AUGMENTED_TRIAD(ChordTypeValidationEnum.AUGMENTED_TRIAD_VALIDATION, 4),
	OTHER_TRIAD(ChordTypeValidationEnum.OTHER_TRIAD_VALIDATION, 5),
	MINOR_SEVENTH(ChordTypeValidationEnum.MINOR_SEVENTH_VALIDATION, 6),
	DOMINANT_SEVENTH(ChordTypeValidationEnum.DOMINANT_SEVENTH_VALIDATION, 7),
	MAJOR_SEVENTH(ChordTypeValidationEnum.MAJOR_SEVENTH_VALIDATION, 8),
	OTHER_FOUR_NOTE_CHORD(ChordTypeValidationEnum.OTHER_FOUR_NOTE_CHORD_VALIDATION, 9),
	COMPLEX_CHORD(ChordTypeValidationEnum.COMPLEX_CHORD_VALIDATION, 10);


	/* FIELDS ***********************************************************************************************/


	/**
	 * A ChordTypeValidationEnum identifying the type of chord and providing a method for checking to see if
	 * any given set of pitch classes corresponds to this chord type.
	 */
	private final ChordTypeValidationEnum	validator;

	/**
	 * The code indicating the type of chord this is.
	 */
	private final int						chord_type_code;


	/* CONSTRUCTOR ******************************************************************************************/


	/**
	 * Instantiate a ChordTypeEnum corresponding to a certain type of code, as specified by validator and
	 * chord_type_code.
	 * 
	 * @param validator			A ChordTypeValidationEnum identifying the type of chord and providing a method
	 *							for checking to see if any given set of pitch classes corresponds to this
	 *							chord type.
	 * @param chord_type_code	A code matching the specified validator.
	 */
	private ChordTypeEnum(ChordTypeValidationEnum validator, int chord_type_code)
	{
		this.validator = validator;
		this.chord_type_code = chord_type_code;
	}


	/* PUBLIC METHODS ***************************************************************************************/


	/**
	 * @return Get the code indicating the type of chord this is.
	 */
	public int getChordTypeCode()
	{
		return chord_type_code;
	}

	

	/* PUBLIC STATIC METHODS ********************************************************************************/


	/**
	 * Identify the type of chord represented by the given pitch classes.
	 * 
	 * @param pitch_class_strengths	An array of size 12 whose index corresponds to the 12 pitch classes, with 
	 *								index 0 corresponding to the note C. The magnitude of each bin corresponds 
	 *								to the accumulated MIDI velocity of all notes with the given pitch class 
	 *								that are sounding (not including Channel 10 unpitched notes). A value of
	 *								zero in a given bin therefore means that the pitch class with the
	 *								corresponding index is not present. For example, the array
	 *								[10, 0, 0, 0, 20, 0, 0, 64, 0, 0, 0, 0] corresponds to a C major chord,
	 *								where the G is most emphasized, and C is least emphasized.
	 * @return						An identifier indicating the type of chord that pitch_class_strengths
	 *								represents. Null is returned if  pitch_class_strengths does not correspond
	 *								to a recognized chord type.
	 * @throws Exception			An informative exception is thrown if pitch_classes_present is null or is
	 *								not size 12. 
	 */
	public static ChordTypeEnum getChordType(int[] pitch_class_strengths)
			throws Exception
	{
		if (pitch_class_strengths == null || pitch_class_strengths.length != 12)
			throw new Exception("The specified set of pitch classes is null or does not correspond to a known chord type.\n");
		for (ChordTypeEnum chord_type : ChordTypeEnum.values())
			if (chord_type.validator.validate(pitch_class_strengths))
				return chord_type;
		return null; //if no chord type is found
	}


	/* INTERNAL ENUM ****************************************************************************************/

	
	/**
	 * An enumeration of chord type validators, one for each chord type, each of which has its own validate
	 * method to check to see if any given set of pitch classes belongs to the given type of chord.
	 */
	private enum ChordTypeValidationEnum
	{
		/* CONSTANTS ****************************************************************************************//* CONSTANTS ****************************************************************************************/
		
		
		// Implement an appropriate validate method for each type of chord 
		PARTIAL_CHORD_VALIDATION
		{
			@Override
			boolean validate(int[] pitch_class_strengths)
			{
				return checkNumberOfPitchClasses(pitch_class_strengths, 2);
			}
		},
		MINOR_TRIAD_VALIDATION
		{
			@Override
			boolean validate(int[] pitch_class_strengths)
			{
				int minor_triad_length = 3;
				int[] root_position = {3, 4};
				int[] first_inversion = {4, 5};
				int[] second_inversion = {5, 3};
				int[][] minor_triad_intervals = {root_position, first_inversion, second_inversion};
				
				return verifyPitchClassesMatchExpectedIntervalsWithInversions(pitch_class_strengths, minor_triad_length, minor_triad_intervals);
			}
		},	
		MAJOR_TRIAD_VALIDATION
		{
			@Override
			public boolean validate(int[] pitch_class_strengths)
			{
				int major_triad_length = 3;
				int[] root_position = {4, 3};
				int[] first_inversion =	{3, 5};
				int[] second_inversion = {5, 4};
				int[][] major_triad_intervals = {root_position, first_inversion, second_inversion};
				
				return verifyPitchClassesMatchExpectedIntervalsWithInversions(pitch_class_strengths, major_triad_length, major_triad_intervals);
			}
		},
		DIMINISHED_TRIAD_VALIDATION
		{
			@Override
			boolean validate(int[] pitch_class_strengths)
			{
				int diminished_triad_length = 3;
				int[] root_position = {3, 3};
				int[] first_inversion = {3, 6};
				int[] second_inversion = {6, 3};
				int[][] diminished_triad_intervals = {root_position, first_inversion, second_inversion};
				
				return verifyPitchClassesMatchExpectedIntervalsWithInversions(pitch_class_strengths, diminished_triad_length, diminished_triad_intervals);
			}
		},
		AUGMENTED_TRIAD_VALIDATION
		{
			@Override
			boolean validate(int[] pitch_class_strengths)
			{
				int augmented_triad_length = 3;
				int[] root_position = {4, 4};
				int[] first_inversion = {4, 4};
				int[] second_inversion = {4, 4};
				int[][] augmented_triad_intervals = {root_position, first_inversion, second_inversion};
				
				return verifyPitchClassesMatchExpectedIntervalsWithInversions(pitch_class_strengths, augmented_triad_length, augmented_triad_intervals);
			}
		},		
		OTHER_TRIAD_VALIDATION
		{
			@Override
			boolean validate(int[] pitch_class_strengths)
			{
				return checkNumberOfPitchClasses(pitch_class_strengths, 3);
			}
		},
		MINOR_SEVENTH_VALIDATION
		{
			@Override
			boolean validate(int[] pitch_class_strengths)
			{
				int minor_seventh_length = 4;
				int[] root_position = {3, 4, 3};
				int[] first_inversion = {4, 3, 2};
				int[] second_inversion = {3, 2, 3};
				int[] third_inversion = {2, 3, 4};
				int[][] minor_seventh_intervals = {root_position, first_inversion, second_inversion, third_inversion};
				
				return verifyPitchClassesMatchExpectedIntervalsWithInversions(pitch_class_strengths, minor_seventh_length, minor_seventh_intervals);
			}
		},	
		DOMINANT_SEVENTH_VALIDATION
		{
			@Override
			boolean validate(int[] pitch_class_strengths)
			{
				int dominant_seventh_length = 4;
				int[] root_position = { 4, 3, 3 };
				int[] first_inversion = {3, 3, 2};
				int[] second_inversion = {3, 2, 4};
				int[] third_inversion = {2, 4, 3};
				int[][] dominant_seventh_intervals = {root_position, first_inversion, second_inversion, third_inversion};
				
				return verifyPitchClassesMatchExpectedIntervalsWithInversions(pitch_class_strengths, dominant_seventh_length, dominant_seventh_intervals);
			}
		},		
		MAJOR_SEVENTH_VALIDATION
		{
			@Override
			boolean validate(int[] pitch_class_strengths)
			{
				int major_seventh_length = 4;
				int[] root_position = {4, 3, 4};
				int[] first_inversion = {3, 4, 1};
				int[] second_inversion = {4, 1, 4};
				int[] third_inversion = {1, 4, 3};
				int[][] major_seventh_intervals = {root_position, first_inversion, second_inversion, third_inversion};
				
				return verifyPitchClassesMatchExpectedIntervalsWithInversions(pitch_class_strengths, major_seventh_length, major_seventh_intervals);
			}
		},	
		OTHER_FOUR_NOTE_CHORD_VALIDATION
		{
			@Override
			boolean validate(int[] pitch_class_strengths)
			{
				return checkNumberOfPitchClasses(pitch_class_strengths, 4);
			}
		},
		COMPLEX_CHORD_VALIDATION
		{
			@Override
			boolean validate(int[] pitch_class_strengths)
			{
				for (int num_pitch_classes = 5; num_pitch_classes < pitch_class_strengths.length; num_pitch_classes++)
					if (checkNumberOfPitchClasses(pitch_class_strengths, num_pitch_classes))
						return true;
				return false;
			}
		};

		
		/* ABSTRACT METHOD **********************************************************************************/

		
		/**
		 * Return true if the given pitch_class_strengths match the type of chord corresponding to the
		 * implementing ChordTypeValidationEnum constant.
		 * 
		 * @param pitch_class_strengths	An array of size 12 whose index corresponds to the 12 pitch classes,
		 *								with index 0 corresponding to the note C. The magnitude of each bin 
		 * 								corresponds to the accumulated MIDI velocity of all notes with the 
		 * 								given pitch class that are sounding (not including Channel 10
		 * 								unpitched notes). A value of zero in a given bin therefore means that
		 * 								the pitch class with the corresponding index is not present. For 
		 * 								example, the array [10, 0, 0, 0, 20, 0, 0, 64, 0, 0, 0, 0] corresponds
		 * 								to a C major chord, where the G is most emphasized, and C is least
		 * 								emphasized.
		 * @return						True if pitch_class_strengths represents the implementing constant's
		 *								chord type, false otherwise.
		 */
		abstract boolean validate(int[] pitch_class_strengths);


		/* PRIVATE STATIC METHODS ***************************************************************************/


		/**
		 * Check to see if the pitch classes present in pitch_class_strengths are separated by one of the
		 * sets of intervals specified in possible_interval_sets. Note that the actual MIDI velocity strengths
		 * specified in pitch_class_strengths are only considered here in terms of whether they are present
		 * (non-zero) or absent (zero).
		 *
		 * @param pitch_class_strengths		An array of size 12 whose index corresponds to the 12 pitch 
		 *									classes, with index 0 corresponding to the note C. The magnitude
		 *									of each bin corresponds to the accumulated MIDI velocity of all
		 *									notes with the given pitch class that are sounding (not including
		 *									Channel 10 unpitched notes). A value of zero in a given bin 
		 *									therefore means that the pitch class with the corresponding index
		 *									is not present. For example, the array 
		 *									[10, 0, 0, 0, 20, 0, 0, 64, 0, 0, 0, 0] corresponds	to a C major
		 *									chord, where the G is most emphasized, and C is least emphasized.
		 * @param number_expected_pcs		Number of non-zero pitch classes expected in 
		 *									pitch_class_strengths.
		 * @param possible_interval_sets	An array of arrays of ordered pitch intervals (in semitones). The
		 *									outer index identifies a possible inversion of a chord type
		 *									under consideration (e.g. root position, first inversion, etc.). 
		 *									Each entry of an inner array indicates a pitch interval (in 
		 *									semitones) expected for the given inversion of the expected chord
		 *									type. For example, the possible_interval_sets for a minor triad
		 *									would be [[3,4],[4,5],[5,3]]. Each of these interval sets are
		 *									compared to the actual intervals between pitch classes present in
		 *									pitch_class_strengths to see if these pitch classes to in fact
		 *									correspond to one of the inversions of the chord type under
		 *									consideration.
		 * @return							True if the pitch classes present in pitch_class_strengths
		 *									correspond one of the sets of pitch intervals specified in 
		 *									possible_interval_sets. Return false otherwise.
		 */
		private static boolean verifyPitchClassesMatchExpectedIntervalsWithInversions( int[] pitch_class_strengths,
																					   int number_expected_pcs,
																					   int[][] possible_interval_sets )
		{
			// Verify that the appropriate number of pitch classes are present
			if (!checkNumberOfPitchClasses(pitch_class_strengths, number_expected_pcs))
				return false;

			// Verify that that number of pitch classes present matches the number of possible inversions
			if (number_expected_pcs != possible_interval_sets.length)
				return false;

			// Verify that the number of pich clases is appropriately matched to the number of intervals in
			// the first specified inversion, at least
			if (number_expected_pcs != possible_interval_sets[0].length + 1)
				return false;
			
			// Prepare a list of all pitch class numbers that have a non-zero accumulated MIDI velocity
			// (i.e. a list of of pitch classes present)
			List<Integer> pitch_classes_present = new ArrayList<>();
			for (int i = 0; i < pitch_class_strengths.length; i++)
				if (pitch_class_strengths[i] > 0)
					pitch_classes_present.add(i);

			// Check each possible inversion specified in possible_interval_sets to see if one of them
			// contains intervals correspoinding to the specified pitch classes present
			for (int inv = 0; inv < possible_interval_sets.length; inv++)
				if (verifyPitchClassesMatchExpectedIntervals(pitch_classes_present, possible_interval_sets[inv]))
					return true;
			
			// Return false if the pitch classes present do not match any of the specified sets of intervals
			return false;
		}


		/**
		 * Compare the given pitch_classes_present with the given expected_intervals in order to see if the
		 * pitch classes present really are separated by the specified intervals (taking order into account).
		 *
		 * @param pitch_classes_present A list of pitch class identifiers noting all pitch classes present.
		 *								0 corresponds to C, 1 to C#/Db, 2 to D, etc. These must be pre-sorted
		 *								from smallest to largest.
		 * @param expected_intervals	An array of pitch intervals (in semitones). These are compared with
		 *								the pitch classes specified in pitch_classes_present (taking order 
		 *								into account), in order to see if the pitch classes there are in fact
		 *								separated by the intervals specified here.
		 * @return						True if the expected_intervals correspond to pitch_classes_present, 
		 *								false otherwise.
		 */
		private static boolean verifyPitchClassesMatchExpectedIntervals( List<Integer> pitch_classes_present,
																		 int[] expected_intervals )
		{
			// Verify that that the number of pitch classes and intervals are appropriate for one another
			if (pitch_classes_present.size() != expected_intervals.length + 1)
				return false;

			// Check if the pitches are in fact separated by the expected intervals
			boolean[] matching_intervals = new boolean[expected_intervals.length];
			for (int pc = 0; pc < pitch_classes_present.size() - 1; pc++)
			{
				int actual_interval = Math.abs(pitch_classes_present.get(pc) - pitch_classes_present.get(pc + 1));
				if (actual_interval == expected_intervals[pc])
					matching_intervals[pc] = true;
				else matching_intervals[pc] = false;
			}
			
			// Only return true if all intervals match
			return checkAllEntriesTrue(matching_intervals);
		}
		
		
		/**
		 * Check if the number of (non-zero) pitch classes in pitch_classes_present match that specified in
		 * number_expected_pitch_classes.
		 *
		 * @param pitch_class_strengths An array of size 12 whose index corresponds to the 12 pitch classes,
		 *								with index 0 corresponding to the note C. The magnitude of each bin 
		 * 								corresponds to the accumulated MIDI velocity of all notes with the 
		 * 								given pitch class that are sounding (not including Channel 10
		 * 								unpitched notes). A value of zero in a given bin therefore means that
		 * 								the pitch class with the corresponding index is not present. For 
		 * 								example, the array [10, 0, 0, 0, 20, 0, 0, 64, 0, 0, 0, 0] corresponds
		 * 								to a C major chord, where the G is most emphasized, and C is least
		 * 								emphasized.
		 * @param number_expected_pcs	Number of non-zero pitch classes expected in pitch_class_strengths.
		 * @return						True if number_expected_pcs matches the number of non-zero entries
		 *								in pitch_classes_present, false otherwise.
		 */
		private static boolean checkNumberOfPitchClasses( int[] pitch_class_strengths,
														  int number_expected_pcs )
		{
			int count = 0;
			for (int i = 0; i < pitch_class_strengths.length; i++)
				if (pitch_class_strengths[i] > 0)
					count++;
			return count == number_expected_pcs;
		}

		
		/**
		 * Check if all entries in the given to_check array are set to true.
		 *
		 * @param to_check	The array to check.
		 * @return			True if all values in to_check are true, false otherwise.
		 */
		private static boolean checkAllEntriesTrue(boolean[] to_check)
		{
			for (int i = 0; i < to_check.length; i++)
				if (to_check[i] == false)
					return false;
			return true;
		}
	}
}