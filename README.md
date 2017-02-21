==================================================================================
 jSymbolic 2.0
==================================================================================
 by Cory McKay
 Copyright (C) 2017 (GNU GPL)


### OVERVIEW

jSymbolic is a software application intended for conducting research in the 
fields of music information retrieval (MIR), music theory and musicology.
Its primary purpose is to extract statistical information from musical data 
stored symbolically in file formats such as MIDI or MEI. This statistical 
information is formulated as feature values, which may be fed directly into
automatic classification systems, may be used to query large musical
datasets, or may be used by musicologists and music theorists for conducting 
empirical musical research.

jSymbolic includes an easy-to-use graphical user interface, and may also be
used via the command line. It also has an API facilitating programmatic use.
The software can be used either with its excellent general-purpose default 
settings, or advanced users can use it under a variety of settings (saved in
a special configuration settings file).

Extracted features can be saved in a variety of formats, including ACE XML
1.1, Weka ARFF and basic CSV text. The Weka ARFF and CSV files are optional
, but the ACE XML files are always generated, and consist of two file types:
a Feature Values file that holds the extracted numerical feature values, and
a Feature Definitions file that specifies explicative metadata about the
extracted feature types. Some feature values each consist of single values, 
and some consist of multi-dimensional vectors of related values (these are
primarily histograms).

jSymbolic is free and open-source, and is designed to be used directly for
conducting research as well as as a platform for iteratively developing new 
features that can then be shared amongst researchers. As such, jSymbolis
emphasizes extensibility, and includes a modular design that facilitates 
the implementation and incorporation of new features, automatic provision
of all other feature values to each new feature and dynamic feature extraction
scheduling that automatically resolves feature dependencies. jSymbolic is 
implemented in Java in order to maximize cross-platform utilization.

jSymbolic is part of the jMIR music classification research software suite, 
and may be used either as part of this suite or independently. In particular,
jSymbolic is designed to integrate especially well with the ACE meta-learning 
automtic classification research suite, as well as the external Weka data-
mining suite.

jSymbolic is also part of the SIMSSA (Single Interface for Music Score 
Searching and Analysis) project, and is integrated with the music stored on 
the associated Elvis database.


### GETTING MORE INFORMATION

More information on jMIR (and the software itself) is available at
http://jmir.sourceforge.net. jSymbolic includes a detailed HTML manual,
which is packaged with the code and also posted on the jMIR web site.

Please Cory McKay (cory.mckay@mail.mcgill.ca) with any bug reports or
questions relating to the software. 


### LICENSING AND LIABILITY

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation.

This program is distributed in the hope that it will be useful, but WITHOUT 
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with 
this program; if not, write to the Free Software Foundation, Inc., 675 
Mass Ave, Cambridge, MA 02139, USA.


### COMPATIBILITY

The jSymbolic software is written in Java, which means that it can in
principle be run on any system that has the Java Runtime Environment (JRE)
installed on it. It is particularly recommended that this software be used
with Windows or Linus, as it was developed and tested primarily under these
operating systems. Although the software should still run perfectly well on
OS X, Solaris or any other operating system with the JRE installed on it, 
users should be advised that jSymbolic has not yet been fully tested on these
platforms, so difficulties may be encountered.

This software was developed with version 8 of the JDK (Java Development Kit)
and because it uses features from Java 8, it is required that users have
Java 8 or newer installed on their systems in order to run jSymbolic
properly.


### INSTALLING THE JAVA RUNTIME ENVIRONMENT

If your system already has the JRE installed, as will most typically be the
case, you may skip this section. If not, you will need to install the JRE in
order to run jSymbolic. The JRE can be downloaded for free from the Java web
site. The JDK typically includes the JRE, or the JRE can simply be installed
alone.

When the JRE download is complete, follow the installation instructions that
come with it in order to install it.


### INSTALLING jSymbolic

The jSymbolic software is available at jmir.sourceforge.net. It is delivered 
in a zipped file, from which jSymbolic can be extracted using any of a variety
of dearchiving utilities (e.g. ZipGenius).

