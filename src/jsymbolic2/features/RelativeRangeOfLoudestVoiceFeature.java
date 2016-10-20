package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the difference between the highest note and the lowest note played in the
 * channel with the highest average loudness (MIDI velocity), divided by the difference between the highest
 * note and the lowest note in the piece as a whole.
 *
 * @author Cory McKay
 */
public class RelativeRangeOfLoudestVoiceFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public RelativeRangeOfLoudestVoiceFeature()
	{
		code = "T-10";
		String name = "Relative Range of Loudest Voice";
		String description = "Difference between the highest note and the lowest note played in the channel with the highest average loudness (MIDI velocity), divided by the difference between the highest note and the lowest note in the piece as a whole.";
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
			// Find the loudest channel
			int max_so_far = 0;
			int loudest_chan = 0;
			for (int chan = 0; chan < sequence_info.channel_statistics.length; chan++)
			{
				if (sequence_info.channel_statistics[chan][0] != 0 && chan != (10 - 1))
				{
					if (sequence_info.channel_statistics[chan][2] > max_so_far)
					{
						max_so_far = sequence_info.channel_statistics[chan][2];
						loudest_chan = chan;
					}
				}
			}

			// Find the range of the loudest channel
			double loudest_range = (double) (sequence_info.channel_statistics[loudest_chan][5]
					- sequence_info.channel_statistics[loudest_chan][4]);

			// Finde the overall range
			int lowest = 127;
			int highest = 0;
			for (int chan = 0; chan < sequence_info.channel_statistics.length; chan++)
			{
				if (sequence_info.channel_statistics[chan][0] != 0 && chan != (10 - 1))
				{
					if (sequence_info.channel_statistics[chan][4] < lowest)
						lowest = sequence_info.channel_statistics[chan][4];
					if (sequence_info.channel_statistics[chan][5] > highest)
						highest = sequence_info.channel_statistics[chan][5];
				}
			}

			// Set value
			value = loudest_range / ((double) (highest - lowest));
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}