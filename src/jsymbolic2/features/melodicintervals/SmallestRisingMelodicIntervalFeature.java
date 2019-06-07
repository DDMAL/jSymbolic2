package jsymbolic2.features.melodicintervals;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * The smallest rising melodic interval in the piece, measured in semitones. Repeated notes are not counted 
 * for this feature, so a value of 0 will only be returned if there are no rising melodic intervals of a 
 * semitone or larger.
 *
 * @author radamian
 */
public class SmallestRisingMelodicIntervalFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public SmallestRisingMelodicIntervalFeature()
	{
		String name = "Smallest Rising Melodic Interval";
		String code = "M-46";
		String description = "The smallest rising melodic interval in the piece, measured in semitones. Repeated notes are not counted for this feature, so a value of 0 will only be returned if there are no rising melodic intervals of a semitone or larger.";
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
			// Initialize with default value
			int smallest_melodic_interval = 0;

			// Find smallest rising melodic interval that is not a repeated note
			for (int n_track = 0; n_track < sequence_info.melodic_intervals_by_track_and_channel.size(); n_track++)
				for (int chan = 0; chan < sequence_info.melodic_intervals_by_track_and_channel.get(n_track).length; chan++)
					for (int i = 0; i < sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[chan].size(); i++)
					{
						int interval = sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[chan].get(i);
						// Check that interval is rising and not a repeated note
						if (interval > 0) 
							// Check if interval is smaller in semitones than the current minimum, or if the 
							// current minimum is set to the default value
							if (interval < smallest_melodic_interval || smallest_melodic_interval == 0)
								smallest_melodic_interval = interval;
					}
						
			
			value = (double) smallest_melodic_interval;
		} 
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}