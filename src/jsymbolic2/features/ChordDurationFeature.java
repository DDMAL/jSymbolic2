package jsymbolic2.features;

import javax.sound.midi.Sequence;
import java.util.ArrayList;
import java.util.Arrays;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the average duration a chord in units of time corresponding to the duration
 * of an idealized quarter note. A "chord" here is considered to stay the same as long as no new pitch classes
 * are added, and no pitch classes are taken away. This "chord" may consist of any number of pitch classes,
 * even only one. A "chord" is not considered to end if it is split by one or more rests (although the rests
 * themselves are not counted in the duration of the "chord").
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public class ChordDurationFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public ChordDurationFeature()
	{
		code = "C-27";
		String name = "Chord Duration";
		String description = "Average duration a chord in units of time corresponding to the duration of an idealized quarter note. A \"chord\" here is considered to stay the same as long as no new pitch classes are added, and no pitch classes are taken away. This \"chord\" may consist of any number of pitch classes, even only one. A \"chord\" is not considered to end if it is split by one or more rests (although the rests themselves are not counted in the duration of the \"chord\").";
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
			// A data structure indicating all pitch classes sounding at each MIDI tick. However, all ticks 
			// during which no notes are playing are excluded from this data structure. The first dimension
			// indicates the MIDI tick (after removal of rest ticks) and the second dimension indicates the 
			// note index (there will be one entry for each pitch class sounding during the given MIDI tick).
			// Each entry indicates the pitch class (0 to 11, where 0 is C) of one of the sounding notes.
			short[][] pitch_classes_present_by_tick_excluding_rests = sequence_info.pitch_classes_present_by_tick_excluding_rests;

			// The duration in ticks of each chord
			ArrayList<Integer> chord_durations_in_ticks = new ArrayList<>();
			
			// Iterate through ticks, looking for chords
			int duration_this_chord_in_ticks = 0;
			for (int tick = 0; tick < pitch_classes_present_by_tick_excluding_rests.length; tick++)
			{
				if (tick == 0)
					duration_this_chord_in_ticks = 1;
				else
				{
					short[] pitch_classes_previous_tick = pitch_classes_present_by_tick_excluding_rests[tick - 1];
					short[] pitch_classes_this_tick = pitch_classes_present_by_tick_excluding_rests[tick];
					
					if (Arrays.equals(pitch_classes_previous_tick, pitch_classes_this_tick))
						duration_this_chord_in_ticks++;
					else
					{
						chord_durations_in_ticks.add(duration_this_chord_in_ticks);
						duration_this_chord_in_ticks = 0;
					}
				}
			}
			chord_durations_in_ticks.add(duration_this_chord_in_ticks);

			// Find the average duration of a chord in ticks
			double average_ticks_per_chord = 0.0;
			int total_number_chords = chord_durations_in_ticks.size();
			for (int i = 0; i < total_number_chords; i++)
				average_ticks_per_chord += chord_durations_in_ticks.get(i);
			average_ticks_per_chord = average_ticks_per_chord / (double) total_number_chords;

			// Convert from ticks to seconds to quarter notes
			// NOTE: This is imperfect, because it does not take into account tempo change messages. Rather,
			// it assumes the tick duration is the same everywhere as on the same tick.
			double initial_duration_of_a_tick_in_seconds = sequence_info.duration_of_ticks_in_seconds[0];
			double seconds_per_chord = average_ticks_per_chord * initial_duration_of_a_tick_in_seconds;
			if (sequence_info.average_quarter_note_duration_in_seconds != 0.0)
				value = seconds_per_chord / sequence_info.average_quarter_note_duration_in_seconds;
			else value = 0.0;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}