There are two versions of jSymbolic, namely the development version and the 
user version. The user version contains everything needed to run jSymbolic, 
but does not include any source code. The developer version does include source
code.

The user version unzips into a single directory. Installation simply involves 
extracting this directory to any desired disk location. 

The developer version presents jSymbolic in the form of a NetBeans project. 
Four directories are contained in the zipped distribution file:

- ACE: A metalearning tool. jSymbolic uses it to save ACE XML files. A NetBeans
project that includes source code.
- jMei2Midi: Conversion software that converts MEI data to MIDI data. This
includes an MEI pipeline which can extract and preserve MEI-specific features
that would be lost in the conversion to MIDI.
- jSymbolic: The jSymbolic source files, as well as other NetBeans project
documents and general documentation.
- ThirdPartyJars: Contains the distributable third party software used by 
jSymbolic and its associated jMIR projects.
- UtilityClasses: A NetBeans project holding general jMIR classes used by 
jSymbolic, including source code.


### RUNNING JSYMBOLIC 

The simplest way of using jSymbolic is to simply run the jSymbolic GUI by double
clicking the jSymbolic.jar file's icon on a graphical disk interface, such as 
Windows or OS X. 

However, a greater range of options are made available by running jSymbolic through
a terminal interface using command line arguments, such as a Windows DOS command
prompt, or a terminal prompt on OS X, Linux, UNIX, etc. Although such a command line
approach can be less intuitive for some initially relative to using the jSymbolic
GUI, it can also sometimes be faster and easier. If you are unsure how to access a
terminal prompt on your computer, please consult instructions for your particular 
operating system on-line for details on how to access and use it.

Once you have accessed a terminal prompt, navigate to the jSymbolic folder and locate
the file named "jSymbolic.jar", which is included in the jSymbolic distribution. You 
must run this (using the Java Runtime Environment) in order to access jSymbolic's
functionality. The particular arguments that you use when running jSymbolic in this
way will determine exactly what jSymbolic does.

The simplest way to run jSymbolic from a terminal prompt is to type the following at
your terminal prompt (after you have navigated to the directory containing the
jSymbolic.jar file):

    java -Xmx3072m -jar jSymbolic2.jar

This will run the jSymbolic GUI set up with default settings.

One must add jSymbolic command line arguments if one wishes to instigate jSymbolic 
processing directly from the command line. Some of these command line arguments
consist of flag/value pairs, where the flag comes first and is preceded with a "-". 
Essentially, the flag indicates the type of option, and the value indicates its 
particular setting.

Other command line arguments simply consist of values, with no flags, of flags with 
no values. For example, a lone flag of "-help" will print to the terminal a list of 
valid flags with explanations of what they are. This can be run as follows:

    java -jar jSymbolic2.jar -help

Invalid command line arguments will also result in the list of legitimate command line 
arguments being printed out to the terminal.

More details on the kinds of command line arguments that may be used with jSymbolic
are provided in the jSymbolic manual.

Important Notes:
- The ordering of the command line arguments does matter in certain cases.
- jSymbolic's operation can also be governed by its configuration settings file, as
explained further below.
- If the user does not specify a configuration settings file at runtime, then jSymboic
will look for a default one in the same folder as the jSymbolic.jar file, and with the 
file name jSymbolicDefaultConfigs.txt. If no such file exists, or if it is not a valid 
jSymbolic configuration settings file, then the jSymbolic will simply run with hard
-coded default settings.
- All file or directory paths specified in command line arguments must either be 
absolute or relative to the directory containing the jSymbolic.jar file.
- Any errors that occur during batch processing of multiple files will be logged to the 
terminal. Processing of other files will not be interrupted..
- jSymbolic can currently process the following kinds of symbolic music files: MIDI files
and MEI files. Any files that cannot be parsed by jSymbolic will cause an error message
to be generated.
- If an MEI-specific feature (e.g. Number of Grace Notes) is set to be extracted, then
it will only be extracted from MEI files. It will be ignored by other files, such as MIDI 
files.
- It is no longer necessary to specify the default JRE heap size from the command line,
for fear of running out of memory, as newer versions of jSymbolic automatically setsan
appropriate heap size.


