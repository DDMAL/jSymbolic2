package jsymbolic2.features.sonority;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Combined prevalence of simple chromatic sonorities that represent triads in root position, first inversion
 * and second inversion. Returns an array with three elements: the first element represents the prevalence of
 * minor triads in all inversions, the second represents the prevalence of major triads in all inversions and
 * the third element gives their combined prevalence. Prevalence of sonorities is calculated in relation to the
 * piece duration. Values returned by this function should therefore be interpreted as percentages of the total
 * duration of the piece for which the sonority represents a triad in the desired inversion. Note that
 * sonorities consisting of 0 pitches (i.e. where all voices are resting) are not considered, not for this
 * feature, nor for the total duration of the piece.
 *
 * @author Jasper Teunen
 */
public class TriadAllChromaticFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public TriadAllChromaticFeature()
	{
		String name = "Prevalence of All Triads (Chromatic)";
		String code = "Son-31";
		String description = "Combined prevalence of simple chromatic sonorities that represent triads in root position, first inversion and second inversion.";
		boolean is_sequential = true;
		int dimensions = 3;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[3];
		dependencies[0] = "Prevalence of Root Position Triads (Chromatic)";
		dependencies[1] = "Prevalence of First Inversion Triads (Chromatic)";
		dependencies[2] = "Prevalence of Second Inversion Triads (Chromatic)";
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
		double[] result = new double[3];
		Arrays.fill(result, 0.0);
		if (sequence_info != null)
		{   
            for (int i=0; i < other_feature_values[0].length; i++) {
                result[i] += other_feature_values[0][i] + other_feature_values[1][i] + other_feature_values[2][i];
            }
        }
		return result;
	}
}
