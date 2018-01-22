package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature extractor that calculates a feature vector consisting of the bin magnitudes of the beat histogram
 * described in the jSymbolic manual. The first 40 bins are not included in this feature vector, however. Each
 * bin corresponds to a different beats per minute periodicity, with tempo increasing with the bin index. The
 * magnitude of each bin is proportional to the cumulative loudness (MIDI velocity) of the notes that occur at
 * that bin's rhythmic periodicity. The histogram is normalized.
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
		code = "RT-16";
		String name = "Beat Histogram";
		String description = "A feature vector consisting of the bin magnitudes of the beat histogram described in the jSymbolic manual. The first 40 bins are not included in this feature vector, however. Each bin corresponds to a different beats per minute periodicity, with tempo increasing with the bin index. The magnitude of each bin is proportional to the cumulative loudness (MIDI velocity) of the notes that occur at that bin's rhythmic periodicity. The histogram is normalized.";
		boolean is_sequential = true;
		int dimensions = 161;
		definition = new FeatureDefinition(name, description, is_sequential, dimensions);
		dependencies = null;
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