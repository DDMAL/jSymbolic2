# jSymbolic 2.2
by Cory McKay
Copyright (C) 2018 (GNU GPL)


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
settings, or advanced users can use it under a variety of settings (which can
also be saved in a special configuration settings file).

Extracted features can be saved in a variety of formats, including ACE XML
1.1, Weka ARFF and basic CSV text. The Weka ARFF and CSV files are optional,
but the ACE XML files are always generated, and consist of two file types:
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

jSymbolic is part of the jMIR (http://jmir.sourceforge.net) music 
classification research software suite, and may be used either as part of this
suite or independently. In particular, jSymbolic is designed to integrate
especially well with the ACE meta-learning automtic classification research
suite, as well as the external Weka data-mining suite.

jSymbolic is also part of the SIMSSA (Single Interface for Music Score 
Searching and Analysis, https://simssa.ca) project, and is integrated with 
the music stored on the associated Elvis database (https://elvisproject.ca).


### GETTING MORE INFORMATION

More information on jMIR in general, and jSymbolic in particular, is available
on the jMIR home page (http://jmir.sourceforge.net).

jSymbolic includes a detailed HTML manual, which is packaged with jSymbolic 
and also posted on the  jMIR web site
(http://jmir.sourceforge.net/manuals/jSymbolic_manual/home.html).

jSymbolic is also packaged with a detailed HTML tutorial that includes worked
examples. First-time users of jSymbolic may wish to follow this tutorial(http://jmir.sourceforge.net/manuals/jSymbolic_tutorial/home.html).

The most recent development code for jSymbolic can be found on its GitHub
page (https://github.com/DDMAL/jSymbolic2). However, it is strongly suggested
that users use the stable release version posted on the SourceForge page
(https://sourceforge.net/projects/jmir/files/jSymbolic/) instead of the 
development GitHub version, as the latter may not be fully tested yet.

Please contact Cory McKay (cory.mckay@mail.mcgill.ca) with any bug reports
or questions relating to the software. 


### LICENSING AND LIABILITY

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation.

This program is distributed in the hope that it will be useful, but WITHOUT 
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You may obtain a copy of the GNU General Public License by writing to the Free
Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

This project also includes software developed by the Apache Software 
Foundation (http://www.apache.org), namely the Xerces library, which is used
to parse XML files. The Xerces license can be accessed via the manual.


### COMPATIBILITY

The jSymbolic software is written entirely in Java, which means that it can in
principle be run on any system that has the Java Runtime Environment (JRE)
installed on it. It is particularly recommended that this software be used
with Windows or Linux, as it was developed and tested primarily using these
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
order to run this project. The JRE can be downloaded for free from the Java web
site. The JDK typically includes the JRE, or the JRE can simply be installed
alone. Warning: Some versions of the Macintosh operating system have reportedly
not supported Java 9, so users may wish to install Java 8 if they are using a
Macintosh just to be safe.

When the JRE download is complete, follow the installation instructions that
come with it in order to install it


### INSTALLING JSYMBOLIC

The release version of jSymbolic may be downloaded from
https://sourceforge.net/projects/jmir/files/jSymbolic/. Alternatively, the
most recent development code for jSymbolic can be found on its GitHub page
(https://github.com/DDMAL/jSymbolic2). However, it is strongly suggested
that users use the stable release version posted on the SourceForge page
(https://sourceforge.net/projects/jmir/files/jSymbolic/) instead of the 
development GitHub version, as the latter may not be fully tested yet. The
information below refers to the content on the release SourceForge page,
not the development GitHub page.

There are two release versions of jSymbolic posted on its SourceForge release
page, namely the developer version and the user version. The user version
contains everything needed to run the project, but does not include any source
code. The developer version does include source code, presented in the form of 
a NetBeans project.

The user version unzips into a single directory. Installation simply involves 
copying this directory into any desired location.

The following directories are contained in the developer zip file:

- jSymbolic2: The jSymbolic2 project, presented in the form of a NetBeans project.
Contains the following directories and files:
	- configfiles: Contains pre-generated jSymbolic configurations files
	that users may wish to use for various purposes.
	- dist: Contains a pre-compiled jSymbolic2.jar file (and associated
	libraries) for direct inclusion in other projects.
	- javadoc: Javadoc documentation for the project source code.
	- jSymbolic-Rodan: Files for using jSymbolic2 in a Rodan wrapper.
	- manual: The software's HTML manual.
	- nbproject: NetBeans project files. This is only relevant to those
	wishing to use the software in a NetBeans IDE context; it is certainly
	possible to use the software in other development environments as well.
	- src: The project's source code.
	- test: The project's unit testing code.
	- tutorial: The software's HTML tutorial.
	- build.xml: NetBeans build instructions. Only relevant if using the
	NetBeans IDE.
	- GNU_GPL.txt: Licensing information for the software.
	- jSymbolicDefaultConfigs.txt: Default configuration settings for the
	software.
	- manifest.mf: The manifest used when building the project Jar file.
	- README.md: Basic overall documentation of the project.
- Third-Party-Jars: Contains the distributable third-party software used by 
the project. Also includes the jar files for other jMIR projects that this
project has as dependencies, if any. These need to be included in the project's
build (in the NetBeans context, this means adding the jar files found here as 
libraries).


### RUNNING JSYMBOLIC 

The simplest way of using jSymbolic is to simply run the jSymbolic GUI by double
clicking the jSymbolic.jar file's icon using a graphical operating system, such as 
Windows or OS X. This is, of course, a perfectly fine way of using the software.

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

    java -Xmx6g -jar jSymbolic2.jar

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

By its nature, the Java Virtual Machine is always assigned a maximum amount of memory 
at runtime. If a specific maximum is not assigned manually at runtime, then this will
be the smaller of 1/4 the computer's physical memory or 1 GB. jSymbolic's processing
can sometimes require quite a bit of memory, so it is best to manually specify a large
maximum when running jSymbolic. This can be done by using the "-Xmx" flag when running
Java, as shown in the examples above.

It is suggested that a maximum of six gigabytes be used, which is why "-Xmx6g" is 
specified above. Of course, the vast majority of processing jobs will require much, 
much less than this amount of memory, but it is suggested that a high maximum such as
this be assigned if possible, as jSymbolic will quit processing (with an explanatory 
error message, of course) if the maximum assigned memory is exceeded while processing
a particularly large or complex piece of music. In the very unlikely event that an 
out-of-memory error does occur, jSymbolic should be rerun with still a larger maximum
specified. Users are advised not to assign an amount of memory greater than the
amount of physical memory that they have on their systems, however, as virtual memory
processing can be quite slow. 

Important Notes:
- The ordering of the command line arguments does matter in certain cases.
- jSymbolic's operation can also be governed by its configuration settings file, as
explained further below.
- If the user does not specify a configuration settings file at runtime, then jSymbolic
will look for a default one in the same folder as the jSymbolic.jar file, and with the 
file name jSymbolicDefaultConfigs.txt. If no such file exists, or if it is not a valid 
jSymbolic configuration settings file, then jSymbolic will simply run with hard-coded
default settings.
- All file or directory paths specified in command line arguments must either be 
absolute or relative to the directory containing the jSymbolic.jar file.
- All recursive directory searches for symbolic files will only return files that have
valid .mid, .midi or .mei extensions (with any mix of upper-case or lower-case letters).
- Errors that occur during batch processing of multiple files will be logged to the 
terminal. Processing of other files will not be interrupted..
- jSymbolic can currently process the following kinds of symbolic music files: MIDI files
and MEI files. Any files that cannot be parsed by jSymbolic will cause an error message
to be generated.
- If an MEI-specific feature (e.g. Number of Grace Notes) is set to be extracted, then
it will only be extracted from MEI files. It will be ignored by other files, such as MIDI 
files.


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
also posted here at startup relating to feature breakdowns, and additional updates are
written here relating to configuration files and symbolic music files selected to have
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

The jSymbolic GUI also includes a single Information menu, with the following commands:

- About: Displays a small window showing basic information about jSymbolic.
- Help: Brings up a browsable window permitting access to the manual.
- Tutorial: Brings up a browsable window permitting access to the tutorial.

More details on the GUI are provided in the jSymbolic manual.


### THE JSYMBOLIC CONFIGURATION SETTINGS FILE

jSymbolic includes a special configuration settings file format. The key advantage of this
file is that it allows users to preserve feature extraction settings between sessions. It
can also be used to keep a record of extraction settings that were used, and can also be 
transmitted to other researchers for the purpose of complete feature extraction 
transparency.

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


### USING THE COMMAND LINE INTERFACE

See the jSymbolic manual for more information on this topic.


### ACCESSING THE API

See the jSymbolic manual for more information on this topic.


### FEATURE EXPLANATIONS

See the jSymbolic manual for more information on this topic.


### PROCESSING SEQUENCE

See the jSymbolic manual for more information on this topic.


### CLASS STRUCTURE

See the jSymbolic manual for more information on this topic.


### EXTENDING JSYMBOLIC

See the jSymbolic manual for more information on this topic.
