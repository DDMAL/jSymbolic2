package jsymbolic2.features;

import javax.sound.midi.Sequence;
import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

/**
 * A feature vector consisting of bin magnitudes of the wrapped vertical interval histogram described in the
 * jSymbolic manual. Each of the bins is associated with a different vertical pitch interval, and is labeled
 * with the number of semitones in that corresponding interval. More specifically, these are numbered from 0
 * (a unison) to 11 (a vertical interval of 11 semitones). The magnitude of each bin is found by going through
 * a recoding MIDI tick by MIDI tick and noting all vertical intervals that are sounding at each tick, as well
 * as the MIDI velocities of the pair of notes involved in each vertical interval. Intervals larger than 11
 * semitones are wrapped (e.g. an octave (12 semitones) is added to the bin for unisons (0 semitones)). The
 * end result is a histogram that indicates which vertical intervals are present, and how significant these
 * vertical intervals are relative to one another, with a weighting based on both MIDI velocity and the
 * aggregated durations with which each interval is held throughout the piece. Finally, the histogram is
 * normalized.
 *
 * @author Tristano Tenaglia and Cory McKay
 */
public class WrappedVerticalIntervalHistogramFeature
		extends MIDIFeatureExtractor 
{
	/* CONSTRUCTOR ******************************************************************************************/

	
	/**
	 * Basic constructor that sets the values of the fields inherited from this class' superclass.
	 */
	public WrappedVerticalIntervalHistogramFeature()
    {
		code = "C-2";
        String name = "Wrapped Vertical Interval Histogram";
        String description = "A feature vector consisting of bin magnitudes of the wrapped vertical interval histogram described in the jSymbolic manual. Each of the bins is associated with a different vertical pitch interval, and is labeled with the number of semitones in that corresponding interval. More specifically, these are numbered from 0 (a unison) to 11 (a vertical interval of 11 semitones). The magnitude of each bin is found by going through a recoding MIDI tick by MIDI tick and noting all vertical intervals that are sounding at each tick, as well as the MIDI velocities of the pair of notes involved in each vertical interval. Intervals larger than 11 semitones are wrapped (e.g. an octave (12 semitones) is added to the bin for unisons (0 semitones)). The end result is a histogram that indicates which vertical intervals are present, and how significant these vertical intervals are relative to one another, with a weighting based on both MIDI velocity and the aggregated durations with which each interval is held throughout the piece. Finally, the histogram is normalized.";
        boolean is_sequential = true;
 		int dimensions = 12;
		definition = new FeatureDefinition(name, description, is_sequential, dimensions);
		dependencies = new String[] {"Vertical Interval Histogram"};
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
		double[] wrapped_vertical_interval_histogram = null;
		
		if (sequence_info != null)
		{
			wrapped_vertical_interval_histogram = new double[12];
			double[] vertical_interval_histogram = other_feature_values[0];
			for (int i = 0; i < vertical_interval_histogram.length; i++)
				wrapped_vertical_interval_histogram[i%12] += vertical_interval_histogram[i];
		}
		
		return wrapped_vertical_interval_histogram;
    }
}