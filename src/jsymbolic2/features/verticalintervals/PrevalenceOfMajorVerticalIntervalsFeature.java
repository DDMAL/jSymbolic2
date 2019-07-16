package jsymbolic2.features.verticalintervals;

import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the fraction of all wrapped vertical intervals that are major 3rds, 6ths or 
 * 7ths. This is weighted by how long each of these intervals are held (e.g. an interval lasting a whole note 
 * will be weighted four times as strongly as an interval lasting a quarter note).
 *
 * @author radamian
 */
public class PrevalenceOfMajorVerticalIntervalsFeature
		extends MIDIFeatureExtractor
{

	/* CONSTRUCTOR ******************************************************************************************/

	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public PrevalenceOfMajorVerticalIntervalsFeature()
	{
		String name = "Prevalence of Major Vertical Intervals";
		String code = "C-53";
		String description = "Fraction of all wrapped vertical intervals that are major 3rds, 6ths or 7ths. This is weighted by how long each of these intervals are held (e.g. an interval lasting a whole note will be weighted four times as strongly as an interval lasting a quarter note).";
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
			double fraction_major_intervals = 0.0;
			
			double[] wrapped_vertical_interval_histogram = other_feature_values[0];
			
			// Sum the fractions of major wrapped vertical intervals (major 3rds count 4 semitones, major 6ths
			// count 9 semitones, and major 7ths count 11 semitones)
			fraction_major_intervals += wrapped_vertical_interval_histogram[4];
			fraction_major_intervals += wrapped_vertical_interval_histogram[9];
			fraction_major_intervals += wrapped_vertical_interval_histogram[11];
			
			value = fraction_major_intervals;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}