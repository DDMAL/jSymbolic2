package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature extractor that calculates a feature vector indicating which pitched instruments are present. Has
 * one entry for each of the 128 General MIDI Instrument patches (0 is Acoustic Piano, 40 is Violin, etc.).
 * Each value is set to 1 if at least one note is played using the corresponding patch, or to 0 if that patch
 * is never used.
 *
 * @author Cory McKay
 */
public class PitchedInstrumentsPresentFeature
	extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public PitchedInstrumentsPresentFeature()
	{
		code = "I-1";
		String name = "Pitched Instruments Present";
		String description = "A feature vector indicating which pitched instruments are present. Has one entry for each of the 128 General MIDI Instrument patches (0 is Acoustic Piano, 40 is Violin, etc.). Each value is set to 1 if at least one note is played using the corresponding patch, or to 0 if that patch is never used.";
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
				if (sequence_info.pitched_instrument_prevalence[instrument][0] > 0)
					result[instrument] = 1.0;
				else result[instrument] = 0.0;
			}
		}
		return result;
	}
}