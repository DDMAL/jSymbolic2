package jsymbolic2.features.renaissance;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Prevalence of notes in fourth immediatly above the most prevalent modal fifth.
 *
 * @author Jasper Teunen
 */
public class UpperFourthPrevalenceFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public UpperFourthPrevalenceFeature()
	{
		String name = "Upper Fourth Prevalence";
		String code = "Ren-09";
		String description = "Prevalence of notes in fourth immediatly above the most prevalent modal fifth.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[1];
		dependencies[0] = "Finalis Octave";
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
            int final_in_corr_octave = (int) other_feature_values[0][0];
            double upper_fourth_freq = 0.0;
			int max_increase = Math.min(12, // Ensure that there are no pitches higher than the maximum
				sequence_info.pitch_histogram_of_first_track.length-final_in_corr_octave);
            for (int i = max_increase; i > 7 ; i--) {
                upper_fourth_freq += sequence_info.pitch_histogram_of_first_track[final_in_corr_octave+i];
            }
            result[0] = upper_fourth_freq;
		}
		return result;
	}
}
