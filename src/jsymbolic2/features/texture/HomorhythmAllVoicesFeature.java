package jsymbolic2.features.texture;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.LinkedList;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the fraction of note onset slices where all notes sounding in the slice 
 * have the same rhythmic value (as defined by the Rhythmic Value Histogram, as described here). Has a 
 * (trivial) value of 1 if there are only zero or one voices with pitched notes in the piece. MIDI Channel 10 
 * notes are ignored.
 *
 * @author radamian
 */
public class HomorhythmAllVoicesFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public HomorhythmAllVoicesFeature()
	{
		String name = "Homorhythm – All Voices";
		String code = "T-40";
		String description = "Fraction of note onset slices where all notes sounding in the slice have the same rhythmic value (as defined by the Rhythmic Value Histogram, as described here). Has a (trivial) value of 1 if there are only zero or one voices with pitched notes in the piece. MIDI Channel 10 notes are ignored.";
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
				// The rhythmic values of the melodic notes in the piece, separated by MIDI track and channel.
				// Each rhythmic value specifies a tempo-independent note duration: each (quantized) duration 
				// is expressed as a fraction of a quarter note (e.g. a value of 0.5 corresponds to the 
				// duration of an eighth note). The possible (rhythmically quantized) values are 0.125, 0.25, 
				// 0.33333333, 0.5, 0.66666667, 0.75, 1.0, 2.0, 3.0, 4.0, 6.0, 8.0, 10.0 and 12.0. This 
				// structure excludes rhythmic values of notes belonging to the melody that are held from 
				// previous slices.
				LinkedList<LinkedList<Double>>[][] rhythmic_value_slices_by_track_and_channel_only_melodic_lines = sequence_info.note_onset_slice_container.getRhythmicValueSlicesByTrackAndChannelOnlyMelodicLines();
				
				// The number of note onset slices containing at least two note onsets, in which all notes 
				// have the same (quantized) rhythmic value
				int number_of_slices_with_homorhythm = 0;
				// The number of voices in the current slice
				int number_of_voices_in_slice = 0;
				// A list of unique quantized rhythmic values in the current slice
				LinkedList<Double> unique_rhythmic_values_in_slice = new LinkedList<>();
				
				// Iterate by slice
				for (int i = 0; i < sequence_info.note_onset_slice_container.NUMBER_OF_ONSET_SLICES; i++)
				{
					number_of_voices_in_slice = 0;
					unique_rhythmic_values_in_slice.clear();
					
					// Iterate through each voice
					for (int n_track = 0; n_track < rhythmic_value_slices_by_track_and_channel_only_melodic_lines.length; n_track++)
						for (int chan = 0; chan < rhythmic_value_slices_by_track_and_channel_only_melodic_lines[n_track].length; chan++)
						{
							// Verify there is a new note onset in this voice
							if (!rhythmic_value_slices_by_track_and_channel_only_melodic_lines[n_track][chan].get(i).isEmpty())
							{
								number_of_voices_in_slice++;
								
								// Add the rhythmic value of the note in this voice to 
								// unique_rhythmic_values_in_slice if it has not yet been encountered in the
								// current note onset slice
								if (!unique_rhythmic_values_in_slice.contains(rhythmic_value_slices_by_track_and_channel_only_melodic_lines[n_track][chan].get(i).get(0)))
									unique_rhythmic_values_in_slice.add(rhythmic_value_slices_by_track_and_channel_only_melodic_lines[n_track][chan].get(i).get(0));
							}
						}
					
					// Increment number_of_slices_with_homorhythm if there are comparable voices and one 
					// unique rhythmic value in the current slice
					if (number_of_voices_in_slice > 1 && unique_rhythmic_values_in_slice.size() == 1)
						number_of_slices_with_homorhythm++;
				}
				
				// Set value
				value = (double) number_of_slices_with_homorhythm / sequence_info.note_onset_slice_container.NUMBER_OF_ONSET_SLICES;
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}