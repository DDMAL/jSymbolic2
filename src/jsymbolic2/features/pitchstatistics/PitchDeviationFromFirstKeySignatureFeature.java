package jsymbolic2.features.pitchstatistics;

import javax.sound.midi.*;
import java.util.LinkedList;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * The fraction of notes in the piece that do not belong to one of the 7 pitch classes specified by the first 
 * key signature in the piece. 0 if there is no key signature specified in the piece.
 *
 * @author radamian
 */
public class PitchDeviationFromFirstKeySignatureFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public PitchDeviationFromFirstKeySignatureFeature()
	{
		String name = "Pitch Deviation from First Key Signature";
		String code = "P-37";
		String description = "The fraction of notes in the piece that do not belong to one of the 7 pitch classes specified by the first key signature in the piece. 0 if there is no key signature specified in the piece.";
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
		double value = 0.0;
		if (sequence_info != null)
		{
			int number_of_sharps_or_flats;
			if (!((LinkedList) sequence_info.overall_metadata[4]).isEmpty()) // Check if key signature specified
			{
				// Get the number of sharps or flats
				int[] key_signature = (int[]) ((LinkedList) sequence_info.overall_metadata[4]).get(0);
				number_of_sharps_or_flats = key_signature[0];
				
				// Add normalized magnitude of pitch class occurrence to value if that pitch class does not 
				// belong to the first key signature.
				if (number_of_sharps_or_flats != -5 &&
					number_of_sharps_or_flats != -4 &&
					number_of_sharps_or_flats != -3 &&
					number_of_sharps_or_flats != -2 &&
					number_of_sharps_or_flats != -1 &&
					number_of_sharps_or_flats != 0 &&
					number_of_sharps_or_flats != 1 &&
					number_of_sharps_or_flats != 7) 
				{ value += sequence_info.pitch_class_histogram[0]; }

				if (number_of_sharps_or_flats != -7 &&
					number_of_sharps_or_flats != -6 &&
					number_of_sharps_or_flats != -5 &&
					number_of_sharps_or_flats != -4 &&
					number_of_sharps_or_flats != 2 &&
					number_of_sharps_or_flats != 3 &&
					number_of_sharps_or_flats != 4 &&
					number_of_sharps_or_flats != 5 &&
					number_of_sharps_or_flats != 6 &&
					number_of_sharps_or_flats != 7) 
				{ value += sequence_info.pitch_class_histogram[1]; }

				if (number_of_sharps_or_flats != -3 &&
					number_of_sharps_or_flats != -2 &&
					number_of_sharps_or_flats != -1 &&
					number_of_sharps_or_flats != 0 &&
					number_of_sharps_or_flats != 1 &&
					number_of_sharps_or_flats != 2 &&
					number_of_sharps_or_flats != 3) 
				{ value += sequence_info.pitch_class_histogram[2]; }

				if (number_of_sharps_or_flats != -7 &&
					number_of_sharps_or_flats != -6 &&
					number_of_sharps_or_flats != -5 &&
					number_of_sharps_or_flats != -4 &&
					number_of_sharps_or_flats != -3 &&
					number_of_sharps_or_flats != -2 &&
					number_of_sharps_or_flats != 4 &&
					number_of_sharps_or_flats != 5 &&
					number_of_sharps_or_flats != 6 &&
					number_of_sharps_or_flats != 7) 
				{ value += sequence_info.pitch_class_histogram[3]; }

				if (number_of_sharps_or_flats != -7 &&
					number_of_sharps_or_flats != -1 &&
					number_of_sharps_or_flats != 0 &&
					number_of_sharps_or_flats != 1 &&
					number_of_sharps_or_flats != 2 &&
					number_of_sharps_or_flats != 3 &&
					number_of_sharps_or_flats != 4 &&
					number_of_sharps_or_flats != 5)
				{ value += sequence_info.pitch_class_histogram[4]; }

				if (number_of_sharps_or_flats != -6 &&
					number_of_sharps_or_flats != -5 &&
					number_of_sharps_or_flats != -4 &&
					number_of_sharps_or_flats != -3 &&
					number_of_sharps_or_flats != -2 &&
					number_of_sharps_or_flats != -1 &&
					number_of_sharps_or_flats != 0 &&
					number_of_sharps_or_flats != 6 &&
					number_of_sharps_or_flats != 7)
				{ value += sequence_info.pitch_class_histogram[5]; }

				if (number_of_sharps_or_flats != -7 &&
					number_of_sharps_or_flats != -6 &&
					number_of_sharps_or_flats != -5 &&
					number_of_sharps_or_flats != 1 &&
					number_of_sharps_or_flats != 2 &&
					number_of_sharps_or_flats != 3 &&
					number_of_sharps_or_flats != 4 &&
					number_of_sharps_or_flats != 5 &&
					number_of_sharps_or_flats != 6 &&
					number_of_sharps_or_flats != 7)
				{ value += sequence_info.pitch_class_histogram[6]; }
				
				if (number_of_sharps_or_flats != -4 &&
					number_of_sharps_or_flats != -3 &&
					number_of_sharps_or_flats != -2 &&
					number_of_sharps_or_flats != -1 &&
					number_of_sharps_or_flats != 0 &&
					number_of_sharps_or_flats != 1 &&
					number_of_sharps_or_flats != 2)
				{ value += sequence_info.pitch_class_histogram[7]; }
				
				if (number_of_sharps_or_flats != -7 &&
					number_of_sharps_or_flats != -6 &&
					number_of_sharps_or_flats != -5 &&
					number_of_sharps_or_flats != -4 &&
					number_of_sharps_or_flats != -3 &&
					number_of_sharps_or_flats != 3 &&
					number_of_sharps_or_flats != 4 &&
					number_of_sharps_or_flats != 5 &&
					number_of_sharps_or_flats != 6 &&
					number_of_sharps_or_flats != 7)
				{ value += sequence_info.pitch_class_histogram[8]; }
				
				if (number_of_sharps_or_flats != -2 &&
					number_of_sharps_or_flats != -1 &&
					number_of_sharps_or_flats != 0 &&
					number_of_sharps_or_flats != 1 &&
					number_of_sharps_or_flats != 2 &&
					number_of_sharps_or_flats != 3 &&
					number_of_sharps_or_flats != 4)
				{ value += sequence_info.pitch_class_histogram[9]; }
				
				if (number_of_sharps_or_flats != -7 &&
					number_of_sharps_or_flats != -6 &&
					number_of_sharps_or_flats != -5 &&
					number_of_sharps_or_flats != -4 &&
					number_of_sharps_or_flats != -3 &&
					number_of_sharps_or_flats != -2 &&
					number_of_sharps_or_flats != -1 &&
					number_of_sharps_or_flats != 5 &&
					number_of_sharps_or_flats != 6 &&
					number_of_sharps_or_flats != 7)
				{ value += sequence_info.pitch_class_histogram[10]; }
				
				if (number_of_sharps_or_flats != -7 &&
					number_of_sharps_or_flats != -6 &&
					number_of_sharps_or_flats != 0 &&
					number_of_sharps_or_flats != 1 &&
					number_of_sharps_or_flats != 2 &&
					number_of_sharps_or_flats != 3 &&
					number_of_sharps_or_flats != 4 &&
					number_of_sharps_or_flats != 5 &&
					number_of_sharps_or_flats != 6)
				{ value += sequence_info.pitch_class_histogram[11]; }
			}		
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}