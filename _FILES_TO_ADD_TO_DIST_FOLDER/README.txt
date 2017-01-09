==========================================================================
 jSymbolic 2.0
 by Cory McKay
 Copyright (C) 2017 (GNU GPL)
==========================================================================


-- OVERVIEW --

jSymbolic is an open source MIDI feature extraction system intended for use
with automatic classification systems. This is a prototype version of the
software only.

jSymbolic was developed as part of the jMIR music classification research
software suite, and may be used either as part of this suite or
independently. More information on jMIR is available at
http://jmir.sourceforge.net.

Please Cory McKay (cory.mckay@mail.mcgill.ca) with any bug reports or
questions relating to the software. 


-- COMPATIBILITY --

The jSymbolic software is written in Java, which means that it can
theoretically be run on any system that has the Java Runtime 
Environment (JRE) installed on it. Although the software should
theoretically run under earlier versions of Windows, OS X, Linux, 
Solaris or any other operating system with the JRE installed on it,
users should be advised that the software has not yet been tested on 
other platforms, so difficulties may be encountered.

This software was developed with version 1.8 of the JDK (Java 
Development Kit), so it is suggested that the corresponding version 
or higher of the JRE be installed on the user's computer.


-- INSTALLING THE JAVA RUNTIME ENVIRONMENT --

If your system already has the JRE installed, you may skip this 
section. If not, you will need to install it in order to run jSymbolic.
The JRE can be downloaded for free from the java.sun.com web site. The
JDK includes the JRE.

When the download is complete, follow the installation instructions 
that come with it in order to install it. 


-- INSTALLING jSymbolic --

The jSymbolic software is delivered in a zipped file, from which 
jSymbolic can be extracted using any of a variety of dearchiving 
utilities (e.g. WinZip).

There are two versions of jSymbolic, namely the development version
and the user version. The user version contains everything needed to
run jSymbolic, but does not include any source code. The developer 
version does include source code.

The user version unzips into a single directory. Installation simply 
involves extracting this directory to any desired disk location.

The developer version presents jSymbolic in the form of a NetBeans 
project. Four directories are contained in the zipped distribution
file:

	- jSymbolic: Contains the jSymbolic source and bytecode 
	files, as well as other NetBeans project documents and 
	general documentation.
	- UtilityClasses: General jMIR classes, including source code,
	used by jSymbolic.
	- ACE: The jMIR ACE NetBeans project, including source code. 
	jSymbolic uses some classes from this software.
	- jMei2Midi: An MEI to MIDI converter which currently allows MIDI
	features to be extracted from MEI XML files.


-- RUNNING THE SOFTWARE -- 

A file named "jSymbolic.jar" is produced upon installation. The
simplest way to start jSymbolic is to simply double click on this 
file.

The software may also be accessed via the command line (e.g. the DOS
prompt). To access DOS under Windows, for example, go to the Start 
Menu, select Run and type "cmd". Then use the "cd" command to move to
the directory that contains the jSymbolic.jar file. In that directory, 
type:

	java -Xmx3072m -jar jSymbolic.jar

Updated commands since the release of jMei2Midi:

Window mode allows for the music to be split up into windows (given as window size in seconds) with an overlap offset which
is given as a fractional value and can make processing more accurate. The SourceFilePath can be either a single file
or a directory:

	java -Xmx3072m -jar jSymbolic.jar -window SourceFilePath FeatureValuesOutputPath FeatureDescriptionsOutputPath WindowSize WindowOverlapOffset

CSV and ARFF file formats can be obtained by adding the -arff and -csv switches to the beginning of the command.
This will output the given file format alongside the ACE xml document:

	java -Xmx3072m -jar jSymbolic.jar -csv -arff SourceFileP…
	java -Xmx3072m -jar jSymbolic.jar -csv -arff -window SourceFileP…

It should be noted that the JRE does not always allocate sufficient
memory for jSymbolic to process large music collections. Running 
jSymbolic using either of the above two methods could therefore result
in an out of memory error (although this is relatively rare).

It is therefore sometimes preferable to manually allocate a greater
amount of memory to jSymbolic before running it. 3 GB should be more
than enough for most situations. This can be done by entering the 
following at the command prompt:

	java -Xmx3072m -jar jSymbolic.jar

If jSymbolic is run without any command line arguments, then the GUI
will be run. Alternatively, jSymbolic may be run directly from the
command line to extract features from a single MIDI file using the
following three command line arguments:

<SourceFilePath> <FeatureValuesOutputPath> <FeatureDescriptionsOutputPath>

Updated commands for configuration file release :

The following 2 commands will startup jSymbolic through the gui or the command line using the specified
configuration file:

    java -Xmx3072m -jar jSymbolic.jar -configgui ConfigurationFilePath
    java -Xmx3072m -jar jSymbolic.jar -configrun ConfigurationFilePath

Configuration file options switches for the command line can also be run with input files and output save paths as follows
(Therefore jSymbolic options and features are stored in the configuration file):

    java -Xmx3072m -jar jSymbolic.jar -configrun ConfigurationFilePath SourceFilePath FeatureValuesOutputPath FeatureDescriptionsOutputPath

-- GUI RUNNING OF SOFTWARE --

The following commands can run the GUI :

    java -Xmx3072m -jar jSymbolic.jar
    java -Xmx3072m -jar jSymbolic.jar -configgui ConfigurationFilePath

