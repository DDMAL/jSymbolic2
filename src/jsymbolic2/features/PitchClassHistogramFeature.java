package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature vector consisting of bin magnitudes of the pitch class histogram described in the jSymbolic
 * manual. Each bin corresponds to one of the 12 pitch classes, ordered in increasing pitch with an interval
 * of a semitone between each (enharmonic equivalents are assigned the same pitch class number). The first bin
 * corresponds to the most common pitch class in the piece under consideration (it does NOT correspond to a
 * set pitch class). The magnitude of of each bin is proportional to the the number of times notes occurred at
 * the bin's pitch class in the piece, relative to all other pitch classes in the piece (the histogram is
 * normalized).
 *
 * @author Cory McKay
 */
public class PitchClassHistogramFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public PitchClassHistogramFeature()
	{
		code = "P-2";
		String name = "Pitch Class Histogram";
		String description = "A feature vector consisting of bin magnitudes of the pitch class histogram described in the jSymbolic manual. Each bin corresponds to one of the 12 pitch classes, ordered in increasing pitch with an interval of a semitone between each (enharmonic equivalents are assigned the same pitch class number). The first bin corresponds to the most common pitch class in the piece under consideration (it does NOT correspond to a set pitch class). The magnitude of of each bin is proportional to the the number of times notes occurred at the bin's pitch class in the piece, relative to all other pitch classes in the piece (the histogram is normalized).";
		boolean is_sequential = true;
		int dimensions = 12;
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
			// Find index of bin with highest frequency
			int index = mckay.utilities.staticlibraries.MathAndStatsMethods.getIndexOfLargest(sequence_info.pitch_class_histogram);

			// Construct new histogram starting with the bin of the most common pitch class
			result = new double[sequence_info.pitch_class_histogram.length];
			for (int i = 0; i < result.length; i++)
			{
				result[i] = sequence_info.pitch_class_histogram[index];
				index++;

				// Wrap around
				if (index == result.length)
					index = 0;
			}
		}
		return result;
	}
}