package jsymbolic2.features.texture;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.LinkedList;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the average number of different MIDI track/channel voices sounding in each 
 * note onset window, divided by the total number of voices in the piece containing at least one pitched note. 
 * Set to 0 if there are no voices containing at least one pitched notes. MIDI Channel 10 notes are ignored.
 *
 * @author radamian
 */
public class TexturalFullnessFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public TexturalFullnessFeature()
	{
		String name = "Textural Fullness";
		String code = "T-36";
		String description = "Average number of different MIDI track/channel voices sounding in each note onset window, divided by the total number of voices in the piece containing at least one pitched note. Set to 0 if there are no voices containing at least one pitched notes. MIDI Channel 10 notes are ignored.";
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
			
			if (active_voices_count == 0)
				value = 0.0;
			else
			{
				// The note onset slices in the piece, divided by MIDI track and channel
				LinkedList<LinkedList<Integer>>[][] note_onset_slices_by_track_and_channel = sequence_info.note_onset_slice_container.getNoteOnsetSlicesByTrackAndChannel();
				
				// An array holding the number of different MIDI track/channel voices sounding in each note
				// onset slice
				double[] number_of_voices_in_slices = new double[sequence_info.note_onset_slice_container.NUMBER_OF_ONSET_SLICES];
				for (int i = 0; i < number_of_voices_in_slices.length; i++)
					number_of_voices_in_slices[i] = 0.0;
				
				for (int i = 0; i < sequence_info.note_onset_slice_container.NUMBER_OF_ONSET_SLICES; i++)
					for (int n_track = 0; n_track < note_onset_slices_by_track_and_channel.length; n_track++)
						for (int chan = 0; chan < note_onset_slices_by_track_and_channel[n_track].length; chan++)
							if (!note_onset_slices_by_track_and_channel[n_track][chan].get(i).isEmpty())
								number_of_voices_in_slices[i]++;
				
				// Set value
				value = mckay.utilities.staticlibraries.MathAndStatsMethods.getAverage(number_of_voices_in_slices);
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}