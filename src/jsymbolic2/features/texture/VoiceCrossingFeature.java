package jsymbolic2.features.texture;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.List;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.NoteInfo;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the fraction of MIDI ticks where at least one pitch sounding on one MIDI 
 * track/channel voice is either above a pitch sounding simultaneously on another MIDI track/channel voice 
 * with a higher average pitch, or below a pitch sounding simultaneously on another MIDI track/channel voice 
 * with a lower average pitch. Set to 0 if there are only 0 or 1 voices containing pitched notes.
 *
 * @author radamian
 */
public class VoiceCrossingFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VoiceCrossingFeature()
	{
		String name = "Voice Crossing";
		String code = "T-29";
		String description = "Fraction of MIDI ticks where at least one pitch sounding on one MIDI track/channel voice is either above a pitch sounding simultaneously on another MIDI track/channel voice with a higher average pitch, or below a pitch sounding simultaneously on another MIDI track/channel voice with a lower average pitch. Set to 0 if there are only 0 or 1 voices containing pitched notes.";
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
				int number_of_ticks_with_voice_crossing = 0;
				
				// Find the number of ticks with voice crossing, iterating by tick
				for (int tick = 0; tick < sequence_info.note_sounding_on_a_track_and_channel_tick_map.length; tick++)
				{
					boolean voice_crossing_found_on_tick = false;
					
					// Iterate by voice, in order of increasing average pitch, and compare the notes in each
					// voice to those in all other voices to see if there is voice crossing
					for (int i = 0; i < sequence_info.track_and_channel_pairs_by_average_pitch.size(); i++)
					{
						int track = sequence_info.track_and_channel_pairs_by_average_pitch.get(i)[0];
						int channel = sequence_info.track_and_channel_pairs_by_average_pitch.get(i)[1];
						List<NoteInfo> notes_in_this_voice = sequence_info.all_notes.getNotesSoundingAtTickOnTrackAndChannel(tick, track, channel);

						if (!notes_in_this_voice.isEmpty())
						{
							// Compare this voices with other voices, in order of increasing average pitch
							for (int j = 0; j < sequence_info.track_and_channel_pairs_by_average_pitch.size(); j++)
							{
								if (i != j)
								{
									int other_track = sequence_info.track_and_channel_pairs_by_average_pitch.get(j)[0];
									int other_channel = sequence_info.track_and_channel_pairs_by_average_pitch.get(j)[1];
									List<NoteInfo> notes_in_other_voice = sequence_info.all_notes.getNotesSoundingAtTickOnTrackAndChannel(tick, other_track, other_channel);

									if (!notes_in_other_voice.isEmpty())
									{
										// The other voice has a lower average pitch than the current voice
										if (j < i)
										{
											// Between the current voice and a different voice with a lower average 
											// pitch, only the lowest note is compared to notes in the voice with the 
											// lower average pitch
											for (int n = 0; n < notes_in_other_voice.size(); n++)
												if (notes_in_this_voice.get(0).getPitch() < notes_in_other_voice.get(n).getPitch())
												{
													voice_crossing_found_on_tick = true;
													break;
												}
										}
										// The other voice has a higher average pitch than the current voice
										else if (j > i)
										{
											// Between the current voice and a different voice with a higher average 
											// pitch, only the highest note is compared to notes in the voice with the 
											// higher average pitch
											for (int n = 0; n < notes_in_other_voice.size(); n++)
												if (notes_in_this_voice.get(notes_in_this_voice.size() - 1).getPitch() > notes_in_other_voice.get(n).getPitch())
												{
													voice_crossing_found_on_tick = true;
													break;
												}
										}

										// End comparison of this voice with other voices
										if (voice_crossing_found_on_tick) break;
									}
								}
							}
						}
						
						// End iteration through voices on this tick
						if (voice_crossing_found_on_tick) break;
					}
					
					if (voice_crossing_found_on_tick) number_of_ticks_with_voice_crossing++;
				}
				
				// Set value
				value = (double) number_of_ticks_with_voice_crossing / sequence_info.note_sounding_on_a_track_and_channel_tick_map.length;
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}