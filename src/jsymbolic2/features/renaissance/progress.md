# Progress

- [Progress](#progress)
  - [Features](#features)
    - [Ren-01: Final Sonority ✓](#ren-01-final-sonority-)
    - [Ren-02: Final Sonority Classes ✓](#ren-02-final-sonority-classes-)
    - [Ren-03: Finalis Naive ✓](#ren-03-finalis-naive-)
    - [Ren-04: Finalis Heuristic ✓](#ren-04-finalis-heuristic-)
    - [Ren-05: Prevalence of Modal Fifths ✓](#ren-05-prevalence-of-modal-fifths-)
    - [Ren-06: Maximum Prevalence of Modal Fifth ✓](#ren-06-maximum-prevalence-of-modal-fifth-)
    - [Ren-07: Finalis Octave ✓](#ren-07-finalis-octave-)
    - [Ren-08: Prevalence of Notes in Lower Fourth ✓](#ren-08-prevalence-of-notes-in-lower-fourth-)
    - [Ren-09: Prevalence of Notes in Upper Fourth ✓](#ren-09-prevalence-of-notes-in-upper-fourth-)
    - [Ren-10: Expected Lower Bound of Modal Octave ✓](#ren-10-expected-lower-bound-of-modal-octave-)
    - [Ren-11: Expected Upper Bound of Modal Octave ✓](#ren-11-expected-upper-bound-of-modal-octave-)
    - [Ren-12: Pitch Class of Expected Modal Octave Bounds ✓](#ren-12-pitch-class-of-expected-modal-octave-bounds-)
    - [Ren-13: Pitch of Modal Scale Degrees ✓](#ren-13-pitch-of-modal-scale-degrees-)
    - [Ren-14: Pitch Class of Modal Scale Degrees ✓](#ren-14-pitch-class-of-modal-scale-degrees-)
    - [Ren-15: Prevalence of Modal Scale Degrees ✓](#ren-15-prevalence-of-modal-scale-degrees-)
  - [Intermediate Representations](#intermediate-representations)
    - [pitch\_histogram\_of\_first\_track ✓](#pitch_histogram_of_first_track-)

## Features

### Ren-01: Final Sonority ✓

A feature calculator that finds all pitches sounding immediately after the last attacked note of the piece. An array of 16 values is returned. If more than 16 different notes are present, only the lowest 16 are returned. If less than 16 are present, the redundant values are set to -1. A value of 0 corresponds to C, and pitches increase chromatically by semitone in integer units (e.g. a value of 2 corresponds to D). Enharmonic equivalents are treated as a single pitch class.

### Ren-02: Final Sonority Classes ✓

A feature calculator that finds all pitche classes sounding immediately after the last attacked note of the piece. An array of 12 values is returned, with each element corresponding to a pitch class. If this element i is zero, that means that pitch class i is not present in the final sonority. If it is 1, it is present. The first element of the array represents pitch class C (or B#), the second C#/Db and so on.

### Ren-03: Finalis Naive ✓

Naive approach to estimating the finalis. Simply returns the pitch class of the lowest pitch present in the final sonority. A value of 0 corresponds to C, and pitches increase chromatically by semitone in integer units (e.g. a value of 2 corresponds to D). Enharmonic equivalents are treated as a single pitch class. Set to 0 if there are no pitched notes.

### Ren-04: Finalis Heuristic ✓

A feature calculator that uses a heuristic to approximate the finalis. The pitch class of the expected finalis is returned. This heuristic works as follows: if the lowest pitch class of the final sonority is also the most common pitch class throughout the piece, this is taken as the finalis. If it isn't, switch to the most common pitch class if it is at least 10% more common and precisely a fifth lower than the lowest pitch class of the final sonority.

### Ren-05: Prevalence of Modal Fifths ✓

Plagal and authentic variants of modes share a range delineated by the final and an ascending fifth above it. This feature returns the prevalence of notes from the first track within these fifth ranges for all octaves. These values are represented as percentages of the total amount of notes and are returned in an array with 11 elements. The first of these elements represents the prevalence for the fifth starting between pitch values 0 and 11, the second within 12 and 23 and so on. The eleventh element represents an incomplete range: pitches 120 to 127. If the eleventh fifth does not fit within this range, it will return a value of -1.

### Ren-06: Maximum Prevalence of Modal Fifth ✓

Plagal and authentic variants of modes share a range delineated by the final and an ascending fifth above it. This feature returns prevalence of notes from the first track within these fifth ranges for all octaves. This value is represented as a percentage of the total amount of notes. In case of a tie between two or more fifth ranges, the prevalence of the lowest range is returned.

### Ren-07: Finalis Octave ✓

Returns the absolute pitch of the final that yields the most prevalent modal fifth. As such, this value determines the octave of the modal ambitus.

### Ren-08: Prevalence of Notes in Lower Fourth ✓

Prevalence of notes in fourth immediatly below the most prevalent modal fifth.

### Ren-09: Prevalence of Notes in Upper Fourth ✓

Prevalence of notes in fourth immediatly above the most prevalent modal fifth.

### Ren-10: Expected Lower Bound of Modal Octave ✓

Returns lower bound of modal octave based on lower & upper fourth prevalence: if lower fourth prevalence is greater than upper fourth prevalence, a plagal mode is assumed. If this is reversed, authentic mode is assumed. In case of plagal mode, this feature returns the fourth below the finalis, in case of authentic mode it returns the finalis (in the correct octave). In case of a tie, authentic mode is assumed.

### Ren-11: Expected Upper Bound of Modal Octave ✓

Returns upper bound of modal octave based on lower & upper fourth prevalence: if lower fourth prevalence is greater than upper fourth prevalence, a plagal mode is assumed. If this is reversed, authentic mode is assumed. In case of plagal mode, this feature returns the fifth above the finalis, in case of authentic mode it returns the finalis (in the correct octave). In case of a tie, authentic mode is assumed.

### Ren-12: Pitch Class of Expected Modal Octave Bounds ✓

Returns the pitch class of the bounds of the modal octave. These bounds are always the same pitch class, as they are the bounds of an octave.

### Ren-13: Pitch of Modal Scale Degrees ✓

Returns an array with eight elements. These elements indicate the pitches of the scale degree of the mode. These pitches are attained by comparing all pairs of possible pitches for each of the scale degrees and choosing the most prevalent one. E.g. in a C mode, the fourth degree could be F or F#, this feature will return the one that most commonly occurs between these two as its fourth element.

### Ren-14: Pitch Class of Modal Scale Degrees ✓

Returns an array with eight elements. These elements indicate the pitch class of the scale degree of the mode. These pitches are attained by comparing all pairs of possible pitches for each of the scale degrees and choosing the most prevalent one. E.g. in a C mode, the fourth degree could be F or F#, this feature will return the one that most commonly occurs between these two as its fourth element.

### Ren-15: Prevalence of Modal Scale Degrees ✓

Returns an array with eight elements. These elements indicate the prevalence of the scale degree of the mode.

## Intermediate Representations

### pitch_histogram_of_first_track ✓

This intermediate representation finds the first track with at least one note in it and then provides the pitch histogram for this track only. To do so, it uses the same logic as basic_pitch_histogram.
