package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the number of notes with a duration less than 0.1 seconds, divided by the
 * total number of notes in the piece.
 *
 * @author Cory McKay
 */
public class AmountOfStaccatoFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public AmountOfStaccatoFeature()
	{
		code = "R-21";
		String name = "Amount of Staccato";
		String description = "Number of notes with a duration less than 0.1 seconds, divided by the total number of notes in the piece.";
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
			// Put durations in an array
			Object[] durations_obj = sequence_info.note_durations.toArray();
			double[] durations = new double[durations_obj.length];
			for (int i = 0; i < durations.length; i++)
				durations[i] = ((Double) durations_obj[i]).doubleValue();

			// Find the number of notes with short durations
			int short_count = 0;
			for (int i = 0; i < durations.length; i++)
				if (durations[i] < 0.1)
					short_count++;

			// Find the total number of note ons
			int count = 0;
			for (int chan = 0; chan < sequence_info.channel_statistics.length; chan++)
				count += sequence_info.channel_statistics[chan][0];

			// Calculate feautre value
			value = (double) short_count / (double) count;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}