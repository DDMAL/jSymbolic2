package jsymbolic2.features.sonority;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Returns an array with 7 elements where element i indicates the prevalence of all sonorities containing
 * diatonic simple interval i+2. Unisons are not included, as the vertical sonority representations exclude
 * duplicate pitch classes, but octaves are included, so that diatonic simple intervals 2 through 8 are
 * represented. Prevalence of sonorities is calculated in relation to the piece duration. Values returned by
 * this function should therefore be interpreted as percentages of the total duration of the piece for which
 * the sonority contains diatonic simple interval i+2. Note that sonorities consisting of 0 pitches (i.e. where
 * all voices are resting) are not considered, not for this feature, nor for the total duration of the piece.
 * 
 * @author Jasper Teunen
 */
public class SonorityWithSpecificSimpleDiatonicIntervalFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public SonorityWithSpecificSimpleDiatonicIntervalFeature()
	{
		String name = "Prevalence of Sonorities With Specific Diatonic Intervals (Simple)";
		String code = "Son-46";
		String description = "Returns an array with 7 elements where element i indicates the prevalence of all sonorities containing diatonic simple interval i+2.";
		boolean is_sequential = true;
		int dimensions = 7;
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
		double[] result = new double[7];
		Arrays.fill(result, 0.0);
		if (sequence_info != null)
		{   
            // Get data for diatonic simple sonorities
            int[][] unique_sonorities = sequence_info.unique_vertical_sonorities_diatonic_simple;
            double[] sonority_prevalence = sequence_info.vertical_sonority_histogram_diatonic_simple;

            // Go through all vertical sonorities
            for (int i = 0; i<unique_sonorities.length; i++){

                // Add prevalence for every interval in the sonority
				for (int j = 0; j<unique_sonorities[i].length; j++){
                    if (unique_sonorities[i][j] != -1)
                    	result[unique_sonorities[i][j]-2] += sonority_prevalence[i];
                }
            }
        }
		return result;
	}
}
