package jsymbolic2.features.texture;

import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import java.util.List;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.NoteInfo;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the average duration of notes (in seconds) in the MIDI track/channel voice 
 * with the highest average pitch, divided by the average duration of notes in all voices that contain at 
 * least one note. Set to 0 if there are no voices containing pitched notes.
 *
 * @author radamian
 */
public class RelativeNoteDurationsOfHighestVoiceFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public RelativeNoteDurationsOfHighestVoiceFeature()
	{
		String name = "Relative Note Durations of Highest Voice";
		String code = "T-19";
		String description = "Average duration of notes (in seconds) in the MIDI track/channel voice with the lowest average pitch, divided by the average duration of notes in all MIDI track/channel voices that contain at least one note. Set to 0 if there are no voices containing pitched notes.";
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
			// Get the track and channel numbers of the MIDI track/channel voice with the highest average 
			// pitch
			int track_highest_voice = sequence_info.track_and_channel_with_highest_average_pitch[0];
			int channel_highest_voice = sequence_info.track_and_channel_with_highest_average_pitch[1];

			double total_durations_in_highest_voice = 0.0;
			double total_durations_across_pitched_voices = 0.0;
			int total_number_of_pitched_notes = 0;
			
			// Sum the total duration of notes across all pitched voices, and separately sum the total 
			// duration of notes in the MIDI track/channel voice with the highest average pitch
			for (int n_track = 0; n_track < sequence_info.track_and_channel_statistics.length; n_track++)
				for (int chan = 0; chan < sequence_info.track_and_channel_statistics[n_track].length; chan++)
					if (sequence_info.track_and_channel_statistics[n_track][chan][0] != 0 && chan != 10 - 1)
					{
						List<NoteInfo> notes_on_track_and_channel = sequence_info.all_notes.getNotesOnTrackAndChannel(n_track, chan);
						
						for (int i = 0; i < notes_on_track_and_channel.size(); i++)
						{
							NoteInfo note = notes_on_track_and_channel.get(i);
							for (int tick = note.getStartTick(); tick < note.getEndTick(); tick++)
							{
								total_durations_across_pitched_voices += sequence_info.duration_of_ticks_in_seconds[tick];
								
								if (n_track == track_highest_voice && chan == channel_highest_voice)
									total_durations_in_highest_voice += sequence_info.duration_of_ticks_in_seconds[tick];
							}
						}
						
						total_number_of_pitched_notes += sequence_info.track_and_channel_statistics[n_track][chan][0];
					}
			
			if (total_number_of_pitched_notes == 0)
				value = 0.0;
			else
			{
				double avg_duration_in_highest_voice = (double) total_durations_in_highest_voice / sequence_info.track_and_channel_statistics[track_highest_voice][channel_highest_voice][0];
				double avg_duration_across_voices = (double) total_durations_across_pitched_voices / total_number_of_pitched_notes;

				if (avg_duration_across_voices == 0)
					value = 0.0;
				else
					value = avg_duration_in_highest_voice / avg_duration_across_voices;
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}