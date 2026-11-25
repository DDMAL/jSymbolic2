package jsymbolic2.features.sonority;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * to do
 * 
 * @author Jasper Teunen
 */
public class SpecificDiatonicIntervalByLowestNoteFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public SpecificDiatonicIntervalByLowestNoteFeature()
	{
		String name = "Prevalence of Diatonic Intervals by Lowest Note Pitch Class";
		String code = "Son-58";
		String description = "to do";
		boolean is_sequential = true;
		int dimensions = 84;
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
		double[] result = new double[84];
		Arrays.fill(result, 0.0);
		if (sequence_info != null)
		{   
            // Get data for diatonic simple sonorities
            int[][][] sons = sequence_info.unique_vertical_sonorities_by_lowest_note_diatonic_simple;
            double[][] hists = sequence_info.vertical_sonority_histogram_by_lowest_note_diatonic_simple;

			// Iterate through all lowest note pitch classes:
			for (int lowest_note = 0; lowest_note < sons.length; lowest_note++) {
				// Iterate through all vertical sonorities
				for (int i = 0; i<sons[lowest_note].length; i++){
					// Add prevalence for every interval in the sonority
					for (int j = 0; j<sons[lowest_note][i].length; j++){
						if (sons[lowest_note][i][j] != -1)
							result[lowest_note*7 + sons[lowest_note][i][j]-2] += hists[lowest_note][i];
					}
				}
			}
        }
		return result;
	}
}
