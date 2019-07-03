package jsymbolic2.features.melodicintervals;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the largest melodic interval in the MIDI channel with the highest average 
 * pitch.
 *
 * @author radamian
 */
public class LargestMelodicIntervalInHighestLineFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public LargestMelodicIntervalInHighestLineFeature()
	{
		String name = "Largest Melodic Interval in Highest Line";
		String code = "M-99";
		String description = "The largest melodic interval in the MIDI channel with the highest average pitch.";
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
			// Get channel with the highest average pitch
			int channel_with_highest_average_pitch = 0;
			for (int chan = 0; chan < 16; chan++)
				if (chan != 10 - 1) // Exclude Channel 10 (Percussion)
					if (sequence_info.channel_statistics[chan][6] > sequence_info.channel_statistics[channel_with_highest_average_pitch][6])
						channel_with_highest_average_pitch = chan;
			
			// Find the largest melodic interval for that channel
			int largest_melodic_interval = 0;
			for (int n_track = 0; n_track < sequence.getTracks().length; n_track++)
				for (int i = 0; i < sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[channel_with_highest_average_pitch].size(); i++)
					if (Math.abs(sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[channel_with_highest_average_pitch].get(i)) > largest_melodic_interval)
						largest_melodic_interval = Math.abs(sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[channel_with_highest_average_pitch].get(i));
			
			// Calculate feature value
			value = (double) largest_melodic_interval;
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
    }
}