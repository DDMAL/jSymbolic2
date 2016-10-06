package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.featureutils.FeatureConversion;
import jsymbolic2.processing.MIDIIntermediateRepresentations;

import javax.sound.midi.Sequence;
import java.util.stream.DoubleStream;

/**
 * A feature extractor that extracts the total amount of time in seconds in which no
 * notes are sounding on any channel divided by the total length of the recording.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class IncidenceOfCompleteRestsFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public IncidenceOfCompleteRestsFeature() {
        String name = "Incidence of Complete Rests";
        String description = "Total amount of time in seconds in which no\n" +
                "notes are sounding on any channel divided by the total length of the recording.";
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
    public double[] extractFeature( Sequence sequence,
                                    MIDIIntermediateRepresentations sequence_info,
                                    double[][] other_feature_values )
            throws Exception
    {
        short[][] vertical_chart = sequence_info.vertical_interval_chart;
        double[] seconds_per_tick = sequence_info.seconds_per_tick;
        // Minus 1 since java doesnt count last tick in tick length
        int tick_count = vertical_chart.length - 1;
        double[] rest_ticks = new double[tick_count];
        for(int tick = 0; tick < tick_count; tick++) {
            short[] tick_array = vertical_chart[tick];
            if(FeatureConversion.allArrayEqual(tick_array, 0)) {
                rest_ticks[tick] = seconds_per_tick[tick];
            }
        }
        double rest_sum = DoubleStream.of(rest_ticks).sum();
        double incidence_rests = rest_sum / sequence_info.recording_length_double;
        long tick_length = sequence.getTickLength();
        return new double[]{incidence_rests};
    }
}
