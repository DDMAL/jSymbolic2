package jsymbolic2.features.sonority;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Returns a list with 21 elements, where element i indicates the prevalence of all sonorities containing two
 * specific simple chromatic intervals. This can be seen as constructing a strictly upper-triangular 7x7 matrix
 * of all possible combinations of two different simple diatonic intervals (2-8). This matrix is then converted
 * to a list in a row-wise manner. In the resulting array, each element i corresponds to the prevalence of a
 * sonority with interval x = 7 - (sqrt(161 - 8i)-1) // 2, and a second interval y = x(x-15)/2+16+i. Or, more
 * simply: the first element represents matrix coordinate (0,1), and the following elements are found by
 * traversing the strictly upper triangular matrix row-wise, this means that elements 0 -> 5 (the first 6
 * elements) contain interval combinations (2,3) through (2,8), elements 5 -> 10 (the next 5 elements) contain
 * interval combinations (3, 4) through (3,8), elements 11 -> 14 (the next 4 elements) hold interval
 * combinations (4,5) through (4,8) and so on.
 *
 * @author Jasper Teunen
 */
public class SonorityWithTwoSpecificDiatonicIntervalsFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public SonorityWithTwoSpecificDiatonicIntervalsFeature()
	{
		String name = "Prevalence of Sonorities With Two Specific Diatonic Intervals (Simple)";
		String code = "Son-48";
		String description = "Returns a list with 21 elements, where element i indicates the prevalence of all sonorities containing two specific simple chromatic intervals.";
		boolean is_sequential = true;
		int dimensions = 21;
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
		double[] result = new double[21];
		Arrays.fill(result, 0.0);
		if (sequence_info != null)
		{   
            // Only looks at simple intervals, the feature vector would be too large for compound intervals
            int[][] unique_sonorities = sequence_info.unique_vertical_sonorities_diatonic_simple;
            double[] sonority_prevalence = sequence_info.vertical_sonority_histogram_diatonic_simple;

            // Go through all sonorities
            for (int i = 0; i<unique_sonorities.length; i++){
                int[] intervals = Arrays.stream(unique_sonorities[i]).filter(x -> x!= -1).toArray();

				if (intervals.length >= 2){ // If at least two intervals
					// Go through all possible pairs of these intervals
					for (int x = 0; x < intervals.length; x++){
						for (int y = x + 1; y < intervals.length; y++){
							// Flattened index of matrix indices i,j found with i*(15-i)/2 + j - 16
							result[Math.round(intervals[x]*(15-intervals[x])/2 + intervals[y] - 16)] += sonority_prevalence[i];
						}
					}
				}

            }
        }
		return result;
	}
}
