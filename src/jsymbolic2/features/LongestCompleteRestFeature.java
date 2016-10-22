package jsymbolic2.features;

import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.FeatureConversion;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that calculates the longest amount of uninterrupted time (in seconds) in which no
 * pitched notes are sounding on any MIDI channel. Non-pitched (MIDI channel 10) notes are not considered in
 * this calculation.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class LongestCompleteRestFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public LongestCompleteRestFeature()
	{
		code = "R-27";
		String name = "Longest Complete Rest";
		String description = "Longest amount of uninterrupted time (in seconds) in which no pitched notes are sounding on any MIDI channel. Non-pitched (MIDI channel 10) notes are not considered in this calculation.";
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
		double value = 0.0;
		if (sequence_info != null)
		{
			short[][] pitch_strength_by_tick_chart = sequence_info.pitch_strength_by_tick_chart;

			// Find the longest streak of ticks with no sounding pitches
			double this_streak_length = 0;
			int this_streak_start_tick = 0;
			int this_streak_end_tick = 0;
			double max_streak_length = 0;
			int max_streak_start_tick = 0;
			int max_streak_end_tick = 0;
			for (int tick = 0; tick < pitch_strength_by_tick_chart.length; tick++)
			{
				// If no pitches are sounding
				if (FeatureConversion.allArrayEqual(pitch_strength_by_tick_chart[tick], 0))
				{
					this_streak_length++;
					if (this_streak_length == 1)
						this_streak_start_tick = tick;
				}
				
				// If at least one pitch is sounding
				else
				{
					this_streak_length = 0;
					if (tick > 0)
						this_streak_end_tick = tick;
				}

				// Compare current max streak to current streak
				if (this_streak_length > max_streak_length)
					max_streak_length = this_streak_length;
				if ( (this_streak_end_tick - this_streak_start_tick) > (max_streak_end_tick - max_streak_start_tick))
				{
					max_streak_end_tick = this_streak_end_tick;
					max_streak_start_tick = this_streak_start_tick;
				}
			}

			// Calculate the duration in seconds of the longest rest
			double[] seconds_per_tick = sequence_info.seconds_per_tick;
			for (int tick = max_streak_start_tick; tick < max_streak_end_tick; tick++)
				value += seconds_per_tick[tick];
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}