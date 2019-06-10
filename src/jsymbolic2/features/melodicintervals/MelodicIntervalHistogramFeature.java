package jsymbolic2.features.melodicintervals;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature vector consisting of the bin magnitudes of the melodic interval histogram described above. Each
 * bin corresponds to a melodic interval, and the bin index indicates the number of semitones comprising the
 * interval associated with the bin (there are 128 bins in all). For example, bin 0 corresponds to repeated
 * pitches, bin 1 to a melodic interval of one semitone, bin 2 to a melodic interval of 2 semitones, etc. The
 * magnitude of each bin is proportional to the fraction of melodic intervals in the piece that are of the
 * kind associated with the bin (this histogram is normalized). Rising and falling intervals are treated as
 * identical. Melodies are assumed to be contained within individual MIDI tracks and channels, so melodic
 * intervals are found separately for each track and channel before being combined in this histogram. If a
 * given note onset slice on a given MIDI track and channel contains multiple pitches, then only the highest
 * pitched note in the slice is counted for the purpose of melody calculations. Also, if the highest note is
 * sustained from one note onset slice to the next, and is still the highest note in the second slice, then
 * this is treated as if there is no change in melody, even if lower pitches in the same track and channel
 * change. It is also assumed that melodies do not cross MIDI tracks or channels (i.e. that they are each
 * separately contained in their own track and channel). Only pitched notes are considered, so all notes on
 * the unpitched MIDI Channel 10 are ignored.
 *
 * @author Cory McKay
 */
public class MelodicIntervalHistogramFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public MelodicIntervalHistogramFeature()
	{
		String name = "Melodic Interval Histogram";
		String code = "M-1";
		String description = "A feature vector consisting of the bin magnitudes of the melodic interval histogram described above. Each bin corresponds to a melodic interval, and the bin index indicates the number of semitones comprising the interval associated with the bin (there are 128 bins in all). For example, bin 0 corresponds to repeated pitches, bin 1 to a melodic interval of one semitone, bin 2 to a melodic interval of 2 semitones, etc. The magnitude of each bin is proportional to the fraction of melodic intervals in the piece that are of the kind associated with the bin (this histogram is normalized). Rising and falling intervals are treated as identical. Melodies are assumed to be contained within individual MIDI tracks and channels, so melodic intervals are found separately for each track and channel before being combined in this histogram. If a given note onset slice on a given MIDI track and channel contains multiple pitches, then only the highest pitched note in the slice is counted for the purpose of melody calculations. Also, if the highest note is sustained from one note onset slice to the next, and is still the highest note in the second slice, then this is treated as if there is no change in melody, even if lower pitches in the same track and channel change. It is also assumed that melodies do not cross MIDI tracks or channels (i.e. that they are each separately contained in their own track and channel). Only pitched notes are considered, so all notes on the unpitched MIDI Channel 10 are ignored.";
		boolean is_sequential = true;
		int dimensions = 128;
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
			result = new double[sequence_info.melodic_interval_histogram.length];
			for (int pitch = 0; pitch < result.length; pitch++)
				result[pitch] = sequence_info.melodic_interval_histogram[pitch];
		}
		return result;
	}
}