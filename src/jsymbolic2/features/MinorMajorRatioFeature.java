package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;

/**
 * A feature extractor that extracts the number of minor vertical intervals divided by number
 * of major vertical intervals.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class MinorMajorRatioFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public MinorMajorRatioFeature()
    {
        String name = "Minor Major Ratio";
        String description = "Number of minor vertical intervals divided by number\n" +
                "of major vertical intervals.";
        boolean is_sequential = true;
        int dimensions = 1;
        definition = new FeatureDefinition( name,
                description,
                is_sequential,
                dimensions );

        dependencies = new String[]{"Vertical Interval Succession Wrapped"};
        offsets = null;
    }

    /**
     * Extracts this feature from the given MIDI sequence given the other
     * feature values.
     *
     * <p>In the case of this feature, the other_feature_values parameters
     * are the VerticalIntervalsWrapped output in other_feature_values[0].
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
        double minor = 0;
        double major = 0;
        minor += vertical_interval_histogram[1]; // minor second
        minor += vertical_interval_histogram[3]; // minor third
        minor += vertical_interval_histogram[8]; // minor sixth
        minor += vertical_interval_histogram[10]; // minor seventh
        major += vertical_interval_histogram[2]; // major second
        major += vertical_interval_histogram[4]; // major third
        major += vertical_interval_histogram[9]; // major sixth
        major += vertical_interval_histogram[11]; // major seventh
        //TODO what if major is 0
        double ratio = minor / major;
        return new double[]{ratio};
    }
}
