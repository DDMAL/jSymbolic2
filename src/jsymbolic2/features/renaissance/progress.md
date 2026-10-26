# Progress

- [Progress](#progress)
  - [Modal Features](#modal-features)
    - [Ren-1: Final Sonority ✓](#ren-1-final-sonority-)
    - [Ren-2: Final Sonority Classes ✓](#ren-2-final-sonority-classes-)
    - [Ren-3: Finalis of Top Voice](#ren-3-finalis-of-top-voice)
    - [Ren-4: Finalis Lowest Note](#ren-4-finalis-lowest-note)
    - [Ren-5: Prevalence of Modal Fifths ✓](#ren-5-prevalence-of-modal-fifths-)
    - [Ren-6: Maximum Prevalence of Modal Fifth ✓](#ren-6-maximum-prevalence-of-modal-fifth-)
    - [Ren-7: Finalis Octave ✓](#ren-7-finalis-octave-)
    - [Ren-8: Combined Prevalence in Lower Fourth ✓](#ren-8-combined-prevalence-in-lower-fourth-)
    - [Ren-9: Combined Prevalence Below Modal Fifth ✓](#ren-9-combined-prevalence-below-modal-fifth-)
    - [Ren-10: Combined Prevalence in Upper Fourth ✓](#ren-10-combined-prevalence-in-upper-fourth-)
    - [Ren-11: Combined Prevalence Above Modal Fifth ✓](#ren-11-combined-prevalence-above-modal-fifth-)
    - [Ren-12: Plagality ✓](#ren-12-plagality-)
    - [Ren-13: Expected Lower Bound of Modal Octave ✓](#ren-13-expected-lower-bound-of-modal-octave-)
    - [Ren-14: Expected Upper Bound of Modal Octave ✓](#ren-14-expected-upper-bound-of-modal-octave-)
    - [Ren-15: Pitch Class of Expected Modal Octave Bounds ✓](#ren-15-pitch-class-of-expected-modal-octave-bounds-)
    - [Ren-16: Pitches of Modal Octave ✓](#ren-16-pitches-of-modal-octave-)
    - [Ren-17: Pitch Classes of Modal Octave ✓](#ren-17-pitch-classes-of-modal-octave-)
    - [Ren-18: Prevalence of Pitches in Modal Octave ✓](#ren-18-prevalence-of-pitches-in-modal-octave-)
    - [Ren-19: Combined Prevalence of Modal Octave ✓](#ren-19-combined-prevalence-of-modal-octave-)
  - [Cadence Features](#cadence-features)
    - [Number of Candidate Cadences ==\> should probably be scaled to something](#number-of-candidate-cadences--should-probably-be-scaled-to-something)
    - [Number of "Confirmed" Cadences ==\> should probably be scaled to something](#number-of-confirmed-cadences--should-probably-be-scaled-to-something)
    - [Proportion of Candidate Cadences Confirmed](#proportion-of-candidate-cadences-confirmed)
    - [Proportion of Confirmed Cadences to Finalis](#proportion-of-confirmed-cadences-to-finalis)
    - [Proportion of Confirmed Cadences to Pitch Classes](#proportion-of-confirmed-cadences-to-pitch-classes)
    - [Something with cadential functions for specific voices? Proportion tenorizans](#something-with-cadential-functions-for-specific-voices-proportion-tenorizans)
    - [Proportion of Cadences with Suspensions](#proportion-of-cadences-with-suspensions)
    - [Types of Suspension](#types-of-suspension)
  - [Other Features](#other-features)
    - [Tenor and Contratenor Overlap](#tenor-and-contratenor-overlap)
    - [Fauxbourdon](#fauxbourdon)
  - [Intermediate Representations](#intermediate-representations)
    - [pitch\_histogram\_of\_highest\_voice ✓](#pitch_histogram_of_highest_voice-)
    - [final\_sonority ✓](#final_sonority-)
    - [last\_note\_of\_highest\_voice ✓](#last_note_of_highest_voice-)
    - [track\_and\_channel\_ordering\_by\_average\_pitch](#track_and_channel_ordering_by_average_pitch)

## Modal Features

### Ren-1: Final Sonority ✓

A feature calculator that finds the final pitch in every individual voice. An array of 16 values is returned. If more than 16 different notes are present, only the lowest 16 are returned. If less than 16 are present, the redundant values are set to -1. A value of 0 corresponds to C, and pitches increase chromatically by semitone in integer units (e.g. a value of 2 corresponds to D). Enharmonic equivalents are treated as a single pitch class. By looking at the final note in each track/channel rather than looking at the end of the piece, indeterminate longs are included even if their duration is not explicitly encoded.

### Ren-2: Final Sonority Classes ✓

A feature calculator that that finds the final pitch class in every individual voice. An array of 12 values is returned, with each element corresponding to a pitch class. If an element i is zero, that means that pitch class i is not present in the final sonority. If it is 1, it is present. The first element of the array represents pitch class C (and its enharmonic equivalents), the second C#/Db and so on.

### Ren-3: Finalis of Top Voice

A feature calculater that returns the pitch class of the last note of the highest voice. A value of 0 corresponds to C, and pitches increase chromatically by semitone in integer units (e.g. a value of 2 corresponds to D). Enharmonic equivalents are treated as a single pitch class.

### Ren-4: Finalis Lowest Note

A feature calculater that returns the pitch class of the lowest note in the final sonority. A value of 0 corresponds to C, and pitches increase chromatically by semitone in integer units (e.g. a value of 2 corresponds to D). Enharmonic equivalents are treated as a single pitch class.

### Ren-5: Prevalence of Modal Fifths ✓

Plagal and authentic variants of modes share a range delineated by the final and an ascending fifth above it. This feature returns the prevalence of notes from the first track within these fifth ranges for all octaves, using the final from the "Naive Final" feature. These values are represented as percentages of the total amount of notes and are returned in an array with 11 elements. The first of these elements represents the prevalence for the fifth starting between pitch values 0 and 11, the second within 12 and 23 and so on. The eleventh element represents an incomplete range: pitches 120 to 127. If the eleventh fifth does not fit within this range, it will return a value of -1.

### Ren-6: Maximum Prevalence of Modal Fifth ✓

Plagal and authentic variants of modes share a range delineated by the final and an ascending fifth above it. This feature returns prevalence of notes from the first track within these fifth ranges for all octaves. This value is represented as a percentage of the total amount of notes. In case of a tie between two or more fifth ranges, the prevalence of the lowest range is returned.

### Ren-7: Finalis Octave ✓

Returns the absolute pitch of the final that yields the most prevalent modal fifth. As such, this value determines the octave of the modal ambitus.

### Ren-8: Combined Prevalence in Lower Fourth ✓

Prevalence of notes in fourth immediatly below the most prevalent modal fifth.

### Ren-9: Combined Prevalence Below Modal Fifth ✓

Returns the combined prevalence of all pitches below shared modal range.

### Ren-10: Combined Prevalence in Upper Fourth ✓

Prevalence of notes in fourth immediatly above the most prevalent modal fifth.

### Ren-11: Combined Prevalence Above Modal Fifth ✓

Returns the combined prevalence of all pitches above shared modal range.

### Ren-12: Plagality ✓

The proportion of notes outside of the modal fifth that are below it. Gives in indication of the likelihood that this voice is in plagal mode. The value ranges from 0 to 1, a value of 1 means that all notes outside of the fifth are below it, meaning that 100% of them are in the plagal range or below. Of value of 0 means that all notes outside of the fifth are above it, meaning that 100% of them are in the authentic range or above.

### Ren-13: Expected Lower Bound of Modal Octave ✓

Returns lower bound of modal octave based on lower & upper fourth prevalence: if lower fourth prevalence is greater than upper fourth prevalence, a plagal mode is assumed. If this is reversed, authentic mode is assumed. In case of plagal mode, this feature returns the fourth below the finalis, in case of authentic mode it returns the finalis (in the correct octave). In case of a tie, authentic mode is assumed.

### Ren-14: Expected Upper Bound of Modal Octave ✓

Returns upper bound of modal octave based on lower & upper fourth prevalence: if lower fourth prevalence is greater than upper fourth prevalence, a plagal mode is assumed. If this is reversed, authentic mode is assumed. In case of plagal mode, this feature returns the fifth above the finalis, in case of authentic mode it returns the finalis (in the correct octave). In case of a tie, authentic mode is assumed.

### Ren-15: Pitch Class of Expected Modal Octave Bounds ✓

Returns the pitch class of the bounds of the modal octave. These bounds are always the same pitch class, as they are the bounds of an octave.

### Ren-16: Pitches of Modal Octave ✓

Returns an array with eight elements. These elements indicate the pitches of the modal octave. These pitches are attained by comparing all pairs of possible pitches for each of positions within the modal octave and choosing the most prevalent one. E.g. in a C mode, the fourth degree could be F or F#, this feature will then return the one that most commonly occurs between these two.

### Ren-17: Pitch Classes of Modal Octave ✓

Returns an array with eight elements. These elements indicate the pitch class of the modal octave. These pitches are attained by comparing all pairs of possible pitches for each of positions within the modal octave and choosing the most prevalent one. E.g. in a C mode, the fourth degree could be F or F#, this feature will then return the one that most commonly occurs between thes>e two.

### Ren-18: Prevalence of Pitches in Modal Octave ✓

Returns an array with eight elements. These elements indicate the prevalence of pitches in the modal octave.

### Ren-19: Combined Prevalence of Modal Octave ✓

Returns the combined prevalence of all pitches in modal octave.

## Cadence Features

### Number of Candidate Cadences ==> should probably be scaled to something

= number of sixths between voice pairs that resolve to octaves

### Number of "Confirmed" Cadences ==> should probably be scaled to something

= candidate cadences where other voices are either absent or have cadential function, probably also take metrical strength into account

### Proportion of Candidate Cadences Confirmed

### Proportion of Confirmed Cadences to Finalis

### Proportion of Confirmed Cadences to Pitch Classes

List with 12 elements

### Something with cadential functions for specific voices? Proportion tenorizans

### Proportion of Cadences with Suspensions

= does the antepenultimate sonority have a dissonance (that is not attacked)

### Types of Suspension

## Other Features

### Tenor and Contratenor Overlap

### Fauxbourdon

## Intermediate Representations

### pitch_histogram_of_highest_voice ✓

This intermediate representation finds the track and channel with the highest average pitch through track_and_channel_with_highest_average_pitch and returns its pitch histogram. This can be seen as a subset of the entire pitch histogram that only contains the notes from the highest voice.

### final_sonority ✓

An array with the final sounding note for every track and channel. Iterates through all note onset slices to find the final note onset for each track and channel with at least one note.

### last_note_of_highest_voice ✓

The last note of the track and channel combination with the highest average pitch.

### track_and_channel_ordering_by_average_pitch

An array that represents the ordering of all of the midi tracks and channels by average pitch, from high to low. Every element has the form [track_number, channel_number]. To get the highest track and channel combination, one can do track_and_channel_ordering_by_average_pitch[0]
