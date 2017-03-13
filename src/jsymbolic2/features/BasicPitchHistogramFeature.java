package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature vector consisting of bin magnitudes of the basic pitch histogram described in the jSymbolic
 * manual. Each bin corresponds to one of the 128 MIDI pitches, ordered from lowest to highest, and with an
 * interval of a semitone between each (enharmonic equivalents are assigned the same pitch number). Bin 60
 * corresponds to middle C. The magnitude of of each bin is proportional to the the number of times notes
 * occurred at the bin's pitch in the piece, relative to all other pitches in the piece (the histogram is
 * normalized).
 *
 * @author Cory McKay
 */
public class BasicPitchHistogramFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public BasicPitchHistogramFeature()
	{
		code = "P-1";
		String name = "Basic Pitch Histogram";
		String description = "A feature vector consisting of bin magnitudes of the basic pitch histogram described in the jSymbolic manual. Each bin corresponds to one of the 128 MIDI pitches, ordered from lowest to highest, and with an interval of a semitone between each (enharmonic equivalents are assigned the same pitch number). Bin 60 corresponds to middle C. The magnitude of of each bin is proportional to the the number of times notes occurred at the bin's pitch in the piece, relative to all other pitches in the piece (the histogram is normalized).";
		boolean is_sequential = true;
		int dimensions = 128;
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
			result = new double[sequence_info.basic_pitch_histogram.length];
			for (int pitch = 0; pitch < result.length; pitch++)
				result[pitch] = sequence_info.basic_pitch_histogram[pitch];
		}
		return result;
	}
}