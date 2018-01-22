package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the standard deviation of the tempo in beats per minute.
 *
 * @author Cory McKay
 */
public class TempoVariabilityFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public TempoVariabilityFeature()
	{
		code = "RT-3";
		String name = "Tempo Variability";
		String description = "Standard deviation of the tempo in beats per minute.";
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
			// Access necessary information
			double ticks_per_beat = (double) sequence.getResolution();
			double[] duration_of_ticks_in_seconds = sequence_info.duration_of_ticks_in_seconds;

			// Calculate all instantaneous tempos
			double[] beats_per_minute = new double[duration_of_ticks_in_seconds.length];
			for (int i = 0; i < beats_per_minute.length; i++)
			{
				double ticks_per_second = 1.0 / duration_of_ticks_in_seconds[i];
				double beats_per_second = ticks_per_second / ticks_per_beat;
				beats_per_minute[i] = beats_per_second * 60.0;
			}

			// Calculate the final feature value
			value = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(beats_per_minute);
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}