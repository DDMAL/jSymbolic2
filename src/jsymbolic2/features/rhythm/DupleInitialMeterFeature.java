package jsymbolic2.features.rhythm;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that is set to 1 if the initial meter is a standard simple or compound duple meter
 * (i.e. if the numerator of the time signature is 2 or 6) and to 0 otherwise.
 *
 * @author Cory McKay
 */
public class DupleInitialMeterFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public DupleInitialMeterFeature()
	{
		String name = "Duple Initial Meter";
		String code = "R-5";
		String description = "Set to 1 if the initial meter is a standard simple or compound duple meter (i.e. if the numerator of the time signature is 2 or 6) and to 0 otherwise.";
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, code, description, is_sequential, dimensions, jsymbolic2.Main.SOFTWARE_NAME_AND_VERSION);
		dependencies = new String[] { "Initial Time Signature" };
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
			// Default to simple meter
			value = 0.0;
			
			// Get the numerator of the time signature
			double initial_time_signature_numerator = other_feature_values[0][0];
			
			// Set to compound meter if appropriate
			if ( initial_time_signature_numerator == 2.0 || initial_time_signature_numerator == 6.0 )
				value = 1.0;
		}

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}