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
public class SparseDiatonicSonorityCompoundFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public SparseDiatonicSonorityCompoundFeature()
	{
		String name = "Prevalence of Sparse Diatonic Sonorities (Compound)";
		String code = "Son-11";
		String description = "to do";
		boolean is_sequential = true;
		int dimensions = 6;
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
		double[] result = new double[6];
		Arrays.fill(result, -1);
		if (sequence_info != null)
		{   
            int[][] unique_sonorities = sequence_info.unique_vertical_sonorities_diatonic_compound;
            double[] sonority_prevalence = sequence_info.vertical_sonority_histogram_diatonic_compound;

            // Get number of notes in sonority with most notes
            int full_son_size = (int) Arrays.stream(unique_sonorities[0]).filter(num -> num != -1).count();

            // Start filling the array of sparse sonorities from the sonority with the most notes, as the
            // sonorities are sorted from most notes to least
            int sparse_index = full_son_size;
            result[sparse_index] = 0.0;

            // Add up prevalence
            for (int i = 0; i<unique_sonorities.length; i++){
                // Get number of notes in current sonority
                int current_son_size = (int) Arrays.stream(unique_sonorities[i]).filter(num -> num != -1).count();

                // If the current sonority has fewer notes than the value of sparse_index, this must be true for
                // all following sonorities. The value of sparse_index is therefore set to this new size. Because
                // of this, only one loop through the sonorities is requires
                if (current_son_size != sparse_index){
                    sparse_index = current_son_size;
                    result[sparse_index] = 0.0;
                } 
                result[sparse_index] += sonority_prevalence[i];
            }
        }
		return result;
	}
}
