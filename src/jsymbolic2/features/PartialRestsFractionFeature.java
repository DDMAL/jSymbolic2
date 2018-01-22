package jsymbolic2.features;

import java.util.stream.DoubleStream;
import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that calculates the fraction of the music during which no note is sounding on at least
 * one active MIDI channel. Non-pitched (MIDI channel 10) notes ARE considered in this calculation. Only
 * channels containing at least one note are counted in this calculation.
 *
 * @author Cory McKay
 */
public class PartialRestsFractionFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public PartialRestsFractionFeature()
	{
		code = "R-42";
		String name = "Partial Rests Fraction";
		String description = "Fraction of the music during which no note is sounding on at least one active MIDI channel. Non-pitched (MIDI channel 10) notes ARE considered in this calculation. Only channels containing at least one note are counted in this calculation.";
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
			// The number of note ons per channel
			int[] notes_per_channel = new int[sequence_info.channel_statistics.length];
			for (int i = 0; i < notes_per_channel.length; i++)
				notes_per_channel[i] = sequence_info.channel_statistics[i][0];
			
			// Tick by tick (first index) indication of whether a note is sounding on each channel (second
			// index).
			boolean[][] note_sounding_on_a_channel_tick_map = sequence_info.note_sounding_on_a_channel_tick_map;
			
			// The duration of each tick
			double[] seconds_per_tick = sequence_info.duration_of_ticks_in_seconds;

			// Set non_empty_channels to true for channels that have at least one note
			boolean[] non_empty_channels = new boolean[notes_per_channel.length];
			for (int i = 0; i < non_empty_channels.length; i++)
			{
				if (notes_per_channel[i] != 0) non_empty_channels[i] = true;
				else non_empty_channels[i] = false;
			}

			// The number of ticks to examine (the minus 1 is because Java doesn't count the last tick
			int ticks_to_test = note_sounding_on_a_channel_tick_map.length - 1;
			
			// Find the durations of partial rests, tick by tick
			double[] seconds_of_rest_per_tick = new double[ticks_to_test];
			for (int i = 0; i < seconds_of_rest_per_tick.length; i++)
				seconds_of_rest_per_tick[i] = 0.0;
			for (int tick = 0; tick < ticks_to_test; tick++)
			{
				boolean at_least_one_chan_not_sounding = false;
				for (int chan = 0; chan < non_empty_channels.length; chan++)
					if (non_empty_channels[chan] && !note_sounding_on_a_channel_tick_map[tick][chan])
					{
						at_least_one_chan_not_sounding = true;
						break;
					}
				if (at_least_one_chan_not_sounding)
					seconds_of_rest_per_tick[tick] = seconds_per_tick[tick];
			}
			
			// Add up the durations of all the rests
			double total_partial_rest_time = DoubleStream.of(seconds_of_rest_per_tick).sum();
			
			// Divide by the length of the piece
			if (sequence_info.sequence_duration_precise == 0.0)
				value = 0.0;
			else
				value = total_partial_rest_time / sequence_info.sequence_duration_precise;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;		
	}
}