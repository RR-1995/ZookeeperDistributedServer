import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import org.apache.log4j.Logger;
//import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;


public class Server{

	public static ZooKeeper zoo;
	public static boolean isConnected = false;
	public static void main(String[] args){

		org.apache.log4j.BasicConfigurator.configure();
		Logger.getLogger("org.apache.zookeeper").setLevel(Level.WARN);

		//String host = "192.168.56.101:2181";
		String host = "192.168.56.101:2181,192.168.56.102:2181,192.168.56.103:2181";
		String inventoryPath = "inventory.txt";
		System.out.printf("starting");

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
	
			//zoo.delete("/store/shoes", -1);
		zoo.create("/command" , placeholder, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	
		zoo.create("/store", placeholder, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

		  //format "customername item quantity"
    	zoo.create("/orders", placeholder, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    	//format "#, #, #"
    	zoo.create("/customers", placeholder, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	} catch (KeeperException e){}
		catch (InterruptedException ex){}
	try{
    // parse the inventory file
  BufferedReader br = new BufferedReader(new FileReader(new File(inventoryPath)));
    	String line;
    	while((line = br.readLine()) != null)
    	{
    		line = line.trim();
    		if(!line.equals(""))
    		{
    			String[] info = line.split(" ");
					byte[] itemCount = {(byte) Integer.parseInt(info[1])};
					zoo.create("/store/" + info[0], itemCount, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    		}
    	}
 		//byte[] shoeCount = {20};
		//zoo.create("/store/shoes", shoeCount, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    	//set watch
		}catch (KeeperException e){}
		catch (InterruptedException ex){}
		catch(IOException e) {}
		/*for (String thisProduct : myStore.list().keySet()){
	    byte[] data = {myStore.list().get(thisProduct)};
			zoo.create("/store/" + thisProduct, data , ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.);
		}
			zoo.create("/user" , ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);*/
	try{
		zoo.getData("/command", new CommandWatcher(zoo), null);
			System.out.println("watching command kkkkkkkkkkkkkkkk");
	} catch (KeeperException e){}
		catch (InterruptedException ex){}
		System.out.println("waiting");
		
		try{
		while (true){
			Thread.sleep(1000);
		}
	} catch (InterruptedException e){}
		
	} 


}
