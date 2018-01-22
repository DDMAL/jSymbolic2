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
 * Created by dinamix on 7/28/16.
 */
public class AverageVariabilityOfRestDurationsAcrossVoicesFeatureTest {
    @Test
    public void extractFeature() throws Exception {
        Sequence test_tracks = new Sequence(Sequence.PPQ, 1);
        Track t1_tracks = test_tracks.createTrack();
        Track t2_tracks = test_tracks.createTrack();
        //Velocities here are always 64
        MidiEvent e_tracks3 = MidiBuildEvent.createNoteOnEvent(0, 0, 1);
        MidiEvent e_tracks4 = MidiBuildEvent.createNoteOffEvent(0, 1, 1);
        MidiEvent e_tracks1 = MidiBuildEvent.createNoteOnEvent(3, 0, 0);
        MidiEvent e_tracks2 = MidiBuildEvent.createNoteOffEvent(3, 1, 0);
        MidiEvent e_tracks5 = MidiBuildEvent.createNoteOnEvent(7, 3, 1);
        MidiEvent e_tracks6 = MidiBuildEvent.createNoteOffEvent(7, 4, 1);
        MidiEvent e_tracks7 = MidiBuildEvent.createNoteOnEvent(7, 4, 0);
        MidiEvent e_tracks8 = MidiBuildEvent.createNoteOffEvent(7, 14, 0);
        t1_tracks.add(e_tracks3);
        t2_tracks.add(e_tracks2);
        t1_tracks.add(e_tracks4);
        t2_tracks.add(e_tracks1);
        t1_tracks.add(e_tracks5);
        t1_tracks.add(e_tracks6);
        t2_tracks.add(e_tracks7);
        t2_tracks.add(e_tracks8);

        MIDIIntermediateRepresentations inter = new MIDIIntermediateRepresentations(test_tracks);
        MIDIFeatureExtractor actual_common = new VariabilityAcrossVoicesOfCombinedRestsFeature();
        double[] actual_rest = actual_common.extractFeature(test_tracks, inter, null);
        double[] expected_rest = {2.82843};
        assertArrayEquals(expected_rest, actual_rest, 0.00001);
    }

}