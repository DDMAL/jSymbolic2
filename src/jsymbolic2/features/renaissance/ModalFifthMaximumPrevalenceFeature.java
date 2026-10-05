package jsymbolic2.features.renaissance;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Plagal and authentic variants of modes share a range delineated by the final and an ascending fifth above it.
 * This feature returns prevalence of notes from the first track within these fifth ranges for all octaves.
 * This value is represented as a percentage of the total amount of notes. In case of a tie between two or more
 * fifth ranges, the prevalence of the lowest range is returned.
 *
 * @author Jasper Teunen
 */
public class ModalFifthMaximumPrevalenceFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public ModalFifthMaximumPrevalenceFeature()
	{
		String name = "Maximum Prevalence of Modal Fifth";
		String code = "Ren-06";
		String description = "Plagal and authentic variants of modes share a range delineated by the final and an ascending fifth above it. This feature returns prevalence of notes from the first track within these fifth ranges for all octaves. This value is represented as a percentage of the total amount of notes. In case of a tie between two or more fifth ranges, the prevalence of the lowest range is returned.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[1];
		dependencies[0] = "Prevalence of Modal Fifths";
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
            for (int i = 0; i < fifth_prevalences.length; i++) {
                if (fifth_prevalences[i] > max_prevalence)
                    max_prevalence = fifth_prevalences[i];
            }
            result[0] = max_prevalence;
        }
		return result;
	}
}
