package jsymbolic2.features.melodicintervals;

import java.util.LinkedList;
import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the mean average (in semitones) of the intervals involved in each of the 
 * melodic intervals in the in the MIDI channel with the lowest average pitch. 
 *
 * @author radamian
 */
public class MeanMelodicIntervalOfLowestLineFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MeanMelodicIntervalOfLowestLineFeature()
	{
		String name = "Mean Melodic Interval of Lowest Line";
		String code = "M-123";
		String description = "Mean average (in semitones) of the intervals involved in each of the melodic intervals in the in the MIDI channel with the lowest average pitch.";
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
			
			// Create list of the melodic intervals in that track and channel
			LinkedList<Integer> melodic_intervals = new LinkedList<>();
			for (int i = 0; i < sequence_info.melodic_intervals_by_track_and_channel.get(track_with_lowest_average_pitch)[channel_with_lowest_average_pitch].size(); i++)
				melodic_intervals.add(Math.abs(sequence_info.melodic_intervals_by_track_and_channel.get(track_with_lowest_average_pitch)[channel_with_lowest_average_pitch].get(i)));
			
			// Prepare array for feature calculation
			int[] melodic_intervals_in_array = new int[melodic_intervals.size()];
			for (int i = 0; i < melodic_intervals_in_array.length; i++)
				melodic_intervals_in_array[i] = melodic_intervals.get(i);
			
			// Calculate feature value
			value = mckay.utilities.staticlibraries.MathAndStatsMethods.getAverage(melodic_intervals_in_array);
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
    }
}