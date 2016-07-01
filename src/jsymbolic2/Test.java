package jsymbolic2;

import org.ddmal.midiUtilities.MidiBuildEvent;

import javax.sound.midi.*;

/**
 * Created by dinamix on 6/27/16.
 */
public class Test {
    public static void main(String[] args) throws InvalidMidiDataException, MidiUnavailableException {
        Sequence s = new Sequence(Sequence.PPQ, 256);
        Track t1 = s.createTrack();
        MidiEvent e3 = MidiBuildEvent.createNoteOnEvent(50, 256, 0);
        MidiEvent e4 = MidiBuildEvent.createNoteOffEvent(50, 300, 0);
        MidiEvent e1 = MidiBuildEvent.createNoteOnEvent(60, 1, 0);
        MidiEvent e2 = MidiBuildEvent.createNoteOffEvent(60, 50, 0);
        MidiEvent e5 = MidiBuildEvent.createNoteOnEvent(47, 256, 0);
        MidiEvent e6 = MidiBuildEvent.createNoteOffEvent(47, 300, 0);
        MidiEvent e7 = MidiBuildEvent.createNoteOnEvent(10, 1, 0);
        MidiEvent e8 = MidiBuildEvent.createNoteOffEvent(10, 30, 0);
        t1.add(e3);
        t1.add(e2);
        t1.add(e4);
        t1.add(e8);
        t1.add(e1);
        t1.add(e5);
        t1.add(e7);
        t1.add(e6);

        for(Track t : s.getTracks()) {
            for(int i = 0; i < t.size(); i++) {
                MidiEvent e = t.get(i);
                //System.out.println(e.getTick());
            }
        }

        /*Sequencer sequencer = MidiSystem.getSequencer();
        sequencer.open();
        sequencer.setSequence(s);
        sequencer.start();*/
    }
}
