package jsymbolic2.features.texture;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the number of Note Ons in the MIDI track/channel voice with the lowest 
 * average pitch, divided by the average number of Note Ons in all channels that contain at least one note. 
 * Set to 0 if there are no voices containing pitched notes.
 *
 * @author radamian
 */
public class RelativeNoteDensityOfLowestVoiceFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public RelativeNoteDensityOfLowestVoiceFeature()
	{
		String name = "Relative Note Density of Lowest Voice";
		String code = "T-22";
		String description = "Number of Note Ons in the MIDI track/channel voice with the lowest average pitch, divided by the average number of Note Ons in all channels that contain at least one note. Set to 0 if there are no voices containing pitched notes.";
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
		{
			// Get the track and channel numbers of the MIDI track/channel voice with the lowest average 
			// pitch
			int track_lowest_voice = sequence_info.track_and_channel_with_lowest_average_pitch[0];
			int channel_lowest_voice = sequence_info.track_and_channel_with_lowest_average_pitch[1];

			// The number of note ons in the lowest MIDI track/channel voice
			int number_of_notes_in_lowest_voice = sequence_info.track_and_channel_statistics[track_lowest_voice][channel_lowest_voice][0];
			
			// Find the number of pitched MIDI track/channnel pairings with at least one note on
			int active_voices_count = 0;
			for (int n_track = 0; n_track < sequence_info.track_and_channel_statistics.length; n_track++)
				for (int chan = 0; chan < sequence_info.track_and_channel_statistics[n_track].length; chan++)
					if (sequence_info.track_and_channel_statistics[n_track][chan][0] != 0 && chan != 10 - 1)
						active_voices_count++;

			// Find the average number of notes in each pitched MIDI track/channnel pairing
			double[] number_of_notes = new double[active_voices_count];
			int count = 0;
			for (int n_track = 0; n_track < sequence_info.track_and_channel_statistics.length; n_track++)
				for (int chan = 0; chan < sequence_info.track_and_channel_statistics[n_track].length; chan++)
					if (sequence_info.track_and_channel_statistics[n_track][chan][0] != 0 && chan != 10 - 1)
					{
						number_of_notes[count] = (double) sequence_info.channel_statistics[chan][0];
						count++;
					}
			
			double total_average = mckay.utilities.staticlibraries.MathAndStatsMethods.getAverage(number_of_notes);

			// Set value
			if (Double.isNaN(total_average) || total_average == 0.0)
				value = 0.0;
			else 
				value = (double) number_of_notes_in_lowest_voice / total_average;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}