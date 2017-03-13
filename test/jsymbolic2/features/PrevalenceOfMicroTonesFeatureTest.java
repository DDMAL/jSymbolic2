package jsymbolic2.features;

import jsymbolic2.featureutils.MIDIFeatureExtractor;
import jsymbolic2.processing.MIDIIntermediateRepresentations;
import org.junit.Test;

import javax.sound.midi.*;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by dinamix on 7/29/16.
 */
public class PrevalenceOfMicroTonesFeatureTest {
    @Test
    public void extractFeature() throws Exception {
        File britten = new File("./test/jsymbolic2/features/resources/Britten_-_Serenade_prologue.midi");
        Sequence test_tracks = MidiSystem.getSequence(britten);
        MIDIIntermediateRepresentations inter = new MIDIIntermediateRepresentations(test_tracks);

        MIDIFeatureExtractor common_interval_feature = new MicrotonePrevalenceFeature();
        double[] actual_prevalence = common_interval_feature.extractFeature(test_tracks, inter, null);
        double[] expected_prevalence = {0.5273};
        assertArrayEquals(expected_prevalence, actual_prevalence, 0.0001);
    }

}