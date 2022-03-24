package jsymbolic2.features.verticalintervals;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the ratio of the fraction all wrapped vertical intervals that are minor 
 * 3rds, 6ths or 7ths to the fraction all wrapped vertical intervals that are major 3rds, 6ths or 7ths. This 
 * is weighted by how long each of these intervals are held (e.g. an interval lasting a whole note will be 
 * weighted four times as strongly as an interval lasting a quarter note) and based on MIDI velocity. Set to 0 
 * if there are no minor vertical intervals or no major vertical intervals.
 *
 * @author radamian
 */
public class MinorToMajorVerticalIntervalsRatioFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MinorToMajorVerticalIntervalsRatioFeature()
	{
		String name = "Minor to Major Vertical Intervals Ratio";
		String code = "C-54";
		String description = "Ratio of the fraction all wrapped vertical intervals that are minor 3rds, 6ths or 7ths to the fraction all wrapped vertical intervals that are major 3rds, 6ths or 7ths. This is weighted by how long each of these intervals are held (e.g. an interval lasting a whole note will be weighted four times as strongly as an interval lasting a quarter note) and based on MIDI velocity. Set to 0 if there are no minor vertical intervals or no major vertical intervals.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[] { "Prevalence of Minor Vertical Intervals", "Prevalence of Major Vertical Intervals"};
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
			double prevalence_of_minor_intervals = other_feature_values[0][0];
			double prevalence_of_major_intervals = other_feature_values[1][0];
			
			if (prevalence_of_major_intervals == 0.0)
				value = 0.0;
			else
				value = prevalence_of_minor_intervals / prevalence_of_major_intervals;		
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
    }
}