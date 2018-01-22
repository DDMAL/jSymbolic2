package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the number of different quantized rhythmic values (e.g. quarter notes,
 * dotted quarter notes, half notes, etc.) that occur at least once in the music. This is found by finding all
 * non-zero entries in the Rhythmic Value Histogram.
 *
 * @author Cory McKay
 */
public class NumberOfDifferentRhythmicValuesPresentFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public NumberOfDifferentRhythmicValuesPresentFeature()
	{
		code = "R-15";
		String name = "Number of Different Rhythmic Values Present";
		String description = "Number of different quantized rhythmic values (e.g. quarter notes, dotted quarter notes, half notes, etc.) that occur at least once in the music. This is found by finding all non-zero entries in the Rhythmic Value Histogram.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, description, is_sequential, dimensions);
		dependencies = new String[] { "Rhythmic Value Histogram" };
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
			double[] rhythmic_value_histogram = other_feature_values[0];
			int count = 0;
			for (int bin = 0; bin < rhythmic_value_histogram.length; bin++)
				if (rhythmic_value_histogram[bin] > 0.0)
					count++;
			value = (double) count;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}