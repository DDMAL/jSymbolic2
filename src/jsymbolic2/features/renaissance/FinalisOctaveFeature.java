package jsymbolic2.features.renaissance;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Returns the absolute pitch of the final that yields the most prevalent modal fifth. As such, this value
 * determines the octave of the modal ambitus.
 *
 * @author Jasper Teunen
 */
public class FinalisOctaveFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public FinalisOctaveFeature()
	{
		String name = "Finalis Octave";
		String code = "Ren-07";
		String description = "Returns the absolute pitch of the final that yields the most prevalent modal fifth. As such, this value determines the octave of the modal ambitus.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[2];
		dependencies[0] = "Prevalence of Modal Fifths";
        dependencies[1] = "Finalis Heuristic";
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
            double[] fifth_prevalences = other_feature_values[0];
            double max_prevalence = 0.0;
            int max_octave = 0;
            for (int i = 0; i < fifth_prevalences.length; i++) {
                if (fifth_prevalences[i] > max_prevalence){
                    max_prevalence = fifth_prevalences[i];
                    max_octave = i;
                }
            }
            result[0] = other_feature_values[1][0] + 12 * max_octave;
        }
		return result;
	}
}
