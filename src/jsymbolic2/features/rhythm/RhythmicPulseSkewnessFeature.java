package jsymbolic2.features.rhythm;

import java.util.LinkedList;
import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the skewness of the rhythmic pulses in the beat histogram (in terms of beat
 * periodicities). Provides a measure of how asymmetrical the distribution is: a value of zero indicates a
 * symmetrical distribution, a negative value indicates a left skew and a positive value indicates a right
 * skew.
 *
 * @author radamian
 */
public class RhythmicPulseSkewnessFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public RhythmicPulseSkewnessFeature()
	{
		String name = "Rhythmic Pulse Skewness";
		String code = "RT-35";
		String description = "Skewness of the rhythmic pulses in the beat histogram (in terms of beat periodicities). Provides a measure of how asymmetrical the distribution is: a value of zero indicates a symmetrical distribution, a negative value indicates a left skew and a positive value indicates a right skew.";
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
		double skewness;
		if (sequence_info != null)
		{
			// Get beat histogram
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
			
			skewness = mckay.utilities.staticlibraries.MathAndStatsMethods.getSkewness(periodicities_array);
		}
		else return null;
		
		double[] result = new double[1];
		result[0] = skewness;
		return result;
	}
}