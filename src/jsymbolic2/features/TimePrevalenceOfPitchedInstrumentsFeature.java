package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature vector indicating the fraction of time during which (pitched) notes are being sounded by each of
 * the 128 General MIDI Instrument patches (0 is Acoustic Piano, 40 is Violin, etc.). Has one entry for each
 * of these instruments, and the value of each is set to to the total time in seconds in a piece during which
 * at least one note is being sounded with the corresponding MIDI patch, divided by the total length of the
 * piece in seconds.
 *
 * @author Cory McKay
 */
public class TimePrevalenceOfPitchedInstrumentsFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public TimePrevalenceOfPitchedInstrumentsFeature()
	{
		code = "I-5";
		String name = "Time Prevalence of Pitched Instruments";
		String description = "A feature vector indicating the fraction of time during which (pitched) notes are being sounded by each of the 128 General MIDI Instrument patches (0 is Acoustic Piano, 40 is Violin, etc.). Has one entry for each of these instruments, and the value of each is set to to the total time in seconds in a piece during which at least one note is being sounded with the corresponding MIDI patch, divided by the total length of the piece in seconds.";
		boolean is_sequential = true;
		int dimensions = 128;
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
		double[] result = null;
		if (sequence_info != null)
		{
			result = new double[128];
			for (int instrument = 0; instrument < result.length; instrument++)
			{
				result[instrument] = sequence_info.pitched_instrument_prevalence[instrument][1]
						/ (double) sequence_info.sequence_duration;
			}
		}
		return result;
	}
}