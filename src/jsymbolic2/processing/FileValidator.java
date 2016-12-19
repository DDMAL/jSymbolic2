/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsymbolic2.processing;

import ca.mcgill.music.ddmal.mei.MeiXmlReader;
import ca.mcgill.music.ddmal.mei.MeiXmlReader.MeiXmlReadException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import org.ddmal.jmei2midi.MeiSequence;

/**
 * 
 * Checks if File input is valid with respect to files in MusicFileFilter.java.
 * If so, it returns the correct sequence, otherwise it logs the error in in the
 * List(String) errorLog and throws the exception back.
 * @author Tristano Tenaglia
 */
public class FileValidator
{
    /**
     *
     * @param file A file to be parsed from.
     * @param errorLog Error log to write out all errors to.
     * @return An mei sequence if the file was in fact a valid mei file.
     * @throws InvalidMidiDataException Thrown if the MIDI data is invalid.
     * @throws IOException Thrown if there is a problem reading from the inputted file.
     * @throws MeiXmlReadException Thrown if there is a problem reading in the MEI XML from the inputted file.
     */
    public static MeiSequence getValidMeiSequence(File file, List<String> errorLog)
            throws InvalidMidiDataException, IOException, MeiXmlReadException
    {
        try
        {
            return new MeiSequence(file);
        }
        catch (InvalidMidiDataException | MeiXmlReadException e)
        {
            errorLog.add("The specified file, " + file + ", is not a valid MEI file.");
            throw e;
        }
    }

    /**
     * Gets a valid Java Sequence object from the given file.
     * If file is not valid, then it throws an exception.
     * @param file file to be validated
     * @param errorLog errors that occur during validation
     * @return sequence object created from either MIDI or MEI
     * @throws InvalidMidiDataException Thrown if the MIDI data is invalid.
     * @throws IOException Thrown if there is a problem reading from the inputted file.
     * @throws MeiXmlReadException Thrown if there is a problem reading in the MEI XML from the inputted file.
     */
    public static Sequence getValidSequence(File file, List<String> errorLog)
            throws InvalidMidiDataException, IOException, MeiXmlReadException 
    {
        Sequence sequence;
        if (isValidMeiFile(file)) 
        {
            sequence = getMEISequence(file, errorLog);
        } 
        else 
        {
            sequence = getMIDISequence(file, errorLog);
        }
        return sequence;
    }
    
    /**
     * Validates an mei file by actually processing it and seeing if it
     * is valid mei
     * @param file mei file to be checked
     * @return true if it is valid or else false
     */
    public static boolean isValidMeiFile(File file)
    {
        try
        {
            MeiXmlReader.loadFile(file);
        }
        catch(MeiXmlReadException ex) 
        {
            return false;
        }
        return true;
    }
    
    /**
     * Get a sequence object form a midi file
     * @param file midi file to be processed
     * @param errorLog errors that come from invalid file or data
     * @return sequence object if we have valid midi file or else exception
     *         is logged and then thrown
     * @throws IOException Thrown if the input file is not valid.
     * @throws InvalidMidiDataException Thrown if the input file does not contain valid MIDI.
     */
    private static Sequence getMIDISequence(File file, List<String> errorLog)
            throws IOException, InvalidMidiDataException 
    {
        Sequence sequence = null;
        try 
        {
            sequence = MidiSystem.getSequence(file);
        } 
        catch (IOException e) 
        {
            errorLog.add("The specified path, " + file + ", does not refer to a valid file.");
            throw e;
        } 
        catch (InvalidMidiDataException e) 
        {
            errorLog.add("The specified file, " + file + ", is not a valid MIDI/MEI file.");
            throw e;
        }
        return sequence;
    }
    
    /**
     * Get a java sequence object from and MEI file.
     * @param file mei file to be processed
     * @param errorLog errors from mei file validation
     * @return sequence returns java sequence if valid mei file or else 
     *         logs error and throws exception
     * @throws InvalidMidiDataException Thrown if the input file does not contain valid MIDI.
     * @throws ca.mcgill.music.ddmal.mei.MeiXmlReader.MeiXmlReadException Thrown if the input file does not contain valid MEI.
     */
    private static Sequence getMEISequence(File file, List<String> errorLog)
            throws InvalidMidiDataException, MeiXmlReadException 
    {
        Sequence sequence = null;
        try 
        {
            MeiSequence meiSequence = new MeiSequence(file);
            sequence = meiSequence.getSequence();
        }
        catch (InvalidMidiDataException e) 
        {
            errorLog.add("The specified file, " + file + ", is not a valid MIDI/MEI file.");
            throw e;
        }
        return sequence;
    }

    /**
     * Checks if the fileName contains .fileExtensions at the end of the fileName.
     * Then returns appropriate fileName with extension.
     * @param fileName file name to be checked.
     * @param fileExtension file extension to be checked.
     * @return if fileName not valid then return fileName.fileExtension
     * otherwise return the fileName as is.
     */
    public static String correctFileExtension(String fileName, String fileExtension)
    {
        String dot = ".";
        String forcedExtension = fileName + dot + fileExtension;
        if(!fileName.contains(dot)) return forcedExtension;

        String[] splitName = fileName.split("\\.");
        if(splitName[splitName.length - 1].equalsIgnoreCase(fileExtension)) {
            return fileName;
        } else {
            return forcedExtension;
        }
    }

    /**
     * Given a file name or path, this will return whether the actual path
     * to this file exists.
     * @param fileName The file name that needs to be checked.
     * @return true if the path to this file exists or if the fileName is path
     * that does exist, otherwise returns false
     */
    public static boolean filePathExists(String fileName) {
        File test = new File(fileName);
        String absolutePath = test.getAbsolutePath();
        String path = absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));
        Path p = Paths.get(path);
        return Files.exists(p);
    }


	/**
      * Print out error log to a JOptionPane window.
      * @param errorLog List(String) to be printed to window.
      */
    public static void windowErrorLog(List<String> errorLog) 
    {
        if(errorLog != null && !errorLog.isEmpty())
        {
            JTextArea textArea = new JTextArea(Arrays.toString(errorLog.toArray(new String[0])));
            textArea.setEditable(false); //Selectable but not editable
            JOptionPane.showMessageDialog(null,
                                          textArea, 
                                          "INVALID FILES", 
                                          JOptionPane.ERROR_MESSAGE);
        }
    }
}
