import java.io.IOException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.BasicConfigurator;
public class Client{

	static ZooKeeper zoo;
	static String myName = "Client1";
	static boolean isConnected = false;
	public static void main(String[] args){

		org.apache.log4j.BasicConfigurator.configure();

		//String host = "192.168.56.101:2181";
		String host = "192.168.56.101:2181,192.168.56.102:2181,192.168.56.103:2181";
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
} catch (IOException e){}
	catch (InterruptedException e){}
	byte[] placeholder = {'0'};
		try{
		zoo.create("/" + myName, placeholder, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

		Watcher clientWatcher = new Watcher() { 
    public void process(WatchedEvent e) {
			try{
				String thisString = new String(zoo.getData("/" + myName, false, null));
				System.out.println(thisString);
			} catch (KeeperException ex){}
				catch (InterruptedException ex){}
    }
};
		zoo.getData("/" + myName, clientWatcher, null);
		System.out.println("kkkkkkkk" + "/" + myName);
	} catch (KeeperException ex){}
		catch (InterruptedException ex){}
			//assume a client has typed the message
	String message = "purchase John shoes 2";
	String sentMessage = message + " " + myName;
	byte[] sentMessageByte = sentMessage.getBytes();
	try{
	zoo.setData("/command", sentMessageByte, -1);
		Watcher clientWatcher = new Watcher() { 
    public void process(WatchedEvent e) {
			try{
				String thisString = new String(zoo.getData("/" + myName, false, null));
				System.out.println(thisString);
			} catch (KeeperException ex){}
				catch (InterruptedException ex){}
    }
};
		zoo.getData("/" + myName, clientWatcher, null);
	zoo.setData("/command", sentMessageByte, -1);
	System.out.println("sent");
	System.out.println(new String(zoo.getData("/" + myName, false, null)));
	while (true){Thread.sleep(1000);}
	} catch (KeeperException e){}
	catch (InterruptedException e){}

}
}
