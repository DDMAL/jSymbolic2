package jsymbolic2.processing;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import javax.sound.midi.*;

import jsymbolic2.api.deprecated.JsymbolicData;
import jsymbolic2.featureutils.MEIFeatureExtractor;
import mckay.utilities.sound.midi.MIDIMethods;
import ace.datatypes.FeatureDefinition;
import ca.mcgill.music.ddmal.mei.MeiXmlReader.MeiXmlReadException;

import java.util.List;
import jsymbolic2.featureutils.MIDIFeatureExtractor;
import org.ddmal.jmei2midi.MeiSequence;
import org.ddmal.jmei2midi.meielements.meispecific.MeiSpecificStorage;


/**
 * This class is used to pre-process and extract features from MIDI recordings.
 * An object of this class should be instantiated with parameters indicating
 * the details of how features are to be extracted.
 *
 * <p>The extractFeatures method should be called whenever recordings are
 * available to be analyzed. This method should be called once for each
 * recording. It will write the extracted feature values to an XML file after
 * each call. This will also save feature definitions to another XML file.
 *
 * <p>The finalize method should be called when all features have been
 * extracted. This will finish writing the feature values to the XML file.
 *
 * <p>Features are extracted for each window and, when appropriate, the average
 * and standard deviation of each of these features is extracted for each
 * recording.
 *
 * @author Cory McKay and Tristano Tenaglia
 */
public class MIDIFeatureProcessor
{
     /* FIELDS ****************************************************************/
     
     /**
      * The window size in seconds used for dividing up the recordings to
      * classify.
      */
     private   double			window_size;
     
     /**
      * The number of seconds that windows are offset by. A value of zero means
      * that there is no window overlap.
      */
     private   double			window_overlap_offset;
     
     /**
      * The features that are to be extracted (including dependencies of features to be saved, not just the
	  * features to be saved themselves).
      */
     private   MIDIFeatureExtractor[]   feature_extractors;
     
     /**
      * The dependencies of the features in the feature_extractors field.
      * The first indice corresponds to the feature_extractors indice
      * and the second identifies the number of the dependent feature.
      * The entry identifies the indice of the feature in feature_extractors
      * that corresponds to a dependant feature. The first dimension will be
      * null if there are no dependent features.
      */
     private   int[][]			feature_extractor_dependencies;
     
     /**
      * The longest number of windows of previous features that each feature
      * must have before it can be extracted. The indice corresponds to that of
      * feature_extractors.
      */
     private	int[]			max_feature_offsets;
     
     /**
      * Which features are to be saved after processing. Entries correspond to
      * the feature_extractors field.
      */
     private   boolean[]		features_to_save;
     
     /**
      * Whether or not to save features individually for each window
      */
     private   boolean			save_features_for_each_window;
     
     /**
      * Whether or not to save the average and standard deviation of each
      * feature across all windows.
      */
     private   boolean			save_overall_recording_features;
     
     /**
      * Used to write to the feature_vector_file file to save feature values to.
      */
     private   OutputStreamWriter         values_writer;
     
     /**
      * Used to write to the feature_key_file file to save feature definitions
      * to.
      */
     private   OutputStreamWriter         definitions_writer;

     private   File feature_values_save_file;

     private   File feature_definitions_save_file;

