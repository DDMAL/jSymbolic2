package jsymbolic2.features.sonority;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Proportion of diatonic sonorities made up of compound intervals where the maximum number of unique pitches
 * occuring simultaneously are present. Because sonorities are represented as arrays of six elements, the
 * maximum number of unique pitches is capped to six. Prevalence of sonorities is calculated in relation to the
 * piece duration. Values returned by this function should therefore be interpreted as a percentage of the
 * total duration of the piece for which the sonority is "full".
 * 
 * Diatonic compound sonorities are representations of each vertical pitch collection in the analysed file.
 * They are stored as arrays containing their lowest note and ascending intervals from that lowest note, with
 * each interval represented diatonically. This means that 2 represents a second, 3 a third and so on. These
 * arrays are always of length 6 and any unused array elements are set to -1, i.e. in a sonority with only 2
 * intervals, only three elements are needed (for the lowest note + two intervals), meaning that the three
 * other elements would be "-1". A C-major chord in root position built on C4 would be [60, 3, 5, -1, -1, -1],
 * with C4, the lowest note, being midi pitch 60 and the intervals in a major chord in root position being 3
 * for the third above the lowest note and 5 for the fifth above the lowest note. Note that this is the same
 * representation as that of a C-minor chord, or even a C-diminished chord. The diatonic representation
 * distinguishes between inversions and intervallic makeup of chords, but doesn't distinguish between different
 * types of chords consisting of the same interval types.
 *
 * @author Jasper Teunen
 */
public class FullDiatonicSonorityCompoundFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public FullDiatonicSonorityCompoundFeature()
	{
		String name = "Prevalence of Full Diatonic Sonority (Compound)";
		String code = "Son-7";
		String description = "Proportion of diatonic sonorities made up of compound intervals in which every voice has a different note (if less than 6 voices) or where the maximum amount of different notes (6) is reached.";
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
            int[][] unique_sonorities = sequence_info.unique_vertical_sonorities_diatonic_compound;
            double[] sonority_prevalence = sequence_info.vertical_sonority_histogram_diatonic_compound;
            double full_prevalence = 0.0;

            // Get number of notes in sonority with most notes
            int full_son_size = (int) Arrays.stream(unique_sonorities[0]).filter(num -> num != -1).count();
            for (int i = 0; i<unique_sonorities.length; i++){
                if ((int) Arrays.stream(unique_sonorities[i]).filter(num -> num != -1).count() == full_son_size){
                    full_prevalence += sonority_prevalence[i];
                }
            }
            result[0] = full_prevalence;
        }
		return result;
	}
}
