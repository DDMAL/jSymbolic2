package jsymbolic2.features.texture;

import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import java.util.List;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.NoteInfo;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the number of pitched notes in the MIDI track/channel voice with the lowest 
 * average pitch that fall outside the range of any other pitched voice, divided by the total number of notes 
 * in the voice with the lowest average pitch. Set to 0 if there are only 0 or 1 voices containing pitched 
 * notes.
 *
 * @author radamian
 */
public class RelativeRangeIsolationOfLowestVoiceFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public RelativeRangeIsolationOfLowestVoiceFeature()
	{
		String name = "Relative Range Isolation of Lowest Voice";
		String code = "T-21";
		String description = "Number of pitched notes in the MIDI track/channel voice with the lowest average pitch that fall outside the range of any other pitched voice, divided by the total number of notes in the voice with the lowest average pitch. Set to 0 if there are only 0 or 1 voices containing pitched notes.";
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
			int pitched_voices_count = 0;
			for (int n_track = 0; n_track < sequence_info.track_and_channel_statistics.length; n_track++)
				for (int chan = 0; chan < sequence_info.track_and_channel_statistics[n_track].length; chan++)
					if (sequence_info.track_and_channel_statistics[n_track][chan][0] != 0 && chan != 10 - 1)
						pitched_voices_count++;

			if (pitched_voices_count < 2)
				value = 0.0;
			else
			{
				// Get the track and channel numbers of the MIDI track/channel voice with the lowest average 
				// pitch
				int track_lowest_voice = sequence_info.track_and_channel_with_lowest_average_pitch[0];
				int channel_lowest_voice = sequence_info.track_and_channel_with_lowest_average_pitch[1];

				// Find the lowest and highest pitches across all pitched MIDI track/channel pairnings but the 
				// MIDI track/channel pairing with the lowest average pitch
				int lowest = 128;
				int highest = -1;
				for (int n_track = 0; n_track < sequence_info.track_and_channel_statistics.length; n_track++)
					for (int chan = 0; chan < sequence_info.track_and_channel_statistics[n_track].length; chan++)
					{
						if ((sequence_info.track_and_channel_statistics[n_track][chan][0] != 0 && chan != 10 - 1) &&
							(n_track != track_lowest_voice && chan != channel_lowest_voice))
						{
							if (sequence_info.track_and_channel_statistics[n_track][chan][4] < lowest)
								lowest = sequence_info.track_and_channel_statistics[n_track][chan][4];
							if (sequence_info.track_and_channel_statistics[n_track][chan][5] > highest)
								highest = sequence_info.track_and_channel_statistics[n_track][chan][5];
						}
					}	
				
				// All notes in the lowest voice
				List<NoteInfo> notes_in_lowest_voice = sequence_info.all_notes.getNotesOnTrackAndChannel(track_lowest_voice, channel_lowest_voice);
				
				// Count the notes in the lowest voice that fall outside the range of any other voice
				int outside_of_range_count = 0;
				for (int i = 0; i < notes_in_lowest_voice.size(); i++)
					if (notes_in_lowest_voice.get(i).getPitch() < lowest || 
						notes_in_lowest_voice.get(i).getPitch() > highest)
						outside_of_range_count++;
				
				// Set value
				value = (double) outside_of_range_count / notes_in_lowest_voice.size();
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}