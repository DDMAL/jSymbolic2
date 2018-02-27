/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsymbolic2.processing;

import ca.mcgill.music.ddmal.mei.MeiXmlReader.MeiXmlReadException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.sound.midi.InvalidMidiDataException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * Unit tests for the FileValidator class.
 * 
 * @author Tristano Tenaglia
 */
public class FileValidatorTest {
    @Rule public ExpectedException exception = ExpectedException.none();
    
    /**
     * Test of getMEISequence from the FileValidator class.
     * @throws InvalidMidiDataException test
     * @throws ca.mcgill.music.ddmal.mei.MeiXmlReader.MeiXmlReadException test
     * @throws IOException test
     */
    @Test
    public void testGetMEISequence() 
            throws InvalidMidiDataException, MeiXmlReadException, IOException, Exception
    {
        File invalid = new File("mei-test/Invalid_Altenburg.mei");
        List<String> errorLog = new ArrayList<>();
        
        exception.expect(FileNotFoundException.class);
        SymbolicMusicFileUtilities.getMidiSequenceFromMidiOrMeiFile(invalid, errorLog);
        
        String errorFile = errorLog.get(0);
        assertEquals("The specified file, mei-test/Invalid_Altenburg.mei, is not a valid MEI file.",
                        errorFile);
    }
    
    /**
     * Test of getMIDISequence from the FileValidator class.
     * @throws InvalidMidiDataException test
     * @throws IOException test
     */
    @Test
    public void testGetMIDISequence() 
            throws InvalidMidiDataException, IOException, Exception
    {
        File dne = new File("dne.midi");
        List<String> errorLog = new ArrayList<>();
        
        exception.expect(IOException.class);
        SymbolicMusicFileUtilities.getMidiSequenceFromMidiOrMeiFile(dne, errorLog);
        
        String errorFile = errorLog.get(0);
        assertEquals("The specified path, dne.midi, does not refer to a valid file.",
                        errorFile);
    }

    /**
     * Test of correctFileExtension from the FileValidator class.
     */
    @Test
    public void testCorrectFileExtension() {
        String fileExtension = "xml";

        String noDot = "test";
        String expectedNoDot = "test.xml";
        assertEquals(expectedNoDot,SymbolicMusicFileUtilities.correctFileExtension(noDot,fileExtension));

        String withDot = "test.two";
        String expectedWithDot = "test.two.xml";
        assertEquals(expectedWithDot,SymbolicMusicFileUtilities.correctFileExtension(withDot,fileExtension));

        String noProblem = "test.Xml";
        String expectedNoProblem = "test.Xml";
        assertEquals(expectedNoProblem,SymbolicMusicFileUtilities.correctFileExtension(noProblem,fileExtension));
    }
}
