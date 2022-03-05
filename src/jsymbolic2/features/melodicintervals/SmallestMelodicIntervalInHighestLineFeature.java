package jsymbolic2.features.melodicintervals;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the smallest melodic interval in the MIDI track and channel with the
 * highest average pitch, measured in semitones. Repeated notes are not counted for this feature, so a value
 * of 0 will only be returned if there are no melodic intervals of a semitone or larger. Rising and falling
 * intervals are treated as equivalent.
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
		String description = "The smallest melodic interval in the MIDI track and channel with the highest average pitch, measured in semitones. Repeated notes are not counted for this feature, so a value of 0 will only be returned if there are no melodic intervals of a semitone or larger. Rising and falling intervals are treated as equivalent.";
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
			// Get track and channel with the highest average pitch
			int track_with_highest_average_pitch = sequence_info.track_and_channel_with_highest_average_pitch[0];
			int channel_with_highest_average_pitch = sequence_info.track_and_channel_with_highest_average_pitch[1];
			
			// Find the smallest melodic interval for that track and channel. The feature value is set to 128 
			// for comparison.
			int smallest_melodic_interval = 128;
			for (int i = 0; i < sequence_info.melodic_intervals_by_track_and_channel.get(track_with_highest_average_pitch)[channel_with_highest_average_pitch].size(); i++)
			{
				int interval = Math.abs(sequence_info.melodic_intervals_by_track_and_channel.get(track_with_highest_average_pitch)[channel_with_highest_average_pitch].get(i));
				if (interval < smallest_melodic_interval && interval > 0)
					smallest_melodic_interval = interval;
			}
				
			// If no melodic interval of a semitone or larger is found, set the feature value to 0
			if (smallest_melodic_interval == 128) smallest_melodic_interval = 0;
			
			value = (double) smallest_melodic_interval;
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
    }
}