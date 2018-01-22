package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the standard deviation of the cumulative amount of time during which one or
 * more notes were sounding in each channel that contains at least one note.
 *
 * @author Cory McKay
 */
public class VoiceEqualityNoteDurationFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VoiceEqualityNoteDurationFeature()
	{
		code = "T-5";
		String name = "Voice Equality - Note Duration";
		String description = "Standard deviation of the cumulative amount of time during which one or more notes were sounding in each channel that contains at least one note.";
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
			// Find the number of channels with no note ons
			int silent_count = 0;
			for (int chan = 0; chan < sequence_info.channel_statistics.length; chan++)
			{
				if (sequence_info.channel_statistics[chan][0] == 0)
					silent_count++;
			}

			// Store the combined note durations in each channel with note ons
			double[] durations = new double[sequence_info.channel_statistics.length - silent_count];
			int count = 0;
			for (int chan = 0; chan < sequence_info.channel_statistics.length; chan++)
			{
				if (sequence_info.channel_statistics[chan][0] != 0)
				{
					durations[count] = sequence_info.total_time_notes_sounding_per_channel[chan];
					count++;
				}
			}

			// Calculate the standard deviation
			if (durations == null || durations.length == 0)
				value = 0.0;
			else 
				value = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(durations);
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}