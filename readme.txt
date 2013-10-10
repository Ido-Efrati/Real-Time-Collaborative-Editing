This readme file was created and saved from our Real Time Collaborative Editor:

In order to start our RTCE Server:
1) go to RTCE.Server package.
2) open RTCE_Server.java.
3) run the server.

In order to start our client- Graphical User Interface:
1) go to RTCE.Client package.
2) open RTCE_Controller.java.
3) run the controller.

Follow the on-screen instructions.

This program was tested and is running on the following OS:
1) Windows 7.
2) OSX 10.8 - Mountain Lion.
3) Ubuntu - Athena computer.

You can also test out RTCE from the command line:

1) Run the server.
2) Connect to 4444 port
3) You can use any of the messages specified in the
   protocol. See Final Design Document.pdf. 
4) Be sure to type "help" for the help message so you
   know what the commands are.  
4) While the GUI makes sure that a client can not
   do an insert or delete before it makes a new file
   or switches to an existing file, the command line
   version does not check for that. So don't insert
   or delete (or enter "view" message without making 
   sure you are on a file. Else, the server will shut down.