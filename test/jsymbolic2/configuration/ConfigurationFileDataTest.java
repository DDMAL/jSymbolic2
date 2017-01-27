package jsymbolic2.configuration;

import jsymbolic2.featureutils.FeatureExtractorAccess;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by dinamix on 5/24/16.
 */
public class ConfigurationFileDataTest {
    @Test
    public void getFeaturesToSaveBoolean() throws Exception {
        List<String> inputFeatures = Arrays.asList("Brass Prevalence","Duration");
        ConfigurationOptionState optionState = new ConfigurationOptionState(10,0.1,true,false,false,false);
        ConfigurationOutputFiles outputFiles = new ConfigurationOutputFiles("valuetest.xml","featuretest.xml");
        ConfigurationInputFiles inputFiles = new ConfigurationInputFiles();
        inputFiles.addValidFile(new File("./test/jsymbolic/features/resources/Saint-Saens_LeCarnevalDesAnimmaux.mei"));
        String configFilePath = "/workspace/ddmal/jSymbolic/test/jsymbolic/configuration/resources/sampleConfiguration.txt";
        ConfigurationFileData data = new ConfigurationFileData(inputFeatures,optionState,outputFiles,configFilePath,inputFiles);

        boolean[] expectedSave = new boolean[FeatureExtractorAccess.getNamesOfAllImplementedFeatures().size()];
        expectedSave = initializeArrayFalse(expectedSave);
        expectedSave[121] = true; //Duration feature
        expectedSave[137] = true; //Brass Fraction feature
        assertArrayEquals(expectedSave,data.getFeaturesToSaveBoolean());
    }

    private boolean[] initializeArrayFalse(boolean[] tempSave) {
        for(int i = 0; i < tempSave.length; i++) {
            tempSave[i] = false;
        }
        return tempSave;
    }
}