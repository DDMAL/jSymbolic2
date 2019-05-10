package jsymbolic2.features.pitchstatistics;

import javax.sound.midi.*;
import java.util.LinkedList;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;
import mckay.utilities.staticlibraries.StringMethods;

/**
 * A feature calculator that outputs the number of sharps in the first key signature.
 *
 * @author Rían Adamian
 */
public class NumberOfSharpsInTheFirstKeySignatureFeature
				    extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public NumberOfSharpsInTheFirstKeySignatureFeature()
	{
		String name = "Number of Sharps in the First Key Signature";
		String code = "P-38";
		String description = "The number of sharps in the first key signature appearing in the piece. Set to 0 if no key signatures are specified or if the first key signature does not have any sharps.";
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
	 * @param sequence		The MIDI data to extract the feature from.
	 * @param sequence_info		Additional data already extracted from the the MIDI sequence.
	 * @param other_feature_values	The values of other features that may be needed to calculate this feature. 
	 *								The order and offsets of these features must be the same as those returned
	 *								by this class' getDependencies and getDependencyOffsets methods, 
	 *								respectively. The first indice indicates the feature/window, and the 
	 *								second indicates the value.
	 * @return			The extracted feature value(s).
	 * @throws Exception		Throws an informative exception if the feature cannot be calculated.
	 */
	@Override
	public double[] extractFeature( Sequence sequence, 
								MIDIIntermediateRepresentations sequence_info, 
								double[][] other_feature_values) 
	throws Exception
	{
		double value;
		if (sequence_info != null)
		{
			//Set default to 0
			value = 0.0;
			
			// If key signature specified
			if (!((LinkedList) sequence_info.overall_metadata[4]).isEmpty())
			{
				// Get information on first key signature
				int[] first_key_signature = (int[]) ((LinkedList) sequence_info.overall_metadata[4]).get(0);
				int flats_or_sharps = first_key_signature[0];

				// Positive indicates it is a number of sharps
				if (flats_or_sharps > 0)
					value = (double) flats_or_sharps;
			}	
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
    
}
