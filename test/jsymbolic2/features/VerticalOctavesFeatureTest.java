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
public class VerticalOctavesFeatureTest {
    @Test
    public void extractFeature() throws Exception {
        Sequence test_tracks = new Sequence(Sequence.PPQ, 256);
        Track t1_tracks = test_tracks.createTrack();
        Track t2_tracks = test_tracks.createTrack();
        //Velocities here are always 64
        MidiEvent e_tracks3 = MidiBuildEvent.createNoteOnEvent(2, 0, 0);
        MidiEvent e_tracks4 = MidiBuildEvent.createNoteOffEvent(2, 3, 0);
        MidiEvent e_tracks1 = MidiBuildEvent.createNoteOnEvent(8, 0, 0);
        MidiEvent e_tracks2 = MidiBuildEvent.createNoteOffEvent(8, 1, 0);
        MidiEvent e_tracks5 = MidiBuildEvent.createNoteOnEvent(14, 0, 0);
        MidiEvent e_tracks6 = MidiBuildEvent.createNoteOffEvent(14, 3, 0);
        t1_tracks.add(e_tracks3);
        t2_tracks.add(e_tracks2);
        t1_tracks.add(e_tracks4);
        t2_tracks.add(e_tracks1);
        t1_tracks.add(e_tracks5);
        t1_tracks.add(e_tracks6);

        MIDIIntermediateRepresentations inter = new MIDIIntermediateRepresentations(test_tracks);
        double[] unwrapped_vertical_intervals = new VerticalIntervalHistogramFeature().extractFeature(test_tracks, inter, null);
        double[][] other_features = new double[2][];
        other_features[0] = unwrapped_vertical_intervals;
        MIDIFeatureExtractor actual_common = new VerticalOctavesFeature();
        double[] vertical_intervals = new WrappedVerticalIntervalHistogramFeature().extractFeature(test_tracks, inter, other_features);
        other_features[0] = vertical_intervals;
        other_features[1] = unwrapped_vertical_intervals;
        double[] actual_chord_type = actual_common.extractFeature(test_tracks, inter, other_features);
        double[] expected_chord_type = {0.6};
        assertArrayEquals(expected_chord_type, actual_chord_type, 0.001);
    }

}