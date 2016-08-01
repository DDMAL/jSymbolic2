package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;

/**
 * Created by dinamix on 7/20/16.
 */
public class RelativeNoteDurationsOfLowestLineFeature extends MIDIFeatureExtractor {
    public RelativeNoteDurationsOfLowestLineFeature() {
        String name = "Relative Note Durations of Lowest Line";
        String description = "Average duration of notes (in\n" +
                "seconds) in the channel with the lowest average pitch divided by the average\n" +
                "duration of notes in all channels that contain at least one note.";
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
        int[][] channel_stats = sequence_info.channel_statistics;

        // Get the lowest average pitch
        int lowest_average_pitch = 0;
        int lowest_average_pitch_channel = 0;
        int total_note_duration = 0;
        int total_number_notes = 0;
        for(int channel = 0; channel < channel_stats.length; channel++) {
            // Get lowest average pitch that is > 0 since 0 means no pitches
            int average_pitch = channel_stats[channel][6];
            if(average_pitch < lowest_average_pitch && average_pitch > 0) {
                lowest_average_pitch = average_pitch;
                lowest_average_pitch_channel = channel;
            }
            // Get total note and numbers
            total_note_duration += channel_stats[channel][1];
            total_number_notes += channel_stats[channel][0];
        }

        // Get average note duration for lowest average pitch
        int total_lowest_duration = channel_stats[lowest_average_pitch_channel][1];
        int total_lowest_notes = channel_stats[lowest_average_pitch_channel][0];
        double average_lowest_duration = total_lowest_duration / total_lowest_notes;
        double average_total_duration = total_note_duration / total_number_notes;
        double melodic_interval_lowest_line = average_lowest_duration / average_total_duration;
        return new double[]{melodic_interval_lowest_line};
    }
}
