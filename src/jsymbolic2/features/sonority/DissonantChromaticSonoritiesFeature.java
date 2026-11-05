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
public class DissonantChromaticSonoritiesFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public DissonantChromaticSonoritiesFeature()
	{
		String name = "Prevalence of Dissonant Chromatic Sonorities";
		String code = "Son-21";
		String description = "to do";
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
		Arrays.fill(result, 0.0);
		if (sequence_info != null)
		{   
            // Only looks at simple intervals, this will get accurate results for large sonorities
            int[][] unique_sonorities = sequence_info.unique_vertical_sonorities_chromatic_simple;
            double[] sonority_prevalence = sequence_info.vertical_sonority_histogram_chromatic_simple;

            // Find dissonances and add the prevalence
            for (int i = 0; i<unique_sonorities.length; i++){

                // Boolean that determines whether or not the current sonority contains a dissonance
				boolean count = VerticalSonorityContainer.containsAnyOf(unique_sonorities[i], new int[] {1, 2, 6, 10, 11});
                if (count) {
                    result[0] += sonority_prevalence[i];
                }
            }
        }
		return result;
	}
}
