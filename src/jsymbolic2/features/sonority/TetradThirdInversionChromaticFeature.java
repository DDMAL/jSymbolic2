package jsymbolic2.features.sonority;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.VerticalSonorityContainer;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * todo
 *
 * @author Jasper Teunen
 */
public class TetradThirdInversionChromaticFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public TetradThirdInversionChromaticFeature()
	{
		String name = "Prevalence of Third Inversion Tetrads (Chromatic)";
		String code = "Son-39";
		String description = "to do";
		boolean is_sequential = true;
		int dimensions = 8;
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
		double[] result = new double[8];
		Arrays.fill(result, 0.0);
		if (sequence_info != null)
		{   
            // Only looks at simple intervals, this will get accurate results for large sonorities
            int[][] unique_sonorities = sequence_info.unique_vertical_sonorities_chromatic_simple;
            double[] sonority_prevalence = sequence_info.vertical_sonority_histogram_chromatic_simple;

			// All possible tetrads, loosely ordered from most to least common to reduce iterations
			int[][] tetrads = new int[][] {
				{2, 6, 9}, // Dominant Seventh
				{2, 5, 9}, // Minor Seventh
				{2, 5, 8}, // Half-diminished Seventh
				{1, 5, 8}, // Major Seventh
				{3, 6, 9}, // Diminished Seventh
				{1, 4, 8}, // Minor Major Seventh
				{1, 5, 9}, // Augmented Major Seventh
			};

            // Find desired consonances and add their prevalence
            for (int i = 0; i<unique_sonorities.length; i++){

				// Check if sonority has at least 3 intervals
				if (unique_sonorities[i][2] != -1) {

					// Go through array tetrads to check all tetrads one by one
					for (int j = 0; j<tetrads.length; j++) {
						// Allowed intervals is the notes in the tetrad and octaves
						int[] allowed_intervals = new int[tetrads[j].length + 1];
						for (int k = 0; k< allowed_intervals.length; k++) {
							allowed_intervals[k] = tetrads[j][k];
						}
						allowed_intervals[tetrads[j].length] = 12;

						// Check if the current sonority is of this tetrad type
						boolean count_tetrad = VerticalSonorityContainer.containsAllOf(unique_sonorities[i], tetrads[j]) && 
							VerticalSonorityContainer.containsOnly(unique_sonorities[i], allowed_intervals);

						// If it is, add to result and immediately go to next sonority
						if (count_tetrad) {
							result[j] += sonority_prevalence[i];
							break;
						}
					}

                }
            }
			// Set last element of result to combined prevalence of tetrads
			result[7] = Arrays.stream(result).sum();
        }
		return result;
	}
}
