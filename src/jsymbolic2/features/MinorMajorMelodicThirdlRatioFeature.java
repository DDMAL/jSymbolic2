package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the combined fraction of all melodic intervals that are minor thirds,
 * divided by the combined fraction of all melodic intervals that are major thirds. Set to 0 if there are no
 * melodic minor thirds or melodic major thirds.
 *
 * @author Cory McKay
 */
public class MinorMajorMelodicThirdlRatioFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MinorMajorMelodicThirdlRatioFeature()
	{
		code = "M-20";
		String name = "Minor Major Melodic Third Ratio";
		String description = "Combined fraction of all melodic intervals that are minor thirds, divided by the combined fraction of all melodic intervals that are major thirds. Set to 0 if there are no melodic minor thirds or melodic major thirds.";
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
		double value = 0.0;
		if (sequence_info != null)
		{
			if ( sequence_info.melodic_interval_histogram[3] != 0 && 
			     sequence_info.melodic_interval_histogram[4] != 0 )
				value = sequence_info.melodic_interval_histogram[3] / sequence_info.melodic_interval_histogram[4];
			
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}