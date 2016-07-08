package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;
import java.util.stream.DoubleStream;

/**
 * Created by dinamix on 7/6/16.
 */
public class VerticalIntervalsWrappedFeature extends MIDIFeatureExtractor {

    public VerticalIntervalsWrappedFeature()
    {
        String name = "Vertical Interval Succession Wrapped";
        String description = "A feature vector consisting of the bin magnitudes of the\n" +
                "vertical interval histogram. In particular, it wraps around every 12 semitones, thus\n" +
                "creating a quantization into the twelve standard pitch classes.";
        boolean is_sequential = true;
        int dimensions = 12; //for each possible MIDI pitch interval with wrapping
        definition = new FeatureDefinition( name,
                description,
                is_sequential,
                dimensions );
        dependencies = null;
        offsets = null;
    }

    @Override
    public double[] extractFeature(Sequence sequence,
                                   MIDIIntermediateRepresentations sequence_info,
                                   double[][] other_feature_values)
            throws Exception
    {
        // Get the vertical interval intermediate representation chart
        int[][] vertical_interval_chart = sequence_info.vertical_interval_chart;

        // An array for all possible interval values
        double[] all_intervals = new double[12];

        // Compute pitch interval frequency based on velocity for each tick
        for(int tick = 0; tick < vertical_interval_chart.length; tick++) {
            for(int pitch = 0; pitch < vertical_interval_chart[tick].length - 1; pitch++) {
                for(int other_pitch = pitch + 1; other_pitch < vertical_interval_chart[tick].length; other_pitch++) {
                    int interval = (other_pitch - pitch) % 12;
                    int pitch_velocity = vertical_interval_chart[tick][pitch];
                    int other_pitch_velocity = vertical_interval_chart[tick][other_pitch];
                    if(pitch_velocity != 0 && other_pitch_velocity != 0) {
                        int total_velocity = pitch_velocity + other_pitch_velocity;
                        all_intervals[interval] += total_velocity;
                    }
                }
            }
        }

        // Normalize all the interval velocity values to the total sum of all values
        double normalize_sum = DoubleStream.of(all_intervals).sum();
        double[] all_intervals_normalized = DoubleStream.of(all_intervals)
                .map((double d) -> d / normalize_sum)
                .toArray();
        return all_intervals_normalized;
    }
}
