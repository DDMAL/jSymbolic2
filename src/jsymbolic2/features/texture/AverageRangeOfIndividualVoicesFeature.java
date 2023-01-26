package jsymbolic2.features.texture;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the mean range (in semitones) of all MIDI track/channel voices that contain 
 * at least two pitched notes. Set to 0 if there are no voices containing at least two pitched notes.
 *
 * @author radamian
 */
public class AverageRangeOfIndividualVoicesFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public AverageRangeOfIndividualVoicesFeature()
	{
		String name = "Average Range of Individual Voices";
		String code = "T-25";
		String description = "Mean range (in semitones) of all MIDI track/channel voices that contain at least two pitched notes. Set to 0 if there are no voices containing at least two pitched notes.";
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
			// Find the number of pitched MIDI track/channnel pairings with at least two note ons
			int active_voices_count = 0;
			for (int n_track = 0; n_track < sequence_info.track_and_channel_statistics.length; n_track++)
				for (int chan = 0; chan < sequence_info.track_and_channel_statistics[n_track].length; chan++)
					if (sequence_info.track_and_channel_statistics[n_track][chan][0] >= 2 && chan != 10 - 1)
						active_voices_count++;

			if (active_voices_count == 0)
				value = 0.0;
			else
			{
				// Store the ranges in each pitched MIDI track/channnel pairing
				double[] ranges = new double[active_voices_count];
				int count = 0;
				for (int n_track = 0; n_track < sequence_info.track_and_channel_statistics.length; n_track++)
					for (int chan = 0; chan < sequence_info.track_and_channel_statistics[n_track].length; chan++)
						if (sequence_info.track_and_channel_statistics[n_track][chan][0] >= 2 && chan != 10 - 1)
						{
							ranges[count] = sequence_info.track_and_channel_statistics[n_track][chan][5] - sequence_info.track_and_channel_statistics[n_track][chan][4];
							count++;
						}

				// Set value
				value = mckay.utilities.staticlibraries.MathAndStatsMethods.getAverage(ranges);
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}