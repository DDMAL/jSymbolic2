package jsymbolic2.features;

import java.util.LinkedList;
import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import mckay.utilities.staticlibraries.StringMethods;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that is set to the number of different (unique) time signatures found in the piece.
 * Set to 1 if no time signature is specified.
 * 
 * @author Cory McKay
 */
public class MetricalDiversityFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MetricalDiversityFeature()
	{
		code = "R-8";
		String name = "Metrical Diversity";
		String description = "The number of different (unique) time signatures found in the piece. Set to 1 if no time signature is specified.";
		boolean is_sequential = true;
		int dimensions = 1;
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
		double value;
		if (sequence_info != null)
		{
			// Default to 1
			value = 1.0;

			// If time signature specified
			if (!((LinkedList) sequence_info.overall_metadata[1]).isEmpty())
			{
				// Convert data types
				Object[] numerator_objects = ((LinkedList) sequence_info.overall_metadata[1]).toArray();
				int[] numerators = new int[numerator_objects.length];
				for (int i = 0; i < numerators.length; i++)
					numerators[i] = (Integer) numerator_objects[i];
				Object[] denominator_objects = ((LinkedList) sequence_info.overall_metadata[2]).toArray();
				int[] denominators = new int[denominator_objects.length];
				for (int i = 0; i < denominators.length; i++)
					denominators[i] = (Integer) denominator_objects[i];

				// Join into united time signatures
				String[] time_signatures = new String[numerators.length];
				for (int i = 0; i < time_signatures.length; i++)
					time_signatures[i] = numerators[i] + "/" + denominators[i];
				
				// Find the number of unique time signatures
				value = (double) StringMethods.getCountsOfUniqueStrings(time_signatures).length;
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}