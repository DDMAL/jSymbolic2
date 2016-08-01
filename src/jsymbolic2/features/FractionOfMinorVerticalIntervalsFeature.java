package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;

/**
 * A feature extractor that extracts the fraction of all minor third vertical intervals
 * over the total number of vertical intervals.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class FractionOfMinorVerticalIntervalsFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public FractionOfMinorVerticalIntervalsFeature() {
        String name = "Fraction of Minor Vertical Intervals";
        String description = "A value between 0 and 1 indicating the fraction of all vertical intervals that contain a minor third.";
        boolean is_sequential = true;
        int dimensions = 1;
        definition = new FeatureDefinition( name,
                description,
                is_sequential,
                dimensions );

        dependencies = new String[]{"Vertical Interval Wrapped Histogram"};
        offsets = null;
    }

    /**
     * Extracts this feature from the given MIDI sequence given the other
     * feature values.
     *
     * <p>In the case of this feature, the other_feature_values parameters
     * are the VerticalIntervals output in other_feature_values[0].
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
        double[] vertical_interval_histogram = other_feature_values[0];
        double minor_third = vertical_interval_histogram[3]; // minor third
        double all_vertical_intervals = 0;
        for(int vertical_interval = 0; vertical_interval < vertical_interval_histogram.length; vertical_interval++) {
            all_vertical_intervals += vertical_interval_histogram[vertical_interval];
        }
        double ratio = minor_third / all_vertical_intervals;
        return new double[]{ratio};
    }
}
