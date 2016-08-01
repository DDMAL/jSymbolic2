package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;
import java.util.List;

/**
 * Created by dinamix on 7/22/16.
 */
public class RelativeRangeIsolationOfLoudestVoiceFeature extends MIDIFeatureExtractor {
    public RelativeRangeIsolationOfLoudestVoiceFeature() {
        String name = "Relative Range Isolation of Loudest Voice";
        String description = "Number of notes in the\n" +
                "channel with the highest average loudness that fall outside the range of any other\n" +
                "channel divided by the total number of notes in the channel with the highest\n" +
                "average loudness.";
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
        List<List<Integer>> channel_pitch = sequence_info.channel_pitches;

        // Get the highest average loud channel
        int highest_average_loud_channel = 0;
        int highest_average_loud = 0;
        for(int channel = 0; channel < channel_stats.length; channel++) {
            int average_loud = channel_stats[channel][2];
            if(average_loud > highest_average_loud) {
                highest_average_loud = average_loud;
                highest_average_loud_channel = channel;
            }
        }

        //Count all notes outside range in the highest avg loud channel
        //Compare these notes to the range of each channel
        int notes_outside_range = 0;
        List<Integer> highest_avg_notes = channel_pitch.get(highest_average_loud_channel);
        for(Integer current_pitch: highest_avg_notes) {
            for(int channel = 0; channel < channel_stats.length; channel++) {
                if(channel == highest_average_loud_channel) continue;
                int lowest_pitch = channel_stats[channel][4];
                int highest_pitch = channel_stats[channel][5];
                if (current_pitch >= 0 &&
                    (current_pitch > highest_pitch ||
                     current_pitch < lowest_pitch))
                {
                    notes_outside_range++;
                    break; //only need to count the note once
                }
            }
        }

        double total_avg_loud_notes = channel_stats[highest_average_loud_channel][0];
        double relative_range_isolation = notes_outside_range / total_avg_loud_notes;

        return new double[]{relative_range_isolation};
    }
}
