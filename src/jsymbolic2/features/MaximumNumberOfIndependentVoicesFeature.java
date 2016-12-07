package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the maximum number of different channels in which notes are sounded
 * simultaneously.
 *
 * @author Cory McKay
 */
public class MaximumNumberOfIndependentVoicesFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MaximumNumberOfIndependentVoicesFeature()
	{
		code = "T-1";
		String name = "Maximum Number of Independent Voices";
		String description = "Maximum number of different channels in which notes are sounded simultaneously.";
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
			int max_so_far = 0;
			for (int tick = 0; tick < sequence_info.note_sounding_on_a_channel_tick_map.length; tick++)
			{
				int count = 0;
				for (int chan = 0; chan < sequence_info.note_sounding_on_a_channel_tick_map[tick].length; chan++)
				{
					if (sequence_info.note_sounding_on_a_channel_tick_map[tick][chan])
						count++;
				}

				if (count > max_so_far)
					max_so_far = count;
			}
			value = (double) max_so_far;
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}