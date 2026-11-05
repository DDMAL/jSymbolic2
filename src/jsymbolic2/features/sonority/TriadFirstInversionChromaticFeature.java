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
public class TriadFirstInversionChromaticFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public TriadFirstInversionChromaticFeature()
	{
		String name = "Prevalence of First Inversion Triads (Chromatic)";
		String code = "Son-27";
		String description = "to do";
		boolean is_sequential = true;
		int dimensions = 3;
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
		double[] result = new double[3];
		Arrays.fill(result, 0.0);
		if (sequence_info != null)
		{   
            // Only looks at simple intervals, this will get accurate results for large sonorities
            int[][] unique_sonorities = sequence_info.unique_vertical_sonorities_chromatic_simple;
            double[] sonority_prevalence = sequence_info.vertical_sonority_histogram_chromatic_simple;

            // Find desired consonances and add their prevalence
            for (int i = 0; i<unique_sonorities.length; i++){

                // Boolean that determines whether or not the current sonority is a minor triad in first inversion.
				// Checks if it contains a major third and a major sixth. Octaves are allowed but not required.
				boolean count_min = VerticalSonorityContainer.containsAllOf(unique_sonorities[i], new int[] {4, 9}) &&
				VerticalSonorityContainer.containsOnly(unique_sonorities[i], new int[] {4, 9, 12});

                // If it is a minor triad in first inversion, add it to the first element of result
                if (count_min) {
                    result[0] += sonority_prevalence[i];
                } else {
                // Boolean that determines whether or not the current sonority is a major triad in first inversion.
				// Checks if it contains a minor third and a minor sixth. Octaves are allowed but not required.
                boolean count_maj = VerticalSonorityContainer.containsAllOf(unique_sonorities[i], new int[] {3, 8}) &&
				VerticalSonorityContainer.containsOnly(unique_sonorities[i], new int[] {3, 8, 12});

					// If it is a major triad in first inversion, add it to the second element of result
					if (count_maj) {
						result[1] += sonority_prevalence[i];
					}
				}
            }
			// Set third element of result to combined prevalence of minor and major triads
			result[2] = result[0] + result[1];
        }
		return result;
	}
}
