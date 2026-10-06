package jsymbolic2.features.renaissance;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Returns an array with eight elements. These elements indicate the pitches of the scale degree of the mode.
 * These pitches are attained by comparing all pairs of possible pitches for each of the scale degrees and
 * choosing the most prevalent one. E.g. in a C mode, the fourth degree could be F or F#, this feature will
 * return the one that most commonly occurs between these two as its fourth element.
 *
 * @author Jasper Teunen
 */
public class ModalScaleDegreePitchFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public ModalScaleDegreePitchFeature()
	{
		String name = "Pitch of Modal Scale Degrees";
		String code = "Ren-13";
		String description = "Returns an array with eight elements. These elements indicate the pitches of the scale degree of the mode. These pitches are attained by comparing all pairs of possible pitches for each of the scale degrees and choosing the most prevalent one. E.g. in a C mode, the fourth degree could be F or F#, this feature will return the one that most commonly occurs between these two as its fourth element.";
		boolean is_sequential = true;
		int dimensions = 8;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[1];
		dependencies[0] = "Expected Lower Bound of Modal Octave";
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
            short first_degree = (short) other_feature_values[0][0];
            result[0] = other_feature_values[0][0];
            result[7] = other_feature_values[0][0] + 12;

            // Second degree: flat or natural
            if (sequence_info.pitch_histogram_of_first_track[first_degree + 1] >
                sequence_info.pitch_histogram_of_first_track[first_degree + 2]){
                    result[1] = (double) first_degree + 1;
                } else result[1] = (double) first_degree + 2;

            // Third degree: flat or natural
            if (sequence_info.pitch_histogram_of_first_track[first_degree + 3] >
                sequence_info.pitch_histogram_of_first_track[first_degree + 4]){
                    result[2] = (double) first_degree + 3;
                } else result[2] = (double) first_degree + 4;

            // Fourth degree: natural or sharp
            if (sequence_info.pitch_histogram_of_first_track[first_degree + 6] >
                sequence_info.pitch_histogram_of_first_track[first_degree + 5]){
                    result[3] = (double) first_degree + 6;
                } else result[3] = (double) first_degree + 5;

            // Fifth degree: flat or natural
            if (sequence_info.pitch_histogram_of_first_track[first_degree + 6] >
                sequence_info.pitch_histogram_of_first_track[first_degree + 7]){
                    result[4] = (double) first_degree + 6;
                } else result[4] = (double) first_degree + 7;

            // Sixth degree: flat or natural
            if (sequence_info.pitch_histogram_of_first_track[first_degree + 8] >
                sequence_info.pitch_histogram_of_first_track[first_degree + 9]){
                    result[5] = (double) first_degree + 8;
                } else result[5] = (double) first_degree + 9;

            // Seventh degree: flat or natural
            if (sequence_info.pitch_histogram_of_first_track[first_degree + 10] >
                sequence_info.pitch_histogram_of_first_track[first_degree + 11]){
                    result[6] = (double) first_degree + 10;
                } else result[6] = (double) first_degree + 11;
        }
		return result;
	}
}
