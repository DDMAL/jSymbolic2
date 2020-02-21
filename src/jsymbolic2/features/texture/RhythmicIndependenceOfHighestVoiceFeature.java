package jsymbolic2.features.texture;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.LinkedList;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the number of note onset slices where the MIDI track/channel voice with the 
 * highest average pitch has a new note onset and this is the only new note onset in the slice, divided by the 
 * total number of note onset slices where the MIDI track/channel voice with the highest average pitch has a 
 * new note onset. Set to 0 if there are only zero or one voices containing pitched notes. MIDI Chanel 10 
 * notes are ignored.
 *
 * @author radamian
 */
public class RhythmicIndependenceOfHighestVoiceFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public RhythmicIndependenceOfHighestVoiceFeature()
	{
		String name = "Rhythmic Independence of Highest Voice";
		String code = "T-38";
		String description = "Number of note onset slices where the MIDI track/channel voice with the highest average pitch has a new note onset and this is the only new note onset in the slice, divided by the total number of note onset slices where the MIDI track/channel voice with the highest average pitch has a new note onset. Set to 0 if there are only zero or one voices containing pitched notes. MIDI Chanel 10 notes are ignored.";
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
				// The note onset slices in the piece, divided by MIDI track and channel and containing only
				// notes from new onsets
				LinkedList<LinkedList<Integer>>[][] note_onset_slices_by_track_and_channel = sequence_info.note_onset_slice_container.getNoteOnsetSlicesByTrackAndChannelOnlyNewOnsets();
				
				// Get the track and channel numbers of the MIDI track/channel voice with the highest average 
				// pitch
				int track_highest_voice = sequence_info.track_and_channel_with_highest_average_pitch[0];
				int channel_highest_voice = sequence_info.track_and_channel_with_highest_average_pitch[1];
				
				// The number of note onset slices where the highest voice has a new onset
				int number_of_slices_with_new_onset_in_highest_voice = 0;
				// The number of note onset slices where only the highest voice has a new onset
				int number_of_slices_with_independent_highest_voice = 0;
				
				// Whether the highest voice has a new onset in the current slice
				boolean onset_in_highest_voice;
				// The number of voices with a new onset in the current slice
				int voices_with_new_onsets;
				
				// Iterate by slice
				for (int i = 0; i < sequence_info.note_onset_slice_container.NUMBER_OF_ONSET_SLICES; i++)
				{
					voices_with_new_onsets = 0;
					onset_in_highest_voice = false;
					
					for (int n_track = 0; n_track < note_onset_slices_by_track_and_channel.length; n_track++)
						for (int chan = 0; chan < note_onset_slices_by_track_and_channel[n_track].length; chan++)
							if (!note_onset_slices_by_track_and_channel[n_track][chan].get(i).isEmpty())
							{
								voices_with_new_onsets++;
								
								if (n_track == track_highest_voice && chan == channel_highest_voice)
								{
									onset_in_highest_voice = true;
									number_of_slices_with_new_onset_in_highest_voice++;
								}
							}
					
					// Increment number_of_slices_with_independent_highest_voice if the only note onset in 
					// this slice belongs to the highest voice
					if (onset_in_highest_voice && voices_with_new_onsets == 1)
						number_of_slices_with_independent_highest_voice++;
				}
				
				// Set value
				value = (double) number_of_slices_with_independent_highest_voice / number_of_slices_with_new_onset_in_highest_voice;
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}