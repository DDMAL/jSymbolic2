package jsymbolic2.features.texture;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the standard deviation of the separations in semi-tones between the average
 * pitches of consecutive MIDI track/channel voices (after sorting based on average pitch) that contain at 
 * least one note. Set to 0 if there are only 0 or 1 voices containing pitched notes.
 *
 * @author Cory McKay and radmian
 */
public class VariabilityOfVoiceSeparationFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VariabilityOfVoiceSeparationFeature()
	{
		String name = "Variability of Voice Separation";
		String code = "T-27";
		String description = "Standard deviation of the separations in semi-tones between the average pitches of consecutive MIDI track/channel voices (after sorting based on average pitch) that contain at least one note. Set to 0 if there are only 0 or 1 voices containing pitched notes.";
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
			// Set value to 0 if there are fewer than two pitched MIDI track/channel voices
			if (sequence_info.track_and_channel_pairs_by_average_pitch.size() < 2)
				value = 0.0;
			else
			{
				double[] separations = new double[sequence_info.track_and_channel_pairs_by_average_pitch.size() - 1];
				
				// Store the differences in semitones between the average pitches of consecutive MIDI track
				// and channel pairings
				double average_pitch_in_previous_voice = 0; 
				for (int i = 0; i < sequence_info.track_and_channel_pairs_by_average_pitch.size(); i++)
				{
					int track = sequence_info.track_and_channel_pairs_by_average_pitch.get(i)[0];
					int channel = sequence_info.track_and_channel_pairs_by_average_pitch.get(i)[1];
					double avergage_pitch_in_this_voice = sequence_info.average_pitch_by_track_and_channel[track][channel];
					
					if (i > 0)
						separations[i - 1] = avergage_pitch_in_this_voice - average_pitch_in_previous_voice;
					
					average_pitch_in_previous_voice = avergage_pitch_in_this_voice;
				}
				
				// Set value
				value = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(separations);
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}