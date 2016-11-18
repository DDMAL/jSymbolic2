package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature vector indicating which unpitched instruments are present. Has one entry for each of the 47 MIDI
 * Percussion Key Map instruments. Each value is set to 1 if at least one note is played using the
 * corresponding instrument, or to 0 if that instrument is never used. It should be noted that only MIDI
 * Channel 10 instruments 35 to 81 are included here, as they are the ones that meet the official standard
 * (they are correspondingly indexed in this feature vector from 0 to 46, such that index 0 corresponds to
 * Acoustic Bass Drum, index 4 corresponds to Hand Clap, etc.).
 *
 * @author Cory McKay
 */
public class UnpitchedInstrumentsPresentFeature
	extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	{
		code = "I-2";
		String name = "Unpitched Instruments Present";
		String description = "A feature vector indicating which unpitched instruments are present. Has one entry for each of the 47 MIDI Percussion Key Map instruments. Each value is set to 1 if at least one note is played using the corresponding instrument, or to 0 if that instrument is never used. It should be noted that only MIDI Channel 10 instruments 35 to 81 are included here, as they are the ones that meet the official standard (they are correspondingly indexed in this feature vector from 0 to 46, such that index 0 corresponds to Acoustic Bass Drum, index 4 corresponds to Hand Clap, etc.).";
		boolean is_sequential = true;
		int dimensions = 47;
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
			result = new double[47];
			for (int instrument = 35; instrument < 82; instrument++)
			{
				if (sequence_info.non_pitched_instrument_prevalence[instrument] > 0)
					result[instrument - 35] = 1.0;
				else result[instrument - 35] = 0.0;
			}
		}
		return result;
	}
}