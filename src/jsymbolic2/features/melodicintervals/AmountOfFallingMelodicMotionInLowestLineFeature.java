package jsymbolic2.features.melodicintervals;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the fraction of melodic intervals that are falling in pitch (repeated notes 
 * are not counted in this calculation) in the MIDI channel with the lowest average pitch. Set to zero if no 
 * rising or falling melodic intervals are found.
 *
 * @author radamian
 */
public class AmountOfFallingMelodicMotionInLowestLineFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public AmountOfFallingMelodicMotionInLowestLineFeature()
	{
		String name = "Amount of Falling Melodic Motion in Lowest Line";
		String code = "M-128";
		String description = "Fraction of melodic intervals that are falling in pitch (repeated notes are not counted in this calculation) in the MIDI channel with the lowest average pitch. Set to zero if no rising or falling melodic intervals are found.";
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
			// Get track and channel with the lowest average pitch
			int track_with_lowest_average_pitch = sequence_info.track_and_channel_with_lowest_average_pitch[0];
			int channel_with_lowest_average_pitch = sequence_info.track_and_channel_with_lowest_average_pitch[1];
			
			int number_of_intervals = 0;
			int number_of_falling_intervals = 0;
			for (int i = 0; i < sequence_info.melodic_intervals_by_track_and_channel.get(track_with_lowest_average_pitch)[channel_with_lowest_average_pitch].size(); i++)
			{
				number_of_intervals++;
				if (sequence_info.melodic_intervals_by_track_and_channel.get(track_with_lowest_average_pitch)[channel_with_lowest_average_pitch].get(i) < 0)
					number_of_falling_intervals++;
			}
			
			// Calculate the feature value
			if (number_of_intervals == 0)
				value = 0.0;
			else
				value = (double) number_of_falling_intervals / number_of_intervals;
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
    }
}