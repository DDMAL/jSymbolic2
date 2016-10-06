package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * Created by dinamix on 7/22/16.
 */
public class VariabilityOfSimultaneityFeature extends MIDIFeatureExtractor {
    public VariabilityOfSimultaneityFeature() {
        String name = "Variability of Simultaneity";
        String description = "Standard deviation of the number of notes\n" +
                "sounding simultaneously.";
        boolean is_sequential = true;
        int dimensions = 1;
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
        short[][] ticks_pitch = sequence_info.vertical_interval_chart;

        // An array for number of notes at each tick
        int total_length = (int) sequence.getTickLength();
        double[] notes_on_tick = new double[total_length];
        int[] simultaneous_ticks = new int[total_length];

        // Compute unisons based on pitches that happen at the same tick on different tracks
        for(int tick = 0; tick < ticks_pitch.length - 1; tick++) {
            for(int pitch = 0; pitch < ticks_pitch[tick].length; pitch++) {
                int current_velocity = ticks_pitch[tick][pitch];
                // add a note count if we have it
                if(current_velocity > 0) {
                    notes_on_tick[tick] += 1.0;
                }
                // add a tick count if we have more than 1 note on a tick
                if(notes_on_tick[tick] > 1) {
                    simultaneous_ticks[tick] = 1;
                }
            }
        }

        // Sum up all simultaneous note ticks
        double total_simultaneous_notes = 0;
        for(double notes: notes_on_tick) {
            if (notes > 1) {
                total_simultaneous_notes += notes;
            }
        }
        double total_simultaneous_ticks = IntStream.of(simultaneous_ticks).sum();
        double simultaneous_avg = total_simultaneous_notes / total_simultaneous_ticks;
        double[] deviations = DoubleStream.of(notes_on_tick)
                .map((double d) -> Math.pow(d - simultaneous_avg, 2))
                .toArray();
        double standard_deviation = DoubleStream.of(deviations)
                .average()
                .getAsDouble();
        return new double[]{standard_deviation};
    }
}
