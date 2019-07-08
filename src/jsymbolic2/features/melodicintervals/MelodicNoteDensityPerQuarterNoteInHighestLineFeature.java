package jsymbolic2.features.melodicintervals;

import javax.sound.midi.*;
import java.util.LinkedList;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.NoteInfo;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the average number of note onsets per unit of time corresponding to an 
 * idealized quarter note in the MIDI channel with the highest average pitch. Multiple notes starting 
 * simultaneously are only treated as a single note in this calculation. 
 *
 * @author radamian
 */
public class MelodicNoteDensityPerQuarterNoteInHighestLineFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MelodicNoteDensityPerQuarterNoteInHighestLineFeature()
	{
		String name = "Melodic Note Density per Quarter Note in Highest Line";
		String code = "M-93";
		String description = "Average number of note onsets per unit of time corresponding to an idealized quarter note in the MIDI channel with the highest average pitch. Multiple notes starting simultaneously are only treated as a single note in this calculation.";
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
			LinkedList<LinkedList<Integer>>[][] slices_by_track_and_channel = sequence_info.note_onset_slice_container.getNoteOnsetSlicesByTrackAndChannelMelodicLinesOnly();
			
			int track_with_highest_average_pitch = sequence_info.track_and_channel_with_highest_average_pitch[0];
			int channel_with_highest_average_pitch = sequence_info.track_and_channel_with_highest_average_pitch[1];
			
			// Count the number of note onsets that belong to the melody
			int number_of_note_onsets = 0;
			for (int slice = 0; slice < slices_by_track_and_channel[track_with_highest_average_pitch][channel_with_highest_average_pitch].size(); slice++)
				if (sequence_info.note_onset_slice_container.isHighestPitchInSliceNewOnset(slice, track_with_highest_average_pitch, channel_with_highest_average_pitch))
					number_of_note_onsets++;
			
			// Calculate the feature value
			if (sequence_info.average_quarter_note_duration_in_seconds == 0.0)
				value = 0.0;
			else
				value = (double) number_of_note_onsets / sequence_info.average_quarter_note_duration_in_seconds;
		} 
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
    }
}