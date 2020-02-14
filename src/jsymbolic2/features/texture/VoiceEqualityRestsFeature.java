package jsymbolic2.features.texture;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the standard deviation of the cumulative amount of time during which no 
 * notes were sounding in each MIDI track/channel voice that contains at least one note. Calculation for each 
 * voice excludes any time before the first note in the voice and after the last note in the voice. Set to 0 
 * if there are no voices containing pitched notes.
 *
 * @author radamian
 */
public class VoiceEqualityRestsFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VoiceEqualityRestsFeature()
	{
		String name = "Voice Equality – Rests";
		String code = "T-7";
		String description = "Standard deviation of the cumulative amount of time during which no notes were sounding in each MIDI track/channel voice that contains at least one note. Calculation for each voice excludes any time before the first note in the voice and after the last note in the voice. Set to 0 if there are no voices containing pitched notes.";
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
			
			if (active_voices_count == 0)
				value = 0.0;
			else
			{
				// Arrays containing the tick number of the first note on and the tick number of the last note off
				// in each voice
				int[] ticks_of_first_notes = new int[active_voices_count];
				int[] ticks_of_last_notes = new int[active_voices_count];

				// A boolean array indicating whether a note on has been encountered yet in each voice
				boolean[] note_encountered_so_far = new boolean[active_voices_count];
				for (int i = 0; i < note_encountered_so_far.length; i++)
					note_encountered_so_far[i] = false;

				// Find the tick at which the first note occurs, and the tick at which the last note ends for each
				// voice
				int count = 0;
				for (int n_track = 0; n_track < sequence_info.track_and_channel_statistics.length; n_track++)
					for (int chan = 0; chan < sequence_info.track_and_channel_statistics[n_track].length; chan++)
						if (sequence_info.track_and_channel_statistics[n_track][chan][0] != 0 && chan != 10 - 1)
						{
							for (int tick = 0; tick < sequence_info.note_sounding_on_a_track_and_channel_tick_map.length; tick++)
								if (sequence_info.note_sounding_on_a_track_and_channel_tick_map[tick][n_track][chan])
								{
									if (!note_encountered_so_far[count])
										ticks_of_first_notes[count] = tick;
									else
										ticks_of_last_notes[count] = tick;
								}

							count++;
						}


				// An array holding the total rest durations for each voice
				double[] rests = new double[active_voices_count];
				for (int i = 0; i < rests.length; i++)
					rests[i] = 0.0;

				// Sum the rest durations for each voice
				count = 0;
				for (int n_track = 0; n_track < sequence_info.track_and_channel_statistics.length; n_track++)
					for (int chan = 0; chan < sequence_info.track_and_channel_statistics[n_track].length; chan++)
						if (sequence_info.track_and_channel_statistics[n_track][chan][0] != 0 && chan != 10 - 1)
						{
							// Iterate by tick, from the first to the last note in the voice
							for (int tick = ticks_of_first_notes[count]; tick < ticks_of_last_notes[count]; tick++)
								if (!sequence_info.note_sounding_on_a_track_and_channel_tick_map[tick][n_track][chan])
									rests[count] += sequence_info.duration_of_ticks_in_seconds[tick];

							count++;
						}

				// Set value
				value = mckay.utilities.staticlibraries.MathAndStatsMethods.getAverage(rests);
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}