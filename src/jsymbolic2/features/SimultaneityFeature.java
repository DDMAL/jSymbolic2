package jsymbolic2.features;

import java.util.stream.IntStream;
import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the average number of notes sounding simultaneously.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class SimultaneityFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public SimultaneityFeature()
	{
		code = "T-16";
		String name = "Simultaneity";
		String description = "Average number of notes sounding simultaneously.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, description, is_sequential, dimensions);
		dependencies = null;
		offsets = null;
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
			// A chart containing ticks in the first index and MIDI pitches in the second index.
			// Each tick corresponds to all 128 possible MIDI ticks.
			// The cumulative velocities of each tick-pitch combination are stored in each array value for the
			// given MIDI sequence.
			short[][] ticks_pitch = sequence_info.pitch_strength_by_tick_chart;

			// An array for number of notes at each tick
			int total_length = (int) sequence.getTickLength() + 1;
			int[] notes_on_tick = new int[total_length];
			int[] simultaneous_ticks = new int[total_length];

			// Compute simultaneous based on pitches that happen at the same tick on different tracks
			for (int tick = 0; tick < ticks_pitch.length; tick++)
			{
				for (int pitch = 0; pitch < ticks_pitch[tick].length; pitch++)
				{
					int current_velocity = ticks_pitch[tick][pitch];

					// Add a note count if we have it
					if (current_velocity > 0)
						notes_on_tick[tick]++;

					// Add a tick count if we have more than 1 note on a tick
					if (notes_on_tick[tick] > 1)
						simultaneous_ticks[tick] = 1;
				}
			}

			// Sum up all simultaneous note ticks
			double total_simultaneous_notes = 0;
			for (int notes : notes_on_tick)
				if (notes > 1)
					total_simultaneous_notes += notes;
			
			// Calculate the total number of simultaneous ticks
			double total_simultaneous_ticks = IntStream.of(simultaneous_ticks).sum();
			value = total_simultaneous_notes / total_simultaneous_ticks;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}