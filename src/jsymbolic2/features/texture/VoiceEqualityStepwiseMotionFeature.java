package jsymbolic2.features.texture;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature calculator that finds the standard deviation across MIDI track/channel voices of the fraction of 
 * melodic motion that is stepwise in each voice that contains at least two pitched notes. Set to 0 if there 
 * are no voices containing melodic intervals.
 *
 * @author radamian
 */
public class VoiceEqualityStepwiseMotionFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public VoiceEqualityStepwiseMotionFeature()
	{
		String name = "Voice Equality - Stepwise Motion";
		String code = "T-10";
		String description = "Standard deviation across MIDI track/channel voices of the fraction of melodic motion that is stepwise in each voice that contains at least two pitched notes. Set to 0 if there are no voices containing melodic intervals.";
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
			// Find the number of pitched MIDI track/channnel pairings with at least one melodic interval
			int active_voices_count = 0;
			for (int n_track = 0; n_track < sequence_info.track_and_channel_statistics.length; n_track++)
				for (int chan = 0; chan < sequence_info.track_and_channel_statistics[n_track].length; chan++)
					if (!sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[chan].isEmpty())
						active_voices_count++;
			
			if (active_voices_count == 0)
				value = 0.0;
			else
			{
				// An array holding the fraction of melodic motion that is stepwise in each voice
				double[] stepwise_motion_fractions = new double[active_voices_count];
				for (int i = 0; i < stepwise_motion_fractions.length; i++)
					stepwise_motion_fractions[i] = 0.0;

				// Fill the array
				int count = 0;
				for (int n_track = 0; n_track < sequence_info.track_and_channel_statistics.length; n_track++)
					for (int chan = 0; chan < sequence_info.track_and_channel_statistics[n_track].length; chan++)
						if (!sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[chan].isEmpty())
						{
							// Iterate by melodic interval
							for (int i = 0; i < sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[chan].size(); i++)
								if (Math.abs(sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[chan].get(i)) == 1 ||
									Math.abs(sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[chan].get(i)) == 2)
									stepwise_motion_fractions[count]++;
							
							stepwise_motion_fractions[count] = stepwise_motion_fractions[count] / sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[chan].size();
							count++;
						}

				// Set value
				value = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(stepwise_motion_fractions);
			}
		}
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}