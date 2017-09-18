package jsymbolic2.features;

import java.util.LinkedList;
import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the number of pitched notes that have associated MIDI Pitch Bend messages
 * change direction at least twice in connection with the note in question, divided by the total number of
 * pitched Note Ons in the piece.
 *
 * @author Cory McKay
 */
public class VibratoPrevalenceFeature
     extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VibratoPrevalenceFeature()
	{
		code = "P-40";
		String name = "Vibrato Prevalence";
		String description = "Number of pitched notes that have associated MIDI Pitch Bend messages change direction at least twice in connection with the note in question, divided by the total number of pitched Note Ons in the piece.";
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
			if (sequence_info.pitch_bends_list.isEmpty()) // If there are no pitch bends
				value = 0.0;
			else
			{
				// Generate array of pitch bends
				Object[] notes_objects = sequence_info.pitch_bends_list.toArray();
				LinkedList[] notes = new LinkedList[notes_objects.length];
				for (int i = 0; i < notes.length; i++)
					notes[i] = (LinkedList) notes_objects[i];
				int[][] pitch_bends = new int[notes.length][];
				for (int i = 0; i < notes.length; i++)
				{
					Object[] this_note_pitch_bends_objects = notes[i].toArray();
					pitch_bends[i] = new int[this_note_pitch_bends_objects.length];
					for (int j = 0; j < pitch_bends[i].length; j++)
						pitch_bends[i][j] = ((Integer) this_note_pitch_bends_objects[j]).intValue();
				}

				// Find the number of changes of direction of bend bend for each note
				int notes_with_vibrato = 0;;
				for (int note = 0; note < pitch_bends.length; note++)
				{
					int changes = 0;
					int last_value = pitch_bends[note][0];
					int direction = 0;
					for (int bend = 0; bend < pitch_bends[note].length; bend++)
					{
						if (pitch_bends[note][bend] > last_value)
						{
							if (direction == -1)
								changes++;
							direction = 1;
						}
						else
						{
							if (pitch_bends[note][bend] < last_value)
							{
								if (direction == 1)
									changes++;
								direction = -1;
							}
						}
					}

					if (changes > 2)
						notes_with_vibrato++;
				}

				// Calculate the value
				if (sequence_info.total_number_pitched_note_ons == 0)
					value = 0.0;
				else 
					value = (double) notes_with_vibrato / (double) sequence_info.total_number_pitched_note_ons;
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}