     /**
      * Indicates whether the feature definitions have been written by the
      * definitions_writer yet.
      */
     private	boolean			definitions_written;

     
     /* CONSTRUCTORS **********************************************************/
     
     
     /**
      * Validates and stores the configuration to use for extracting features
      * from MIDI recordings. Prepares the feature_vector_file and
      * feature_key_file XML files for saving.
      *
      * @param	window_size                       The size of the windows in
      *                                           seconds that the MIDI
      *                                           recordings are to be broken
      *                                           into.
      * @param	window_overlap                    The fraction of overlap
      *                                           between adjacent windows. Must
      *                                           be between 0.0 and less than
      *                                           1.0, with a value of 0.0
      *                                           meaning no overlap.
      * @param	all_feature_extractors            All features that can be
      *                                           extracted.
      * @param	features_to_save_among_all        Which features are to be
      *                                           saved. Entries correspond to
      *                                           the all_feature_extractors
      *                                           parameter.
      * @param	save_features_for_each_window     Whether or not to save
      *                                           features individually for each
      *                                           window.
      * @param	save_overall_recording_features   Whetehr or not to save the
      *                                           average and standard deviation
      *                                           of each feature accross all
      *                                           windows.
      * @param	feature_values_save_path          The path of the
      *                                           feature_vector_file XML file
      *                                           to save feature values to.
      * @param	feature_definitions_save_path     The path of the
      *                                           feature_key_file file to save
      *                                           feature definitions to.
      * @throws	Exception                         Throws an informative
      *                                           exception if the input
      *                                           parameters are invalid, including if any of the feature
	  *											  in all_feature_extractors have dependencies that do not
	  *											  exist in all_feature_extractors.
      */
     public MIDIFeatureProcessor( double window_size,
          double window_overlap,
          MIDIFeatureExtractor[] all_feature_extractors,
          boolean[] features_to_save_among_all,
          boolean save_features_for_each_window,
          boolean save_overall_recording_features,
          String feature_values_save_path,
          String feature_definitions_save_path )
          throws Exception
     {
		  // Throw an exception if the control parameters are invalid
          if (!save_features_for_each_window && !save_overall_recording_features)
               throw new Exception( "You must save at least one of the windows-based\n" +
                    "features and the overall file-based features if\n" +
                    "windows are to be used." );
          if (feature_values_save_path.equals(""))
               throw new Exception("No save path specified for feature values.");
          if (feature_definitions_save_path.equals(""))
               throw new Exception("No save path specified for feature definitions.");
          if (window_overlap < 0.0 || window_overlap >= 1.0)
               throw new Exception( "Window overlap fraction is " + window_overlap + ".\n" +
                    "This value must be 0.0 or above and less than 1.0.");
          if (window_size < 0.0)
               throw new Exception( "Window size is " + window_size + ".\n" +
                    "This value must be at or above 0.0 seconds." );
          boolean one_selected = false;
          for (int i = 0; i < features_to_save_among_all.length; i++)
               if (features_to_save_among_all[i])
                    one_selected = true;
          if (!one_selected)
               throw new Exception("No features have been set to be saved.");
		  
		  /* DEBUGGING: Print a list of all features and their dependencies to standard out
		  for (int feat = 0; feat < all_feature_extractors.length; feat++)
		  {
			  String[] this_feature_dependencies = all_feature_extractors[feat].getDepenedencies();
			  if (this_feature_dependencies != null)
				for (int dep = 0; dep < this_feature_dependencies.length; dep++)
					System.out.println("- " + (feat + 1) + " " + all_feature_extractors[feat].getFeatureCode() + " " + all_feature_extractors[feat].getFeatureDefinition().name + " -> " + this_feature_dependencies[dep]);
		  }*/
		  
		  // Verify that feature names referred to by all dependencies actually exist.
		  for (int feat = 0; feat < all_feature_extractors.length; feat++)
		  {
			  String[] this_feature_dependencies = all_feature_extractors[feat].getDepenedencies();
			  if (this_feature_dependencies != null)
			  {
				boolean found_dependency = false;
				for (int dep = 0; dep < this_feature_dependencies.length; dep++)
				{
					for (int i = 0; i < all_feature_extractors.length; i++)
					{
						if (this_feature_dependencies[dep].equals(all_feature_extractors[i].getFeatureDefinition().name))
						{
							found_dependency = true;
							break;
						}
					}
					if (!found_dependency)
						throw new Exception("The " + all_feature_extractors[feat].getFeatureDefinition().name + " feature needs the " + this_feature_dependencies[dep] + " feature in order to be calculated, yet no feature with the latter name could be found.");
				}
			  }
		  }

          // Prepare the files for writing
          feature_values_save_file = new File(feature_values_save_path);
          feature_definitions_save_file = new File(feature_definitions_save_path);
          
          // Throw an exception if the given file paths are not writable. Involves
          // creating a blank file if one does not already exist.
          if (feature_values_save_file.exists())
               if (!feature_values_save_file.canWrite())
                    throw new Exception("Cannot write to " + feature_values_save_path + ".");
          if (feature_definitions_save_file.exists())
               if (!feature_definitions_save_file.canWrite())
                    throw new Exception("Cannot write to " + feature_definitions_save_path + ".");
          if (!feature_values_save_file.exists())
               feature_values_save_file.createNewFile();
          if (!feature_definitions_save_file.exists())
               feature_definitions_save_file.createNewFile();
          
          // Prepare stream writers
          FileOutputStream values_to = new FileOutputStream(feature_values_save_file);
          FileOutputStream definitions_to = new FileOutputStream(feature_definitions_save_file);
          values_writer = new OutputStreamWriter(values_to, StandardCharsets.UTF_8);
          definitions_writer = new OutputStreamWriter(definitions_to, StandardCharsets.UTF_8);
          definitions_written = false;
          
          // Save parameters as fields
          this.window_size = window_size;
          this.save_features_for_each_window = save_features_for_each_window;
          this.save_overall_recording_features = save_overall_recording_features;
          
          // Calculate the window offset
          window_overlap_offset = window_overlap * window_size;
          
          // Find which features need to be extracted and in what order. Also find
          // the indices of dependencies and the maximum offsets for each feature.
          findAndOrderFeaturesToExtract(all_feature_extractors, features_to_save_among_all);
          
          // Write the headers of the feature_vector_file
          writeValuesXMLHeader();
     }
     
     
     /* PUBLIC METHODS ********************************************************/

	/**
	 * @return	The features that are to be extracted (including dependencies of features to be saved, not 
	 *			just the features to be saved themselves).
	 */
	public MIDIFeatureExtractor[] getFinalFeaturesToBeExtracted()
	{
		return feature_extractors;
	}

