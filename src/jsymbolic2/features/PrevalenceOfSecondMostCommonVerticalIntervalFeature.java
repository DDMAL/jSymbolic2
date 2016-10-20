package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;

/**
 * A feature extractor that extracts the fraction of vertical intervals corresponding
 * to the second most common vertical interval.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class PrevalenceOfSecondMostCommonVerticalIntervalFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public PrevalenceOfSecondMostCommonVerticalIntervalFeature()
    {
		code = "C-10";
        String name = "Prevalence of Second Most Common Vertical Interval";
        String description = "The fraction of vertical intervals corresponding to the second most common vertical interval.";
        boolean is_sequential = true;
        int dimensions = 1;
        definition = new FeatureDefinition( name,
                description,
                is_sequential,
                dimensions );

        dependencies = new String[]{"Wrapped Vertical Interval Histogram", "Second Most Common Vertical Interval"};
        offsets = null;
    }

    /**
     * Extracts this feature from the given MIDI sequence given the other
     * feature values.
     *
     * <p>In the case of this feature, the other_feature_values parameters
     * are the VerticalIntervalsWrapped output in other_feature_values[0]
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
        double[] most_common_interval_array = other_feature_values[1];
        int most_common_interval = (int) Math.round(most_common_interval_array[0]);
        double[] vertical_interval_chart = other_feature_values[0];
        double most_prevalent_interval = vertical_interval_chart[most_common_interval];
        return new double[]{most_prevalent_interval};
    }
}
