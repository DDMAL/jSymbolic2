package jsymbolic2.features.texture;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.LinkedList;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator whose output has a value of 1 if all note onset slices have either only one note, a 
 * vertical unison, a vertical octave or some other single vertical interval. Has a value of 0 if one or more 
 * note onset slices has a vertical interval that is not a unison, not an octave or not the other single 
 * vertical interval (i.e. if there are more than two non-unison and non-octave vertical intervals in the 
 * piece). Set to 1 if there are only zero or one voices containing pitched notes. MIDI Channel 10 notes are 
 * ignored.
 *
 * @author radamian
 */
public class MonophonyFixedIntervalBetweenVoicesFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MonophonyFixedIntervalBetweenVoicesFeature()
	{
		String name = "Monophony - Fixed Interval Between Voices";
		String code = "T-43";
		String description = "Has a value of 1 if all note onset slices have either only one note, a vertical unison, a vertical octave or some other single vertical interval. Has a value of 0 if one or more note onset slices has a vertical interval that is not a unison, not an octave or not the other single vertical interval (i.e. if there are more than two non-unison and non-octave vertical intervals in the piece). Set to 1 if there are only zero or one voices containing pitched notes. MIDI Channel 10 notes are ignored.";
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
			// Find the number of pitched MIDI track/channnel pairings with at least one note on
			int active_voices_count = 0;
			for (int n_track = 0; n_track < sequence_info.track_and_channel_statistics.length; n_track++)
				for (int chan = 0; chan < sequence_info.track_and_channel_statistics[n_track].length; chan++)
					if (sequence_info.track_and_channel_statistics[n_track][chan][0] != 0 && chan != 10 - 1)
						active_voices_count++;
			
			if (active_voices_count < 2)
				value = 1.0;
			else
			{
				// The note onset slices in the piece, with duplicate pitches excluded (i.e. an onset slice 
				// with a vertical unison have a size of one)
				LinkedList<LinkedList<Integer>> note_onset_slices = sequence_info.note_onset_slice_container.getNoteOnsetSlices();
				// The note onset slices in the piece, with duplicate pitches included (i.e. an onset slice
				// with a single note will have a size of one)
				LinkedList<LinkedList<Integer>> note_onset_slices_duplicates_included = sequence_info.note_onset_slice_container.getNoteOnsetSlicesDuplicatePitchesIncluded();
				
				// Whether all slices have either a single note, a vertical unison or octave, or some other
				// fixed vertical interval
				boolean piece_is_monophonic_by_fixed_interval = true;
				// Whether an onset slice with a single note has been encountered
				boolean single_note_encountered = false;
				//	Whether an onset slice with a vertical unison has been encountered
				boolean unison_encountered = false;
				// Whether an onset slice with a non-unison vertical interval has been encountered
				boolean non_unison_interval_encountered = false;
				// The non-unison vertical interval encountered, in number of semitones
				int fixed_interval = 0;
				
				// Iterate by slice
				for (int slice = 0; slice < note_onset_slices.size(); slice++)
				{
					// This slice has a single note
					if (note_onset_slices_duplicates_included.get(slice).size() == 1)
					{
						single_note_encountered = true;
					}
					else
					{
						// This slice has a vertical unison
						if (note_onset_slices.get(slice).size() == 1)
						{
							unison_encountered = true;
						}
						
						// This slice has a non-unison vertical interval
						else if (note_onset_slices.get(slice).size() == 2)
						{
							// If a non-unison vertical interval has been encountered, verify whether the
							// non-unison vertical interval in this slice matches it
							if (non_unison_interval_encountered)
							{
								if (note_onset_slices.get(slice).get(1) - note_onset_slices.get(slice).get(0) != fixed_interval)
								{
									piece_is_monophonic_by_fixed_interval = false;
									break;
								}
							}
							// Else set a fixed interval
							else
							{
								fixed_interval = note_onset_slices.get(slice).get(1) - note_onset_slices.get(slice).get(0);
								non_unison_interval_encountered = true;
							}
						}
						
						// There are more than two unique pitches in this slice
						else if (note_onset_slices.get(slice).size() > 2)
						{
							piece_is_monophonic_by_fixed_interval = false;
							break;
						}
					}
					
					// Check conditions for monophony by a fixed interval between voices
					if (!(single_note_encountered ^ unison_encountered ^ non_unison_interval_encountered))
					{
						piece_is_monophonic_by_fixed_interval = false;
						break;
					}
				}
				
				// Set value
				if (piece_is_monophonic_by_fixed_interval)
					value = 1.0;
				else
					value = 0.0;
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}