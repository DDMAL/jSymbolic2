package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.*;
import java.util.stream.DoubleStream;

/**
 * A feature extractor that extracts a feature vector consisting of the bin magnitudes of the
 * vertical interval histogram.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class VerticalIntervalHistogramFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public VerticalIntervalHistogramFeature()
    {
        String name = "Vertical Interval Histogram";
        String description = "A feature vector consisting of the bin magnitudes of the\n" +
                "vertical interval histogram";
        boolean is_sequential = true;
        int dimensions = 128; //for each possible MIDI pitch
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
    public double[] extractFeature(Sequence sequence,
                                   MIDIIntermediateRepresentations sequence_info,
                                   double[][] other_feature_values)
            throws Exception
    {
        // Get the vertical interval intermediate representation chart
        int[][] vertical_interval_chart = sequence_info.vertical_interval_chart;

        // An array for all possible interval values
        double[] all_intervals = new double[128];
        all_intervals[0] = sequence_info.total_unison_velocity;

        /* Compute unisons based on pitches that happen at the same tick on different tracks
        for(int track = 0; track < tracks_ticks_pitch.length; track++) {
            for(int tick = 0; tick < tracks_ticks_pitch[track].length; tick++) {
                for(int pitch = 0; pitch < tracks_ticks_pitch[track][tick].length; pitch++) {
                    for(int other_track = track + 1; other_track < tracks_ticks_pitch.length; other_track++) {
                        int current_velocity = tracks_ticks_pitch[track][tick][pitch];
                        try {
                            //May get a null pointer exception if that tick and pitch dne on the other track
                            int other_velocity = tracks_ticks_pitch[other_track][tick][pitch];
                            if(other_velocity > 0 && current_velocity > 0) {
                                all_intervals[0] += current_velocity + other_velocity;
                            }
                        } catch (Exception ex) {
                            continue;
                        }
                    }
                }
            }
        }*/

        // Compute pitch interval frequency based on velocity for each tick
        for(int tick = 0; tick < vertical_interval_chart.length; tick++) {
            for(int pitch = 0; pitch < vertical_interval_chart[tick].length - 1; pitch++) {
                for(int other_pitch = pitch + 1; other_pitch < vertical_interval_chart[tick].length; other_pitch++) {
                    int interval = other_pitch - pitch;
                    int pitch_velocity = vertical_interval_chart[tick][pitch];
                    int other_pitch_velocity = vertical_interval_chart[tick][other_pitch];
                    if(pitch_velocity != 0 && other_pitch_velocity != 0) {
                        int total_velocity = pitch_velocity + other_pitch_velocity;
                        all_intervals[interval] += total_velocity;
                    }
                }
            }
        }

        double normalize_sum = DoubleStream.of(all_intervals).sum();
        double[] all_intervals_normalized = DoubleStream.of(all_intervals)
                                                        .map((double d) -> d / normalize_sum)
                                                        .toArray();
        return all_intervals_normalized;
    }
}
