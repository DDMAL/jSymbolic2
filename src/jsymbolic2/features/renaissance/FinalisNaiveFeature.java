package jsymbolic2.features.renaissance;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Returns the finalis, assuming all voices are present and that the file is encoded in such a way that its ending
 * is also the piece's ending. This is especially relevant for pieces with repeats, where the piece ending might
 * not correspond to the encodings ending. This feature simply provides the pitch class of the lowest pitch
 * present in the final sonority. A value of 0 corresponds to C, and pitches increase chromatically by semitone
 * in integer units (e.g. a value of 2 corresponds to D). Enharmonic equivalents are treated as a single pitch
 * class. Set to 0 if there are no pitched notes.
 *
 * @author Jasper Teunen
 */
public class FinalisNaiveFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public FinalisNaiveFeature()
	{
		String name = "Finalis Naive";
		String code = "Ren-3";
		String description = "Naive approach to estimating the finalis that assumes all voices are present and that the file is encoded in such a way that its ending is also the piece's ending. This is especially relevant for pieces with repeats, where the piece ending might not correspond to the encodings ending. This feature simply provides the pitch class of the lowest pitch present in the final sonority. A value of 0 corresponds to C, and pitches increase chromatically by semitone in integer units (e.g. a value of 2 corresponds to D). Enharmonic equivalents are treated as a single pitch class. Set to 0 if there are no pitched notes.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[1];
		dependencies[0] = "Final Sonority";
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
		if (sequence_info != null)
		{
			if (sequence_info.pitches_present_by_tick_excluding_rests.length > 0)
			{
				// Final sonority taken from final sonority feature
				double[] final_sonority = other_feature_values[0];

				// Pitch class of lowest pitch in final sonority, this is always simply its first element,
				// as the final sonority feature returns a sorted array.
				result[0] = (double) final_sonority[0] % 12;
			}
			else result[0] = -1.0;
		} 
		else result[0] = -1.0;
		return result;
	}
}