     /**
      * Extract the features from the provided MIDI or MEI file. This may involve
      * windowing, depending on the instantiation parameters of this object. The
      * feature values are automatically saved to the feature_vector_file XML
      * file referred to by the values_writer field. The definitions of the
      * features that are saved are also saved to the feature_key_file XML file
      * referred to by the definitions_writer field.
      *
      * @param recording_file	The music file to extract features from.
      * @param errorLog  A List(String) that holds all the files with errors.
      * @throws InvalidMidiDataException Thrown if the MIDI data is invalid.
      * @throws IOException Thrown if there is a problem reading from the inputted file.
      * @throws MeiXmlReadException Thrown if there is a problem reading in the MEI XML from the inputted file.
      * @throws Exception When an unforeseen runtime exception occurs.
      */
     public void extractFeatures(File recording_file, List<String> errorLog) 
             throws InvalidMidiDataException, MeiXmlReadException, IOException, Exception
     {
         if(window_overlap_offset > window_size) 
             throw new Exception("Window overlap offset is greater than window size, this is not possible.");
         
          // Extract the data from the file and check for exceptions
          Sequence full_sequence = null;
          MeiSequence mei_sequence = null;
		  if(SymbolicMusicFileUtilities.isValidMidiFile(recording_file))
             full_sequence = SymbolicMusicFileUtilities.getMidiSequenceFromMidiOrMeiFile(recording_file,errorLog);
		  else if(SymbolicMusicFileUtilities.isValidMeiFile(recording_file))
		  {
               mei_sequence = SymbolicMusicFileUtilities.getMeiSequenceFromMeiFile(recording_file,errorLog);
               full_sequence = mei_sequence.getSequence();
          }
  
         /**
          * Mei Specific Storage added here
          * null is set if the file is not an mei file
          **/
          MeiSpecificStorage meiSpecificStorage = null;
          if(mei_sequence != null) {
               meiSpecificStorage = mei_sequence.getNonMidiStorage();
          }
         /**
          *
          */

          // Prepare the windows for feature extraction with correct times
          // Tick arrays have been added to account for multiple windows
          Sequence[] windows;
          double[] seconds_per_tick = MIDIMethods.getSecondsPerTick(full_sequence);
          List<int[]> startEndTickArrays;
          int[] start_ticks;
          int[] end_ticks;
          try
          {
              if (!save_features_for_each_window)
               {
                  startEndTickArrays = MIDIMethods.getStartEndTickArrays(full_sequence, 
                                                                           full_sequence.getMicrosecondLength() / 1000000.0, 
                                                                           0.0,
                                                                           seconds_per_tick);
                   start_ticks = startEndTickArrays.get(0);
                   end_ticks = startEndTickArrays.get(1);
                   windows = new Sequence[1];
                   windows[0] = full_sequence;
               }
               else
               {
                    startEndTickArrays = MIDIMethods.getStartEndTickArrays(full_sequence, 
                                                                           window_size, 
                                                                           window_overlap_offset,
                                                                           seconds_per_tick);
                    start_ticks = startEndTickArrays.get(0);
                    end_ticks = startEndTickArrays.get(1);
                    windows = MIDIMethods.breakSequenceIntoWindows( full_sequence,
                         window_size,
                         window_overlap_offset,
                         start_ticks,
                         end_ticks);
               }
          }
          catch (RuntimeException e)
          {
               throw new Exception("An error occured while processing the following file: " + recording_file + ".\n");
          }
          
          // Extract the feature values from the samples
         double[][][] window_feature_values = getFeatures(windows, meiSpecificStorage);
          
          // Find the feature averages and standard deviations if appropriate
          FeatureDefinition[][] overall_feature_definitions = new FeatureDefinition[1][];
          overall_feature_definitions[0] = null;
          double[][] overall_feature_values = null;
          if (save_overall_recording_features)
               overall_feature_values = getOverallRecordingFeatures( window_feature_values,
                    overall_feature_definitions );
          
          // Save the feature values for this recording
          saveFeatureVectorsForARecording( window_feature_values,
               recording_file.getPath(),
               overall_feature_values,
               overall_feature_definitions[0],
               full_sequence,
               windows,
               start_ticks,
               end_ticks,
               seconds_per_tick);
          
          // Save the feature definitions
          if (!definitions_written)
               saveFeatureDefinitions(window_feature_values, overall_feature_definitions[0]);
     }

