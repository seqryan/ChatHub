Developed this java app as a part of an assignment over a period of 4 days. The app isn't perfect and i intend to add more features and improvements in my free time, which may take a while. The app uses only sockets to communicate (assignment restrictions).

About the app
-------------
The app provides basic chat functionalities like creating user, adding and removing friends and chatting with online friends. 
To begin application MySQL must be running and chat server should also be up and running.
To test the app
1. Run one instance of the app. Create user and login.
2. Run another instance of the app and create a user and login as well.
2. Both the users should add each other as friends.
3. Start chat by clicking on the friends name from friend list.

To configure and start database
-------------------------------
1. Import database using "mysql -u [user_name] -p < App/conf/database.sql".
2. Run MySQL server using "mysqld" command.

To run:
-------------
1. Start server using "run_server.bat" for windows and "run_server.sh" for Linux.
2. Note down the IP of the machine running the server.
3. Create 2 copies on App folder if running two instances of chat application on same machine.
4. Edit "server.ip" property in "conf/chat.properties" file.
5. Edit "client.port" if needed. Make sure the ports are different if running two instances on same machine.
6. Execute "run_application.bat" on windows or "run_application.sh" in Linux.

To build
-------------
Prerequisite: Apache Maven installed

1. Go to code directory and execute "mvn clean package".
2. Copy the generated ChatHub-0.0.1-SNAPSHOT-jar-with-dependencies.jar file from the target directory and paste it in App directory.

TODO List
-------------
1. Code improvements, including better error handling.
2. Use a portable db.
3. GUI improvements.
4. Add webcam and audio.
