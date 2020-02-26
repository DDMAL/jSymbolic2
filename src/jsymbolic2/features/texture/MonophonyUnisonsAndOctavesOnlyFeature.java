package jsymbolic2.features.texture;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.LinkedList;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the fraction of note onset slices where either there is only one note, or 
 * where the only vertical intervals present are unisons or octaves. Set to 1 if there are only zero or one 
 * voices containing pitched notes. MIDI Channel 10 notes are ignored.
 *
 * @author radamian
 */
public class MonophonyUnisonsAndOctavesOnlyFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MonophonyUnisonsAndOctavesOnlyFeature()
	{
		String name = "Monophony - Unisons and Octaves Only";
		String code = "T-42";
		String description = "Fraction of note onset slices where either there is only one note, or where the only vertical intervals present are unisons or octaves. Set to 1 if there are only zero or one voices containing pitched notes. MIDI Channel 10 notes are ignored.";
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
				// The note onset slices in the piece, where octaves are collapsed (i.e. the list of pitches
				// for a slice where there is one note, or where the only vertical intervals present are 
				// unisons or octaves, will have a size of one)
				LinkedList<LinkedList<Integer>> note_onset_slices_in_pitch_classes = sequence_info.note_onset_slice_container.getNoteOnsetSlicesUsingPitchClasses();
				
				// Count the number of monophonic slices
				int number_of_monophonic_slices = 0;
				for (int slice = 0; slice < note_onset_slices_in_pitch_classes.size(); slice++)
					if (note_onset_slices_in_pitch_classes.get(slice).size() == 1)
						number_of_monophonic_slices++;
				
				// Set value
				value = (double) number_of_monophonic_slices / sequence_info.note_onset_slice_container.NUMBER_OF_ONSET_SLICES;
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}