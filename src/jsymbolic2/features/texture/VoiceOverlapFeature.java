package jsymbolic2.features.texture;

import java.util.List;
import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.NoteInfo;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the number of notes played within the range of another MIDI track/channel 
 * voice, divided by the total number of notes in the piece as a whole. Set to 0 if there if there are fewer 
 * than 2 pitches in the music.
 *
 * @author Tristano Tenaglia, Cory McKay, and radamian
 */
public class VoiceOverlapFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VoiceOverlapFeature()
	{
		String name = "Voice Overlap";
		String code = "T-28";
		String description = "Number of notes played within the range of another MIDI track/channel voice, divided by the total number of notes in the piece as a whole. Set to 0 if there if there are fewer than 2 pitches in the music.";
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
			// Sum the number of notes that fall within the range of at least one other MIDI track/channel
			// voice
			int number_of_notes_within_range = 0;
			int total_number_of_notes = 0;
			for (int voice = 0; voice < sequence_info.track_and_channel_pairs_by_average_pitch.size(); voice++)
			{
				int track = sequence_info.track_and_channel_pairs_by_average_pitch.get(voice)[0];
				int channel = sequence_info.track_and_channel_pairs_by_average_pitch.get(voice)[1];
				
				// Iterate over all notes in this voice
				List<NoteInfo> notes_in_voice = sequence_info.all_notes.getNotesOnTrackAndChannel(track, channel);
				for (int i = 0; i < notes_in_voice.size(); i++)
				{
					int pitch = notes_in_voice.get(i).getPitch();

					for (int other_voice = 0; other_voice < sequence_info.track_and_channel_pairs_by_average_pitch.size(); other_voice++)
						if (voice != other_voice)
						{
							int other_track = sequence_info.track_and_channel_pairs_by_average_pitch.get(other_voice)[0];
							int other_channel = sequence_info.track_and_channel_pairs_by_average_pitch.get(other_voice)[1];
							
							int lowest = sequence_info.track_and_channel_statistics[other_track][other_channel][4];
							int highest = sequence_info.track_and_channel_statistics[other_track][other_channel][5];
							
							// If this pitch is within the range of one other voice, increment 
							// number_of_notes_within_range and continue to the next note
							if (pitch <= highest && pitch >= lowest)
							{
								number_of_notes_within_range++;
								break;
							}
						}
				}
				
				total_number_of_notes += notes_in_voice.size();
			}
			
			// Set value
			if (total_number_of_notes < 2)
				value = 0.0;
			else
				value = (double) number_of_notes_within_range / total_number_of_notes;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}