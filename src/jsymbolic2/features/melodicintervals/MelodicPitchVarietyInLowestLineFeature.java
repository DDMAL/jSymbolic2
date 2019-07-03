package jsymbolic2.features.melodicintervals;

import java.util.LinkedList;
import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the average number of notes that go by in the MIDI channel with the lowest 
 * average pitch before a note's pitch is repeated (including the repeated note itself). Similar assumptions 
 * are made in the calculation of this feature as for the Melodic Interval Histogram. Notes that do not recur 
 * after 16 notes in the same channel are not included in this calculation. Set to 0 if there are no 
 * qualifying repeated notes in the piece. 
 *
 * @author radamian, Cory McKay and Tristano Tenaglia
 */
public class MelodicPitchVarietyInLowestLineFeature
		extends MIDIFeatureExtractor
{

	/* CONSTRUCTOR ******************************************************************************************/

	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MelodicPitchVarietyInLowestLineFeature()
	{
		String name = "Melodic Pitch Variety in Lowest Line";
		String code = "M-137";
		String description = "Average number of notes that go by in the MIDI channel with the lowest average pitch before a note's pitch is repeated (including the repeated note itself). Similar assumptions are made in the calculation of this feature as for the Melodic Interval Histogram. Notes that do not recur after 16 notes in the same channel are not included in this calculation. Set to 0 if there are no qualifying repeated notes in the piece. ";
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
			
			LinkedList<LinkedList<Integer>>[][] slices_by_track_and_channel = sequence_info.note_onset_slice_container.getNoteOnsetSlicesByTrackAndChannelMelodicLinesOnly();
			
			// Build structure containing onset slices only for the channel with the highest line
			LinkedList<LinkedList<Integer>> slices_for_channel_with_highest_line = new LinkedList<>();
			for (int slice = 0; slice < slices_by_track_and_channel[0][channel_with_lowest_average_pitch].size(); slice++)
			{
				// Create new slice
				slices_for_channel_with_highest_line.add(new LinkedList<>());
				for (int n_track = 0; n_track < sequence.getTracks().length; n_track++)
					for (int i = 0; i < slices_by_track_and_channel[n_track][channel_with_lowest_average_pitch].get(slice).size(); i++)
						slices_for_channel_with_highest_line.get(slice).add(slices_by_track_and_channel[n_track][channel_with_lowest_average_pitch].get(slice).get(i));

				// Sort slice by increasing pitch
				slices_for_channel_with_highest_line.get(slice).sort((s1, s2) -> s1.compareTo(s2));
			}
					
			// The total number of notes for which a repeated note is found
			double number_of_repeated_notes_found = 0.0;

			// The total number of notes that go by before a note is repeated, all added together for all
			// note that are repeated within max_notes_that_can_go_by
			double summed_number_of_notes_before_pitch_repeated = 0.0;
			
			// The maximum number of notes that can go by before a note is discounted for the purposes
			// of this feature
			final int max_notes_that_can_go_by = 16;

			// Create a list of pitches encountered
			LinkedList<Integer> pitches_encountered = new LinkedList<>();
			// Create array that contains, for each pitch, the count of notes gone by since the last 
			// time a note with that pitch was encountered
			int[] counts_since_pitch_last_encountered = new int [128];

			// Go through onset slices
			for (int slice = 0; slice < slices_for_channel_with_highest_line.size(); slice++)
				if (!slices_for_channel_with_highest_line.get(slice).isEmpty())
				{
					// Get pitch belonging to the melody (the last pitch in the slice)
					int melodic_pitch = slices_for_channel_with_highest_line.get(slice).get(slices_for_channel_with_highest_line.get(slice).size() - 1);

					if (!pitches_encountered.contains(melodic_pitch))
					{
						pitches_encountered.add(melodic_pitch);
					}
					else
					{
						if (counts_since_pitch_last_encountered[melodic_pitch] <= max_notes_that_can_go_by)
						{
							number_of_repeated_notes_found++;
							summed_number_of_notes_before_pitch_repeated += counts_since_pitch_last_encountered[melodic_pitch];
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
				value = summed_number_of_notes_before_pitch_repeated / number_of_repeated_notes_found;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}