package jsymbolic2.features.melodicintervals;

import java.util.LinkedList;
import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the standard deviation of the number of consecutive melodically rising 
 * notes that occur before a melodic change of direction, including the two end notes. Melodic unisons are not 
 * considered to break a run. Similar assumptions are made in the calculation of this feature as for the 
 * Melodic Interval Histogram. Set to 0 if no such runs are found.
 *
 * @author radamian and Cory McKay
 */
public class VariabilityInLengthOfRisingMelodicRunsFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VariabilityInLengthOfRisingMelodicRunsFeature()
	{
		String name = "Variability in Length of Rising Melodic Runs";
		String code = "M-75";
		String description = "Standard deviation of the number of consecutive melodically rising notes that occur before a melodic change of direction, including the two end notes. Melodic unisons are not considered to break a run. Similar assumptions are made in the calculation of this feature as for the Melodic Interval Histogram. Set to 0 if no such runs are found.";
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
			LinkedList<Integer> melodic_run_lengths = new LinkedList<>();
			
			for (int track = 0; track < sequence_info.melodic_intervals_by_track_and_channel.size(); track++)
			{
				LinkedList<Integer>[] melodic_intervals_by_channel = sequence_info.melodic_intervals_by_track_and_channel.get(track);
			
				for (int chan = 0; chan < melodic_intervals_by_channel.length; chan++)
				{
					if (chan != (10 - 1))
					{
						// Convert list of melodic intervals in this channel to an array
						Object[] list_contents = melodic_intervals_by_channel[chan].toArray();
						int[] intervals = new int[list_contents.length];
						for (int i = 0; i < intervals.length; i++)
							intervals[i] = ((Integer) list_contents[i]).intValue();

						int direction = 0;
						int length_of_run = 0;
						
						// Find the length of each rising melodic run
						for (int i = 0; i < intervals.length; i++)
						{
							// If melodic arc is currently descending
							if (direction == -1)
							{
								if (intervals[i] > 0)
								{
									length_of_run = 2;
									direction = 1;
								}
							}

							// If melodic arc is currently ascending
							else if (direction == 1)
							{
								if (intervals[i] > 0)
									length_of_run++;
								else if (intervals[i] < 0)
								{
									melodic_run_lengths.add(length_of_run);
									length_of_run = 0;
									direction = -1;
								}
								else if (intervals[i] == 0)
								{
									// A lookahead to find the next non-zero interval; unison intervals only 
									// contribute to the run's length if they occur mid-run.
									for (int j = i; j < intervals.length; j++)
										if (intervals[j] < 0)
											break;
										else if (intervals[j] > 0)
										{
											length_of_run++;
											break;
										}
								}
							}

							// Handle the first interval
							else if (direction == 0)
							{
								if (intervals[i] > 0)
								{
									length_of_run = 2;
									direction = 1;
								}
								else if (intervals[i] < 0)
									direction = -1;
							}
							
							// Handle case when last interval is encountered
							if (i == intervals.length - 1 && length_of_run > 0)
								melodic_run_lengths.add(length_of_run);
						}
					}
				}
			}
			
			// Prepare array for standard deviation calculation
			int[] melodic_run_lengths_in_array = new int[melodic_run_lengths.size()];
			for (int i = 0; i < melodic_run_lengths_in_array.length; i++)
				melodic_run_lengths_in_array[i] = melodic_run_lengths.get(i);
			
			// Calculate the value
			if (melodic_run_lengths_in_array.length == 0)
				value = 0.0;
			else
				value = (double) mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(melodic_run_lengths_in_array);
		} 
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}