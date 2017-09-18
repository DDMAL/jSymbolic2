package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the absolute value of the difference (in semitones) between the pitches of
 * the two most frequently occurring pitch classes.
 *
 * @author Cory McKay
 */
public class IntervalBetweenMostPrevalentPitchClassesFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public IntervalBetweenMostPrevalentPitchClassesFeature()
	{
		code = "P-23";
		String name = "Interval Between Most Prevalent Pitch Classes";
		String description = "Absolute value of the difference (in semitones) between the pitches of the two most frequently occurring pitch classes.";
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
			// Find the highest bin
			int max_index = mckay.utilities.staticlibraries.MathAndStatsMethods.getIndexOfLargest(sequence_info.pitch_class_histogram);

			// Find the second highest bin
			double second_max = 0;
			int second_max_index = 0;
			for (int bin = 0; bin < sequence_info.pitch_class_histogram.length; bin++)
			{
				if (sequence_info.pitch_class_histogram[bin] > second_max
						&& bin != max_index)
				{
					second_max = sequence_info.pitch_class_histogram[bin];
					second_max_index = bin;
				}
			}

			// Calculate the value
			int difference = Math.abs(max_index - second_max_index);
			value = (double) difference;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}