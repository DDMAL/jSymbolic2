package jsymbolic2.features;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;
import org.ddmal.midiUtilities.MidiBuildEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import static org.junit.Assert.*;

/**
 * Created by dinamix on 7/13/16.
 */
public class RatioOfPrevalenceOfTwoMostCommonVerticalIntervalsFeatureTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void extractFeature() throws Exception {
        Sequence test_tracks = new Sequence(Sequence.PPQ, 256);
        Track t1_tracks = test_tracks.createTrack();
        Track t2_tracks = test_tracks.createTrack();
        //Velocities here are always 64
        MidiEvent e_tracks3 = MidiBuildEvent.createNoteOnEvent(0, 0, 0);
        MidiEvent e_tracks4 = MidiBuildEvent.createNoteOffEvent(0, 3, 0);
        MidiEvent e_tracks1 = MidiBuildEvent.createNoteOnEvent(4, 0, 0);
        MidiEvent e_tracks2 = MidiBuildEvent.createNoteOffEvent(4, 1, 0);
        MidiEvent e_tracks5 = MidiBuildEvent.createNoteOnEvent(7, 0, 0);
        MidiEvent e_tracks6 = MidiBuildEvent.createNoteOffEvent(7, 3, 0);
        t1_tracks.add(e_tracks3);
        t2_tracks.add(e_tracks2);
        t1_tracks.add(e_tracks4);
        t2_tracks.add(e_tracks1);
        t1_tracks.add(e_tracks5);
        t1_tracks.add(e_tracks6);

        MIDIIntermediateRepresentations inter = new MIDIIntermediateRepresentations(test_tracks);
        double[] unwrapped_vertical_intervals = new VerticalIntervalHistogramFeature().extractFeature(test_tracks, inter, null);
        double[][] vertical_interval_other_features = new double[1][];
        vertical_interval_other_features[0] = unwrapped_vertical_intervals;
        double[] vertical_intervals = new WrappedVerticalIntervalHistogramFeature().extractFeature(test_tracks, inter, vertical_interval_other_features);
        double[][] other_features = new double[2][];
        other_features[0] = vertical_intervals;
        MIDIFeatureExtractor common_interval_feature = new MostCommonVerticalIntervalFeature();
        double[] most_common_vertical_interval = common_interval_feature.extractFeature(test_tracks, inter, other_features);
        other_features[1] = most_common_vertical_interval;
        MIDIFeatureExtractor seccond_common_feature = new SecondMostCommonVerticalIntervalFeature();
        double[] second_common_value = seccond_common_feature.extractFeature(test_tracks, inter, other_features);
        MIDIFeatureExtractor prevalence_common = new PrevalenceOfMostCommonVerticalIntervalFeature();
        double[] prevalence_value = prevalence_common.extractFeature(test_tracks, inter, other_features);
        other_features[1] = second_common_value;
        MIDIFeatureExtractor second_prevalence_feature = new PrevalenceOfSecondMostCommonVerticalIntervalFeature();
        double[] second_prevalence_value = second_prevalence_feature.extractFeature(test_tracks, inter, other_features);
        other_features[0] = prevalence_value;
        other_features[1] = second_prevalence_value;
        MIDIFeatureExtractor actual_common = new PrevalenceRatioOfTwoMostCommonVerticalIntervalsFeature();
        double[] actual_prevalence = actual_common.extractFeature(test_tracks, inter, other_features);
        double[] expected_prevalence = {0.3333};
        assertArrayEquals(expected_prevalence, actual_prevalence, 0.0001);
    }

}