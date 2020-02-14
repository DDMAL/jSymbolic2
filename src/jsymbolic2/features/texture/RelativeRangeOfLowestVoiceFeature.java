package jsymbolic2.features.texture;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the pitch difference in semitones between the highest note and the lowest 
 * note played in the MIDI track/channel voice with the lowest average pitch, divided by the difference 
 * between the highest note and the lowest note in the piece overall. Set to 0 if there if there are fewer 
 * than 2 pitches in the music.
 *
 * @author radamian
 */
public class RelativeRangeOfLowestVoiceFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public RelativeRangeOfLowestVoiceFeature()
	{
		String name = "Relative Range of Lowest Voice";
		String code = "T-20";
		String description = "Pitch difference in semitones between the highest note and the lowest note played in the MIDI track/channel voice with the lowest average pitch, divided by the difference between the highest note and the lowest note in the piece overall. Set to 0 if there if there are fewer than 2 pitches in the music.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[] { "Range" };
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
			if (sequence_info.total_number_pitched_note_ons < 2)
				value = 0.0;
			else
			{
				// Get the track and channel numbers of the MIDI track/channel voice with the lowest average 
				// pitch
				int track_lowest_voice = sequence_info.track_and_channel_with_lowest_average_pitch[0];
				int channel_lowest_voice = sequence_info.track_and_channel_with_lowest_average_pitch[1];
				
				// Get the range in that MIDI track/channel voice
				int lowest = sequence_info.track_and_channel_statistics[track_lowest_voice][channel_lowest_voice][4];
				int highest = sequence_info.track_and_channel_statistics[track_lowest_voice][channel_lowest_voice][5];
				int range_in_lowest_voice = highest - lowest;
				
				// The overall range in the piece
				double overall_range = other_feature_values[0][0];
				
				if (overall_range == 0)
					value = 0.0;
				else
					value = (double) range_in_lowest_voice / overall_range;
			}	
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}