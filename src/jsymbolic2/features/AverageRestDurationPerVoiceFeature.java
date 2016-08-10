package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;

/**
 * A feature extractor that extracts the average, in seconds, of the average
 * amounts of time in each channel in which no note is sounding (counting only
 * channels with at least one note), divided by the total duration of the recording.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class AverageRestDurationPerVoiceFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public AverageRestDurationPerVoiceFeature() {
        String name = "Average Rest Duration Per Voice";
        String description = "Average, in seconds, of the average\n" +
                "amounts of time in each channel in which no note is sounding (counting only\n" +
                "channels with at least one note), divided by the total duration of the recording.";
        boolean is_sequential = true;
        int dimensions = 1;
        definition = new FeatureDefinition( name,
                description,
                is_sequential,
                dimensions );

        dependencies = null;

        offsets = null;
    }

    /**
     * Extracts this feature from the given MIDI sequence given the other
     * feature values.
     *
     * <p>In the case of this feature, the other_feature_values parameters
     * are ignored.
     *
     * @param sequence			The MIDI sequence to extract the feature
     *                                 from.
     * @param sequence_info		Additional data about the MIDI sequence.
     * @param other_feature_values	The values of other features that are
     *					needed to calculate this value. The
     *					order and offsets of these features
     *					must be the same as those returned by
     *					this class's getDependencies and
     *					getDependencyOffsets methods
     *                                 respectively. The first indice indicates
     *                                 the feature/window and the second
     *                                 indicates the value.
     * @return				The extracted feature value(s).
     * @throws Exception		Throws an informative exception if the
     *					feature cannot be calculated.
     */
    @Override
    public double[] extractFeature( Sequence sequence,
                                    MIDIIntermediateRepresentations sequence_info,
                                    double[][] other_feature_values )
            throws Exception
    {
        int[][] channel_stats = sequence_info.channel_statistics;
        double total_time_rests = 0;
        for(int channel = 0; channel < channel_stats.length; channel++) {
            int number_note_ons = channel_stats[channel][0];
            if(number_note_ons == 0) continue;
            total_time_rests += sequence_info.recording_length_double - channel_stats[channel][1];
        }
        double average_rest_duration = total_time_rests / sequence_info.recording_length_double;
        return new double[]{average_rest_duration};
    }
}
