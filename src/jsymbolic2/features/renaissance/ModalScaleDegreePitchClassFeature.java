package jsymbolic2.features.renaissance;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Returns an array with eight elements. These elements indicate the pitch class of the scale degree of the mode.
 * These pitches are attained by comparing all pairs of possible pitches for each of the scale degrees and
 * choosing the most prevalent one. E.g. in a C mode, the fourth degree could be F or F#, this feature will
 * return the one that most commonly occurs between these two as its fourth element.
 *
 * @author Jasper Teunen
 */
public class ModalScaleDegreePitchClassFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public ModalScaleDegreePitchClassFeature()
	{
		String name = "Pitch Class of Modal Scale Degrees";
		String code = "Ren-14";
		String description = "Returns an array with eight elements. These elements indicate the pitch class of the scale degree of the mode. These pitches are attained by comparing all pairs of possible pitches for each of the scale degrees and choosing the most prevalent one. E.g. in a C mode, the fourth degree could be F or F#, this feature will return the one that most commonly occurs between these two as its fourth element.";
		boolean is_sequential = true;
		int dimensions = 8;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[1];
		dependencies[0] = "Pitch of Modal Scale Degrees";
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
		double[] result = new double[8];
		Arrays.fill(result, -1);
		if (sequence_info != null)
		{   
            for (int i=0; i<other_feature_values[0].length; i++){
                result[i] = other_feature_values[0][i] % 12;
            }
        }
		return result;
	}
}
