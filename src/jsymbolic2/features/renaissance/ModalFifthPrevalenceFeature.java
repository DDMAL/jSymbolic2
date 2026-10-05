package jsymbolic2.features.renaissance;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Plagal and authentic variants of modes share a range delineated by the final and an ascending fifth above it.
 * This feature returns the prevalence of notes from the first track within these fifth ranges for all octaves.
 * These values are represented as percentages of the total amount of notes and are returned in an array with
 * 11 elements. The first of these elements represents the prevalence for the fifth starting between pitch values
 * 0 and 11, the second within 12 and 23 and so on. The eleventh element represents an incomplete range:
 * pitches 120 to 127. If the eleventh fifth does not fit within this range, it will return a value of -1.
 *
 * @author Jasper Teunen
 */
public class ModalFifthPrevalenceFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public ModalFifthPrevalenceFeature()
	{
		String name = "Prevalence of Modal Fifths";
		String code = "Ren-05";
		String description = "Plagal and authentic variants of modes share a range delineated by the final and an ascending fifth above it. This feature returns the prevalence of notes from the first track within these fifth ranges for all octaves. These values are represented as percentages of the total amount of notes and are returned in an array with 11 elements. The first of these elements represents the prevalence for the fifth starting between pitch values 0 and 11, the second within 12 and 23 and so on. The eleventh element represents an incomplete range: pitches 120 to 127. If the eleventh fifth does not fit within this range, it will return a value of -1.";
		boolean is_sequential = true;
		int dimensions = 11;
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
		double[] result = new double[11];
		Arrays.fill(result, -1);
		if (sequence_info != null)
		{   
            int final_pitch_class = (int) other_feature_values[0][0];
			for (int i=final_pitch_class; i<sequence_info.pitch_histogram_of_first_track.length-7; i+=12){
                double curr_fifth_freq = sequence_info.pitch_histogram_of_first_track[i];
                for (int j=1; j<8; j++){
                    curr_fifth_freq += sequence_info.pitch_histogram_of_first_track[i+j];
                }
                result[i / 12] = curr_fifth_freq;
            }
		}
		return result;
	}
}
