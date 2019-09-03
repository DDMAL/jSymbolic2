package jsymbolic2.features.melodicintervals;

import java.util.LinkedList;
import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the standard deviation of the number of notes comprising melodic arcs,
 * including the notes at the peaks and troughs. Similar assumptions are made in the calculation of this
 * feature as for the Melodic Interval Histogram. Set to 0 if no melodic arcs are found.
 *
 * @author radamian and Cory McKay
 */
public class VariabilityInLengthOfMelodicArcsFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VariabilityInLengthOfMelodicArcsFeature()
	{
		String name = "Variability in Length of Melodic Arcs";
		String code = "M-83";
		String description = "Standard deviation of the number of notes comprising melodic arcs, including the notes at the peaks and troughs. Similar assumptions are made in the calculation of this feature as for the Melodic Interval Histogram. Set to 0 if no melodic arcs are found.";
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
			LinkedList<Integer> lengths_of_melodic_arcs = new LinkedList<>();
			
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
						// The number of notes separating a peak from a trough. It is reset to 0 once the next
						// peak is met and the distance from the last peak to the new peak is calculated.
						int length_of_descent = 0;
						// The number of notes separating a trough from a peak. It is reset to 0 once the next
						// trough is met and the distance from the last trough to the new trough is 
						// calculated.
						int length_of_ascent = 0;
						
						// Find the lengths of each melodic arc
						for (int i = 0; i < intervals.length; i++)
						{
							// If arc is currently descending
							if (direction == -1)
							{
								if (intervals[i] < 0)
									length_of_descent++;
								else if (intervals[i] > 0)
								{
									if (length_of_ascent != 0)
									{
										lengths_of_melodic_arcs.add(length_of_ascent + length_of_descent + 1);
										length_of_ascent = 0;
									}
									direction = 1;
									length_of_ascent++;
								}
								else if (intervals[i] == 0)
								{
									// A lookahead to find the next non-zero interval; unison intervals only 
									// contribute to the arc's length if they occur mid-arc.
									for (int j = i; j < intervals.length; j++)
										if (intervals[j] < 0)
										{
											length_of_descent++;
											break;
										}
										else if (intervals[j] > 0)
											break;
								}	
							}

							// If arc is currently ascending
							else if (direction == 1)
							{
								if (intervals[i] > 0)
									length_of_ascent++;
								else if (intervals[i] < 0)
								{
									if (length_of_descent != 0)
									{
										lengths_of_melodic_arcs.add(length_of_ascent + length_of_descent + 1);
										length_of_descent = 0;
									}
									direction = -1;
									length_of_descent++;
								}
								else if (intervals[i] == 0)
								{
									// A lookahead to find the next non-zero interval; unison intervals only 
									// contribute to the arc's length if they occur mid-arc.
									for (int j = i; j < intervals.length; j++)
										if (intervals[j] < 0)
											break;
										else if (intervals[j] > 0)
										{
											length_of_ascent++;
											break;
										}
								}
							}

							// Handle the first interval
							else if (direction == 0)
							{
								if (intervals[i] > 0)
								{
									direction = 1;
									length_of_ascent++;
								}
								else if (intervals[i] < 0)
								{
									direction = -1;
									length_of_descent++;
								}
							}
							
							// Handle case when last interval is encountered
							if (i == intervals.length - 1)
								if (length_of_descent != 0 && length_of_ascent != 0)
									lengths_of_melodic_arcs.add(length_of_ascent + length_of_descent + 1);
						}
					}
				}
			}
			
			// Prepare array for standard deviation calculation
			int[] lengths_of_melodic_arcs_in_array = new int[lengths_of_melodic_arcs.size()];
			for (int i = 0; i < lengths_of_melodic_arcs_in_array.length; i++)
				lengths_of_melodic_arcs_in_array[i] = lengths_of_melodic_arcs.get(i);
			
			// Calculate the value
			if (lengths_of_melodic_arcs_in_array.length == 0)
				value = 0.0;
			else
				value = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(lengths_of_melodic_arcs_in_array);
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}