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
public class TetradInversionsByLowestNoteChromaticFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public TetradInversionsByLowestNoteChromaticFeature()
	{
		String name = "Prevalence of Chromatic Tetrad Inversions by Lowest Note Pitch Class";
		String code = "Son-61";
		String description = "to do";
		boolean is_sequential = true;
		int dimensions = 480;
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
		double[] result = new double[480];
		Arrays.fill(result, 0.0);
		if (sequence_info != null)
		{   
            // Get data for chromatic simple sonorities
            int[][][] sons = sequence_info.unique_vertical_sonorities_by_lowest_note_chromatic_simple;
            double[][] hists = sequence_info.vertical_sonority_histogram_by_lowest_note_chromatic_simple;

            // Define which intervals to search in which order
            int[][] inversion_key = new int[28][];
            // root position
            inversion_key[0] = new int[]{4, 7, 10}; // Dominanth seventh
            inversion_key[1] = new int[]{3, 7, 10}; // Minor seventh
            inversion_key[2] = new int[]{3, 6, 10}; // Half-diminished seventh
            inversion_key[3] = new int[]{4, 7, 11}; // Major seventh
            inversion_key[4] = new int[]{3, 6, 9};  // Diminished seventh
            inversion_key[5] = new int[]{3, 7, 11}; // Minor-major seventh
            inversion_key[6] = new int[]{4, 8, 11}; // Augmented major seventh
            // first inversion
            inversion_key[7] = new int[]{3, 6, 8};  // Dominanth seventh
            inversion_key[8] = new int[]{4, 7, 9};  // Minor seventh
            inversion_key[9] = new int[]{3, 7, 9};  // Half-diminished seventh
            inversion_key[10] = new int[]{3, 7, 8}; // Major seventh
            inversion_key[11] = new int[]{3, 6, 9}; // Diminished seventh
            inversion_key[12] = new int[]{4, 8, 9}; // Minor-major seventh
            inversion_key[13] = new int[]{4, 7, 8}; // Augmented major seventh
            // second inversion
            inversion_key[14] = new int[]{3, 5, 9}; // Dominanth seventh
            inversion_key[15] = new int[]{3, 5, 8}; // Minor seventh
            inversion_key[16] = new int[]{4, 6, 9}; // Half-diminished seventh
            inversion_key[17] = new int[]{4, 5, 9}; // Major seventh
            inversion_key[18] = new int[]{3, 6, 9}; // Diminished seventh
            inversion_key[19] = new int[]{4, 5, 8}; // Minor-major seventh
            inversion_key[20] = new int[]{3, 4, 8}; // Augmented major seventh
            // third inversion
            inversion_key[21] = new int[]{2, 6, 9}; // Dominanth seventh
            inversion_key[22] = new int[]{2, 5, 9}; // Minor seventh
            inversion_key[23] = new int[]{2, 5, 8}; // Half-diminished seventh
            inversion_key[24] = new int[]{1, 5, 8}; // Major seventh
            inversion_key[25] = new int[]{3, 6, 9}; // Diminished seventh
            inversion_key[26] = new int[]{1, 4, 8}; // Minor-major seventh
            inversion_key[27] = new int[]{1, 5, 9}; // Augmented major seventh

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
                                result[lowest_note*40 + (j/7) * 8 + j % 7] += hists[lowest_note][i];
                            }
                        }
                    }
                }
                // Add for combined prevalences
                for (int j = 0; j < 12; j++) {
                    if (j < 4){
                        double sum_of_tetrads = 0;
                        for (int k = 0; k < 7; k++) {
                            sum_of_tetrads += result[lowest_note*40 + j*8 + k];
                        }
                        result[lowest_note*40 + 7 + j*8] = sum_of_tetrads;
                    } else {
                        double sum_of_tetrads = 0;
                        for (int k = 0; k < 4; k++) {
                            sum_of_tetrads += result[lowest_note*40 + k*8 + j-4];
                        }
                        result[lowest_note*40 + 28 + j] = sum_of_tetrads;
                    }
                }
            }
        }
		return result;
	}
}
