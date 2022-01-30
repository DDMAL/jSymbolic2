package jsymbolic2.features.rhythm;

import java.util.LinkedList;
import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the standard deviation of the beat histogram(in terms of beat 
 * periodicities).
 *
 * @author Cory McKay
 */
public class RhythmicVariabilityFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public RhythmicVariabilityFeature()
	{
		String name = "Rhythmic Variability";
		String code = "RT-34";
		String description = "Standard deviation of the beat histogram (in terms of beat periodicities).";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[1];
		dependencies[0] = "Beat Histogram";
		offsets = null;
		is_default = true;
		is_secure = false;
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
			// Access the beat histogram
			double[] beat_histogram = other_feature_values[0];

			// Populate a list of periodicities (of size about 1000)
			LinkedList<Integer> periodicities = new LinkedList<>();
			for (int bin = 0; bin < beat_histogram.length; bin++)
			{
				int number_of_items_to_add = (int) (1000.0 * beat_histogram[bin]);
				int this_beat = 40 + bin;
				for (int i = 0; i < number_of_items_to_add; i++)
					periodicities.add(this_beat);
			}
			double[] periodicities_array = new double[periodicities.size()];
			for (int i = 0; i < periodicities_array.length; i++)
				periodicities_array[i] = (double) periodicities.get(i);

			// Calculate the value
			value = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(periodicities_array);
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}