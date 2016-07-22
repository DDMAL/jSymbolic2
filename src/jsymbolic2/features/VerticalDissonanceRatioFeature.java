package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;

/**
 * A feature extractor that extracts the total number of vertical 2nds, tritones, 7ths
 * and 9ths divided by the total number of vertical unisons, 4ths, 5ths, 6ths, octaves
 * and 10ths.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class VerticalDissonanceRatioFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public VerticalDissonanceRatioFeature() {
        String name = "Vertical Dissonance Ratio";
        String description = "Total number of vertical 2nds, tritones, 7ths\n" +
                "and 9ths divided by the total number of vertical unisons, 4ths, 5ths, 6ths, octaves\n" +
                "and 10ths.";
        boolean is_sequential = true;
        int dimensions = 1;
        definition = new FeatureDefinition( name,
                description,
                is_sequential,
                dimensions );

        dependencies = new String[]{"Vertical Interval Succession", "Vertical Interval Succession Wrapped"};
        offsets = null;
    }


    /**
     * Extracts this feature from the given MIDI sequence given the other
     * feature values.
     *
     * <p>In the case of this feature, the other_feature_values parameters
     * are the VerticalIntervals output in other_feature_values[0]
     * and the VerticalIntervalsWrapped output in other_feature_values[1].
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
        double[] vertical_interval_chart = other_feature_values[0];
        double[] vertical_interval_wrapped = other_feature_values[1];
        double octaves = 0;
        double ninths = 0;
        double tenths = 0;
        for(int interval = 0; interval < vertical_interval_chart.length; interval++) {
            if(interval % 8 == 0) {
                octaves += vertical_interval_chart[interval];
            }
            else if(interval % 9 == 0) {
                ninths += vertical_interval_chart[interval];
            }
            else if(interval % 10 == 0) {
                tenths += vertical_interval_chart[interval];
            }
        }
        double dissonance = vertical_interval_wrapped[2] + vertical_interval_wrapped[3] + vertical_interval_wrapped[6]
                            + vertical_interval_wrapped[10] + vertical_interval_wrapped[11] + ninths;
        double consonance = vertical_interval_wrapped[0] + vertical_interval_wrapped[5] + vertical_interval_wrapped[7]
                            + vertical_interval_wrapped[8] + vertical_interval_wrapped[9] + octaves + tenths;
        double ratio = dissonance / consonance;
        return new double[]{ratio};
    }
}
