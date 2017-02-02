package jsymbolic2.features;

import jsymbolic2.processing.MIDIIntermediateRepresentations;
import mckay.utilities.sound.midi.MIDIMethods;
import org.ddmal.jmei2midi.MeiSequence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sound.midi.Sequence;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by dinamix on 5/6/16.
 */
public class AcousticGuitarFractionFeatureTest {

    double deviation = 0.0001;
    String saintSaensFileName = "test/jsymbolic2/features/resources/Saint-Saens_LeCarnevalDesAnimmaux.mei";
    Sequence saintSaensSequence;

    @Before
    public void setUp() throws Exception {
        MeiSequence saintSaensMEI = new MeiSequence(saintSaensFileName);
        saintSaensSequence = saintSaensMEI.getSequence();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void extractFeature() throws Exception {
        //Checking entire midi file
        AcousticGuitarPrevalenceFeature agff = new AcousticGuitarPrevalenceFeature();
        MIDIIntermediateRepresentations mir = new MIDIIntermediateRepresentations(saintSaensSequence);
        double[] actualArray = agff.extractFeature(saintSaensSequence, mir, null);
        double actualGuitarFraction = actualArray[0];
        double expectedGuitarFraction = 0.856301;
        assertEquals(expectedGuitarFraction,actualGuitarFraction,deviation);

        //Checking sequence windows
        double[] saintSaens_seconds_per_tick = MIDIMethods.getSecondsPerTick(saintSaensSequence);
        List<int[]> saintSaensStartEndTickArrays = MIDIMethods.getStartEndTickArrays(saintSaensSequence,
                10, 0, saintSaens_seconds_per_tick);
        int[] saintSaens_start_ticks = saintSaensStartEndTickArrays.get(0);
        int[] saintSaens_end_ticks = saintSaensStartEndTickArrays.get(1);
        Sequence[] saintSaensWindows = MIDIMethods.breakSequenceIntoWindows(saintSaensSequence,
                10, 0, saintSaens_start_ticks, saintSaens_end_ticks);

        double[] expectedGuitarFractionArray = {0.9091,0.8108,0.8732,0.8108,0.8438,0.8406,0.8525,0.8421,0.8438,
                                                0.8333,0.8571,0.8,0.8205,0.9268};
        double[] actualGuitarFractionArray = new double[expectedGuitarFractionArray.length];
        for(int i = 0; i < saintSaensWindows.length; i++) {
            Sequence s = saintSaensWindows[i];
            MIDIIntermediateRepresentations mirwindow = new MIDIIntermediateRepresentations(s);
            double[] actualArrayWindow = agff.extractFeature(s, mirwindow, null);
            actualGuitarFractionArray[i] = actualArrayWindow[0];
        }
        for(int i = 0; i < saintSaensWindows.length; i++)  {
            double expectedGuitarFractionWindow = expectedGuitarFractionArray[i];
            double actualGuitarFractionWindow = actualGuitarFractionArray[i];
            assertEquals(expectedGuitarFractionWindow,actualGuitarFractionWindow,deviation);
        }
    }
}