    /**
     * Extract and return all possible data and features the jSymbolic processes.
     * @param recording_file  The music file to extract features from.
     * @param errorLog  A List(String) that holds all the files with errors.
	 * @param error_print_stream	A stream to print error messages to if necessary.
     * @return An object that contains all the appropriate jSymbolicData.
     * @throws InvalidMidiDataException Thrown if the MIDI data is invalid.
     * @throws IOException Thrown if there is a problem reading from the inputted file.
     * @throws MeiXmlReadException Thrown if there is a problem reading in the MEI XML from the inputted file.
     * @throws Exception When an unforeseen runtime exception occurs.
     */
     public JsymbolicData extractAndReturnFeatures(File recording_file, List<String> errorLog, PrintStream error_print_stream)
             throws InvalidMidiDataException, MeiXmlReadException, IOException, Exception
     {
          if(window_overlap_offset > window_size)
               throw new Exception("Window overlap offset is greater than window size, this is not possible.");

          // Extract the data from the file and check for exceptions
          Sequence full_sequence = null;
          MeiSequence mei_sequence = null;
		  if(SymbolicMusicFileUtilities.isValidMidiFile(recording_file))
			full_sequence = SymbolicMusicFileUtilities.getMidiSequenceFromMidiOrMeiFile(recording_file,errorLog);
		  else if(SymbolicMusicFileUtilities.isValidMeiFile(recording_file))
		  {
               mei_sequence = SymbolicMusicFileUtilities.getMeiSequenceFromMeiFile(recording_file,errorLog);
               full_sequence = mei_sequence.getSequence();
          }

          /**
           * Mei Specific Storage added here
           * null is set if the file is not an mei file
           **/
          MeiSpecificStorage meiSpecificStorage = null;
          if(mei_sequence != null) {
               meiSpecificStorage = mei_sequence.getNonMidiStorage();
          }
          /**
           *
           */

          // Prepare the windows for feature extraction with correct times
          // Tick arrays have been added to account for multiple windows
          Sequence[] windows;
          double[] seconds_per_tick = MIDIMethods.getSecondsPerTick(full_sequence);
          List<int[]> startEndTickArrays;
          int[] start_ticks;
          int[] end_ticks;
          try
          {
               if (!save_features_for_each_window)
               {
                    startEndTickArrays = MIDIMethods.getStartEndTickArrays(full_sequence,
                            full_sequence.getMicrosecondLength() / 1000000.0,
                            0.0,
                            seconds_per_tick);
                    start_ticks = startEndTickArrays.get(0);
                    end_ticks = startEndTickArrays.get(1);
                    windows = new Sequence[1];
                    windows[0] = full_sequence;
               }
               else
               {
                    startEndTickArrays = MIDIMethods.getStartEndTickArrays(full_sequence,
                            window_size,
                            window_overlap_offset,
                            seconds_per_tick);
                    start_ticks = startEndTickArrays.get(0);
                    end_ticks = startEndTickArrays.get(1);
                    windows = MIDIMethods.breakSequenceIntoWindows( full_sequence,
                            window_size,
                            window_overlap_offset,
                            start_ticks,
                            end_ticks);
               }
          }
          catch (RuntimeException e)
          {
               throw new Exception("An error occured while processing the following file: " + recording_file + ".\n");
          }

          // Extract the feature values from the samples
          double[][][] window_feature_values = getFeatures(windows, meiSpecificStorage);

          // Find the feature averages and standard deviations if appropriate
          FeatureDefinition[][] overall_feature_definitions = new FeatureDefinition[1][];
          overall_feature_definitions[0] = null;
          double[][] overall_feature_values = null;
          if (save_overall_recording_features)
               overall_feature_values = getOverallRecordingFeatures( window_feature_values,
                       overall_feature_definitions );

          // Save the feature values for this recording
          saveFeatureVectorsForARecording( window_feature_values,
                  recording_file.getPath(),
                  overall_feature_values,
                  overall_feature_definitions[0],
                  full_sequence,
                  windows,
                  start_ticks,
                  end_ticks,
                  seconds_per_tick);

          // Write ending tags for
          finalizeFeatureValuesFile();

          // Save the feature definitions
          if (!definitions_written)
               saveFeatureDefinitions(window_feature_values, overall_feature_definitions[0]);

          return new JsymbolicData(meiSpecificStorage, feature_values_save_file, feature_definitions_save_file, null, null, error_print_stream);
     }
     
     
     /**
      * Write the ending tags to the feature_vector_file XML file.
      * Close the DataOutputStreams that were used to write it.
      *
      * <p>This method should be called when all features have been extracted.
      *
      * @throws	Exception	Throws an exception if cannot write or close the
      *						output streams properly. Not thrown if stream is
      *					    already closed.
      */
     public void finalizeFeatureValuesFile()
     throws Exception
     {
          try {
              values_writer.write("</feature_vector_file>");
              values_writer.close();
          }
          catch (IOException e) {
              //Squelch the already closed stream since its already closed
              //since no other errors will occur
              if(!e.getMessage().equals("Stream Closed")) {
                  throw e;
              }
          }
     }
     
     
     /* PRIVATE METHODS *******************************************************/
     
     
     /**
      * Fills the feature_extractors, feature_extractor_dependencies,
      * max_feature_offsets and features_to_save fields. This involves finding
      * which features need to be extracted and in what order and finding
      * the indices of dependencies and the maximum offsets for each feature.
      *
      * @param	all_feature_extractors       All features that can be extracted.
      * @param	features_to_save_among_all   Which features are to be saved.
      *                                      Entries correspond to the
      *                                      all_feature_extractors parameter.
      */
     private void findAndOrderFeaturesToExtract( MIDIFeatureExtractor[] all_feature_extractors,
          boolean[] features_to_save_among_all )
     {
          // Find the names of all features
          String[] all_feature_names = new String[all_feature_extractors.length];
          for (int feat = 0; feat < all_feature_extractors.length; feat++)
               all_feature_names[feat] = all_feature_extractors[feat].getFeatureDefinition().name;

          // Find the dependencies of each feature marked to be extracted.
          // Mark an entry as null if that entry's matching feature is not set to be extracted.
		  // Note that an entry will also be null if the corresponding feature has no dependencies.
          String[][] dependencies = new String[all_feature_extractors.length][];
          for (int feat = 0; feat < all_feature_extractors.length; feat++)
          {
               if (features_to_save_among_all[feat])
                    dependencies[feat] = all_feature_extractors[feat].getDepenedencies();
               else
                    dependencies[feat] = null;
          }
          
		  // Start off the array of which features to extract by making sure to extract all those features
		  // whose values are marked to be saved.
          boolean[] features_to_extract_including_dependencies = new boolean[all_feature_extractors.length];
          for (int feat = 0; feat < all_feature_extractors.length; feat++)
			  features_to_extract_including_dependencies[feat] = features_to_save_among_all[feat];

		  // Update features_to_extract_including_dependencies to ALSO include those features that are not
		  // marked to be saved, but are needed as dependencies in order to calculate features that are 
		  // marked to be saved. Also update dependencies to include any new dependencies that are introduced
		  // by scheduling new features to be extracted because they themselves are dependencies of other 
		  // features.
          boolean done = false;
          while (!done)
          {
               done = true;
               for (int feat = 0; feat < all_feature_extractors.length; feat++)
			   {
                    if (dependencies[feat] != null)
					{
						for (int dep = 0; dep < dependencies[feat].length; dep++)
                        {
							 String this_depency_name = dependencies[feat][dep];
							for (int j = 0; j < all_feature_extractors.length; j++)
							{
								 if (this_depency_name.equals(all_feature_names[j]))
								 {
									  if (!features_to_extract_including_dependencies[j])
									  {
										   features_to_extract_including_dependencies[j] = true;
										   dependencies[j] = all_feature_extractors[j].getDepenedencies();
										   if (dependencies[j] != null)
												done = false;
									  }
									  j = all_feature_extractors.length;
								 }
							}
                        }
					}
			   }	
           } 
         
		 // Begin the process of finding the correct order to extract features in by filling the
		 // feature_extractors field with all features that are to be extracted (i.e. the combination of
		 // those features whose values are marked to be saved and those features that are needed in order
		 // to calculate those features marked to be saved). The ordering consists of the originally
		 // specified feature order, with dependent features added in before they are needed.
		 // Also note which of these have values that are actually to be saved by filling in
		 // features_to_save.
		 int number_features_to_extract = 0;
		 for (int i = 0; i < features_to_extract_including_dependencies.length; i++)
			 if (features_to_extract_including_dependencies[i])
				 number_features_to_extract++;
		 feature_extractors = new MIDIFeatureExtractor[number_features_to_extract];
		 features_to_save = new boolean[number_features_to_extract];
		 for (int i = 0; i < features_to_save.length; i++)
			 features_to_save[i] = false;
		 boolean[] feature_added = new boolean[all_feature_extractors.length];
		 for (int i = 0; i < feature_added.length; i++)
			 feature_added[i] = false;
		 int current_position = 0;
		 done = false;
		 while (!done)
		 {
			 done = true;

			 // Add all features that have no remaining dependencies and remove
			 // their dependencies from all unadded features
			 for (int feat = 0; feat < all_feature_extractors.length; feat++)
			 {
				 if (features_to_extract_including_dependencies[feat] && !feature_added[feat])
				 {
					 if (dependencies[feat] == null) // add feature if it has no dependencies
					 {
						 feature_added[feat] = true;
						 feature_extractors[current_position] = all_feature_extractors[feat];
						 features_to_save[current_position] = features_to_save_among_all[feat];
						 current_position++;
						 done = false;

						 // Remove this dependency from all features that have
						 // it as a dependency and are marked to be extracted
						 for (int i = 0; i < all_feature_extractors.length; i++)
						 {
							 if (features_to_extract_including_dependencies[i] && dependencies[i] != null)
							 {
								 int num_defs = dependencies[i].length;
								 for (int j = 0; j < num_defs; j++)
								 {
									 if (dependencies[i][j].equals(all_feature_names[feat]))
									 {
										 if (dependencies[i].length == 1)
										 {
											 dependencies[i] = null;
											 j = num_defs;
										 }
										 else
										 {
											 String[] temp = new String[dependencies[i].length - 1];
											 int m = 0;
											 for (int k = 0; k < dependencies[i].length; k++)
											 {
												 if (k != j)
												 {
													 temp[m] = dependencies[i][k];
													 m++;
												 }
											 }
											 dependencies[i] = temp;
											 j--;
											 num_defs--;
										 }
									 }
								 }
							 }
						 }
					 }
				 }
			 }
		 }

          // Find the indices of the feature extractor dependencies for each feature
          // extractor
          feature_extractor_dependencies = new int[feature_extractors.length][];
		  String[] feature_names = new String[feature_extractors.length];
          for (int feat = 0; feat < feature_names.length; feat++)
               feature_names[feat] = feature_extractors[feat].getFeatureDefinition().name;
          String[][] feature_dependencies_str = new String[feature_extractors.length][];
          for (int feat = 0; feat < feature_dependencies_str.length; feat++)
               feature_dependencies_str[feat] = feature_extractors[feat].getDepenedencies();
          for (int i = 0; i < feature_dependencies_str.length; i++)
               if (feature_dependencies_str[i] != null)
               {
               feature_extractor_dependencies[i] = new int[feature_dependencies_str[i].length];
               for (int j = 0; j < feature_dependencies_str[i].length; j++)
                    for (int k = 0; k < feature_names.length; k++)
                         if (feature_dependencies_str[i][j].equals(feature_names[k]))
                              feature_extractor_dependencies[i][j] = k;
               }
          
          // Find the maximum offset for each feature
          max_feature_offsets = new int[feature_extractors.length];
          for (int i = 0; i < max_feature_offsets.length; i++)
          {
               if (feature_extractors[i].getDepenedencyOffsets() == null)
                    max_feature_offsets[i] = 0;
               else
               {
                    int[] these_offsets = feature_extractors[i].getDepenedencyOffsets();
                    max_feature_offsets[i] = Math.abs(these_offsets[0]);
                    for (int k = 0; k < these_offsets.length; k++)
                         if (Math.abs(these_offsets[k]) > max_feature_offsets[i])
                              max_feature_offsets[i] = Math.abs(these_offsets[k]);
               }
          }
		  
		  // DEBUGGING: Print all features set to be extracted in the order they are set to be extracted
		  //for (int i = 0; i < feature_extractors.length; i++)
		  //	System.out.println(feature_extractors[i].getFeatureCode() + " " + feature_extractors[i].getFeatureDefinition().name);
     }
     
     
     /**
      * Extracts features from each window of the given MIDI sequences. If the
      * passed windows parameter consists of only one window, then this could
      * be a whole unwindowed MIDI file.
      *
      * @param	windows       The ordered MIDI windows to extract features from.
      * @param meiSpecificStorage The mei specific data storage used to extract
      *                           mei specific features from the sequence windows.
      *                           This will be null if the file is not an mei file
      *                           and otherwise it will contain all mei specific data
      *                           extracted by jMei2Midi.
      * @return               The extracted feature values for this recording.
      *                       The first indice identifies the window, the second
      *                       identifies the feature and the third identifies
      *                       the feature value. The third dimension will be
      *                       null if the given feature could not be extracted
      *                       for the given window.
      * @throws	Exception     Throws an exception if a problem occurs.
      */
     public double[][][] getFeatures(Sequence[] windows, MeiSpecificStorage meiSpecificStorage)
     throws Exception
     {
          // The extracted feature values for this recording. The first indice
          // identifies the window, the second identifies the feature and the
          // third identifies the feature value.
          double[][][] results = new double[windows.length][feature_extractors.length][];
          
          // Extract features from each window one by one and add save the results.
          // The last window is zero-padded at the end if it falls off the edge of the
          // provided samples.
          for (int win = 0; win < windows.length; win++)
          {
               // Extract information from sequence that is needed to extract features
              MIDIIntermediateRepresentations intermediate = new MIDIIntermediateRepresentations(windows[win]);
               
               // Extract the features one by one
               for (int feat = 0; feat < feature_extractors.length; feat++)
               {
                    // Only extract this feature if enough previous information
                    // is available to extract this feature
                    if (win >= max_feature_offsets[feat])
                    {
                         // Find the correct feature
                         MIDIFeatureExtractor feature = feature_extractors[feat];
                         
                         // Find previously extracted feature values that this feature
                         // needs
                         double[][] other_feature_values = null;
                        if (feature_extractor_dependencies[feat] != null)
                         {
                              other_feature_values = new double[feature_extractor_dependencies[feat].length][];
                              for (int i = 0; i < feature_extractor_dependencies[feat].length; i++)
                              {
                                   int feature_indice = feature_extractor_dependencies[feat][i];
                                   //TODO Check if this is a correct bug fix
                                   if(feature.getDepenedencyOffsets() == null) {
                                        other_feature_values[i] = results[win][feature_indice];
                                   } else {
                                        int offset = feature.getDepenedencyOffsets()[i];
                                        other_feature_values[i] = results[win + offset][feature_indice];
                                   }
                              }
                         }

                         //Check here if the file is an MEI file and if the feature is an MEI feature
                         //Otherwise just extract the midi feature data
                         if(meiSpecificStorage != null &&
                                 feature instanceof MEIFeatureExtractor) {
                              results[win][feat] = ((MEIFeatureExtractor) feature).extractMEIFeature(
                                      meiSpecificStorage,
                                      windows[win],
                                      intermediate,
                                      other_feature_values);
                         } else if(meiSpecificStorage == null &&
                                   feature instanceof MEIFeatureExtractor) {
                              //Skip if this is a non-mei file as mei features are not valid
                              continue;
                         } else {
                              // Store the extracted feature values
                              results[win][feat] = feature.extractFeature(windows[win],
                                      intermediate,
                                      other_feature_values);
                         }
                    }
                    else
                         results[win][feat] = null;
               }
          }
          
          // Return the results
         return results;
     }
     
     
     /**
      * Calculates the averages and standard deviations over a whole recording
      * of each of the windows-based features. Generates a feature definition
	  * for each such feature. If only one value is present (dep.e. only one
	  * window) then this value is stored without any standard deviation.
      *
      * @param	window_feature_values        The extracted window feature values
      *                                      for this recording. The first
      *                                      indice identifies the window, the
      *                                      second identifies the feature and
      *                                      the third identifies the feature
      *                                      value. The third dimension will
      *                                      be null if the given feature could
      *                                      not be extracted for the given
      *                                      window.
      * @param	overall_feature_definitions  The feature definitions of the
      *                                      features that are returned by this
      *                                      method. This array will be filled
      *                                      by this method, and should be an
      *                                      empty FeatureDefintion[1][] when it
      *                                      is passed to this method. The first
      *                                      indice will be filled by this
      *                                      method with a single array of
      *                                      FeatureDefinitions, which have the
      *                                      same order as the returned feature
      *                                      values.
      * @return                              The extracted overall average and
      *                                      standard deviations of the window
      *                                      feature values that were passed to
      *                                      this method. The first indice
      *                                      identifies the feature and the
      *                                      second iddentifies the feature
      *                                      value. The order of the features
      *                                      correspond to the
      *                                      FeatureDefinitions that the
      *                                      overall_feature_definitions
      *                                      parameter is filled with.
      */
     private double[][] getOverallRecordingFeatures( double[][][] window_feature_values,
          FeatureDefinition[][] overall_feature_definitions )
     {
          LinkedList<double[]> values = new LinkedList<double[]>();
          LinkedList<FeatureDefinition> definitions = new LinkedList<FeatureDefinition>();
          
          for (int feat = 0; feat < feature_extractors.length; feat++)
               if ( window_feature_values[window_feature_values.length - 1][feat] != null &&
               features_to_save[feat] )
               {
               if (window_feature_values.length == 1)
               {
                    definitions.add(feature_extractors[feat].getFeatureDefinition());
                    values.add(window_feature_values[0][feat]);
               }
               else
               {
                    // Make the definitions
                    FeatureDefinition this_def = feature_extractors[feat].getFeatureDefinition();
                    FeatureDefinition average_definition = new FeatureDefinition( this_def.name + " Overall Average",
                         this_def.description + "\nThis is the overall average over all windows.",
                         this_def.is_sequential,
                         window_feature_values[window_feature_values.length - 1][feat].length );
                    FeatureDefinition stdv_definition = new FeatureDefinition( this_def.name + " Overall Standard Deviation",
                         this_def.description + "\nThis is the overall standard deviation over all windows.",
                         this_def.is_sequential,
                         window_feature_values[window_feature_values.length - 1][feat].length );
                    
                    // Find the averages and standard deviations
                    double[] averages = new double[window_feature_values[window_feature_values.length - 1][feat].length];
                    double[] stdvs = new double[window_feature_values[window_feature_values.length - 1][feat].length];
                    for (int val = 0; val < window_feature_values[window_feature_values.length - 1][feat].length; val++)
                    {
                         // Find the number of windows that have values for this value feature
                         int count = 0;
                         for (int win = 0; win < window_feature_values.length; win++)
                              if (window_feature_values[win][feat] != null)
                                   count++;
                         
                         // Find the values to find the average and standard deviations of
                         double[] values_to_process = new double[count];
                         int current = 0;
                         for (int win = 0; win < window_feature_values.length; win++)
                              if (window_feature_values[win][feat] != null)
                              {
                              values_to_process[current] = window_feature_values[win][feat][val];
                              current++;
                              }
                         
                         // Calculate the averages and standard deviations
                         averages[val] = mckay.utilities.staticlibraries.MathAndStatsMethods.getAverage(values_to_process);
                         stdvs[val] = mckay.utilities.staticlibraries.MathAndStatsMethods.getStandardDeviation(values_to_process);
                    }
                    
                    // Store the results
                    values.add(averages);
                    definitions.add(average_definition);
                    values.add(stdvs);
                    definitions.add(stdv_definition);
               }
               }
          
          // Finalize the values
          overall_feature_definitions[0] = definitions.toArray(new FeatureDefinition[1]);
          return values.toArray(new double[1][]);
     }
     
     
     /**
      * Writes the headers, consisting mainly of the DTD, to the
      * feature_vector_file..
      *
      * @throws	Exception	Throws an exception if cannot write.
      */
     private void writeValuesXMLHeader()
     throws Exception
     {
          String feature_vector_header = new String
               (
               "<?xml version=\"1.0\"?>\n" +
               "<!DOCTYPE feature_vector_file [\n" +
               "   <!ELEMENT feature_vector_file (comments, data_set+)>\n" +
               "   <!ELEMENT comments (#PCDATA)>\n" +
               "   <!ELEMENT data_set (data_set_id, section*, feature*)>\n" +
               "   <!ELEMENT data_set_id (#PCDATA)>\n" +
               "   <!ELEMENT section (feature+)>\n" +
               "   <!ATTLIST section start CDATA \"\"\n" +
               "                     stop CDATA \"\">\n" +
               "   <!ELEMENT feature (name, v+)>\n" +
               "   <!ELEMENT name (#PCDATA)>\n" +
               "   <!ELEMENT v (#PCDATA)>\n" +
               "]>\n\n" +
               "<feature_vector_file>\n\n" +
               "   <comments></comments>\n\n"
               );
          values_writer.write(feature_vector_header);
     }
     
     
     /**
      * Writes the given feature values extracted from a recording to the
      * feature_vector_file XML file referred to by the values_writer field.
      * Writes both the individual window features and the overall recording
      * features to disk.
      *
      * @param	feature_values               The extracted feature values for
      *                                      this recording. The first indice
      *                                      identifies the window, the second
      *                                      identifies the feature and the
      *                                      third identifies the feature value.
      *                                      The third dimension will be null if
      *                                      the given feature could not be
      *                                      extracted for the given window.
      * @param	identifier                   A string to use for identifying
      *                                      this recording. Often a file path.
      * @param	overall_feature_values       The extracted overall average and
      *                                      standard deviations of the window
      *                                      feature values. The first indice
      *                                      identifies the feature and the
      *                                      second identifies the feature
      *                                      value. The order of the features
      *                                      correspond to the
      *                                      overall_feature_definitions
      *                                      parameter. This value is null if
      *                                      overall feature values were not
      *                                      extracted.
      * @param	overall_feature_definitions  The feature definitions of the
      *                                      features that are in the
      *                                      overall_feature_values parameter.
      *                                      Will be null if no overall features
      *                                      were extracted.
      * @param original_Sequence             The original sequence that contains the MIDI data.
      * @param windows                       The original sequence split up into timed MIDI windows.
      * @param start_ticks                   The start ticks that correspond to each MIDI window.
      * @param end_ticks                     The end ticks that correspond to each MIDI window.
      * @param seconds_per_tick              The number of seconds in a MIDI tick given by the sequence.
      * @throws	Exception                    Throws an exception if cannot
      *                                      write.
      */
     private void saveFeatureVectorsForARecording( double[][][] feature_values,
          String identifier,
          double[][] overall_feature_values,
          FeatureDefinition[] overall_feature_definitions,
          Sequence original_Sequence,
          Sequence[] windows,
          int[] start_ticks,
          int[] end_ticks,
          double[] seconds_per_tick)
          throws Exception
     {
          // Start the entry for the recording
          values_writer.write("\t<data_set>\n");
          values_writer.write("\t\t<data_set_id>" + identifier + "</data_set_id>\n");
          
          // Write the features for individual windows
          if (save_features_for_each_window) {
              //could change to win < windows.length
               for (int win = 0; win < feature_values.length; win++)
               {
               //Compute start and end times
               int start_tick = start_ticks[win];
               int end_tick = end_ticks[win];
               double start_time = MIDIMethods.getSecondsAtTick(start_tick, seconds_per_tick);
               start_time = (start_time > 0) ? start_time : 0; //check for non negative
               double end_time = MIDIMethods.getSecondsAtTick(end_tick, seconds_per_tick);
               
               values_writer.write( "\t\t<section start=\"" + start_time +
                    "\" stop=\"" + end_time + "\">\n");
               for (int feat = 0; feat < feature_values[win].length; feat++)
               {
                    if (features_to_save[feat])
                         if (feature_values[win][feat] != null)
                         {
                         String feature_name = feature_extractors[feat].getFeatureDefinition().name;
                         values_writer.write("\t\t\t<feature>\n");
                         values_writer.write("\t\t\t\t<name>" + feature_name + "</name>\n");
                         for (int val = 0; val < feature_values[win][feat].length; val++)
                         {
                              String value = mckay.utilities.staticlibraries.StringMethods.getDoubleInScientificNotation(feature_values[win][feat][val], 4);
                              values_writer.write("\t\t\t\t<v>" + value + "</v>\n");
                         }
                         values_writer.write("\t\t\t</feature>\n");
                         }
               }
               values_writer.write("\t\t</section>\n");
               }
          }
          
          // Write the features for the file
          if (overall_feature_values != null)
               for (int feat = 0; feat < overall_feature_values.length; feat++)
               {
               values_writer.write("\t\t<feature>\n");
               values_writer.write("\t\t\t<name>" + overall_feature_definitions[feat].name + "</name>\n");
               for (int val = 0; val < overall_feature_values[feat].length; val++)
               {
                    String value = mckay.utilities.staticlibraries.StringMethods.getDoubleInScientificNotation(overall_feature_values[feat][val], 4);
                    values_writer.write("\t\t\t<v>" + value + "</v>\n");
               }
               values_writer.write("\t\t</feature>\n");
               }
          
          // End the entry for the recording
          values_writer.write("\t</data_set>\n\n");
     }
     
     
     /**
      * Writes feature definitions to the XML file referred to by the
      * definitions_writer field. Writes both overall and individual feature
      * definitions.
      *
      * @param	feature_values               The extracted feature values for a
      *                                      recording. The first indice
      *                                      identifies the window, the second
      *                                      identifies the feature and the
      *                                      third identifies the feature value.
      * @param	overall_feature_definitions  The feature definitions of the
      *                                      features that are in the features
      *                                      for the recording. Will be null if
      *                                      no overallfeatures were extracted.
      * @throws	Exception                    Throws an exception if cannot
      *                                      write.
      */
     private void saveFeatureDefinitions( double[][][] feature_values,
          FeatureDefinition[] overall_feature_definitions )
          throws Exception
     {
          String feature_key_header = new String
               (
               "<?xml version=\"1.0\"?>\n" +
               "<!DOCTYPE feature_key_file [\n" +
               "   <!ELEMENT feature_key_file (comments, feature+)>\n" +
               "   <!ELEMENT comments (#PCDATA)>\n" +
               "   <!ELEMENT feature (name, description?, is_sequential, parallel_dimensions)>\n" +
               "   <!ELEMENT name (#PCDATA)>\n" +
               "   <!ELEMENT description (#PCDATA)>\n" +
               "   <!ELEMENT is_sequential (#PCDATA)>\n" +
               "   <!ELEMENT parallel_dimensions (#PCDATA)>\n" +
               "]>\n\n" +
               "<feature_key_file>\n\n" +
               "   <comments></comments>\n\n"
               );
          definitions_writer.write(feature_key_header);
          
          double[][] last_window_features = feature_values[feature_values.length - 1];
          
          // Write the window functions
          if (save_features_for_each_window)
               for (int feat = 0; feat < feature_extractors.length; feat++)
                    if (features_to_save[feat])
                         if (last_window_features[feat] != null)
                         {
               FeatureDefinition def = feature_extractors[feat].getFeatureDefinition();
               definitions_writer.write("   <feature>\n");
               definitions_writer.write("      <name>" + def.name + "</name>\n");
               definitions_writer.write("      <description>" + def.description + "</description>\n");
               definitions_writer.write("      <is_sequential>" + def.is_sequential + "</is_sequential>\n");
               definitions_writer.write("      <parallel_dimensions>" + last_window_features[feat].length + "</parallel_dimensions>\n");
               definitions_writer.write("   </feature>\n\n");
                         }
          
          // Write the overall file functions
          if (overall_feature_definitions != null)
               for (int feat = 0; feat < overall_feature_definitions.length; feat++)
               {
               FeatureDefinition def = overall_feature_definitions[feat];
               definitions_writer.write("   <feature>\n");
               definitions_writer.write("      <name>" + def.name + "</name>\n");
               definitions_writer.write("      <description>" + def.description + "</description>\n");
               definitions_writer.write("      <is_sequential>" + def.is_sequential + "</is_sequential>\n");
               definitions_writer.write("      <parallel_dimensions>" + def.dimensions + "</parallel_dimensions>\n");
               definitions_writer.write("   </feature>\n\n");
               }
          
          definitions_writer.write("</feature_key_file>");
          
          definitions_writer.close();
          
          definitions_written = true;
     }
}
