package jsymbolic2.features.instrumentation;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the General MIDI patch (0 is Acoustic Piano, 40 is Violin, etc.) of the 
 * pitched instrument that is used to play more notes than any other pitched instrument. If there is a tie, 
 * then the patch with the lowest code number is reported. Set to 128 if there are no notes in the piece 
 * played by pitched instruments.
 *
 * @author radamian
 */
public class MostCommonPitchedInstrumentFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MostCommonPitchedInstrumentFeature()
	{
		String name = "Most Common Pitched Instrument";
		String code = "I-14";
		String description = "The General MIDI patch (0 is Acoustic Piano, 40 is Violin, etc.) of the pitched instrument that is used to play more notes than any other pitched instrument. If there is a tie, then the patch with the lowest code number is reported. Set to 128 if there are no notes in the piece played by pitched instruments.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = null;
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
				value = 128.0;
			else
			{
				int most_common_instrument = 0;
				for (int instrument = 0; instrument < sequence_info.pitched_instrument_prevalence.length; instrument++)
					if (sequence_info.pitched_instrument_prevalence[instrument][0] > sequence_info.pitched_instrument_prevalence[most_common_instrument][0])
						most_common_instrument = instrument;

				value = (double) most_common_instrument;
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}