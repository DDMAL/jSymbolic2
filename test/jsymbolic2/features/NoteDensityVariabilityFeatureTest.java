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
 * Created by dinamix on 7/29/16.
 */
public class NoteDensityVariabilityFeatureTest {
    @Test
    public void extractFeature() throws Exception {
        Sequence test_tracks = new Sequence(Sequence.PPQ, 1);
        Track t1_tracks = test_tracks.createTrack();
        Track t2_tracks = test_tracks.createTrack();
        //Velocities here are always 64
        MidiEvent e_tracks3 = MidiBuildEvent.createNoteOnEvent(5, 0, 0);
        MidiEvent e_tracks4 = MidiBuildEvent.createNoteOffEvent(5, 2, 0);
        MidiEvent e_tracks1 = MidiBuildEvent.createNoteOnEvent(4, 0, 1);
        MidiEvent e_tracks2 = MidiBuildEvent.createNoteOffEvent(4, 2, 1);
        MidiEvent e_tracks5 = MidiBuildEvent.createNoteOnEvent(10, 13, 1);
        MidiEvent e_tracks6 = MidiBuildEvent.createNoteOffEvent(10, 14, 1);
        t1_tracks.add(e_tracks3);
        t2_tracks.add(e_tracks2);
        t1_tracks.add(e_tracks4);
        t2_tracks.add(e_tracks1);
        t1_tracks.add(e_tracks5);
        t1_tracks.add(e_tracks6);

        MIDIIntermediateRepresentations inter = new MIDIIntermediateRepresentations(test_tracks);
        MIDIFeatureExtractor actual_common = new NoteDensityVariabilityFeature();
        double[] actual_chord_type = actual_common.extractFeature(test_tracks, inter, null);
        double[] expected_chord_type = {1.06066};
        assertArrayEquals(expected_chord_type, actual_chord_type, 0.0001);
    }

}