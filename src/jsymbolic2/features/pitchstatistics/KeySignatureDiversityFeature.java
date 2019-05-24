package jsymbolic2.features.pitchstatistics;

import javax.sound.midi.*;
import java.util.LinkedList;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;
import mckay.utilities.staticlibraries.StringMethods;

/**
 * A feature calculator that finds the number of different (unique) key signatures found in the piece. Set to
 * 1 if no key signature is specified.
 *
 * @author Rían Adamian
 */
public class KeySignatureDiversityFeature
			    extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/


	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public KeySignatureDiversityFeature()
	{
		String name = "Key Signature Diversity";
		String code = "P-36";
		String description = "The number of different (unique) key signatures found in the piece. Set to 1 if no key signature is specified.";
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
	 * @param sequence              The MIDI data to extract the feature from.
	 * @param sequence_info     	Additional data already extracted from the the MIDI sequence.
	 * @param other_feature_values	The values of other features that may be needed to calculate this feature.
	 *								The order and offsets of these features must be the same as those returned
	 *								by this class' getDependencies and getDependencyOffsets methods,
	 *								respectively. The first indice indicates the feature/window, and the
	 *								second indicates the value.
	 * @return                      The extracted feature value(s).
	 * @throws Exception        	Throws an informative exception if the feature cannot be calculated.
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
			//Set default to 1
			value = 1.0;
			
			// If key signature specified
			if (!((LinkedList) sequence_info.overall_metadata[4]).isEmpty())
			{
				Object[] key_signature_objects = ((LinkedList) sequence_info.overall_metadata[4]).toArray();
				String[] key_signatures = new String[key_signature_objects.length];
			
				for (int i = 0; i < key_signatures.length; i++)
				{
					int[] key_signature_as_int_array = (int[]) key_signature_objects[i];
					
					// Get key signature(s) as string(s)
					if (key_signature_as_int_array[0] == -7) // Number of flats or sharps
					{
						if (key_signature_as_int_array[1] == 0) // Major key
							key_signatures[i] = "Cbmaj";
						else if (key_signature_as_int_array[1] == 1) // Minor key
							key_signatures[i] = "Abmin";
					}
					else if (key_signature_as_int_array[0] == -6)
					{
						if (key_signature_as_int_array[1] == 0)
							key_signatures[i] = "Gbmaj";
						else if (key_signature_as_int_array[1] == 1)
							key_signatures[i] = "Ebmin";
					}
					else if (key_signature_as_int_array[0] == -5)
					{
						if (key_signature_as_int_array[1] == 0)
							key_signatures[i] = "Dbmaj";
						else if (key_signature_as_int_array[1] == 1)
							key_signatures[i] = "Bbmin";
					}
					else if (key_signature_as_int_array[0] == -4)
					{
						if (key_signature_as_int_array[1] == 0)
							key_signatures[i] = "Abmaj";
						else if (key_signature_as_int_array[1] == 1)
							key_signatures[i] = "Fmin";
					}
					else if (key_signature_as_int_array[0] == -3)
					{
						if (key_signature_as_int_array[1] == 0)
							key_signatures[i] = "Ebmaj";
						else if (key_signature_as_int_array[1] == 1)
							key_signatures[i] = "Cmin";
					}
					else if (key_signature_as_int_array[0] == -2)
					{
						if (key_signature_as_int_array[1] == 0)
							key_signatures[i] = "Bbmaj";
						else if (key_signature_as_int_array[1] == 1)
							key_signatures[i] = "Gmin";
					}
					else if (key_signature_as_int_array[0] == -1)
					{
						if (key_signature_as_int_array[1] == 0)
							key_signatures[i] = "Fmaj";
						else if (key_signature_as_int_array[1] == 1)
							key_signatures[i] = "Dmin";
					}
					else if (key_signature_as_int_array[0] == 0)
					{
						if (key_signature_as_int_array[1] == 0)
							key_signatures[i] = "Cmaj";
						else if (key_signature_as_int_array[1] == 1)
							key_signatures[i] = "Amin";
					}
					else if (key_signature_as_int_array[0] == 1)
					{
						if (key_signature_as_int_array[1] == 0)
							key_signatures[i] = "Gmaj";
						else if (key_signature_as_int_array[1] == 1)
							key_signatures[i] = "Emin";
					}
					else if (key_signature_as_int_array[0] == 2)
					{
						if (key_signature_as_int_array[1] == 0)
							key_signatures[i] = "Dmaj";
						else if (key_signature_as_int_array[1] == 1)
							key_signatures[i] = "Bmin";
					}
					else if (key_signature_as_int_array[0] == 3)
					{
						if (key_signature_as_int_array[1] == 0)
							key_signatures[i] = "Amaj";
						else if (key_signature_as_int_array[1] == 1)
							key_signatures[i] = "F#min";
					}
					else if (key_signature_as_int_array[0] == 4)
					{
						if (key_signature_as_int_array[1] == 0)
							key_signatures[i] = "Emaj";
						else if (key_signature_as_int_array[1] == 1)
							key_signatures[i] = "C#min";
					}
					else if (key_signature_as_int_array[0] == 5)
					{
						if (key_signature_as_int_array[1] == 0)
							key_signatures[i] = "Bmaj";
						else if (key_signature_as_int_array[1] == 1)
							key_signatures[i] = "G#min";
					}
					else if (key_signature_as_int_array[0] == 6)
					{
						if (key_signature_as_int_array[1] == 0)
							key_signatures[i] = "F#maj";
						else if (key_signature_as_int_array[1] == 1)
							key_signatures[i] = "D#min";
					}
					else if (key_signature_as_int_array[0] == 7)
					{
						if (key_signature_as_int_array[1] == 0)
							key_signatures[i] = "C#maj";
						else if (key_signature_as_int_array[1] == 1)
							key_signatures[i] = "A#min";
					}
				}
				
			// Find the number of unique key signatures
			value = (double) StringMethods.getCountsOfUniqueStrings(key_signatures).length;
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}