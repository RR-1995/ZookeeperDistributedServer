package zookeeper;
import java.io.IOException;
import java.util.Scanner;

// import zookeeper classes
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.log4j.Logger;
//import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
public class Client{
	static boolean wait = false;
	static ZooKeeper zoo;
	static String myName = "John";
	static boolean isConnected = false;
	public static void main(String[] args){
		org.apache.log4j.BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.WARN);
		String host = "localhost:2181,localhost:2182,localhost:2183";
try{
		zoo = new ZooKeeper(host,5000, new Watcher() {
		
         public void process(WatchedEvent we) {

             if (we.getState() == KeeperState.SyncConnected) {
               isConnected = true;
            }
         }
      });
			while (!isConnected){
				Thread.sleep(100);
			}
} catch (Exception e){System.out.println(e.getMessage());}
	byte[] placeholder = {'0'};
		try{
		zoo.create("/" + myName, placeholder, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

		Watcher clientWatcher = new Watcher() { 
    public void process(WatchedEvent e) {
			try{
				System.out.println(new String(zoo.getData("/" + myName, false, null)));
			} catch (Exception ex){System.out.println(ex.getMessage());}
    }
};
		zoo.getData("/" + myName, clientWatcher, null);
	} catch (Exception ex){System.out.println(ex.getMessage());}
			//assume a client has typed the message
	//String message = "purchase John shoes 2";
	//String sentMessage = message + " " + myName;
	//byte[] sentMessageByte = sentMessage.getBytes();
	Scanner sc = new Scanner(System.in);
	try{
	//zoo.setData("/command", sentMessageByte, -1);
	//zoo.setData("/command", sentMessageByte, -1);
	System.out.println("commands");
		while (sc.hasNextLine()){
	final double samples = 10;
	String endTime;
	
			String cmd = sc.nextLine();
		//String cmd = "purchase robert xbox 1";//"list";//
	    cmd += " " + myName;
	    final long startTime = System.nanoTime();
		//for(int i = 0; i < samples; i++)
		//{
			Watcher clientWatcher;
			/*if(i == samples - 1)
			{
				clientWatcher = new Watcher() {
				public void process(WatchedEvent e) {
					try{
						System.out.println(new String(zoo.getData("/" + myName, false, null)));
						zoo.getData("/" + myName, false, null);
						wait = false;
						long elapsedTime = System.nanoTime() - startTime;
						double avgTime = elapsedTime/samples/1000000.0;
						System.out.println("elapsed time: " + avgTime);
					} catch (Exception ex){System.out.println(ex.getMessage());}
				}
				};			
			}
			else
			{*/
				clientWatcher = new Watcher() {
					public void process(WatchedEvent e) {
						try{
							System.out.println(new String(zoo.getData("/" + myName, false, null)));
							//zoo.getData("/" + myName, false, null);
							wait = false;
						} catch (Exception ex){System.out.println(ex.getMessage());}
					}
					};	
			//}
			zoo.getData("/" + myName, clientWatcher, null);
			//System.out.println("watch set");
			wait = true;
			zoo.setData("/command", cmd.getBytes(), -1);
			while(wait){Thread.sleep(1000);}
			//Thread.sleep(1000);
		}
	//}
	//long elapsedTime = System.nanoTime() - startTime;
	//double avgTime = elapsedTime/samples/1000000.0;
	//System.out.println("Average time: " + avgTime + " ms");
	} catch (Exception e){System.out.println(e.getMessage());}

}
}
