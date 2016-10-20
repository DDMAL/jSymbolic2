package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;
import java.util.stream.IntStream;

/**
 * A feature extractor that extracts a feature vector consisting of the bin magnitudes of the
 * vertical interval histogram.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class AverageNumberOfSimultaneousPitchClassesFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public AverageNumberOfSimultaneousPitchClassesFeature()
    {
		code = "C-4";
        String name = "Average Number of Simultaneous Pitch Classes";
        String description = "Average number of\n" +
                "different pitch classes sounding simultaneously.";
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
    public double[] extractFeature(Sequence sequence,
                                   MIDIIntermediateRepresentations sequence_info,
                                   double[][] other_feature_values)
            throws Exception
    {
        // Get the vertical interval intermediate representation chart
        short[][] vertical_interval_chart = sequence_info.pitch_strength_by_tick_chart;

        // An array for all possible interval values
        int[] simultaneous_count = new int[vertical_interval_chart.length];

        // Compute pitch interval frequency based on velocity for each tick
        for(int tick = 0; tick < vertical_interval_chart.length; tick++) {
            for(int pitch = 0; pitch < vertical_interval_chart[tick].length - 1; pitch++) {
                int pitch_velocity = vertical_interval_chart[tick][pitch];
                // Once we find a pitch, then go through all other pitches to check pitch classes once
                if(pitch_velocity != 0) {
                    for (int other_pitch = pitch + 1; other_pitch < vertical_interval_chart[tick].length; other_pitch++) {
                        int other_pitch_velocity = vertical_interval_chart[tick][other_pitch];
                        if (simultaneous_count[tick] == 0 && pitch_velocity != 0) {
                            simultaneous_count[tick]++;
                        }
                        if (pitch_velocity != 0 && other_pitch_velocity != 0) {
                            simultaneous_count[tick]++;
                        }
                    }
                    break; // go to next tick
                }
            }
        }
        double simultaneous_sum = IntStream.of(simultaneous_count).sum();
        double simultaneous_avg = simultaneous_sum / vertical_interval_chart.length;
        return new double[]{simultaneous_avg};
    }
}
