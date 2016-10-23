package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.ChordTypesEnum;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A feature extractor that extracts the average duration of a chord in seconds.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class ChordDurationFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public ChordDurationFeature() {
 		code = "C-22";
        String name = "Chord Duration";
        String description = "Average duration of a chord in seconds.";
        boolean is_sequential = true;
        int dimensions = 1;
        definition = new FeatureDefinition( name,
                description,
                is_sequential,
                dimensions );

        dependencies = new String[]{"Chord Type Histogram"};
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
        short[][] vertical_interval_chart = sequence_info.pitch_strength_by_tick_chart;
        int total_intervals = 128;
        int quantized_intervals = 12;

        // Keep data of previous chord for comparison
        int[] previous_chord = new int[total_intervals];
        ChordTypesEnum previous_chord_type = null;
        List<Double> chord_tick_duration = new ArrayList<>();
        boolean[] chord_tick_array = new boolean[vertical_interval_chart.length];
        for(int tick = 0; tick < vertical_interval_chart.length; tick++) {
            //Get the chord at this tick
            int[] quantized_chord = new int[quantized_intervals];
            int[] current_chord = new int[total_intervals];
            for(int pitch = 0; pitch < vertical_interval_chart[tick].length - 1; pitch++) {
                int quantized_pitch = pitch % quantized_intervals;
                int velocity = vertical_interval_chart[tick][pitch];
                quantized_chord[quantized_pitch] += velocity;
                if(velocity > 0) {
                    current_chord[pitch]++;
                }
            }
            ChordTypesEnum chord_type = ChordTypesEnum.getChordType(quantized_chord);
            if(chord_type != null &&
                    !Arrays.equals(current_chord, previous_chord))
            {
                // New chord
                chord_tick_duration.add(1.0);
                chord_tick_array[tick] = true;
            }
            else if(chord_type != null &&
                    tick > 0 &&
                    Arrays.equals(current_chord, previous_chord) &&
                    chord_type.equals(previous_chord_type))
            {
                // Same as old chord
                int current_chord_index = chord_tick_duration.size() - 1;
                Double current_chord_length = chord_tick_duration.get(current_chord_index);
                current_chord_length++;
                chord_tick_duration.remove(current_chord_index);
                chord_tick_duration.add(current_chord_length);
                chord_tick_array[tick] = true;
            }
            previous_chord = current_chord;
            previous_chord_type = ChordTypesEnum.getChordType(quantized_chord);
        }

        //Convert ticks to seconds and then get average duration length
        double[] seconds_per_tick = sequence_info.seconds_per_tick;
        for(int chord_tick = 0; chord_tick < chord_tick_duration.size(); chord_tick++) {
            if(chord_tick_array[chord_tick] == true) {
                double current_value = chord_tick_duration.get(chord_tick);
                double new_value = current_value * seconds_per_tick[chord_tick];
                chord_tick_duration.set(chord_tick, new_value);
            }
        }
        int number_of_chords = chord_tick_duration.size();
        double chord_length_sum = chord_tick_duration.stream().mapToDouble(Double::doubleValue).sum();
        double chord_length_average = chord_length_sum / number_of_chords;
        return new double[]{chord_length_average};
    }
}