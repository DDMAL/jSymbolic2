package jsymbolic2.features.texture;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the standard deviation of the average loudness (MIDI velocity) of notes in
 * each MIDI track/channel voice that contains at least one note. Set to 0 if there are no voices containing 
 * pitched notes.
 *
 * @author Cory McKay and radamian
 */
public class VoiceEqualityDynamicsFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VoiceEqualityDynamicsFeature()
	{
		String name = "Voice Equality - Dynamics";
		String code = "T-8";
		String description = "Standard deviation of the average loudness (MIDI velocity) of notes in each channel that contains at least one note. Set to 0 if there are no voices containing pitched notes.";
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
			// Find the number of pitched MIDI track/channnel pairings with at least one note on
			int active_voices_count = 0;
			for (int n_track = 0; n_track < sequence_info.track_and_channel_statistics.length; n_track++)
				for (int chan = 0; chan < sequence_info.track_and_channel_statistics[n_track].length; chan++)
					if (sequence_info.track_and_channel_statistics[n_track][chan][0] != 0 && chan != 10 - 1)
						active_voices_count++;

			// Store the combined dynamics on each pitched MIDI track/channel pairing with note ons
			double[] dynamics = new double[active_voices_count];
			int count = 0;
			for (int n_track = 0; n_track < sequence_info.track_and_channel_statistics.length; n_track++)
				for (int chan = 0; chan < sequence_info.track_and_channel_statistics[n_track].length; chan++)
					if (sequence_info.track_and_channel_statistics[n_track][chan][0] != 0 && chan != 10 - 1)
					{
						dynamics[count] = (double) sequence_info.track_and_channel_statistics[n_track][chan][2];
						count++;
					}

			// Calculate the standard deviation
			if (dynamics == null || dynamics.length == 0.0)
				value = 0.0;
			else
				value = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(dynamics);
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}