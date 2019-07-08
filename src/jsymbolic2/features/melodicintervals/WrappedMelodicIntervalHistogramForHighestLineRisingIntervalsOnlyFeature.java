package jsymbolic2.features.melodicintervals;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature vector consisting of the bin magnitudes of the Wrapped Melodic Interval Histogram described 
 * above, but only considering melodic intervals in the MIDI channel with the highest average pitch, and where 
 * falling intervals are ignored (repeated pitches are counted). Entries for melodic intervals over an octave 
 * are amalgamated (e.g. 2nds and 9ths are combined). Each bin corresponds to a melodic interval, and the bin 
 * index indicates the number of semitones comprising the interval associated with the bin (there are 12 bins 
 * in all). For example, bin 0 corresponds to repeated pitches (and octave multiples), bin 1 to a melodic 
 * interval of one semitone (and octave multiples), bin 2 to a melodic interval of 2 semitones (and octave 
 * multiples), etc. The magnitude of each bin is proportional to the fraction of rising melodic intervals in 
 * the highest voice that are of the kind associated with the bin (this histogram is normalized). If a given 
 * note onset slice contains multiple pitches, then only the highest pitched note in the slice is counted for 
 * the purpose of melody calculations. Also, if the highest note is sustained from one note onset slice to the 
 * next, and is still the highest note in the second slice, then this is treated as if there is no change in 
 * melody, even if lower pitches change. 
 *
 * @author radamian
 */
public class WrappedMelodicIntervalHistogramForHighestLineRisingIntervalsOnlyFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public WrappedMelodicIntervalHistogramForHighestLineRisingIntervalsOnlyFeature()
	{
		String name = "Wrapped Melodic Interval Histogram for Highest Line - Rising Intervals Only";
		String code = "M-91";
		String description = "A feature vector consisting of the bin magnitudes of the Wrapped Melodic Interval Histogram described above, but only considering melodic intervals in the MIDI channel with the highest average pitch, and where falling intervals are ignored (repeated pitches are counted). Entries for melodic intervals over an octave are amalgamated (e.g. 2nds and 9ths are combined). Each bin corresponds to a melodic interval, and the bin index indicates the number of semitones comprising the interval associated with the bin (there are 12 bins in all). For example, bin 0 corresponds to repeated pitches (and octave multiples), bin 1 to a melodic interval of one semitone (and octave multiples), bin 2 to a melodic interval of 2 semitones (and octave multiples), etc. The magnitude of each bin is proportional to the fraction of rising melodic intervals in the highest voice that are of the kind associated with the bin (this histogram is normalized). If a given note onset slice contains multiple pitches, then only the highest pitched note in the slice is counted for the purpose of melody calculations. Also, if the highest note is sustained from one note onset slice to the next, and is still the highest note in the second slice, then this is treated as if there is no change in melody, even if lower pitches change.";
		boolean is_sequential = true;
		int dimensions = 12;
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
		double[] result = null;
		
		if (sequence_info != null)
		{
			// Initialize histogram
			result = new double[12];
			for (int i = 0; i < result.length; i++)
				result[i] = 0.0;
			
			// Fill histogram
			int track_with_highest_average_pitch = sequence_info.track_and_channel_with_highest_average_pitch[0];
			int channel_with_highest_average_pitch = sequence_info.track_and_channel_with_highest_average_pitch[1];
			for (int i = 0; i < sequence_info.melodic_intervals_by_track_and_channel.get(track_with_highest_average_pitch)[channel_with_highest_average_pitch].size(); i++)
				if (sequence_info.melodic_intervals_by_track_and_channel.get(track_with_highest_average_pitch)[channel_with_highest_average_pitch].get(i) >= 0)
					result[sequence_info.melodic_intervals_by_track_and_channel.get(track_with_highest_average_pitch)[channel_with_highest_average_pitch].get(i) % 12]++;
			
			// Normalize histogram
			result = mckay.utilities.staticlibraries.MathAndStatsMethods.normalize(result);
		}
		
		return result;
    }
}