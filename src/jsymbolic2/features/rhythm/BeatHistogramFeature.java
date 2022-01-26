package jsymbolic2.features.rhythm;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature extractor that calculates a feature vector consisting of the bin magnitudes of the
 * autocorrelation-based beat histogram described in the jSymbolic manual. Each bin corresponds to a different
 * beats per minute periodicity, with periodicity tempo increasing by 1 BPM with each bin index. The first bin
 * corresponds to 40 BPM (lower BPM values are not included, as they tend to be too noisy), and the last bin
 * corresponds to 260 BPM. The magnitude of each bin is proportional to the cumulative loudness (MIDI
 * velocity) of all the notes that occur at that bin's rhythmic periodicity. The histogram is normalized.
 * Calculations use the overall mean tempo of the piece, in order to emphasize the metrical notation of the
 * recording, and thus do not take into account tempo variations in the piece.
 *
 * @author Cory McKay
 */
public class BeatHistogramFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public BeatHistogramFeature()
	{
		String name = "Beat Histogram";
		String code = "RT-16";
		String description = "A feature vector consisting of the bin magnitudes of the autocorrelation-based beat histogram described in the jSymbolic manual. Each bin corresponds to a different beats per minute periodicity, with periodicity tempo increasing by 1 BPM with each bin index. The first bin corresponds to 40 BPM (lower BPM values are not included, as they tend to be too noisy), and the last bin corresponds to 260 BPM. The magnitude of each bin is proportional to the cumulative loudness (MIDI velocity) of all the notes that occur at that bin's rhythmic periodicity. The histogram is normalized. Calculations use the overall mean tempo of the piece, in order to emphasize the metrical notation of the recording, and thus do not take into account tempo variations in the piece.";
		boolean is_sequential = true;
		int dimensions = 221;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = null;
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
		double[] result = null;
		if (sequence_info != null)
		{
			result = new double[sequence_info.beat_histogram.length - 40];
			for (int i = 0; i < result.length; i++)
				result[i] = sequence_info.beat_histogram[i + 40];
		}
		
		return result;
	}
}