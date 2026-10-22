# Progress

- [Progress](#progress)
  - [Modal Features](#modal-features)
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
    - [Ren-13: Pitches of Modal Octave ✓](#ren-13-pitches-of-modal-octave-)
    - [Ren-14: Pitch Classes of Modal Octave ✓](#ren-14-pitch-classes-of-modal-octave-)
    - [Ren-15: Prevalence of Pitches in Modal Octave ✓](#ren-15-prevalence-of-pitches-in-modal-octave-)
    - [Ren-16: Combined Prevalence of Modal Octave](#ren-16-combined-prevalence-of-modal-octave)
    - [Ren-17: Plagality v1](#ren-17-plagality-v1)
    - [Ren-18: Plagality v2](#ren-18-plagality-v2)
    - [Ren-19: Weighted Plagality v1](#ren-19-weighted-plagality-v1)
    - [Ren-20: Weighted Plagality v2](#ren-20-weighted-plagality-v2)
    - [Ren-21: Weighted Plagality v3](#ren-21-weighted-plagality-v3)
    - [Ren-22: Plagality Scores](#ren-22-plagality-scores)
    - [Ren-23: Authenticity Scores](#ren-23-authenticity-scores)
    - [Ren-24: Weighted Plagality Scores](#ren-24-weighted-plagality-scores)
    - [Ren-25: Weighted Authenticity Scores](#ren-25-weighted-authenticity-scores)
  - [Cadence Features](#cadence-features)
    - [Number of Candidate Cadences ==\> should probably be scaled to something](#number-of-candidate-cadences--should-probably-be-scaled-to-something)
    - [Number of "Confirmed" Cadences ==\> should probably be scaled to something](#number-of-confirmed-cadences--should-probably-be-scaled-to-something)
    - [Proportion of Candidate Cadences Confirmed](#proportion-of-candidate-cadences-confirmed)
    - [Proportion of Confirmed Cadences to Finalis](#proportion-of-confirmed-cadences-to-finalis)
    - [Proportion of Confirmed Cadences to Pitch Classes](#proportion-of-confirmed-cadences-to-pitch-classes)
    - [Something with cadential functions for specific voices? Proportion tenorizans ...](#something-with-cadential-functions-for-specific-voices-proportion-tenorizans-)
    - [Proportion of Cadences with Suspensions](#proportion-of-cadences-with-suspensions)
    - [Types of Suspension](#types-of-suspension)
  - [Other Features](#other-features)
    - [Tenor and Contratenor Overlap](#tenor-and-contratenor-overlap)
    - [Fauxbourdon](#fauxbourdon)
  - [Intermediate Representations](#intermediate-representations)
    - [pitch\_histogram\_of\_first\_track ✓](#pitch_histogram_of_first_track-)
    - [final\_sonority ✓](#final_sonority-)

## Modal Features

### Ren-01: Final Sonority ✓

A feature calculator that finds the final pitch in every individual voice. An array of 16 values is returned. If more than 16 different notes are present, only the lowest 16 are returned. If less than 16 are present, the redundant values are set to -1. A value of 0 corresponds to C, and pitches increase chromatically by semitone in integer units (e.g. a value of 2 corresponds to D). Enharmonic equivalents are treated as a single pitch class. By looking at the final note in each track/channel rather than looking at the end of the piece, indeterminate longs are included even if their duration is not explicitly encoded.

### Ren-02: Final Sonority Classes ✓

A feature calculator that that finds the final pitch class in every individual voice. An array of 12 values is returned, with each element corresponding to a pitch class. If an element i is zero, that means that pitch class i is not present in the final sonority. If it is 1, it is present. The first element of the array represents pitch class C (and its enharmonic equivalents), the second C#/Db and so on.

### Ren-03: Finalis Naive ✓

Returns the finalis, assuming all voices are present and that the file is encoded in such a way that its ending is also the piece's ending. This is especially relevant for pieces with repeats, where the piece ending might not correspond to the encodings ending. This feature simply provides the pitch class of the lowest pitch present in the final sonority. A value of 0 corresponds to C, and pitches increase chromatically by semitone in integer units (e.g. a value of 2 corresponds to D). Enharmonic equivalents are treated as a single pitch class. Set to 0 if there are no pitched notes.

### Ren-04: Finalis Heuristic ✓

A feature calculator that uses a heuristic to approximate the finalis. The pitch class of the expected finalis is returned. This heuristic works as follows: if the lowest pitch class of the final sonority is also the most common pitch class throughout the piece, this is taken as the finalis. If it isn't, switch to the most common pitch class if it is at least 10% more common and precisely a fifth from the lowest pitch class of the final sonority.

### Ren-05: Prevalence of Modal Fifths ✓

Plagal and authentic variants of modes share a range delineated by the final and an ascending fifth above it. This feature returns the prevalence of notes from the first track within these fifth ranges for all octaves, using the final from the "Naive Final" feature. These values are represented as percentages of the total amount of notes and are returned in an array with 11 elements. The first of these elements represents the prevalence for the fifth starting between pitch values 0 and 11, the second within 12 and 23 and so on. The eleventh element represents an incomplete range: pitches 120 to 127. If the eleventh fifth does not fit within this range, it will return a value of -1.

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

### Ren-13: Pitches of Modal Octave ✓

Returns an array with eight elements. These elements indicate the pitches of the modal octave. These pitches are attained by comparing all pairs of possible pitches for each of positions within the modal octave and choosing the most prevalent one. E.g. in a C mode, the fourth degree could be F or F#, this feature will then return the one that most commonly occurs between these two.

### Ren-14: Pitch Classes of Modal Octave ✓

Returns an array with eight elements. These elements indicate the pitch class of the modal octave. These pitches are attained by comparing all pairs of possible pitches for each of positions within the modal octave and choosing the most prevalent one. E.g. in a C mode, the fourth degree could be F or F#, this feature will then return the one that most commonly occurs between these two.

### Ren-15: Prevalence of Pitches in Modal Octave ✓

Returns an array with eight elements. These elements indicate the prevalence of pitches in the modal octave.

### Ren-16: Combined Prevalence of Modal Octave

Returns the combined prevalence of all pitches in modal octave.

### Ren-17: Plagality v1

Proportion of notes below modal fifth to those above

### Ren-18: Plagality v2

Proportion of notes below plagal range to those above authentic range

### Ren-19: Weighted Plagality v1

Weighted for distance outside ambitus

### Ren-20: Weighted Plagality v2

Weighted for number of consecutive notes outside ambitus

### Ren-21: Weighted Plagality v3

Weighted for distance and consecutive notes

### Ren-22: Plagality Scores

Uses +1 for every note below plagal ambitus and -1 for every note above

### Ren-23: Authenticity Scores

Uses +1 for every note above authentic ambitus and -1 for every note below

### Ren-24: Weighted Plagality Scores

Instead of +1 and -1, use the distance from the expected ambitus

### Ren-25: Weighted Authenticity Scores

Instead of +1 and -1, use the distance from the expected ambitus

## Cadence Features

### Number of Candidate Cadences ==> should probably be scaled to something

= number of sixths between voice pairs that resolve to octaves

### Number of "Confirmed" Cadences ==> should probably be scaled to something

= candidate cadences where other voices are either absent or have cadential function, probably also take metrical strength into account

### Proportion of Candidate Cadences Confirmed

### Proportion of Confirmed Cadences to Finalis

### Proportion of Confirmed Cadences to Pitch Classes

List with 12 elements

### Something with cadential functions for specific voices? Proportion tenorizans ...

### Proportion of Cadences with Suspensions

= does the antepenultimate sonority have a dissonance (that is not attacked)

### Types of Suspension

## Other Features

### Tenor and Contratenor Overlap

### Fauxbourdon

## Intermediate Representations

### pitch_histogram_of_first_track ✓

This intermediate representation finds the first track with at least one note in it and then provides the pitch histogram for this track only. To do so, it uses the same logic as basic_pitch_histogram.

### final_sonority ✓

An array with the final sounding note for every track and channel. Iterates through all note onset slices to find the final note onset for each track and channel with at least one note.
