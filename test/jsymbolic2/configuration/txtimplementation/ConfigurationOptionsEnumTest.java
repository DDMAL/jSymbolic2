package jsymbolic2.configuration.txtimplementation;

import jsymbolic2.configurationfile.EnumWindowingAndOutputFormatSettings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dinamix on 5/17/16.
 */
public class ConfigurationOptionsEnumTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void contains() throws Exception {
        assertTrue(EnumWindowingAndOutputFormatSettings.contains("window_size"));
        assertTrue(EnumWindowingAndOutputFormatSettings.contains("window_overlap"));
        assertTrue(EnumWindowingAndOutputFormatSettings.contains("save_features_for_each_window"));
        assertTrue(EnumWindowingAndOutputFormatSettings.contains("save_overall_recording_features"));
        assertTrue(EnumWindowingAndOutputFormatSettings.contains("convert_to_arff"));
        assertTrue(EnumWindowingAndOutputFormatSettings.contains("convert_to_csv"));
    }

    @Test
    public void checkValue() throws Exception {
        assertTrue(EnumWindowingAndOutputFormatSettings.window_size.checkValue("5"));
        assertTrue(EnumWindowingAndOutputFormatSettings.window_size.checkValue("0"));
        assertTrue(EnumWindowingAndOutputFormatSettings.window_overlap.checkValue("0.1"));
        assertTrue(EnumWindowingAndOutputFormatSettings.window_overlap.checkValue("0"));
        assertFalse(EnumWindowingAndOutputFormatSettings.window_overlap.checkValue("1.1"));
        assertTrue(EnumWindowingAndOutputFormatSettings.convert_to_arff.checkValue("false"));
        assertFalse(EnumWindowingAndOutputFormatSettings.window_size.checkValue("-1"));
    }

}