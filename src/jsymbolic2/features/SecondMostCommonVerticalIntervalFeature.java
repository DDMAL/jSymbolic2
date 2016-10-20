package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;

/**
 * A feature extractor that extracts the interval in semitones corresponding
 * to the vertical interval histogram bin with the second highest magnitude.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class SecondMostCommonVerticalIntervalFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public SecondMostCommonVerticalIntervalFeature()
    {
		code = "C-7";
        String name = "Second Most Common Vertical Interval";
        String description = "The interval in semitones corresponding\n" +
                "to the vertical interval histogram bin with the second highest magnitude.";
        boolean is_sequential = true;
        int dimensions = 1;
        definition = new FeatureDefinition( name,
                description,
                is_sequential,
                dimensions );

        dependencies = new String[2];
        dependencies[0] = "Wrapped Vertical Interval Histogram";
        dependencies[1] = "Most Common Vertical Interval";
        offsets = null;
    }

    /**
     * Extracts this feature from the given MIDI sequence given the other
     * feature values.
     *
     * <p>In the case of this feature, the other_feature_values parameters
     * are the VerticalIntervals output in other_feature_values[0]
     * and the MostCommonVerticalInterval output in other_feature_values[1].
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
        double[] vertical_interval_succession = other_feature_values[0];
        double[] most_common_vertical_interval = other_feature_values[1];
        int most_common_interval = (int) Math.round(most_common_vertical_interval[0]);
        int second_common_interval = 0;
        double first_magnitude = vertical_interval_succession[most_common_interval];
        double second_magnitude = 0;
        for(int interval = 0; interval < vertical_interval_succession.length; interval++) {
            double current_magnitude = vertical_interval_succession[interval];
            if(current_magnitude > second_magnitude && current_magnitude < first_magnitude) {
                second_magnitude = current_magnitude;
                second_common_interval = interval;
            }
        }
        return new double[]{second_common_interval};
    }
}
