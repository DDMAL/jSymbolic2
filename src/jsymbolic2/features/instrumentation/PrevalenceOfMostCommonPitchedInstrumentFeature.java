package jsymbolic2.features.instrumentation;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the fraction of notes played by the pitched instrument that is used to play 
 * more notes than any other pitched instrument. Set to 0 if there are no notes in the piece played by pitched 
 * instruments.
 *
 * @author radamian
 */
public class PrevalenceOfMostCommonPitchedInstrumentFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public PrevalenceOfMostCommonPitchedInstrumentFeature()
	{
		String name = "Prevalence of Most Common Pitched Instrument";
		String code = "I-16";
		String description = "The fraction of notes played by the pitched instrument that is used to play more notes than any other pitched instrument. Set to 0 if there are no notes in the piece played by pitched instruments.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[1];
		dependencies[0] = "Most Common Pitched Instrument";
		offsets = null;
		is_default = true;
		is_secure = false;
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
			if (sequence_info.total_number_pitched_note_ons == 0)
				value = 0.0;
			else
			{
				int most_common_instrument = (int) other_feature_values[0][0];
			
				// Calculate the feature value
				value = (double) sequence_info.pitched_instrument_prevalence[most_common_instrument][0] / sequence_info.total_number_note_ons;
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}