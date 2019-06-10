package jsymbolic2.features.rhythm;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the kurtosis of the rhythmic pulses in the tempo-standardized beat histogram. 
 *
 * @author radamian
 */
public class RhythmicPulseKurtosisTempoStandardizedFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public RhythmicPulseKurtosisTempoStandardizedFeature()
	{
		String name = "Rhythmic Pulse Kurtosis - Tempo Standardized";
		String code = "R-74";
		String description = "Kurtosis of the rhythmic pulses in the tempo-standardized beat histogram. Provides a measure of how peaked or flat the pulse distribution is. The higher the kurtosis, the more the pulses are clustered near the mean and the fewer outliers there are.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = null;
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
		double kurtosis;
		if (sequence_info != null)
		{
			// Get tempo-standardized beat histogram
			double[] beat_histogram_standardized = new double[sequence_info.beat_histogram_120_bpm_standardized.length - 40];
			for (int i = 0; i < beat_histogram_standardized.length; i++)
				beat_histogram_standardized[i] = sequence_info.beat_histogram_120_bpm_standardized[i + 40];
			
			kurtosis = mckay.utilities.staticlibraries.MathAndStatsMethods.getSampleExcessKurtosis(beat_histogram_standardized);
		}
		else kurtosis = -1.0;
		
		double[] result = new double[1];
		result[0] = kurtosis;
		return result;
	}
}