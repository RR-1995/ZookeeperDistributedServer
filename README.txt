Zookeeper Server

I have set up a basic Client-Server connection that runs on 3 nodes.
The Client.java and Server.java processes are actually both "clients" to Zookeeper. In order to set up 3 servers, on 3 different machines run ./bin/zkServer.sh start in the Zookeeper directory. The Client.java and Server.java will both connect to the 3 servers. Run Server.java first.

How this works is that there is a shared "/command" node shared between the Server.java and all Client.javas. When a Client.java wants to send a command, it knows the "/command" node exsists and modifies it. Server.java has set up a Watcher (CommandWatcher.java) that is started when it calls getData() on the "/command" node initally. When any other process calls setDat() on the "/command" node, the Watcher triggers, and the store is edited. 

You need to reset the watcher each time. In Commandwatcher, a new CommandWatcher is called at the end. In Client.java, it sets a new watcher each time it sends a request to Server.java.

zoo.getData(path, boolean=false, -1) makes it so that there are no watchers.

zoo.getData(path, Watcher, -1) makes it so that a watcher is set up. Any setData to that path will trigger the watcher.

The Client.java also makes a epherenal node "Client1" and sets a watcher on that, and passes the node name to the Server. The Server watcher process writes to that node, and the Client reads that and prints out the response.

The Server.java has a /store and /store/shoes nodes that it edits within itself. The Client.java never accesses these nodes directly.




To compile the file:

You need to include these files in your classpath:

zookeeper-3.4.8.jar
slf4j-api-1.6.1.jar
slf4j-og5j12-1.6.1.jar
log4j-1.2.16.jar
jline-0.9.94.jar
netty-3.7.0.Final.jar

zookeeper-3.4.8.jar needs to be first, and I think that the two slf4j jars need to follow it.

I tried including the files from the Zookeeper folder but it wouldn't recognize it for some reason. It worked when I put the jars
directly inside the folder where you put the java files. If you find a better way to compile let me know.

To compile, this was what I used. Change this depending on your folder setup

javac -cp ~/zookeeper-3.4.8/build/zookeeper-3.4.8.jar:./slf4j-log4j12-1.6.1.jar:./slf4j-api-1.6.1.jar:./log4j-1.2.16.jar:./jline-0.9.94.jar:./netty-3.7.0.Final.jar Server.java CommandWatcher.java

javac -cp ~/zookeeper-3.4.8/build/zookeeper-3.4.8.jar:./slf4j-log4j12-1.6.1.jar:./slf4j-api-1.6.1.jar:./log4j-1.2.16.jar:./jline-0.9.94.jar:./netty-3.7.0.Final.jar Client.java


To run, I also needed to include the classpath. 

java -cp ~/zookeeper-3.4.8/build/zookeeper-3.4.8.jar:./slf4j-log4j12-1.6.1.jar:./slf4j-api-1.6.1.jar:./log4j-1.2.16.jar:./jline-0.9.94.jar:./netty-3.7.0.Final.jar:. Server

java -cp ~/zookeeper-3.4.8/build/zookeeper-3.4.8.jar:./slf4j-log4j12-1.6.1.jar:./slf4j-api-1.6.1.jar:./log4j-1.2.16.jar:./jline-0.9.94.jar:./netty-3.7.0.Final.jar:. Client
