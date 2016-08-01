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
 * Created by dinamix on 7/6/16.
 */
public class VerticalIntervalsFeatureTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void extractFeature() throws Exception {
        //Test for tracks tracks
        Sequence test_tracks = new Sequence(Sequence.PPQ, 256);
        Track t1_tracks = test_tracks.createTrack();
        Track t2_tracks = test_tracks.createTrack();
        //Velocities here are always 64
        MidiEvent e_tracks3 = MidiBuildEvent.createNoteOnEvent(0, 0, 0);
        MidiEvent e_tracks4 = MidiBuildEvent.createNoteOffEvent(0, 3, 0);
        MidiEvent e_tracks1 = MidiBuildEvent.createNoteOnEvent(2, 1, 0);
        MidiEvent e_tracks2 = MidiBuildEvent.createNoteOffEvent(2, 4, 0);
        MidiEvent e_tracks5 = MidiBuildEvent.createNoteOnEvent(4, 2, 0);
        MidiEvent e_tracks6 = MidiBuildEvent.createNoteOffEvent(4, 4, 0);
        t1_tracks.add(e_tracks3);
        t2_tracks.add(e_tracks2);
        t1_tracks.add(e_tracks4);
        t2_tracks.add(e_tracks1);
        t1_tracks.add(e_tracks5);
        t1_tracks.add(e_tracks6);

        MIDIFeatureExtractor vertical = new VerticalIntervalHistogramFeature();
        MIDIIntermediateRepresentations actual_representation_tracks = new MIDIIntermediateRepresentations(test_tracks);
        double[] actual_vertical = vertical.extractFeature(test_tracks, actual_representation_tracks, null);

        double[] expected_vertical = new double[128];
        expected_vertical[2] = 0.8;
        expected_vertical[4] = 0.2;

        assertArrayEquals(expected_vertical, actual_vertical, 0.0001);
    }

}