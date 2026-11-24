package jsymbolic2.features.sonority;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.Arrays;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Sum of the prevalences of simple chromatic sonorities that represent tetrads in root position, first
 * inversion, second inversion and third inversion. Returns an array with eight elements: the first seven
 * represent the prevalences of specific types of tetrads in third inversion, whilst the eighth element
 * corresponds to their combined prevalence. The seven types of tetrads are, respectively, dominanth seventh,
 * minor seventh, half-diminished seventh, major seventh, diminshed seventh, minor-major seventh and augmented
 * major seventh. Prevalence of sonorities is calculated in relation to the piece duration. Values returned by
 * this function should therefore be interpreted as percentages of the total duration of the piece for which
 * the sonority represents a tetrad of the desired type. Note that sonorities consisting of 0 pitches (i.e.
 * where all voices are resting) are not considered, not for this feature, nor for the total duration of the
 * piece.
 *
 * @author Jasper Teunen
 */
public class TetradAllChromaticFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public TetradAllChromaticFeature()
	{
		String name = "Prevalence of All Tetrads (Chromatic)";
		String code = "Son-41";
		String description = "Sum of the prevalences of simple chromatic sonorities that represent tetrads in root position, first inversion, second inversion and third inversion.";
		boolean is_sequential = true;
		int dimensions = 8;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[4];
		dependencies[0] = "Prevalence of Root Position Tetrads (Chromatic)";
		dependencies[1] = "Prevalence of First Inversion Tetrads (Chromatic)";
		dependencies[2] = "Prevalence of Second Inversion Tetrads (Chromatic)";
		dependencies[3] = "Prevalence of Third Inversion Tetrads (Chromatic)";
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
		Arrays.fill(result, 0.0);
		if (sequence_info != null)
		{   
            for (int i=0; i < other_feature_values.length; i++) {
				for (int j=0; j < other_feature_values[i].length; j++) {
					result[j] += other_feature_values[i][j];
				}
            }
        }
		return result;
	}
}
