package jsymbolic2.features;

import ace.datatypes.FeatureDefinition;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;
import mckay.utilities.sound.midi.MIDIMethods;

import javax.sound.midi.Sequence;
import java.util.List;
import java.util.stream.DoubleStream;

/**
 * A feature extractor that extracts the recording is broken into windows with 5
 * second durations. The note density is then calculated for each window, and the
 * standard deviation of these windows is then calculated.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Tristano Tenaglia
 */
public class NoteDensityVariabilityFeature extends MIDIFeatureExtractor {

    /**
     * Basic constructor that sets the definition and dependencies (and their
     * offsets) of this feature.
     */
    public NoteDensityVariabilityFeature() {
        String name = "Note Density Variability";
        String description = "The recording is broken into windows with 5\n" +
                "second durations. The note density is then calculated for each window, and the\n" +
                "standard deviation of these windows is then calculated.";
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
        // Break up sequence into 5 second MIDI windows
        int window_size = 5;
        int window_overlap = 0;
        double[] seconds_per_tick = MIDIMethods.getSecondsPerTick(sequence);
        List<int[]> startEndTickArrays = MIDIMethods.getStartEndTickArrays(sequence, window_size, window_overlap, seconds_per_tick);
        int[] start_ticks = startEndTickArrays.get(0);
        int[] end_ticks = startEndTickArrays.get(1);
        Sequence[] windows = MIDIMethods.breakSequenceIntoWindows(sequence, window_size, window_overlap, start_ticks, end_ticks);

        // Compute the note density for each window using the note density feature
        // Need to recompute intermediate representation for each sequence and
        // so we extract feature from scratch without a dependency
        double[] note_density_windows = new double[windows.length];
        for(int window = 0; window < windows.length; window++) {
            Sequence this_window = windows[window];
            MIDIIntermediateRepresentations window_info = new MIDIIntermediateRepresentations(this_window);
            note_density_windows[window] = new NoteDensityFeature().extractFeature(this_window, window_info, null)[0];
        }

        // Compute the standard deviation between all note density windows
        double note_density_avg = DoubleStream.of(note_density_windows)
                .average()
                .getAsDouble();
        double[] deviations = DoubleStream.of(note_density_windows)
                .map((double d) -> Math.pow(d - note_density_avg, 2))
                .toArray();
        double standard_deviation = DoubleStream.of(deviations)
                .average()
                .getAsDouble();

        return new double[]{standard_deviation};
    }
}