The first command will run jSymbolic, initialized with the default settings. Adding the -configgui switch will
initialize the jSymbolic GUI with the settings specified in the configuration file denoted by the input of the
ConfigurationFilePath.

The GUI can be interacted with by adding or removing files to the Recording Selector Panel. Features can be selected
on the Feature Selector Panel and all options can be assigned accordingly. A configuration file can also be
generated with the current GUI settings specified.

-- CONFIGURATION FILE SPECIFICATIONS --

Full specification for .txt implementation of configuration file can be found in jSymbolicConfigurationFileSpec.txt
or directly at the following URL below:

    https://github.com/DDMAL/jSymbolic/blob/development/jSymbolicConfigurationFileSpec.txt

-- ACCESSING THE API --

The jSymbolic API can be accessed by creating a jSymbolicProcessor instance from the api package. This instance needs
to be populated with the particular jSymbolic settings that the user desires. This can either be done by inputting
the raw data into the jSymbolicProcessor during construction or it can be done by specifying the File object of
a configuration file and the jSymbolicProcessor will then be constructed with the settings given in the specified
configuration file. The corresponding configuration file must contain all jSymbolic headers to function.
It is worth noting that input files from the configuration file will currently not effect the jSymbolic processing.

The raw data that the jSymbolicProcessor object requires includes the window size, the window overlap, a list
of feature names, whether to save for MIDI windows, whether to save for entire music file, the save path for the
feature values and the save path for the feature definitions. Either window or overall recording must be true but
both cannot be true. If overall file feature extraction is specified, then the window size and window overlap will
have no effect on the processing. Each feature name given to the jSymbolicProcessor must be a valid jSymbolic feature
name, otherwise an exception will be thrown on jSymbolicProcessor instantiation.

Once the user has a jSymbolicProcessor instance in their software, feature extraction can be performed for either
a single music file using the computeJsymbolicData function or for a directory using the computeJsymbolicDataDirectory
function. All sub-directories will also be parsed in the latter case. In the single file case, a single jSymbolicData
object will be returned, which includes Mei Specific data, the feature value and feature definition output File objects
and the ACE DataBoard object corresponding to the specified file. The directory parsing function will return a Map with
the key as a File object and the corresponding jSymbolicData object as a value. They specific File keys can be obtained
using the keySet() function of the Map object. This is recommended as then the appropriate jSymbolicData value can be
obtained to get the corresponding desired data.

-- ADDING MIDI FEATURES --

1) Implement a class for the new feature in the jSymbolic/features
   directory. It must extend the MIDIFeatureExtractor abstract class.
2) Add a reference to the new class to the FeatureExtractor class in the
   processing package. New features need to be added to the MIDIFeatureExtractor[]
   allExtractors in the static initializer block. An ordered, corresponding
   boolean value must also be added to the boolean[] defaultFeaturesToSave in the
   static initializer block.

-- ADDING MEI FEATURES --

1) Implement a class for the new feature in jSymbolic/features directory.
   It must extend the MEIFeatureExtractor abstract class.

-- BUILDING THE JSYMBOLIC JAR --

When building the JAR from scratch, it is required to set the jSymbolicRunner.java class as the main method to initially
run. This allows jSymbolic to run as a separate process with enough heap space allocated.

-- LICENSING AND LIABILITY -- 

jSymbolic 2.0
Copyright (C) 2017 Cory McKay

This program is free software; you can redistribute it
and/or modify it under the terms of the GNU General 
Public License as published by the Free Software Foundation.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public 
License along with this program; if not, write to the Free 
Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139,
USA.


-- UPDATES SINCE VERSION 1.0 -- 

jSymbolic 2.0:
- Added MEI input format form jMei2Midi and jmei libraries. 
Total MEI parsing is still under development as
the MEI encoding language is itself still under development.
- A non-MIDI MEI pipeline is now available for the MEI features.
- Architecture similar to the MIDIFeatureExtractor has been added for MEI
via the MEIFeatureExtractor class.
- Windowing files with overlap offsets has also been added.
- CSV and ARFF files can now be obtained from the ACE XML format.
- Configuration file implemented to run with both command line and GUI.
- Configuration file can now be built through the save configuration file button on the GUI.
- Extensible configuration file architecture in case of change in configuration file format.
- Command line switches updated to deal with configuration file for both command line and GUI.
- Command line switch architecture is now extensible for easy addition of new switches.
- Includes fully featured manual for jSymbolic.
- jSymbolic can now run as a separate process to account for changes in Java heap size.
Therefore, the user no longer has to account for JVM specific command line switches.
- jSymbolic can now be accessed through an API where ACE XML files, ACE DataBoards and
Mei Specific Data can be accessed through any Java software.

jSymbolic 1.2.1:
- Added basic command line functionality to complement the GUI. Note
that this involved updating the FeatureSelectionPanel class with a
new public getAllAvailableFeatureExtractors method to provide access to
the available features, as well as updating the private 
populateFeatureExtractors method to now use this new method to set up
features for the GUI internally. New features that were previously added
using the populateFeatureExtractors method should now be added with the
getAllAvailableFeatureExtractors method.
- Includes updated support libraries (ACE and UtilityClasses).

jSymbolic 1.2:
- Includes updated support libraries (ACE and UtilityClasses).

jSymbolic 1.1:
- Imported into the NetBeans framework to assist further development.
