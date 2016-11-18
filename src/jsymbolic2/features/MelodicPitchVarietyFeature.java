package jsymbolic2.features;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.NoteInfo;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the average number of notes that go by in a MIDI channel before a note's
 * pitch is repeated (calculated across each channel individually before being combined). Notes that do not
 * recur after sixteen notes in the same channel are not included in this calculation.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class MelodicPitchVarietyFeature
		extends MIDIFeatureExtractor
{

	/* CONSTRUCTOR ******************************************************************************************/

	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MelodicPitchVarietyFeature()
	{
		code = "M-20";
		String name = "Melodic Pitch Variety";
		String description = "Average number of notes that go by in a MIDI channel before a note's pitch is repeated (calculated across each channel individually before being combined). Notes that do not recur after sixteen notes in the same channel are not included in this calculation.";
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
			value = 0.0;
			for (int channel = 0; channel < 16; channel++)
			{
				// Get and sort all notes in this channel
				List<NoteInfo> notes_in_this_channel = sequence_info.all_notes.getNotesOnChannel(channel);
				notes_in_this_channel.sort((n1, n2) -> ((Integer) n1.getStartTick()).compareTo(n2.getStartTick()));
				
				// A Map of playing notes in this channel indexed by tick
				Map<Integer, List<NoteInfo>> notes_in_this_channel_by_tick = sequence_info.all_notes.noteListToStartTickNoteMap(notes_in_this_channel);
				Set<Integer> channelTicks = notes_in_this_channel_by_tick.keySet();

				double channel_notes_by = 0;
				double number_variety_notes = 0;
				
				// For each note in this channel, compare with all other notes up to 16 notes away
				for (NoteInfo current_note : notes_in_this_channel)
				{
					int current_tick = current_note.getStartTick();
					int note_tick_count = 0;
					for (Integer tick : channelTicks)
					{
						if (tick > current_tick)
						{
							List<NoteInfo> notesAtTick = notes_in_this_channel_by_tick.get(tick);
							for (NoteInfo other_note : notesAtTick)
							{
								if (current_note.getPitch() == other_note.getPitch() && note_tick_count < 16)
								{
									// subtract to not include the repeated note
									channel_notes_by += note_tick_count - 1;
									number_variety_notes++;
								}
							}
						}
						note_tick_count++;
					}
				}
				
				// To avoid channels with no notes in them
				if (number_variety_notes != 0)
					value += channel_notes_by / number_variety_notes;
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}