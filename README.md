# AndroidProj
This project is for Reading temperature values from the BMP180 sensor via a Ardunio Nano Board and sending the values via bluetooth communication,recieving this bit values and process them via an Android application and displaying the values with a perfect synchronization using a simple but effective graphical interface. .ino file is a C++ Ardinuo file wich to be run with a Ardinuo board.It contains code for reading Sensor data and sending them via HC-06 type bluetooth adapter in bits. The main folder contains the main Android Java code file and resources for graphical interface. Android Program's job is that it opens and maintains bluetooth connection with the client and recieves Temperature data, it processes the data and displays them in human readable format(°C) using custom built Graphical Intarface. Overall these two programs with the required hardware achives wireless temperature reading with Android.
