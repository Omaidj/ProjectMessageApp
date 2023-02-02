Message App (IntelliJ IDEA)
---------------

This is a simple Message App that allows Clients(Users) to connect to a specific channel and the user can send messages 
to other users in that channel.

Getting started
---------------

First we would need to click on the 'file' option, in the top right corner.
You would need to select Project Structure and then select Libraries.
Then you would have to left-click the Plus '+' button, and select the Maven option.
After doing so, please search these following json's, and click on OK.

1: eduworks.json.ld
2: google.api.client.gson
3: json

After searching the following json's, select OK, and then click on the apply button.

==============================================================================================================

Server:
-------
In order to run the server, please right-click the Server class and click on the "Run 'Server.main()' " option.

Client:
-------
In order to run the Client, please right-click the Client class and click on the "Run 'Client.main()' " option.


==============================================================================================================

You will be then be prompted for your username and the channel you to join or create. Once the user is connected, 
you can start sending and receiving messages with other clients in the same channel. To exit the connection, simply 
close the Client console window. To shut down the Server simply close the Server console window.

NOTE: Make sure that the Server is running before starting any Clients.

/////////////////////////////////////////////////////

Developer(Author): Omaid Jlilzad

\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\