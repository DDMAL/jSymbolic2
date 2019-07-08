package jsymbolic2.features.melodicintervals;

import java.util.LinkedList;
import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the standard deviation of the melodic interval (in semitones) separating 
 * the highest note and the lowest note in runs of consecutive melodically falling notes in the MIDI channel 
 * with the lowest average pitch. Melodic unisons are not considered to break a run. Similar assumptions are 
 * made in the calculation of this feature as for the Melodic Interval Histogram. Set to 0 if no melodic such 
 * runs are found.
 *
 * @author radamian and Cory McKay
 */
public class VariabilityInIntervalSpannedByFallingMelodicRunsInLowestLineFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VariabilityInIntervalSpannedByFallingMelodicRunsInLowestLineFeature()
	{
		String name = "Variability in Interval Spanned by Falling Melodic Runs in Lowest Line";
		String code = "M-136";
		String description = "Standard deviation of the melodic interval (in semitones) separating the highest note and the lowest note in runs of consecutive melodically falling notes in the MIDI channel with the lowest average pitch. Melodic unisons are not considered to break a run. Similar assumptions are made in the calculation of this feature as for the Melodic Interval Histogram. Set to 0 if no melodic such runs are found.";
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
			int track_with_lowest_average_pitch = sequence_info.track_and_channel_with_lowest_average_pitch[0];
			int channel_with_lowest_average_pitch = sequence_info.track_and_channel_with_lowest_average_pitch[1];
			
			LinkedList<Integer> intervals_spanned_by_falling_runs = new LinkedList<>();

			int direction = 0;
			int interval_spanned_by_run = 0;

			// Find the interval spanned by each falling melodic run
			LinkedList<Integer> intervals = sequence_info.melodic_intervals_by_track_and_channel.get(track_with_lowest_average_pitch)[channel_with_lowest_average_pitch];
			for (int i = 0; i < intervals.size(); i++)
			{
				// If melodic arc is currently descending
				if (direction == -1)
				{
					if (intervals.get(i) > 0)
					{
						intervals_spanned_by_falling_runs.add(Math.abs(interval_spanned_by_run));
						interval_spanned_by_run = 0;
						direction = 1;
					}
					else if (intervals.get(i) < 0)
						interval_spanned_by_run += intervals.get(i);
				}

				// If melodic arc is currently ascending
				else if (direction == 1)
				{
					if (intervals.get(i) < 0)
					{
						interval_spanned_by_run += intervals.get(i);
						direction = -1;
					}
				}

				// Handle the first interval
				else if (direction == 0)
				{
					if (intervals.get(i) > 0)
						direction = 1;
					else if (intervals.get(i) < 0)
					{
						interval_spanned_by_run += intervals.get(i);
						direction = -1;
					}
				}

				// Handle case when last interval is encountered
				if (i == intervals.size() - 1 && interval_spanned_by_run < 0)
					intervals_spanned_by_falling_runs.add(Math.abs(interval_spanned_by_run));
			}
			
			// Prepare array for standard deviation calculation
			int[] intervals_spanned_by_falling_runs_in_array = new int[intervals_spanned_by_falling_runs.size()];
			for (int i = 0; i < intervals_spanned_by_falling_runs_in_array.length; i++)
				intervals_spanned_by_falling_runs_in_array[i] = intervals_spanned_by_falling_runs.get(i);
			
			// Calculate the value
			if (intervals_spanned_by_falling_runs_in_array.length == 0)
				value = 0.0;
			else
				value = (double) mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(intervals_spanned_by_falling_runs_in_array);
		} 
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}