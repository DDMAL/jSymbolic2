package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the average separation in semitones between the average pitches of
 * consecutive channels (after sorting based on average pitch) that contain at least one note.
 *
 * @author Cory McKay
 */
public class VoiceSeparationFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VoiceSeparationFeature()
	{
		code = "T-17";
		String name = "Voice Separation";
		String description = "Average separation in semitones between the average pitches of consecutive channels (after sorting based on average pitch) that contain at least one note. ";
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
			// Find the number of channels with no note ons (or that is channel 10)
			int silent_count = 0;
			for (int chan = 0; chan < sequence_info.channel_statistics.length; chan++)
				if (sequence_info.channel_statistics[chan][0] == 0 || chan == (10 - 1))
					silent_count++;

			// If there's only one voice
			if (silent_count > 14)
				value = 0.0;
			else
			{
				// Store the average melodic interval of notes in the other channels
				double[] average_pitches = new double[sequence_info.channel_statistics.length - silent_count];
				int count = 0;
				for (int chan = 0; chan < sequence_info.channel_statistics.length; chan++)
				{
					if (sequence_info.channel_statistics[chan][0] != 0 && chan != (10 - 1))
					{
						average_pitches[count] = (double) sequence_info.channel_statistics[chan][6];
						count++;
					}
				}

				// Sort the average pitches
				average_pitches = mckay.utilities.staticlibraries.SortingMethods.sortDoubleArray(average_pitches);

				// Find the intervals
				double[] intervals = new double[average_pitches.length - 1];
				for (int i = 0; i < average_pitches.length - 1; i++)
					intervals[i] = average_pitches[i + 1] - average_pitches[i];

				// Find the average interval
				if (intervals == null || intervals.length == 0)
					value = 0.0;
				else
					value = mckay.utilities.staticlibraries.MathAndStatsMethods.getAverage(intervals);
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}