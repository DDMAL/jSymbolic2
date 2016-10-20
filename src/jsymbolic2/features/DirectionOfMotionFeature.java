package jsymbolic2.features;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the fraction of melodic intervals that are rising rather than falling.
 *
 * @author Cory McKay
 */
public class DirectionOfMotionFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public DirectionOfMotionFeature()
	{
		code = "M-17";
		String name = "Direction of Motion";
		String description = "Fraction of melodic intervals that are rising rather than falling.";
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
			int ups = 0;
			int downs = 0;
			for (int chan = 0; chan < sequence_info.melody_list.length; chan++)
			{
				if (chan != (10 - 1)) // Note Channel 10 unpitched instruments
				{
					// Convert to array
					Object[] list_contents = sequence_info.melody_list[chan].toArray();
					int[] intervals = new int[list_contents.length];
					for (int i = 0; i < intervals.length; i++)
						intervals[i] = ((Integer) list_contents[i]).intValue();

					// Find amount of upper and downward motion
					for (int i = 0; i < intervals.length; i++)
					{
						if (intervals[i] > 0)
							ups++;
						else if (intervals[i] < 0)
							downs++;
					}
				}
			}

			// Calculate the feature value
			value = (double) ups / ((double) (ups + downs));
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}