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
 * pitch class if it is at least 10% more common and precisely a fifth lower than the lowest pitch class of the
 * final sonority.
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
		double value;
		if (sequence_info != null)
		{
            int lowest_last_pitch_class = (7 * (int) other_feature_values[0][0]) % 12;
			// Check all 7peaks
            double max_freq = sequence_info.fifths_pitch_histogram[lowest_last_pitch_class];
        	int finalis = 0;
			for (int bin = 0; bin < sequence_info.fifths_pitch_histogram.length; bin++)
			{
                double bin_freq = sequence_info.fifths_pitch_histogram[bin];
				if (bin_freq >= max_freq)
				{
                    max_freq = bin_freq;
					int right = (bin + 1) % sequence_info.fifths_pitch_histogram.length;

					// Heuristic: choose most common pitch only if  it is a descending fifth from final pitch,
					//						otherwise always choose final pitch
					if (right == lowest_last_pitch_class &&
						bin_freq/sequence_info.fifths_pitch_histogram[right] >= 1.1){
							finalis = bin;
						}
					else finalis = lowest_last_pitch_class;
				}
			}

			// Calculate the value
			value = (double) (7 * finalis) % 12;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}