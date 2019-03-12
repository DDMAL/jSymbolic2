package jsymbolic2.features.pitchstatistics;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the skewness of the MIDI pitches of all pitched notes in the piece. 
 * Provides a measure of how asymmetrical the pitch distribution is to either the left or the right of the 
 * mean pitch. A value of zero indicates no skew.
 *
 * @author Cory McKay
 */
public class PitchSkewnessFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public PitchSkewnessFeature()
	{
		String name = "Pitch Skewness";
		String code = "P-27";
		String description = "Skewness of the MIDI pitches of all pitched notes in the piece. Provides a measure of how asymmetrical the pitch distribution is to either the left or the right of the mean pitch. A value of zero indicates no skew.";
		boolean is_sequential = true;
		int dimensions = 1;
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
		double value;
		if (sequence_info != null)
			value = mckay.utilities.staticlibraries.MathAndStatsMethods.getMedianSkewness(sequence_info.pitches_of_all_note_ons);
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}