package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;
import java.util.stream.DoubleStream;

/**
 * A feature extractor that extracts the fraction of all vertical intervals that are octaves.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class VerticalOctavesFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public VerticalOctavesFeature() {
		code = "C-17";
        String name = "Vertical Octaves";
        String description = "Fraction of all vertical intervals that are octaves.";
        boolean is_sequential = true;
        int dimensions = 1;
        definition = new FeatureDefinition( name,
                description,
                is_sequential,
                dimensions );

        dependencies = new String[]{"Vertical Interval Histogram"};
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
        double[] vertical_interval = other_feature_values[0];
        double vertical_octave = 0;
        for(int interval = 0; interval < vertical_interval.length; interval++) {
            if(interval % 8 == 0) {
                vertical_octave += vertical_interval[interval];
            }
        }

        double normalize_sum = DoubleStream.of(vertical_interval).sum();
        double octave_fraction = vertical_octave / normalize_sum;
        return new double[]{octave_fraction};
    }
}
