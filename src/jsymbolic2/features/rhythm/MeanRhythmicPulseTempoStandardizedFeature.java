package jsymbolic2.features.rhythm;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the mean tempo-standardized beat periodicity (in BPM), as averaged across
 * the tempo-standardized beat histogram.
 *
 * @author radamian
 */
public class MeanRhythmicPulseTempoStandardizedFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MeanRhythmicPulseTempoStandardizedFeature()
	{
		String name = "Mean Rhythmic Pulse - Tempo Standardized";
		String code = "R-63";
		String description = "Mean tempo-standardized beat periodicity (in BPM), as averaged across the tempo-standardized beat histogram.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[1];
		dependencies[0] = "Beat Histogram Tempo Standardized";
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
		double value = 0;
		if (sequence_info != null)
		{
			// Access the beat histogram
			double[] beat_histogram = other_feature_values[0];

			// Calculate the mean value of the beat histogram
			for (int i = 0; i < beat_histogram.length; i++)
			{
				double bin_value = 40.0 + (double) i;
				value += bin_value * beat_histogram[i];
			}
		}
		else value = -1.0;
		
		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}