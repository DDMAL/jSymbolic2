package jsymbolic2.features;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;
import org.ddmal.midiUtilities.MidiBuildEvent;
import org.junit.Test;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import static org.junit.Assert.*;

/**
 * Created by dinamix on 7/15/16.
 */
public class VerticalFifthsFeatureTest {
    @Test
    public void extractFeature() throws Exception {
        //Test for track unisons
        Sequence test_uni = new Sequence(Sequence.PPQ, 256);
        Track uni1_tracks = test_uni.createTrack();
        Track uni2_tracks = test_uni.createTrack();
        //Velocities here are always 64
        MidiEvent uni_tracks3 = MidiBuildEvent.createNoteOnEvent(0, 0, 0);
        MidiEvent uni_tracks4 = MidiBuildEvent.createNoteOffEvent(0, 2, 0);
        MidiEvent uni_tracks1 = MidiBuildEvent.createNoteOnEvent(7, 1, 0);
        MidiEvent uni_tracks2 = MidiBuildEvent.createNoteOffEvent(7, 2, 0);
        MidiEvent uni_tracks5 = MidiBuildEvent.createNoteOnEvent(3, 0, 0);
        MidiEvent uni_tracks6 = MidiBuildEvent.createNoteOffEvent(3, 1, 0);
        uni1_tracks.add(uni_tracks3);
        uni2_tracks.add(uni_tracks2);
        uni1_tracks.add(uni_tracks4);
        uni2_tracks.add(uni_tracks1);
        uni1_tracks.add(uni_tracks5);
        uni1_tracks.add(uni_tracks6);

        MIDIIntermediateRepresentations actual_representation_unison = new MIDIIntermediateRepresentations(test_uni);
        double[] unwrapped_vertical_intervals = new VerticalIntervalHistogramFeature().extractFeature(test_uni, actual_representation_unison, null);
        double[][] vertical_interval_other_features = new double[1][];
        vertical_interval_other_features[0] = unwrapped_vertical_intervals;
        MIDIFeatureExtractor unison = new VerticalPerfectFifthsFeature();
        double[] vertical_intervals = new WrappedVerticalIntervalHistogramFeature().extractFeature(test_uni, actual_representation_unison, vertical_interval_other_features);
        double[][] other_features = new double[1][];
        other_features[0] = vertical_intervals;
        double[] actual_unison = unison.extractFeature(test_uni, actual_representation_unison, other_features);

        double[] expected_intervals = new double[1];
        expected_intervals[0] = 0.5;

        assertArrayEquals(expected_intervals, actual_unison, 0.0001);
    }

}