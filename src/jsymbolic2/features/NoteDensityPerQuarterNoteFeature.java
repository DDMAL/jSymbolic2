package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the average number of note onsets per unit of time corresponding to an
 * idealized quarter note. Takes into account all notes in all voices, including both pitched and unpitched
 * notes.
 *
 * @author Cory McKay
 */
public class NoteDensityPerQuarterNoteFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public NoteDensityPerQuarterNoteFeature()
	{
		code = "R-10";
		String name = "Note Density per Quarter Note";
		String description = "Average number of note onsets per unit of time corresponding to an idealized quarter note. Takes into account all notes in all voices, including both pitched and unpitched notes.";
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
			// Find the total number of note ons
			int count = 0;
			for (int chan = 0; chan < sequence_info.channel_statistics.length; chan++)
				count += sequence_info.channel_statistics[chan][0];

			// Calculate the notes per second
			double notes_per_second = 0.0;
			if (sequence_info.sequence_duration != 0)
				notes_per_second = (double) count / sequence_info.sequence_duration_precise;

			// Calculate the notes per quarter note
			value = notes_per_second * sequence_info.average_quarter_note_duration_in_seconds;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}