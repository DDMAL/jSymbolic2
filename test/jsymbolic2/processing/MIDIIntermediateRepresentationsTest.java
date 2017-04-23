package jsymbolic2.processing;

import org.ddmal.midiUtilities.MidiBuildEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import static org.junit.Assert.*;

/**
 * Created by dinamix on 7/4/16.
 */
public class MIDIIntermediateRepresentationsTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void verticalIntervalChart() throws Exception {
        //Test for note on after same note off
        Sequence test_overlap = new Sequence(Sequence.PPQ, 256);
        Track t1 = test_overlap.createTrack();
        //Velocities here are always 64
        MidiEvent e3 = MidiBuildEvent.createNoteOnEvent(0, 0, 0);
        MidiEvent e4 = MidiBuildEvent.createNoteOffEvent(0, 3, 0);
        MidiEvent e1 = MidiBuildEvent.createNoteOnEvent(1, 1, 0);
        MidiEvent e2 = MidiBuildEvent.createNoteOffEvent(1, 4, 0);
        MidiEvent e5 = MidiBuildEvent.createNoteOnEvent(0, 3, 0);
        MidiEvent e6 = MidiBuildEvent.createNoteOffEvent(0, 4, 0);
        t1.add(e3);
        t1.add(e2);
        t1.add(e4);
        t1.add(e1);
        t1.add(e6);
        t1.add(e5);

        MIDIIntermediateRepresentations actual_representation = new MIDIIntermediateRepresentations(test_overlap);
        short[][] actual_pitch_tick = actual_representation.pitch_strength_by_tick_chart;

        short[][] expected_pitch_tick = new short[5][128];
        for(int x = 0; x < expected_pitch_tick.length; x++) {
            for(int y = 0; y < expected_pitch_tick[0].length; y++) {
                expected_pitch_tick[x][y] = 0;
            }
        }
        expected_pitch_tick[0][0] = 64;
        expected_pitch_tick[1][0] = 64;
        expected_pitch_tick[2][0] = 64;
        expected_pitch_tick[3][0] = 64;
        expected_pitch_tick[1][1] = 64;
        expected_pitch_tick[2][1] = 64;
        expected_pitch_tick[3][1] = 64;

        assertArrayEquals(expected_pitch_tick, actual_pitch_tick);

        //Test for more notes on same pitch
        Sequence test_more_notes = new Sequence(Sequence.PPQ, 256);
        Track t1_more = test_more_notes.createTrack();
        //Velocities here are always 64
        MidiEvent e_more3 = MidiBuildEvent.createNoteOnEvent(0, 0, 0);
        MidiEvent e_more4 = MidiBuildEvent.createNoteOffEvent(0, 3, 0);
        MidiEvent e_more1 = MidiBuildEvent.createNoteOnEvent(0, 1, 0);
        MidiEvent e_more2 = MidiBuildEvent.createNoteOffEvent(0, 5, 0);
        t1_more.add(e_more3);
        t1_more.add(e_more2);
        t1_more.add(e_more4);
        t1_more.add(e_more1);

        MIDIIntermediateRepresentations actual_representation_more = new MIDIIntermediateRepresentations(test_more_notes);
        short[][] actual_pitch_more = actual_representation_more.pitch_strength_by_tick_chart;

        short[][] expected_pitch_more = new short[6][128];
        for(int x = 0; x < expected_pitch_more.length; x++) {
            for(int y = 0; y < expected_pitch_more[0].length; y++) {
                expected_pitch_more[x][y] = 0;
            }
        }
        expected_pitch_more[0][0] = 64;
        expected_pitch_more[1][0] = 128;
        expected_pitch_more[2][0] = 128;
        expected_pitch_more[3][0] = 0;
        expected_pitch_more[4][0] = 0;

        assertArrayEquals(expected_pitch_more, actual_pitch_more);

        //Test for tracks tracks
        Sequence test_tracks = new Sequence(Sequence.PPQ, 256);
        Track t1_tracks = test_tracks.createTrack();
        Track t2_tracks = test_tracks.createTrack();
        //Velocities here are always 64
        MidiEvent e_tracks3 = MidiBuildEvent.createNoteOnEvent(0, 0, 0);
        MidiEvent e_tracks4 = MidiBuildEvent.createNoteOffEvent(0, 3, 0);
        MidiEvent e_tracks1 = MidiBuildEvent.createNoteOnEvent(0, 1, 0);
        MidiEvent e_tracks2 = MidiBuildEvent.createNoteOffEvent(0, 5, 0);
        t1_tracks.add(e_tracks3);
        t2_tracks.add(e_tracks2);
        t1_tracks.add(e_tracks4);
        t2_tracks.add(e_tracks1);

        MIDIIntermediateRepresentations actual_representation_tracks = new MIDIIntermediateRepresentations(test_tracks);
        short[][] actual_pitch_tracks = actual_representation_tracks.pitch_strength_by_tick_chart;

        short[][] expected_pitch_tracks = new short[6][128];
        for(int x = 0; x < expected_pitch_tracks.length; x++) {
            for(int y = 0; y < expected_pitch_tracks[0].length; y++) {
                expected_pitch_tracks[x][y] = 0;
            }
        }
        expected_pitch_tracks[0][0] = 64;
        expected_pitch_tracks[1][0] = 128;
        expected_pitch_tracks[2][0] = 128;
        expected_pitch_tracks[3][0] = 64;
        expected_pitch_tracks[4][0] = 64;

        assertArrayEquals(expected_pitch_tracks, actual_pitch_tracks);
    }

}