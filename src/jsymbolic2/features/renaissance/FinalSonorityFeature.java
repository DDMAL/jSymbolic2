package jsymbolic2.features.renaissance;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the final pitch in every individual voice. An array of 16 values is returned.
 * If more than 16 different notes are present, only the lowest 16 are returned. If less than 16 are present, the
 * redundant values are set to -1. A value of 0 corresponds to C, and pitches increase chromatically by semitone
 * in integer units (e.g. a value of 2 corresponds to D). Enharmonic equivalents are treated as a single pitch
 * class. By looking at the final note in each track/channel rather than looking at the end of the piece,
 * indeterminate longs are included even if their duration is not explicitly encoded.
 *
 * @author Jasper Teunen
 */
public class FinalSonorityFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public FinalSonorityFeature()
	{
		String name = "Final Sonority";
		String code = "Ren-01";
		String description = "A feature calculator that finds the final pitch in every individual voice. An array of 16 values is returned. If more than 16 different notes are present, only the lowest 16 are returned. If less than 16 are present, the redundant values are set to -1. A value of 0 corresponds to C, and pitches increase chromatically by semitone in integer units (e.g. a value of 2 corresponds to D). Enharmonic equivalents are treated as a single pitch class. By looking at the final note in each track/channel rather than looking at the end of the piece, indeterminate longs are included even if their duration is not explicitly encoded.";
		boolean is_sequential = true;
		int dimensions = 16;
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
		double[] result = new double[16];
		Arrays.fill(result, -1);
		if (sequence_info != null)
		{
			for (int i = 0; i < Math.min(16, sequence_info.final_sonority.length); i++){
				result[i] = (double) sequence_info.final_sonority[i];
			}
		} 
		return result;
	}
}
