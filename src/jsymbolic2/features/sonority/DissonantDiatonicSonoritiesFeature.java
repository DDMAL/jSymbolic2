package jsymbolic2.features.sonority;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * todo
 *
 * @author Jasper Teunen
 */
public class DissonantDiatonicSonoritiesFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public DissonantDiatonicSonoritiesFeature()
	{
		String name = "Prevalence of Dissonant Diatonic Sonorities";
		String code = "Son-22";
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
		Arrays.fill(result, -1);
		if (sequence_info != null)
		{   
            // Only looks at simple intervals, this will get accurate results for large sonorities
            int[][] unique_sonorities = sequence_info.unique_vertical_sonorities_diatonic_simple;
            double[] sonority_prevalence = sequence_info.vertical_sonority_histogram_diatonic_simple;

            double dissonance_prevalence = 0.0;

            int[] dissonances = {2, 7};

            // Find desired dissonances and add their prevalence
            for (int i = 0; i<unique_sonorities.length; i++){
                // Boolean that determines whether or not to add the current sonority
                boolean count = false;

                // Ids for dissonance and interval
                int diss_id = 0;
                int itv_id = 0;

                // Break if all intervals checked
                while (itv_id < unique_sonorities[i].length && diss_id < dissonances.length) {
                    int itv = unique_sonorities[i][itv_id];
                    int diss = dissonances[diss_id];

                    // If the current interval is larger than the current dissonance, take the next dissonance
                    if (itv > diss){
                        diss_id++;

                    // If it is smaller, this means the interval is consonant, so take the next interval
                    // not counted
                    } else if (itv < diss) {
						itv_id++;

                    // If neither of the previous were true, the interval must be dissonant, so the sonority must
                    // be counted
                    } else {
                        count = true;
						break;
                    }
                }
                // If the loop was exited with count set to true, there was a dissonance
                if (count) {
                    dissonance_prevalence += sonority_prevalence[i];
                }
            }
            result[0] = dissonance_prevalence;
        }
		return result;
	}
}
