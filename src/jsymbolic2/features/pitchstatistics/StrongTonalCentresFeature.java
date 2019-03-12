package jsymbolic2.features.pitchstatistics;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the Number of isolated peaks in the fifths pitch histogram that each
 * individually account for at least 9% of all notes in the piece.
 *
 * @author Cory McKay
 */
public class StrongTonalCentresFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public StrongTonalCentresFeature()
	{
		String name = "Strong Tonal Centres";
		String code = "P-13";
		String description = "Number of isolated peaks in the fifths pitch histogram that each individually account for at least 9% of all notes in the piece.";
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
		double value;
		if (sequence_info != null)
		{
			// Check all peaks
			int peaks = 0;
			for (int bin = 0; bin < sequence_info.fifths_pitch_histogram.length; bin++)
			{
				if (sequence_info.fifths_pitch_histogram[bin] >= 0.09)
				{
					int left = bin - 1;;
					int right = bin + 1;

					// Account for wrap around
					if (right == sequence_info.fifths_pitch_histogram.length)
						right = 0;
					if (left == -1)
						left = sequence_info.fifths_pitch_histogram.length - 1;

					// Check if is a peak
					if ( sequence_info.fifths_pitch_histogram[bin] > sequence_info.fifths_pitch_histogram[left] &&
					     sequence_info.fifths_pitch_histogram[bin] > sequence_info.fifths_pitch_histogram[right] )
					{
						peaks++;
					}
				}
			}

			// Calculate the value
			value = (double) peaks;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}