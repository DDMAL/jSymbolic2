package jsymbolic2.features.melodicintervals;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the smallest melodic interval in the MIDI channel with the highest 
 * average pitch, measured in semitones. Repeated notes are not counted for this feature, so a value of 0 will 
 * only be returned if there are no melodic intervals of a semitone or larger.
 *
 * @author radamian
 */
public class SmallestMelodicIntervalInHighestLineFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public SmallestMelodicIntervalInHighestLineFeature()
	{
		String name = "Smallest Melodic Interval in Highest Line";
		String code = "M-98";
		String description = "The smallest melodic interval in the MIDI channel with the highest average pitch, measured in semitones. Repeated notes are not counted for this feature, so a value of 0 will only be returned if there are no melodic intervals of a semitone or larger.";
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
			
			// Create melodic interval histogram for that channel
			int[] melodic_interval_histogram = new int[128];
			for (int n_track = 0; n_track < sequence.getTracks().length; n_track++)
				for (int i = 0; i < sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[channel_with_highest_average_pitch].size(); i++)
					melodic_interval_histogram[Math.abs(sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[channel_with_highest_average_pitch].get(i))]++;
			
			// Initialize with default value
			int smallest_melodic_interval = 0;

			// Find smallest melodic interval that is not a repeated note (i.e. bin 0 of the melodic interval
			// histogram)
			for (int bin = 1; bin < melodic_interval_histogram.length; bin++)
				if (melodic_interval_histogram[bin] > 0.0)
				{
					smallest_melodic_interval = bin;
					break;
				}		
			
			value = (double) smallest_melodic_interval;
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
    }
}