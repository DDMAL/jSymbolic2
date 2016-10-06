package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.FeatureConversion;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;

/**
 * A feature extractor that extracts the maximum amount of time in seconds
 * in which no notes are sounding on any channel.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class MaximumCompleteRestDurationFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public MaximumCompleteRestDurationFeature() {
        String name = "Maximum Complete Rest Duration";
        String description = "Maximum amount of time in seconds\n" +
                "in which no notes are sounding on any channel.";
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
        short[][] vertical_chart = sequence_info.vertical_interval_chart;

        // Find the longest streak of ticks with no valid pitches on them
        double rest_streak = 0;
        int start_tick = 0;
        int end_tick = 0;
        double max_rest = 0;
        int max_start_tick = 0;
        int max_end_tick = 0;
        for(int tick = 0; tick < vertical_chart.length; tick++) {
            short[] tick_array = vertical_chart[tick];
            // Check if nothing occurs at this tick
            if(FeatureConversion.allArrayEqual(tick_array, 0)) {
                rest_streak++;
                if(rest_streak == 1) {
                    start_tick = tick;
                }
            } else {
                rest_streak = 0;
                if(tick > 0) {
                    end_tick = tick;
                }
            }

            // Compare current max to new streak values
            if(rest_streak > max_rest) {
                max_rest = rest_streak;
            }
            if(Math.abs(end_tick - start_tick) > Math.abs(max_end_tick - max_start_tick)) {
                max_end_tick = end_tick;
                max_start_tick = start_tick;
            }
        }

        // Get the actual second values at each tick where notes are sounding
        double total_rest_seconds = 0;
        double[] seconds_per_tick = sequence_info.seconds_per_tick;
        for(int tick = max_start_tick; tick < max_end_tick; tick++) {
            total_rest_seconds += seconds_per_tick[tick];
        }
        return new double[]{total_rest_seconds};
    }
}
