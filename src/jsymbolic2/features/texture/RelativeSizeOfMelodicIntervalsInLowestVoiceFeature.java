package jsymbolic2.features.texture;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the average melodic interval in semitones in the MIDI track/channel voice 
 * with the lowest average pitch, divided by the average melodic interval in all MIDI track/channel voices 
 * that contain at least one melodic interval. Set to 0 if there are no voices containing melodic intervals.
 *
 * @author Cory McKay and radamian
 */
public class RelativeSizeOfMelodicIntervalsInLowestVoiceFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public RelativeSizeOfMelodicIntervalsInLowestVoiceFeature()
	{
		String name = "Relative Size of Melodic Intervals in Lowest Voice";
		String code = "T-24";
		String description = "Average melodic interval in semitones in the MIDI track/channel voice with the lowest average pitch, divided by the average melodic interval in all MIDI track/channel voices that contain at least two notes. Set to 0 if there are no voices containing melodic intervals.";
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
			// Get the track and channel numbers of the MIDI track/channel voice with the lowest average pitch
			int track_lowest_voice = sequence_info.track_and_channel_with_lowest_average_pitch[0];
			int channel_lowest_voice = sequence_info.track_and_channel_with_lowest_average_pitch[1];

			// Find the average melodic interval across all voices with at least one melodic interval
			int total_melodic_intervals = 0;
			int number_of_melodic_intervals = 0;
			for (int n_track = 0; n_track < sequence_info.melodic_intervals_by_track_and_channel.size(); n_track++)
				for (int chan = 0; chan < sequence_info.melodic_intervals_by_track_and_channel.get(n_track).length; chan++)
					if (!sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[chan].isEmpty())
					{
						for (int i = 0; i < sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[chan].size(); i++)
							total_melodic_intervals += Math.abs(sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[chan].get(i));
						
						number_of_melodic_intervals += sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[chan].size();
					}
			
			if (number_of_melodic_intervals == 0)
				value = 0.0;
			else
			{
				int avg_melodic_interval_in_lowest_voice = sequence_info.track_and_channel_statistics[track_lowest_voice][channel_lowest_voice][3];
				double avg_melodic_interval_across_voices = (double) total_melodic_intervals / number_of_melodic_intervals;
				
				// Set value
				if (avg_melodic_interval_across_voices == 0)
					value = 0.0;
				else
					value = (double) avg_melodic_interval_in_lowest_voice / avg_melodic_interval_across_voices;
			}
			
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}