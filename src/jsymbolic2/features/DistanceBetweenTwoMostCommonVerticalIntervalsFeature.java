package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;

/**
 * A feature extractor that extracts the difference between the bin labels
 * of the two most common vertical intervals.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class DistanceBetweenTwoMostCommonVerticalIntervalsFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public DistanceBetweenTwoMostCommonVerticalIntervalsFeature()
    {
        String name = "Distance Between Two Most Common Vertical Intervals";
        String description = "The difference between the bin labels of the two most common vertical intervals.";
        boolean is_sequential = true;
        int dimensions = 1;
        definition = new FeatureDefinition( name,
                description,
                is_sequential,
                dimensions );

        dependencies = new String[]{"Most Common Vertical Interval", "Second Most Common Vertical Interval"};
        offsets = null;
    }

    /**
     * Extracts this feature from the given MIDI sequence given the other
     * feature values.
     *
     * <p>In the case of this feature, the other_feature_values parameters
     * are the MostCommonVerticalInterval output in other_feature_values[0]
     * and the SecondMostCommonVerticalInterval output in other_feature_values[1].
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
        double[] most_common_vertical_interval = other_feature_values[0];
        double[] second_common_vertical_interval = other_feature_values[1];
        double most_common_interval = most_common_vertical_interval[0];
        double second_common_interval = second_common_vertical_interval[0];
        double distance = Math.round(Math.abs(most_common_interval - second_common_interval));
        return new double[]{distance};
    }
}
