package jsymbolic2.features.melodicintervals;

import java.util.LinkedList;
import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the average number of notes that go by in the MIDI channel with the highest 
 * average pitch before a note's pitch is repeated (including the repeated note itself). Similar assumptions 
 * are made in the calculation of this feature as for the Melodic Interval Histogram. Notes that do not recur 
 * after 16 notes in the same channel are not included in this calculation. Set to 0 if there are no 
 * qualifying repeated notes in the piece. 
 *
 * @author radamian, Cory McKay and Tristano Tenaglia
 */
public class MelodicPitchVarietyInHighestLineFeature
		extends MIDIFeatureExtractor
{

	/* CONSTRUCTOR ******************************************************************************************/

	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MelodicPitchVarietyInHighestLineFeature()
	{
		String name = "Melodic Pitch Variety in Highest Line";
		String code = "M-117";
		String description = "Average number of notes that go by in the MIDI channel with the highest average pitch before a note's pitch is repeated (including the repeated note itself). Similar assumptions are made in the calculation of this feature as for the Melodic Interval Histogram. Notes that do not recur after 16 notes in the same channel are not included in this calculation. Set to 0 if there are no qualifying repeated notes in the piece. ";
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
			int track_with_highest_average_pitch = sequence_info.track_and_channel_with_highest_average_pitch[0];
			int channel_with_highest_average_pitch = sequence_info.track_and_channel_with_highest_average_pitch[1];
					
			// The total number of notes for which a repeated note is found
			int number_of_repeated_notes_found = 0;

			// The total number of notes that go by before a note is repeated, all added together for all
			// note that are repeated within max_notes_that_can_go_by
			int summed_number_of_notes_before_pitch_repeated = 0;
			
			// The maximum number of notes that can go by before a note is discounted for the purposes
			// of this feature
			final int max_notes_that_can_go_by = 16;

			// Create a list of pitches encountered
			LinkedList<Integer> pitches_encountered = new LinkedList<>();
			// Create array that contains, for each pitch, the count of notes gone by since the last 
			// time a note with that pitch was encountered
			int[] counts_since_pitch_last_encountered = new int [128];
			for (int i = 0; i < counts_since_pitch_last_encountered.length; i++)
						counts_since_pitch_last_encountered[i] = 0;

			// Go through onset slices of the track and channel with the highest average pitch
			LinkedList<LinkedList<Integer>> onset_slices = sequence_info.note_onset_slice_container.getNoteOnsetSlicesByTrackAndChannelMelodicLinesOnly()[track_with_highest_average_pitch][channel_with_highest_average_pitch];
			for (int slice = 0; slice < onset_slices.size(); slice++)
				if (!onset_slices.get(slice).isEmpty())
				{
					// Get pitch belonging to the melody (the highest pitch in the slice)
					int melodic_pitch = onset_slices.get(slice).get(onset_slices.get(slice).size() - 1);

					if (!pitches_encountered.contains(melodic_pitch))
					{
						pitches_encountered.add(melodic_pitch);
					}
					else
					{
						if (counts_since_pitch_last_encountered[melodic_pitch] <= max_notes_that_can_go_by)
						{
							number_of_repeated_notes_found++;
							summed_number_of_notes_before_pitch_repeated += counts_since_pitch_last_encountered[melodic_pitch] + 1;
						}
						counts_since_pitch_last_encountered[melodic_pitch] = 0;
					}

					// Increment the count of notes gone by since the last time a pitch was encountered
					for (Integer pitch: pitches_encountered)
						if (pitch != melodic_pitch)
							counts_since_pitch_last_encountered[pitch]++;
				}
			
			// To deal with music with no repeated notes
			if (number_of_repeated_notes_found == 0)
				value = 0.0;
			else
				value = (double) summed_number_of_notes_before_pitch_repeated / number_of_repeated_notes_found;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}