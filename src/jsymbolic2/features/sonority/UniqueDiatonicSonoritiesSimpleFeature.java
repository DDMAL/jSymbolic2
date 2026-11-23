package jsymbolic2.features.sonority;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Returns the number of unique simple interval combinations in all diatonic vertical sonorities of the
 * analysed piece. Diatonic intervals are converted from chromatic intervals in the following manner:
 * 
 * 1, 2   -> 2 (second)  
 * 3, 4   -> 3 (third)  
 * 5      -> 4 (fourth)  
 * 6, 7   -> 5 (fifth)  
 * 8, 9   -> 6 (sixth)  
 * 10, 11 -> 7 (seventh)  
 * 12     -> 8 (eighth/octave)  
 * 13, 14 -> 2 (second)  
 * 15, 16 -> 3 (third)  
 * etc.
 * 
 * This means that no distinction is made between minor and major version of intervals and that
 * augmented/diminished intervals are treated as their enharmonic equivalent. One uncomfortable assumption has
 * to be made: the tritone is treated as a diminished fifth and not as an augmented fourth. Due to the
 * prevalence of diminished fifths in seventh chords for tonal music and the aversion of augmented fourths in
 * renaissance theory, this assumption is deemed preferable over the inverse assumption.
 * 
 * Diatonic vertical sonorities are representations of each vertical pitch collection in the analysed file.
 * They are stored as arrays containing their lowest note and ascending intervals from that lowest note, with
 * each interval represented diatonically. This means that 2 represents a second, 3 a third and so on. Because
 * these are simple intervals, no intervals larger than 8 can be obtained, with 8 itself used to distinguish
 * between unisons and octaves. These arrays are always of length 6 and any unused array elements are set to -1,
 * i.e. in a sonority with only 2 intervals, only three elements are needed (for the lowest note + two intervals
 * , meaning that the three other elements would be "-1". A C-major chord in root position built on C4 would be
 * [60, 3, 5, -1, -1, -1], with C4, the lowest note, being midi pitch 60 and the intervals in a major chord in
 * root position being 3 for the third above the lowest note and 5 for the fifth above the lowest note. Note
 * that this is the same representation as that of a C-minor chord, or even a C-diminished chord. The diatonic
 * representation distinguishes between inversions and intervallic makeup of chords, but doesn't distinguish
 * between different types of chords consisting of the same interval types. This feature only looks at the
 * intervals and not at the lowest note of the sonorities to count the number of unique sonorities.
 *
 * @author Jasper Teunen
 */
public class UniqueDiatonicSonoritiesSimpleFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public UniqueDiatonicSonoritiesSimpleFeature()
	{
		String name = "Unique Diatonic Sonorities (Simple)";
		String code = "Son-4";
		String description = "The number of unique diatonic sonorities consisting of simple intervals.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = null;
		offsets = null;
		is_default = true;
		is_secure = true;
	}
	

	/* PUBLIC METHODS ***************************************************************************************/
	
	
	/**
	 * Extract this feature from the given sequence of MIDI data and its associated information.
	 *
	 * @param sequence				The MIDI data to extract the feature from.
	 * @param sequence_info			Additional data already extracted from the the MIDI sequence.
	 * @param other_feature_values	The values of other features that may be needed to calculate this feature. 
	 *								The order and offsets of these features must be the same as those returned
	 *								by this class' getDependencies and getDependencyOffsets methods, 
	 *								respectively. The first indice indicates the feature/window, and the 
	 *								second indicates the value.
	 * @return						The extracted feature value(s).
	 * @throws Exception			Throws an informative exception if the feature cannot be calculated.
	 */
	@Override
	public double[] extractFeature( Sequence sequence,
									MIDIIntermediateRepresentations sequence_info,
									double[][] other_feature_values )
	throws Exception
	{
		double[] result = new double[1];
		Arrays.fill(result, -1);
		if (sequence_info != null)
		{   
            result[0] = sequence_info.number_of_unique_sonorities_diatonic_simple;
        }
		return result;
	}
}
