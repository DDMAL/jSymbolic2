package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the average change of loudness from one note to the next note in the same
 * MIDI channel.
 *
 * @author Cory McKay
 */
public class AverageNoteToNoteChangeInDynamics
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	{
		code = "D-4";
		String name = "Average Note to Note Change in Dynamics";
		String description = "Average change of loudness from one note to the next note in the same MIDI channel. ";
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
			// Find the total number of intervals between notes on each channel
			int number_changes = 0;
			for (int i = 0; i < sequence_info.note_loudnesses.length; i++)
				if (sequence_info.note_loudnesses[i].length > 1)
					number_changes += sequence_info.note_loudnesses[i].length;

			// Find all of the loudness intervals
			double[] loudness_intervals = new double[number_changes];
			int count = 0;
			for (int i = 0; i < sequence_info.note_loudnesses.length; i++)
			{
				if (sequence_info.note_loudnesses[i].length > 1)
				{
					for (int j = 1; j < sequence_info.note_loudnesses[i].length; j++)
					{
						loudness_intervals[count] = (double) Math.abs(sequence_info.note_loudnesses[i][j]
								- sequence_info.note_loudnesses[i][j - 1]);
						count++;
					}
				}
			}

			// Calculate the average
			if (loudness_intervals == null || loudness_intervals.length == 0)
				value = 0.0;
			else
				value = mckay.utilities.staticlibraries.MathAndStatsMethods.getAverage(loudness_intervals);
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}