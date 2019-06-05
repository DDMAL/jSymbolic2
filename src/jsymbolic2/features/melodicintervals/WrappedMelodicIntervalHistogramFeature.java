package jsymbolic2.features.melodicintervals;

import javax.sound.midi.*;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A wrapped feature vector consisting of the bin magnitudes of the Melodic Interval Histogram described 
 * above. Entries for melodic intervals over an octave are amalgamated (e.g. 2nds and 9ths are combined). 
 * Each bin corresponds to a melodic interval, and the bin index indicates the number of semitones comprising 
 * the interval associated with the bin (there are 12 bins in all). For example, bin 0 corresponds to 
 * repeated pitches (and octave multiples), bin 1 to a melodic interval of one semitone (and octave 
 * multiples), bin 2 to a melodic interval of 2 semitones (and octave multiples), etc. The magnitude of each 
 * bin is proportional to the fraction of melodic intervals in the piece that are of the kind associated with 
 * the bin (this histogram is normalized). Rising and falling intervals are treated as identical. Melodies 
 * are assumed to be contained within individual MIDI tracks and channels, so melodic intervals are found 
 * separately for each track and channel before being combined in this histogram. It is also assumed that 
 * there is only one melody at a time per MIDI channel (if multiple notes occur simultaneously on the same 
 * MIDI tick on the same MIDI track and channel, then all notes but the first note on that tick are ignored). 
 * Other than this, all notes on the same track and the same channel are treated as if they are part of a 
 * single melody. It is also assumed that melodies do not cross MIDI tracks or channels (i.e. that they are 
 * each separately contained in their own track and channel). Only pitched notes are considered, so all notes 
 * on the unpitched MIDI Channel 10 are ignored.
 *
 * @author radamian
 */
public class WrappedMelodicIntervalHistogramFeature
		extends MIDIFeatureExtractor
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public WrappedMelodicIntervalHistogramFeature()
	{
		String name = "Wrapped Melodic Interval Histogram";
		String code = "M-2";
		String description = "A wrapped feature vector consisting of the bin magnitudes of the Melodic Interval Histogram described above. Entries for melodic intervals over an octave are amalgamated (e.g. 2nds and 9ths are combined). Each bin corresponds to a melodic interval, and the bin index indicates the number of semitones comprising the interval associated with the bin (there are 12 bins in all). For example, bin 0 corresponds to repeated pitches (and octave multiples), bin 1 to a melodic interval of one semitone (and octave multiples), bin 2 to a melodic interval of 2 semitones (and octave multiples), etc. The magnitude of each bin is proportional to the fraction of melodic intervals in the piece that are of the kind associated with the bin (this histogram is normalized). Rising and falling intervals are treated as identical. Melodies are assumed to be contained within individual MIDI tracks and channels, so melodic intervals are found separately for each track and channel before being combined in this histogram. It is also assumed that there is only one melody at a time per MIDI channel (if multiple notes occur simultaneously on the same MIDI tick on the same MIDI track and channel, then all notes but the first note on that tick are ignored). Other than this, all notes on the same track and the same channel are treated as if they are part of a single melody. It is also assumed that melodies do not cross MIDI tracks or channels (i.e. that they are each separately contained in their own track and channel). Only pitched notes are considered, so all notes on the unpitched MIDI Channel 10 are ignored.";
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
			for (int bin = 0; bin < sequence_info.melodic_interval_histogram.length; bin++)
				result[bin % 12] += sequence_info.melodic_interval_histogram[bin];
		}
		
		return result;
    }
}