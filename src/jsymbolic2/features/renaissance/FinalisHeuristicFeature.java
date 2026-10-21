package jsymbolic2.features.renaissance;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import java.util.ArrayList;

import java.util.List;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that uses a heuristic to approximate the finalis. The pitch class of the expected finalis
 * is returned. This heuristic works as follows: if the lowest pitch class of the final sonority is also the most
 * common pitch class throughout the piece, this is taken as the finalis. If it isn't, switch to the most common
 * pitch class if it is at least 10% more common and precisely a fifth from the lowest pitch class of the final
 * sonority.
 *
 * @author Jasper Teunen
 */
public class FinalisHeuristicFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public FinalisHeuristicFeature()
	{
		String name = "Finalis Heuristic";
		String code = "Ren-04";
		String description = "Heuristic estimate of finalis based on fifths pitch histogram";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[1];
		dependencies[0] = "Finalis Naive";
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
		double[] result = new double[1];
		if (sequence_info != null)
		{
			// Get naive estimates
            int finalis_naive = (7 * (int) other_feature_values[0][0]) % 12;
			double finalis_naive_freq = sequence_info.fifths_pitch_histogram[finalis_naive];

			// Initialize to naive estimates
            double max_freq = finalis_naive_freq;
			int max_pitch = finalis_naive;

			// Go through adjacent bins in fifths_pitch_histogram to check adjacent fifths
			int bins[] = {-1, 1};
			for (int bin = 0; bin <2; bin++){
				int curr_pitch = (finalis_naive + 12 + bins[bin]) % 12;
				double curr_freq = sequence_info.fifths_pitch_histogram[curr_pitch];
				// If frequency of bin is greater than the highest found so far and at least 10% greater than that
				// of the naive finalis, switch finalis estimate.
				if ((curr_freq > max_freq)
					&& (curr_freq >= 1.1 * finalis_naive_freq)){
					max_pitch = curr_pitch;
					max_freq = sequence_info.fifths_pitch_histogram[curr_pitch];
				}
			}

			// Calculate the value
			result[0] = (double) (7 * max_pitch) % 12;
		}
		else result[0] = -1.0;

		return result;
	}
}