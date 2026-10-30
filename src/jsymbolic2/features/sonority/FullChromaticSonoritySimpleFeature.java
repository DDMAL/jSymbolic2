package jsymbolic2.features.sonority;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Proportion of chromatic sonorities made up of simple intervals in which every voice has a different note
 * (if <=6 voices) or where the maximum amount of different notes (6) is reached.
 *
 * @author Jasper Teunen
 */
public class FullChromaticSonoritySimpleFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public FullChromaticSonoritySimpleFeature()
	{
		String name = "Prevalence of Full Chromatic Sonority (Simple)";
		String code = "Son-6";
		String description = "Proportion of chromatic sonorities made up of simple intervals in which every voice has a different note (if less than 6 voices) or where the maximum amount of different notes (6) is reached.";
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
            int[][] unique_sonorities = sequence_info.unique_vertical_sonorities_simple;
            double[] sonority_prevalence = sequence_info.simple_vertical_sonority_histogram;
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
