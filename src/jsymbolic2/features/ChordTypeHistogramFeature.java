package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.ChordTypesEnum;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;
import java.util.stream.DoubleStream;

/**
 * Created by dinamix on 7/6/16.
 */
public class ChordTypeHistogramFeature extends MIDIFeatureExtractor {

    private final int number_of_chord_types = ChordTypesEnum.values().length;

    public ChordTypeHistogramFeature()
	{
        code = "C-3";
		String name = "Chord Type Histogram";
        String description = "A feature vector consisting of the bin magnitudes of the\n" +
                "chords of each type in each bin. These bin-chord numbers are specified" +
                " in this classes Javadoc.";
        boolean is_sequential = true;
        int dimensions = number_of_chord_types; //for each possible MIDI pitch
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
        short[][] vertical_interval_chart = sequence_info.pitch_strength_by_tick_chart;
        double[] chord_types = new double[number_of_chord_types];
        int total_intervals = 12;
        for(int tick = 0; tick < vertical_interval_chart.length; tick++) {
            //Get the chord at this tick
            int[] tick_chord = new int[total_intervals];
            for(int pitch = 0; pitch < vertical_interval_chart[tick].length - 1; pitch++) {
                int quantized_pitch = pitch % total_intervals;
                tick_chord[quantized_pitch] += vertical_interval_chart[tick][pitch];
            }
            ChordTypesEnum chord_type = ChordTypesEnum.getChordType(tick_chord);
            if(chord_type != null) {
                double added_velocity = computeAverageVelocity(tick_chord);
                chord_types[chord_type.getChord_number()] += added_velocity;
            }
        }

        //Normalize the chord type array
        double normalize_sum = DoubleStream.of(chord_types).sum();
        double[] all_chords_normalized = DoubleStream.of(chord_types)
                .map((double d) -> d / normalize_sum)
                .toArray();
        return all_chords_normalized;
    }

    private static double computeAverageVelocity(int[] tick_chord) {
        int note_count = 0;
        double total_velocity = 0;
        for(int note = 0; note < tick_chord.length; note++) {
            int current_velocity = tick_chord[note];
            if(current_velocity > 0) {
                note_count++;
                total_velocity += current_velocity;
            }
        }
        if(note_count == 0) {
            return 0;
        } else {
            return total_velocity / note_count;
        }
    }
}
