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
public class ImperfectConsonanceOnlyDiatonicWithOctavesFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public ImperfectConsonanceOnlyDiatonicWithOctavesFeature()
	{
		String name = "Prevalence of Sonorities With Only Imperfect Diatonic Consonance (With Octaves)";
		String code = "Son-20";
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

            double desired_prevalence = 0.0;

            // Desired consonances
            int[] desired_consonances = {1, 3, 6, 8, -1};

            // Find desired consonances and add their prevalence
            for (int i = 0; i<unique_sonorities.length; i++){
                // Boolean that determines whether or not to add the current sonority
                boolean count = true;

                // Ids for consonance and interval
                int cons_id = 0;
                int itv_id = 0;

                // Break if all intervals checked
                while (itv_id < unique_sonorities[i].length && cons_id < desired_consonances.length) {
                    int itv = unique_sonorities[i][itv_id];
                    int cons = desired_consonances[cons_id];
					// If the current interval is the current consonance, check the next interval
                    if (itv == cons) {
                        itv_id++;

                    // If it isn't, check the next consonance
                    } else {
                        cons_id++;
						// If this was already the last consonance, the interval was not a match and the sonority
						// is not counted
						if (cons_id >= desired_consonances.length){
							count = false;
						}
					}
                }
                // If the loop was exited with count remaining true, all intervals were desired consonances, so the
                // prevalence will be counted
                if (count) {
                    desired_prevalence += sonority_prevalence[i];
                }
            }
            result[0] = desired_prevalence;
        }
		return result;
	}
}
