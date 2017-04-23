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
 * Created by dinamix on 8/1/16.
 */
public class ParallelMotionFeatureTest {
    @Test
    public void extractFeature() throws Exception {
        Sequence test_tracks = new Sequence(Sequence.PPQ, 256);
        Track t1_tracks = test_tracks.createTrack();
        Track t2_tracks = test_tracks.createTrack();
        //Velocities here are always 64
        MidiEvent e_tracks3 = MidiBuildEvent.createNoteOnEvent(0, 0, 0);
        MidiEvent e_tracks4 = MidiBuildEvent.createNoteOffEvent(0, 11, 0);
        MidiEvent e_tracks1 = MidiBuildEvent.createNoteOnEvent(4, 0, 0);
        MidiEvent e_tracks2 = MidiBuildEvent.createNoteOffEvent(4, 11, 0);
        MidiEvent e_tracks5 = MidiBuildEvent.createNoteOnEvent(2, 12, 1);
        MidiEvent e_tracks6 = MidiBuildEvent.createNoteOffEvent(2, 24, 1);
        MidiEvent e_tracks7 = MidiBuildEvent.createNoteOnEvent(6, 12, 1);
        MidiEvent e_tracks8 = MidiBuildEvent.createNoteOffEvent(6, 24, 1);
        MidiEvent e_tracks9 = MidiBuildEvent.createNoteOnEvent(7, 25, 0);
        MidiEvent e_tracks10 = MidiBuildEvent.createNoteOffEvent(7, 30, 0);
        MidiEvent e_tracks13 = MidiBuildEvent.createNoteOnEvent(1, 25, 1);
        MidiEvent e_tracks14 = MidiBuildEvent.createNoteOffEvent(1, 30, 1);
        t1_tracks.add(e_tracks3);
        t2_tracks.add(e_tracks2);
        t1_tracks.add(e_tracks4);
        t2_tracks.add(e_tracks1);
        t1_tracks.add(e_tracks5);
        t1_tracks.add(e_tracks6);
        t2_tracks.add(e_tracks7);
        t2_tracks.add(e_tracks8);
        t1_tracks.add(e_tracks9);
        t1_tracks.add(e_tracks10);
        t2_tracks.add(e_tracks13);
        t2_tracks.add(e_tracks14);

        MIDIIntermediateRepresentations inter = new MIDIIntermediateRepresentations(test_tracks);
        MIDIFeatureExtractor actual_common = new ParallelMotionFeature();
        double[] actual_value = actual_common.extractFeature(test_tracks, inter, null);
        double[] expected_value = {0.5};
        assertArrayEquals(expected_value, actual_value, 0.0001);
    }

}