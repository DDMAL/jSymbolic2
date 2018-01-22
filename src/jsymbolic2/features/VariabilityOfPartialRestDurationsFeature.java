package jsymbolic2.features;

import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that calculates the standard deviation of the durations of rests in the piece,
 * expressed as a fraction of the duration of a quarter note. This is calculated voice-by-voice, where each
 * rest included in the calculation corresponds to a rest in one MIDI channel, regardless of what may or may
 * not be happening simultaneously in any other MIDI channels. Non-pitched (MIDI channel 10) notes ARE
 * considered in this calculation. Only channels containing at least one note are counted in this calculation.
 * Rests shorter than 0.1 of a quarter note are ignored in this calculation.
 *
 * @author Cory McKay
 */
public class VariabilityOfPartialRestDurationsFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VariabilityOfPartialRestDurationsFeature()
	{
		code = "R-51";
		String name = "Variability of Partial Rest Durations";
		String description = "Standard deviation of the durations of rests in the piece, expressed as a fraction of the duration of a quarter note. This is calculated voice-by-voice, where each rest included in the calculation corresponds to a rest in one MIDI channel, regardless of what may or may not be happening simultaneously in any other MIDI channels. Non-pitched (MIDI channel 10) notes ARE considered in this calculation. Only channels containing at least one note are counted in this calculation. Rests shorter than 0.1 of a quarter note are ignored in this calculation.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, description, is_sequential, dimensions);
		dependencies = null;
		offsets = null;
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
		{
			if (sequence_info.rest_durations_separated_by_channel == null)
				value = 0.0;
			else
			{
				double[] rest_durations = mckay.utilities.staticlibraries.ArrayMethods.flattenMatrix(sequence_info.rest_durations_separated_by_channel);
				value = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(rest_durations);
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;		
	}
}