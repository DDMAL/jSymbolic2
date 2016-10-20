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
 * Created by dinamix on 7/18/16.
 */
public class ComplexChordsFeatureTest {
    @Test
    public void extractFeature() throws Exception {
        Sequence test_tracks = new Sequence(Sequence.PPQ, 256);
        Track t1_tracks = test_tracks.createTrack();
        Track t2_tracks = test_tracks.createTrack();
        //Velocities here are always 64
        MidiEvent e_tracks3 = MidiBuildEvent.createNoteOnEvent(0, 0, 0);
        MidiEvent e_tracks4 = MidiBuildEvent.createNoteOffEvent(0, 2, 0);
        MidiEvent e_tracks1 = MidiBuildEvent.createNoteOnEvent(4, 0, 0);
        MidiEvent e_tracks2 = MidiBuildEvent.createNoteOffEvent(4, 2, 0);
        MidiEvent e_tracks5 = MidiBuildEvent.createNoteOnEvent(7, 1, 0);
        MidiEvent e_tracks6 = MidiBuildEvent.createNoteOffEvent(7, 2, 0);
        MidiEvent e_tracks7 = MidiBuildEvent.createNoteOnEvent(10, 1, 0);
        MidiEvent e_tracks8 = MidiBuildEvent.createNoteOffEvent(10, 2, 0);
        MidiEvent e_tracks9 = MidiBuildEvent.createNoteOnEvent(11, 1, 0);
        MidiEvent e_tracks10 = MidiBuildEvent.createNoteOffEvent(11, 2, 0);
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

        MIDIIntermediateRepresentations inter = new MIDIIntermediateRepresentations(test_tracks);
        double[] vertical_intervals = new ChordTypeHistogramFeature().extractFeature(test_tracks, inter, null);
        double[][] other_features = new double[1][];
        other_features[0] = vertical_intervals;
        MIDIFeatureExtractor actual_common = new ComplexChordsFeature();
        double[] actual_chord_type = actual_common.extractFeature(test_tracks, inter, other_features);
        double[] expected_chord_type = {0.5};
        assertArrayEquals(expected_chord_type, actual_chord_type, 0.01);
    }

}