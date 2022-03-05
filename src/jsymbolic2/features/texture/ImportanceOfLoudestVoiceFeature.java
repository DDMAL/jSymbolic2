package jsymbolic2.features.texture;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the difference between the average loudness (MIDI velocity) of the loudest
 * MIDI track/channel voice and the average loudness of the other MIDI track/channel voices. Only voices that
 * contain at least one pitched note are considered. Set to 0 if there are no voices containing pitched
 * notes..
 *
 * @author Cory McKay and radamian
 */
public class ImportanceOfLoudestVoiceFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public ImportanceOfLoudestVoiceFeature()
	{
		String name = "Importance of Loudest Voice";
		String code = "T-12";
		String description = "Difference between the average loudness (MIDI velocity) of the loudest MIDI track/channel voice and the average loudness of the other MIDI track/channel voices. Only voices that contain at least one pitched note are considered. Set to 0 if there are no voices containing pitched notes.";
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

			if (active_voices_count < 2)
				value = 0.0;
			else
			{
				// Find the loudest MIDI track/channel pairing
				int max_so_far = 0;
				int loudest_track = 0;
				int loudest_channel = 0;
				for (int n_track = 0; n_track < sequence_info.track_and_channel_statistics.length; n_track++)
					for (int chan = 0; chan < sequence_info.track_and_channel_statistics[n_track].length; chan++)
						if (sequence_info.track_and_channel_statistics[n_track][chan][0] != 0 && chan != 10 - 1)
							if (sequence_info.track_and_channel_statistics[n_track][chan][2] > max_so_far)
							{
								max_so_far = sequence_info.track_and_channel_statistics[n_track][chan][2];
								loudest_track = n_track;
								loudest_channel = chan;
							}
				double loudness_of_loudest_voice = (double) max_so_far;
				
				// Find the average loudnesses on all other MIDI track/channel pairings
				double[] other_averages = new double[active_voices_count - 1];
				int count = 0;
				for (int n_track = 0; n_track < sequence_info.track_and_channel_statistics.length; n_track++)
					for (int chan = 0; chan < sequence_info.track_and_channel_statistics[n_track].length; chan++)
						if (sequence_info.track_and_channel_statistics[n_track][chan][0] != 0 && 
							chan != 10 - 1 &&
							(n_track != loudest_track && chan != loudest_channel) )
						{
							other_averages[count] = (double) sequence_info.track_and_channel_statistics[n_track][chan][2];
							count++;
						} 
				double average_loudness_of_other_voices = mckay.utilities.staticlibraries.MathAndStatsMethods.getAverage(other_averages);
				
				// Set value
				value = loudness_of_loudest_voice - average_loudness_of_other_voices;
			}
		} 
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}