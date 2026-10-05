package jsymbolic2.features.renaissance;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Returns the species of fourth and fifth as a two-dimensional value. The first value gives the species of fourth,
 * whilst the second indicates that of the fifth Intended for midi/mei files with only one track, if multiple 
 * tracks are present, only the highest track is used. The species of fourth ranges from 0-2, indicating
 * re-sol if 0, mi-la if 1 and ut-fa if 2, the species of fifth range from 0-3, 0: re-la, 1: mi-mi, 2: fa-fa and
 * 3: ut-sol.
 *
 * @author Jasper Teunen
 */
public class ModalSpeciesFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public ModalSpeciesFeature()
	{
		String name = "Modal Species";
		String code = "Ren-05";
		String description = "Returns the species of fourth and fifth as a two-dimensional value. The first value gives the species of fourth, whilst the second indicates that of the fifth. Intended for midi/mei files with only one track, if multiple tracks are present, only the first is used. The species of fourth ranges from 0-2, indicating re-sol if 0, mi-la if 1 and ut-fa if 2, the species of fifth range from 0-3, 0: re-la, 1: mi-mi, 2: fa-fa and 3: ut-sol.";
		boolean is_sequential = true;
		int dimensions = 10;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[1];
		dependencies[0] = "Finalis Heuristic";
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
		double[] result = new double[10];
		Arrays.fill(result, -1);
		if (sequence_info != null)
		{   
            // Find fifth
            double max_fifth_freq = 0.0;
            int final_in_corr_octave = 0;
            int final_pitch_class = (int) other_feature_values[0][0];
			for (int i=final_pitch_class; i<sequence_info.pitch_histogram_of_first_track.length-7; i+=12){
                double curr_fifth_freq = sequence_info.pitch_histogram_of_first_track[i];
                for (int j=1; j<8; j++){
                    curr_fifth_freq += sequence_info.pitch_histogram_of_first_track[i+j];
                }
                if (curr_fifth_freq>max_fifth_freq){
                    max_fifth_freq = curr_fifth_freq;
                    final_in_corr_octave = i;
                }
            }
            double lower_fourth_freq = 0.0;
            double upper_fourth_freq = 0.0;
            for (int i = 0; i < 5; i++) {
                lower_fourth_freq += sequence_info.pitch_histogram_of_first_track[final_in_corr_octave-5+i];
                upper_fourth_freq += sequence_info.pitch_histogram_of_first_track[final_in_corr_octave+8+i];
            }
            result[0] = final_in_corr_octave;
            result[1] = max_fifth_freq;
            result[2] = lower_fourth_freq;
            result[3] = upper_fourth_freq;
            if (lower_fourth_freq > upper_fourth_freq){
                result[4] = final_in_corr_octave - 5;
            } else result[4] = final_in_corr_octave;
            result[5] = result[4] + 12;
            int max = 0;
            double max_freq = 0.0;
            int sec_max = 0;
            double sec_max_freq = 0.0;
            for (int i = (int) result[4]; i<result[5]+1; i++){
                double curr_freq = sequence_info.pitch_histogram_of_first_track[i];
                if (curr_freq > max_freq){
                    sec_max = max;
                    sec_max_freq = max_freq;
                    max_freq = curr_freq;
                    max = i;
                }
                else{
                    if (curr_freq > sec_max_freq){
                        sec_max = i;
                        sec_max_freq = curr_freq;
                    }
                }
            }
            result[6] = max;
            result[7] = sec_max;
            result[8] = max_freq;
            result[9] = sec_max_freq;
		}
		return result;
	}
}
