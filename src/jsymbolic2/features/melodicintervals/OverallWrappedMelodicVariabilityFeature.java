package jsymbolic2.features.melodicintervals;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * Standard deviation of the wrapped melodic intervals in the piece. Melodies are calculated using the same 
 * conventions described for the Melodic Interval Histogram. Provides a measure of how close the wrapped 
 * melodic intervals as a whole are to the mean wrapped melodic interval.
 *
 * @author radamian
 */
public class OverallWrappedMelodicVariabilityFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public OverallWrappedMelodicVariabilityFeature()
	{
		String name = "Overall Wrapped Melodic Variability";
		String code = "M-38";
		String description = "Standard deviation of the wrapped melodic intervals in the piece. Melodies are calculated using the same conventions described for the Melodic Interval Histogram. Provides a measure of how close the wrapped melodic intervals as a whole are to the mean wrapped melodic interval.";
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
			// Find the number of melodic intervals in the piece
			int number_of_intervals = 0;
			for (int n_track = 0; n_track < sequence_info.melodic_intervals_by_track_and_channel.size(); n_track++)
				for (int chan = 0; chan < sequence_info.melodic_intervals_by_track_and_channel.get(n_track).length; chan++)
					for (int i = 0; i < sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[chan].size(); i++)
						number_of_intervals++;
			
			// Fill array containing each melodic interval in the piece
			int[] all_melodic_intervals = new int[number_of_intervals];
			int index = 0;
			for (int n_track = 0; n_track < sequence_info.melodic_intervals_by_track_and_channel.size(); n_track++)
				for (int chan = 0; chan < sequence_info.melodic_intervals_by_track_and_channel.get(n_track).length; chan++)
					for (int i = 0; i < sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[chan].size(); i++)
					{
						all_melodic_intervals[index] = Math.abs(sequence_info.melodic_intervals_by_track_and_channel.get(n_track)[chan].get(i)) % 12;
						index++;
					}
			
			// Calculate the feature value
			value = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(all_melodic_intervals);
		} 
		else value = -1.0;

		double[] result = new double[1];
		result[0] = value;
		return result;
	}
}