package jsymbolic2.features.sonority;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;
import java.util.stream.IntStream;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.VerticalSonorityContainer;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * to do
 *
 * @author Jasper Teunen
 */
public class TriadInversionsByLowestNoteChromaticFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public TriadInversionsByLowestNoteChromaticFeature()
	{
		String name = "Prevalence of Chromatic Triad Inversions by Lowest Note Pitch Class";
		String code = "Son-59";
		String description = "to do";
		boolean is_sequential = true;
		int dimensions = 144;
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
		double[] result = new double[144];
		Arrays.fill(result, 0.0);
		if (sequence_info != null)
		{   
            // Get data for chromatic simple sonorities
            int[][][] sons = sequence_info.unique_vertical_sonorities_by_lowest_note_chromatic_simple;
            double[][] hists = sequence_info.vertical_sonority_histogram_by_lowest_note_chromatic_simple;

            // Define which intervals to search in which order
            int[][] inversion_key = new int[6][];
            inversion_key[0] = new int[]{3, 7}; // root pos minor
            inversion_key[1] = new int[]{4, 7}; // root pos major
            inversion_key[2] = new int[]{4, 9}; // first inv minor
            inversion_key[3] = new int[]{3, 8}; // first inv major
            inversion_key[4] = new int[]{5, 9}; // second inv minor
            inversion_key[5] = new int[]{5, 8}; // second inv major

            // Iterate through lowest note pitch classes
            for (int lowest_note = 0; lowest_note < sons.length; lowest_note++){
                // Find desired consonances and add their prevalence
                for (int i = 0; i<sons[lowest_note].length; i++){
                    if ((int) Arrays.stream(sons[lowest_note][i]).filter(num -> num != -1).count() > 1) {
                        for (int j = 0; j<inversion_key.length; j++){
                            // Get all inversions
                            int[] required_intervals = inversion_key[j];
                            boolean count = VerticalSonorityContainer.containsAllOf(sons[lowest_note][i],
                                                required_intervals) &&
                                            VerticalSonorityContainer.containsOnly(sons[lowest_note][i],
                                                IntStream.concat(Arrays.stream(required_intervals), IntStream.of(12)).toArray());
                            if (count) {
                                result[lowest_note*12 + (j/2) * 3 + j % 2] += hists[lowest_note][i];
                            }
                        }
                    }
                }
                // Add for combined prevalences
                for (int j = 0; j < 6; j++) {
                    if (j < 3){
                        result[lowest_note*12 + 2 + j*3] = result[lowest_note*12 + j*3] + result[lowest_note*12 + 1 + j*3];
                    } else {
                        result[lowest_note*12 + j + 6] = result[lowest_note*12 + j%3] + result[lowest_note*12 + 3 + j%3] + result[lowest_note*12 + 6 + j%3];
                    }
                }
            }
        }
		return result;
	}
}
