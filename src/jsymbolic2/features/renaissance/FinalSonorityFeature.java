package jsymbolic2.features.renaissance;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds all pitches sounding immediately after the last attacked note of the piece.
 * An array of 16 values is returned. If more than 16 different notes are present, only the lowest 16 are returned.
 * If less than 16 are present, the redundant values are set to -1. A value of 0 corresponds to C, and pitches
 * increase chromatically by semitone in integer units (e.g. a value of 2 corresponds to D). Enharmonic equivalents
 * are treated as a single pitch class.
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
		String description = "A feature calculator that finds all pitches sounding immediately after the last attacked note of the piece.An array of 16 values is returned. If more than 16 different notes are present, only the lowest 16 are returned. If less than 16 are present, the redundant values are set to -1. A value of 0 corresponds to C, and pitches increase chromatically by semitone in integer units (e.g. a value of 2 corresponds to D). Enharmonic equivalents are treated as a single pitch class.";
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
			if (sequence_info.pitches_present_by_tick_excluding_rests.length > 0)
			{
				int last_tick_index = sequence_info.pitches_present_by_tick_excluding_rests.length;
				short[] final_sonority = sequence_info.pitches_present_by_tick_excluding_rests[last_tick_index-1];

				for (int i = 0; i<Math.min(final_sonority.length, 16); i++){
                    result[i] = (double) final_sonority[i];
                }
			}
		} 
		return result;
	}
}
