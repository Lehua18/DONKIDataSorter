# DONKI Data Grabber
The [Space Weather Database Of Notifications, Knowledge,
Information (DONKI)](https://kauai.ccmc.gsfc.nasa.gov/DONKI/), developed at the 
[Community Coordinated Modeling Center (CCMC)](https://ccmc.gsfc.nasa.gov/), 
provides updated data on space weather events for the use of the general space
science community. I am currently using this data to study the solar cycle, which may
require the compilation of this data. This program is the first step of that process, as
it simply grabs the data from the database, allows the user to search for specific events,
and prints that data in an easy-to-use format.

## How to use
### Download
eee
### Use
<video width="320" height="240" controls>
  <source src="Video/READMEVideo.mp4" type="video/mp4">
</video>

Upon running the program, you will be prompted to enter an event type. These types must be
entered as their three letter 'key' for the program to function. The keys are as follows:
- ```CME``` --> Coronal Mass Ejection
- ```GST``` --> Geomagnetic Storm
- ```IPS``` --> Interplanetary Shock
- ```FLR``` --> Solar Flare
- ```SEP``` --> Solar Energetic Particle
- ```MPC``` --> Magnetopause Crossing
- ```RBE``` --> Radiation Belt Enhancement
- ```HSS``` --> High Speed Stream

If you enter a key not on this list, the program may crash. 