### USING THE JSYMBOLIC GUI

The jSymbolic GUI makes it quite easy to use jSymbolic. Users are advised to read the 
section of the jSymbolic manual on the GUI for more details, but a brief overview is
provided here. The jSymbolic GUI consists of several component panels:

- Music File Selector Panel: This panel, on the top left of the jSymbolic GUI, contains
a table listing all symbolic music files from which features are to be extracted. The first
column indicates the name of each file, and the second indicates its file path. Double
clicking on a given row provides additional metadata about its associated file. Buttons are
included for adding or removing files from the table, as well as for sonifying them. The
table may be sorted by clicking on either of the column headings.
- Feature Selector Panel: This panel, on the top right of the jSymbolic GUI, contains a
table containing one row for each feature, and whose first column contains check boxes
allowing users to select which features to save during feature extraction. This feature
table also allows users to view additional metadata about each feature, and still further
metadata can be seen for a feature (including its feature dependencies) by double clicking
on its row. Buttons are included for auto-selecting all features, no features or just the
default features.
- Processing Information Panel: This panel, on the middle left of the jSymbolic GUI, is a
space where processing updates are posted during feature extraction. Summary information is
also posted here at startup relating to feature breakdowns, and additional updates ar
e written here relating to configuration files and symbolic music files selected to have
features extracted from them.
- Error Reports Panel: This panel, on the middle right of the jSymbolic GUI, is a space
where any problems encountered during feature extraction processing are posted (such
problems typically relate to invalid files).
- Configuration File and Windowing Settings Panel: This panel, on the bottom left of the
jSymbolic GUI, allows the user to save and load configuration settings files. The user can
also specify settings related to windowed feature extraction.
- Feature Extraction and Saving Settings Panel: This panel, on the bottom right of the
jSymbolic GUI, allows the user to specify the kinds and paths of output files to be
generated by jSymbolic when feature extraction is complete, and to initiate feature
extraction.


### THE JSYMBOLIC CONFIGURATION SETTINGS FILE

jSymbolic includes a special configuration settings file format. The key advantage of this
file is that it allows users to preserve feature extraction settings between sessions. It
can also be used to keep a record of extraction settings that were used, and can also be 
transmitted to other researchers for the purpose of complete feature extraction t
ransparency.

The optional jSymbolic configuration settings file is a simple text file that allows the
user to specify jSymbolic settings related to:

- Which features are to be extracted.
- What kinds of output files are to be generated.
- Whether windowed feature extraction is to be used and, if so, using what window duration
and overlap.
- Input file paths of symbolic files from which features are to be extracted.
- Output file paths for saving extracted feature values and feature metadata.

The input file paths and output file paths are optional, and may be omitted if desired.
However, if one is to be used, then both must be present (only having input paths without
output paths is considered invalid, and vice versa).

In fact, the configuration settings file itself is entirely optional. jSymbolic's 
functionality may be accessed via the GUI, command line interface or API without a 
configuration file if desired. In such a case, default feature extraction settings will be
used, unless otherwise specified by the user (e.g. the user may still certainly enable or
disable certain features for extraction without a configuration file using the GUI). 
However, a configuration file is required if the user wishes to use the command line
interface or API with any selection of features other than the default.

More details on the jSymbolic configuration settings file and how to use it are provided in 
the jSymbolic manual.


### ACCESSING THE API

See the jSymbolic manual for more information on this topic.


### EXTENDING JSYMBOLIC

See the jSymbolic manual for more information on this topic.


### ACCESSING THE API

See the jSymbolic manual for more information on this topic.


### LICENSING AND LIABILITY 

jSymbolic 2
Copyright (C) 2017
Cory McKay

This program is free software; you can redistribute it and/or modify it under the terms
of the GNU General Public License as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program;
if not, write to the Free Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139,
USA.
