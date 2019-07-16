package jsymbolic2.features.verticalintervals;

import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the ratio of all wrapped vertical intervals that are dissonant (2nds, 
 * perfect 4ths, tritones and 7ths) to all wrapped vertical intervals that are consonant (unisons, 3rds, 
 * perfect 5ths, 6ths, octaves). This is weighted by how long each of these intervals are held (e.g. an 
 * interval lasting a whole note will be weighted four times as strongly as an interval lasting a quarter 
 * note). Set to 0 if there are no dissonant vertical intervals or no consonant vertical intervals.
 *
 * @author radamian
 */
public class VerticalDissonanceRatioFourthsDissonantFeature
		extends MIDIFeatureExtractor
{

	/* CONSTRUCTOR ******************************************************************************************/

	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VerticalDissonanceRatioFourthsDissonantFeature()
	{
		String name = "Vertical Dissonance Ratio - Fourths Dissonant";
		String code = "C-51";
		String description = "Ratio of all wrapped vertical intervals that are dissonant (2nds, perfect 4ths, tritones and 7ths) to all wrapped vertical intervals that are consonant (unisons, 3rds, perfect 5ths, 6ths, octaves). This is weighted by how long each of these intervals are held (e.g. an interval lasting a whole note will be weighted four times as strongly as an interval lasting a quarter note). Set to 0 if there are no dissonant vertical intervals or no consonant vertical intervals.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[] { "Wrapped Vertical Interval Histogram" };
		offsets = null;
		is_default = true;
		is_secure = true;
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
			double[] wrapped_vertical_interval_histogram = other_feature_values[0];
			
			// Sum the fractions of dissonant wrapped vertical intervals
			double dissonance = wrapped_vertical_interval_histogram[1] +
								wrapped_vertical_interval_histogram[2] +
								wrapped_vertical_interval_histogram[5] +
								wrapped_vertical_interval_histogram[6] +
								wrapped_vertical_interval_histogram[10] +
								wrapped_vertical_interval_histogram[11];
			
			// Sum the fractions of consonant wrapped vertical intervals
			double consonance = wrapped_vertical_interval_histogram[0] +
								wrapped_vertical_interval_histogram[3] +
								wrapped_vertical_interval_histogram[4] +
								wrapped_vertical_interval_histogram[7] +
								wrapped_vertical_interval_histogram[8] +
								wrapped_vertical_interval_histogram[9];
			
			if (consonance == 0.0)
				value = 0.0;
			else
				value = dissonance / consonance;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}