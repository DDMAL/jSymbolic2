package jsymbolic2.features;

import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the average of the total amount of combined time (in seconds) in each MIDI
 * channel in which no note is sounding in that particular channel, averaged across channels, and then divided
 * by the total duration of the piece. Only channels with at least one note are counted in this calculation.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class AverageRestFractionPerVoiceFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public AverageRestFractionPerVoiceFeature()
	{
		code = "R-28";
		String name = "Average Rest Fraction Per Voice";
		String description = "Average of the total amount of combined time (in seconds) in each MIDI channel in which no note is sounding in that particular channel, averaged across channels, and then divided by the total duration of the piece. Only channels with at least one note are counted in this calculation.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, description, is_sequential, dimensions);
		dependencies = null;
		offsets = null;
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
			// Get information from sequence_info
			int[][] channel_stats = sequence_info.channel_statistics;

			// Find the total amount of rest time accumulated across channels, as well as the number of
			// channels with notes
			double total_rest_time_all_channels_combined = 0.0;
			int number_channels_with_notes = 0;
			for (int channel = 0; channel < channel_stats.length; channel++)
			{
				// Only process this channel if it contains at least one note
				int total_notes_on_this_channel = channel_stats[channel][0];
				if (total_notes_on_this_channel == 0)
					continue;

				// Note there are notes this channel and accumulate the total amount of rest time
				number_channels_with_notes++;
				int total_non_silence_this_channel = channel_stats[channel][1];
				total_rest_time_all_channels_combined += (sequence_info.sequence_duration_precise - total_non_silence_this_channel);
			}
			
			// Find the average across channels, and then scale by duration
			value = total_rest_time_all_channels_combined / (double) number_channels_with_notes;
			value = value / sequence_info.sequence_duration_precise;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}