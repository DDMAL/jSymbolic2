package jsymbolic2.features.renaissance;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Returns upper bound of modal octave based on lower & upper fourth prevalence: if lower fourth prevalence is
 * greater than upper fourth prevalence, a plagal mode is assumed. If this is reversed, authentic mode is assumed.
 * In case of plagal mode, this feature returns the fifth above the finalis, in case of authentic mode it returns
 * the finalis (in the correct octave). In case of a tie, authentic mode is assumed.
 *
 * @author Jasper Teunen
 */
public class ModalOctaveUpperBoundFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public ModalOctaveUpperBoundFeature()
	{
		String name = "Expected Upper Bound of Modal Octave";
		String code = "Ren-11";
		String description = "Returns upper bound of modal octave based on lower and upper fourth prevalence: if lower fourth prevalence is greater than upper fourth prevalence, a plagal mode is assumed. If this is reversed, authentic mode is assumed. In case of plagal mode, this feature returns the fifth above the finalis, in case of authentic mode it returns the finalis (in the correct octave). In case of a tie, authentic mode is assumed.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[3];
		dependencies[0] = "Finalis Octave";
		dependencies[1] = "Lower Fourth Prevalence";
		dependencies[2] = "Upper Fourth Prevalence";
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
        if (other_feature_values[1][0] > other_feature_values[2][0]){
            result[0] = other_feature_values[0][0] + 7;
        } else if (other_feature_values[1][0] <= other_feature_values[2][0]){
            result[0] = other_feature_values[0][0] + 12;
		}
        return result;
	}
}
