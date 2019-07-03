package jsymbolic2.features.melodicintervals;

import java.util.LinkedList;
import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;
import mckay.utilities.staticlibraries.MathAndStatsMethods;

/**
 * A feature calculator that finds the fraction of all melodic intervals that corresponds to the most common 
 * melodic interval in the in the MIDI channel with the lowest average pitch. 
 *
 * @author radamian
 */
public class PrevalenceOfMostCommonMelodicIntervalInLowestLineFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public PrevalenceOfMostCommonMelodicIntervalInLowestLineFeature()
	{
		String name = "Prevalence of Most Common Melodic Interval in Lowest Line";
		String code = "M-124";
		String description = "Fraction of all melodic intervals that corresponds to the most common melodic interval in the in the MIDI channel with the lowest average pitch.";
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
			// Get channel with the lowest average pitch
			int channel_with_lowest_average_pitch = -1;
			for (int chan = 0; chan < 16; chan++)
				// Exclude Channel 10 (Percussion) and check that there are notes on the given channel
				if (chan != 10 - 1 && sequence_info.channel_statistics[chan][0] > 0)
					if (channel_with_lowest_average_pitch == -1 || sequence_info.channel_statistics[chan][6] < sequence_info.channel_statistics[channel_with_lowest_average_pitch][6])
						channel_with_lowest_average_pitch = chan;
			
			// Create list of the melodic intervals in that channel
			LinkedList<Integer> melodic_intervals = new LinkedList<>();
			for (int n_track = 0; n_track < sequence.getTracks().length; n_track++)
				for (int i = 0; i < sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[channel_with_lowest_average_pitch].size(); i++)
					melodic_intervals.add(sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[channel_with_lowest_average_pitch].get(i));
			
			// Prepare melodic interval histogram for feature calculation
			double[] melodic_interval_histogram = new double[128];
			for (int i = 0; i < melodic_intervals.size(); i++)
				melodic_interval_histogram[Math.abs(melodic_intervals.get(i))]++;
			
			// Normalize melodic interval histogram
			melodic_interval_histogram = mckay.utilities.staticlibraries.MathAndStatsMethods.normalize(melodic_interval_histogram);
			
			// Calculate feature value
			value = melodic_interval_histogram[mckay.utilities.staticlibraries.MathAndStatsMethods.getIndexOfLargest(melodic_interval_histogram)];
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
    }
}