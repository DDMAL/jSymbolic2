package jsymbolic2.features.renaissance;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds all pitche classes sounding immediately after the last attacked note of the
 * piece. An array of 12 values is returned, with each element corresponding to a pitch class. If this element i
 * is zero, that means that pitch class i is not present in the final sonority. If it is 1, it is present. The
 * first element of the array represents pitch class C (or B#), the second C#/Db and so on.
 *
 * @author Jasper Teunen
 */
public class FinalSonorityClassesFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public FinalSonorityClassesFeature()
	{
		String name = "Final Sonority Classes";
		String code = "Ren-02";
		String description = "A feature calculator that finds all pitche classes sounding immediately after the last attacked note of the piece. An array of 12 values is returned, with each element corresponding to a pitch class. If this element i is zero, that means that pitch class i is not present in the final sonority. If it is 1, it is present. The first element of the array represents pitch class C (or B#), the second C#/Db and so on.";
		boolean is_sequential = true;
		int dimensions = 12;
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
		double[] result = new double[12];
		if (sequence_info != null)
		{
			if (sequence_info.pitches_present_by_tick_excluding_rests.length > 0)
			{
				int last_tick_index = sequence_info.pitch_classes_present_by_tick_excluding_rests.length;
				short[] final_sonority_classes = sequence_info.pitch_classes_present_by_tick_excluding_rests[last_tick_index-1];

				for (int i = 0; i<final_sonority_classes.length; i++){
                    result[final_sonority_classes[i]] = (double) 1;
                }
			}
		} 
		else result[0] = -1.0;
		return result;
	}
}
