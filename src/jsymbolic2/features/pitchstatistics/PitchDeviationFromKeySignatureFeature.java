package jsymbolic2.features.pitchstatistics;

import javax.sound.midi.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.NoteInfo;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * The fraction of notes in the piece that do not belong to one of the 7 pitch classes specified by the key 
 * signature in effect at the time that they occur. 0 if there is no key signature specified in the piece.
 *
 * @author radamian
 */
public class PitchDeviationFromKeySignatureFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public PitchDeviationFromKeySignatureFeature()
	{
		String name = "Pitch Deviation from Key Signature";
		String code = "P-38";
		String description = "The fraction of notes in the piece that do not belong to one of the 7 pitch classes specified by the key signature in effect at the time that they occur. 0 if there is no key signature specified in the piece.";
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
			if (!((LinkedList) sequence_info.overall_metadata[4]).isEmpty()) // Check if key signature specified
			{
				Object[] key_signatures = ((LinkedList) sequence_info.overall_metadata[4]).toArray();
				int number_of_sharps_or_flats = 0;
				
				double deviating_notes = 0.0; // Total count of notes deviating from the key signature at the time they occur
				double[] pitch_class_histogram_by_signature = new double[12]; // Working pitch class histogram for each key signature
				for (int i = 0; i < pitch_class_histogram_by_signature.length; i++)
					pitch_class_histogram_by_signature[i] = 0.0;
				
				Map<Integer, List<NoteInfo>> start_tick_note_map = sequence_info.all_notes.getStartTickNoteMap();
				int tick_start = 0;
				int tick_end = 0;
				
				for (int i = 0; i < key_signatures.length; i++)
				{
					// Update key signature
					int[] key_signature = (int[]) key_signatures[i];
					number_of_sharps_or_flats = key_signature[0];
					
					// Update start and end tick values for the key signature
					if (key_signatures.length == 1) { tick_start = 0; }
					else tick_start = key_signature[2];
					if (key_signatures.length > i + 1) { tick_end = ((int[]) key_signatures[i + 1])[2]; }
					else tick_end = (int) sequence.getTickLength();
					
					// Record notes to working pitch class histogram
					for (int tick = tick_start; tick < tick_end; tick++)
					{
						List<NoteInfo> notes_at_tick = start_tick_note_map.get(tick);
						if (notes_at_tick != null)
							for (NoteInfo note: notes_at_tick)
								if (note.getChannel() != 10 - 1) // Not Channel 10 (Percussion)
									pitch_class_histogram_by_signature[note.getPitch() % 12]++;
					}

					// Increment deviating notes after finishing section with the key signature
					if (number_of_sharps_or_flats != -5 &&
						number_of_sharps_or_flats != -4 &&
						number_of_sharps_or_flats != -3 &&
						number_of_sharps_or_flats != -2 &&
						number_of_sharps_or_flats != -1 &&
						number_of_sharps_or_flats != 0 &&
						number_of_sharps_or_flats != 1 &&
						number_of_sharps_or_flats != 7) 
					{ deviating_notes += pitch_class_histogram_by_signature[0]; }

					if (number_of_sharps_or_flats != -7 &&
						number_of_sharps_or_flats != -6 &&
						number_of_sharps_or_flats != -5 &&
						number_of_sharps_or_flats != -4 &&
						number_of_sharps_or_flats != 2 &&
						number_of_sharps_or_flats != 3 &&
						number_of_sharps_or_flats != 4 &&
						number_of_sharps_or_flats != 5 &&
						number_of_sharps_or_flats != 6 &&
						number_of_sharps_or_flats != 7) 
					{ deviating_notes += pitch_class_histogram_by_signature[1]; }

					if (number_of_sharps_or_flats != -3 &&
						number_of_sharps_or_flats != -2 &&
						number_of_sharps_or_flats != -1 &&
						number_of_sharps_or_flats != 0 &&
						number_of_sharps_or_flats != 1 &&
						number_of_sharps_or_flats != 2 &&
						number_of_sharps_or_flats != 3) 
					{ deviating_notes += pitch_class_histogram_by_signature[2]; }

					if (number_of_sharps_or_flats != -7 &&
						number_of_sharps_or_flats != -6 &&
						number_of_sharps_or_flats != -5 &&
						number_of_sharps_or_flats != -4 &&
						number_of_sharps_or_flats != -3 &&
						number_of_sharps_or_flats != -2 &&
						number_of_sharps_or_flats != 4 &&
						number_of_sharps_or_flats != 5 &&
						number_of_sharps_or_flats != 6 &&
						number_of_sharps_or_flats != 7) 
					{ deviating_notes += pitch_class_histogram_by_signature[3]; }

					if (number_of_sharps_or_flats != -7 &&
						number_of_sharps_or_flats != -1 &&
						number_of_sharps_or_flats != 0 &&
						number_of_sharps_or_flats != 1 &&
						number_of_sharps_or_flats != 2 &&
						number_of_sharps_or_flats != 3 &&
						number_of_sharps_or_flats != 4 &&
						number_of_sharps_or_flats != 5)
					{ deviating_notes += pitch_class_histogram_by_signature[4]; }

					if (number_of_sharps_or_flats != -6 &&
						number_of_sharps_or_flats != -5 &&
						number_of_sharps_or_flats != -4 &&
						number_of_sharps_or_flats != -3 &&
						number_of_sharps_or_flats != -2 &&
						number_of_sharps_or_flats != -1 &&
						number_of_sharps_or_flats != 0 &&
						number_of_sharps_or_flats != 6 &&
						number_of_sharps_or_flats != 7)
					{ deviating_notes += pitch_class_histogram_by_signature[5]; }

					if (number_of_sharps_or_flats != -7 &&
						number_of_sharps_or_flats != -6 &&
						number_of_sharps_or_flats != -5 &&
						number_of_sharps_or_flats != 1 &&
						number_of_sharps_or_flats != 2 &&
						number_of_sharps_or_flats != 3 &&
						number_of_sharps_or_flats != 4 &&
						number_of_sharps_or_flats != 5 &&
						number_of_sharps_or_flats != 6 &&
						number_of_sharps_or_flats != 7)
					{ deviating_notes += pitch_class_histogram_by_signature[6]; }

					if (number_of_sharps_or_flats != -4 &&
						number_of_sharps_or_flats != -3 &&
						number_of_sharps_or_flats != -2 &&
						number_of_sharps_or_flats != -1 &&
						number_of_sharps_or_flats != 0 &&
						number_of_sharps_or_flats != 1 &&
						number_of_sharps_or_flats != 2)
					{ deviating_notes += pitch_class_histogram_by_signature[7]; }

					if (number_of_sharps_or_flats != -7 &&
						number_of_sharps_or_flats != -6 &&
						number_of_sharps_or_flats != -5 &&
						number_of_sharps_or_flats != -4 &&
						number_of_sharps_or_flats != -3 &&
						number_of_sharps_or_flats != 3 &&
						number_of_sharps_or_flats != 4 &&
						number_of_sharps_or_flats != 5 &&
						number_of_sharps_or_flats != 6 &&
						number_of_sharps_or_flats != 7)
					{ deviating_notes += pitch_class_histogram_by_signature[8]; }

					if (number_of_sharps_or_flats != -2 &&
						number_of_sharps_or_flats != -1 &&
						number_of_sharps_or_flats != 0 &&
						number_of_sharps_or_flats != 1 &&
						number_of_sharps_or_flats != 2 &&
						number_of_sharps_or_flats != 3 &&
						number_of_sharps_or_flats != 4)
					{ deviating_notes += pitch_class_histogram_by_signature[9]; }

					if (number_of_sharps_or_flats != -7 &&
						number_of_sharps_or_flats != -6 &&
						number_of_sharps_or_flats != -5 &&
						number_of_sharps_or_flats != -4 &&
						number_of_sharps_or_flats != -3 &&
						number_of_sharps_or_flats != -2 &&
						number_of_sharps_or_flats != -1 &&
						number_of_sharps_or_flats != 5 &&
						number_of_sharps_or_flats != 6 &&
						number_of_sharps_or_flats != 7)
					{ deviating_notes += pitch_class_histogram_by_signature[10]; }

					if (number_of_sharps_or_flats != -7 &&
						number_of_sharps_or_flats != -6 &&
						number_of_sharps_or_flats != 0 &&
						number_of_sharps_or_flats != 1 &&
						number_of_sharps_or_flats != 2 &&
						number_of_sharps_or_flats != 3 &&
						number_of_sharps_or_flats != 4 &&
						number_of_sharps_or_flats != 5 &&
						number_of_sharps_or_flats != 6)
					{ deviating_notes += pitch_class_histogram_by_signature[11]; }
					
					// Re-initialize working pitch class histogram
					for (int bin = 0; bin < pitch_class_histogram_by_signature.length; bin++)
						pitch_class_histogram_by_signature[bin]= 0.0;
				}
				
				value = (double) deviating_notes / sequence_info.total_number_pitched_note_ons;
			}
			else value = 0.0;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}