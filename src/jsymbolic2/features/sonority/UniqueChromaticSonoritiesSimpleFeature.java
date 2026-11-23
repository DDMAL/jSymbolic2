package jsymbolic2.features.sonority;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Returns the number of unique simple interval combinations in all chromatic vertical sonorities of the
 * analysed piece. They are stored as arrays containing their lowest note and ascending intervals from that
 * lowest note, with each interval represented chromatically. This means that 1 represents a minor second (or
 * enharmonic equivalent), 2 a major second (or enharmonic equivalent), 3 a minor third (or enharmonic
 * equivalent) and so on. Because these are simple intervals, no intervals larger than 12 can be obtained, with
 * 12 itself used to distinguish between unisons and octaves. These arrays are always of length 6 and any
 * unused array elements are set to -1, i.e. in a sonority with only 2 intervals, only three elements are
 * needed (for the lowest note + two intervals), meaning that the three other elements would be "-1". A C-major
 * chord in root position built on C4 would be [60, 4, 7, -1, -1, -1], with C4, the lowest note, being midi
 * pitch 60 and the intervals in a major chord in root position being 4 for the major third above the lowest
 * note and 7 for the perfect fifth above the lowest note. This feature only looks at the intervals and not at
 * the lowest note of the sonorities to count the number of unique sonorities.
 *
 * @author Jasper Teunen
 */
public class UniqueChromaticSonoritiesSimpleFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public UniqueChromaticSonoritiesSimpleFeature()
	{
		String name = "Unique Chromatic Sonorities (Simple)";
		String code = "Son-2";
		String description = "The number of unique chromatic sonorities consisting of simple intervals.";
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
            result[0] = sequence_info.number_of_unique_sonorities_chromatic_simple;
        }
		return result;
	}
}
