package jsymbolic2.features;

import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Ratio of all wrapped vertical intervals that are dissonant (2nds, tritones, and 7ths to all wrapped
 * vertical intervals that are consonant (unisons, 3rds, 4ths, 5ths, 6ths, octaves). This is weighted by how
 * long each of these intervals are held (e.g. an interval lasting a whole note will be weighted four times as
 * strongly as an interval lasting a quarter note). Set to 0 if there are no dissonant vertical intervals or
 * no consonant vertical intervals.
 *
 * @author Tristano Tenaglia Cory McKay
 */
public class VerticalDissonanceRatioFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VerticalDissonanceRatioFeature()
	{
		code = "C-24";
		String name = "Vertical Dissonance Ratio";
		String description = "Ratio of all wrapped vertical intervals that are dissonant (2nds, tritones, and 7ths to all wrapped vertical intervals that are consonant (unisons, 3rds, 4ths, 5ths, 6ths, octaves). This is weighted by how long each of these intervals are held (e.g. an interval lasting a whole note will be weighted four times as strongly as an interval lasting a quarter note). Set to 0 if there are no dissonant vertical intervals or no consonant vertical intervals.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, description, is_sequential, dimensions);
		dependencies = new String[] { "Wrapped Vertical Interval Histogram" };
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
			double[] wrappped_vertical_interval_histogram = other_feature_values[0];
			
			double dissonance = wrappped_vertical_interval_histogram[1] +
			                    wrappped_vertical_interval_histogram[2] +
			                    wrappped_vertical_interval_histogram[6] +
			                    wrappped_vertical_interval_histogram[10] +
			                    wrappped_vertical_interval_histogram[11];
			
			double consonance = wrappped_vertical_interval_histogram[0] +
			                    wrappped_vertical_interval_histogram[3] +
			                    wrappped_vertical_interval_histogram[4] +
			                    wrappped_vertical_interval_histogram[5] +
			                    wrappped_vertical_interval_histogram[7] +
			                    wrappped_vertical_interval_histogram[8] +
			                    wrappped_vertical_interval_histogram[9];
			
			if (dissonance == 0.0 || consonance == 0.0)
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