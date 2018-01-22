package jsymbolic2.features;

import java.util.ArrayList;
import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the standard deviation of the total amount of time (expressed as a fraction
 * of the duration of a quarter note) per active MIDI channel in which no notes are sounding in that channel.
 * Only channels containing at least one note are counted in this calculation. Non-pitched (MIDI channel 10) 
 * notes ARE considered in this calculation.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class VariabilityAcrossVoicesOfCombinedRestsFeature extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VariabilityAcrossVoicesOfCombinedRestsFeature()
	{
		code = "R-52";
		String name = "Variability Across Voices of Combined Rests";
		String description = "Standard deviation of the total amount of time (expressed as a fraction of the duration of a quarter note) per active MIDI channel in which no notes are sounding in that channel. Only channels containing at least one note are counted in this calculation. Non-pitched (MIDI channel 10) notes ARE considered in this calculation.";
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
			double[] total_time_notes_sounding_per_channel = sequence_info.total_time_notes_sounding_per_channel; 
			
			// Find which channels have notes, and what the total amount of rest is per channel
			ArrayList<Double> total_rest_time_per_channel = new ArrayList<>();
			for (int channel = 0; channel < channel_stats.length; channel++)
			{
				int total_notes_on_this_channel = channel_stats[channel][0];
				if (total_notes_on_this_channel > 0)
				{
					double total_non_silence_this_channel = total_time_notes_sounding_per_channel[channel];
					total_rest_time_per_channel.add(sequence_info.sequence_duration_precise - total_non_silence_this_channel);
				}
			}
			
			// Convert to an array, and express as a fraction of a quarter note
			double[] total_rest_time_per_channel_array = new double[total_rest_time_per_channel.size()];
			for (int i = 0; i < total_rest_time_per_channel_array.length; i++)
				total_rest_time_per_channel_array[i] = total_rest_time_per_channel.get(i) / sequence_info.average_quarter_note_duration_in_seconds;

			// Calculate the standard deviation
			if (total_rest_time_per_channel_array.length == 0)
				value = 0.0;
			else 
				value = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(total_rest_time_per_channel_array);
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}