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
public class MelodicPitchVarietyFeatureTest {
    @Test
    public void extractFeature() throws Exception {
        Sequence test_tracks = new Sequence(Sequence.PPQ, 256);
        Track t1_tracks = test_tracks.createTrack();
        Track t2_tracks = test_tracks.createTrack();
        //Velocities here are always 64
        MidiEvent e_tracks3 = MidiBuildEvent.createNoteOnEvent(0, 0, 0);
        MidiEvent e_tracks4 = MidiBuildEvent.createNoteOffEvent(0, 1, 0);
        MidiEvent e_tracks1 = MidiBuildEvent.createNoteOnEvent(4, 2, 0);
        MidiEvent e_tracks2 = MidiBuildEvent.createNoteOffEvent(4, 3, 0);
        MidiEvent e_tracks5 = MidiBuildEvent.createNoteOnEvent(0, 4, 0);
        MidiEvent e_tracks6 = MidiBuildEvent.createNoteOffEvent(0, 5, 0);
        t1_tracks.add(e_tracks3);
        t2_tracks.add(e_tracks2);
        t1_tracks.add(e_tracks4);
        t2_tracks.add(e_tracks1);
        t1_tracks.add(e_tracks5);
        t1_tracks.add(e_tracks6);

        MIDIIntermediateRepresentations inter = new MIDIIntermediateRepresentations(test_tracks);
        MIDIFeatureExtractor actual_common = new MelodicPitchVarietyFeature();
        double[] actual_variety = actual_common.extractFeature(test_tracks, inter, null);
        double[] expected_variety = {2};
        assertArrayEquals(expected_variety, actual_variety, 0.1);
    }

}