# WeCom
Chat client and server backend written in Java and JavaFX

## Build
Built with
- Netbeans 8
- Java 8 and JavaFX


## Details
### Server & Client
Parses a socketstream as a state machine. Data is sent to between server an client as tags, 
for instance, a <msg></msg> pair's contained message will be broadcast to all connected clients.   
Illegal tags and their content are discarded. 

### Server
Handles connections to clients independently on dedicated threads, liveness of each thread and its objects is guaranteed.

### Client
Authenticates and sends messages over the same connection to the server, and provides a list of connected users.
