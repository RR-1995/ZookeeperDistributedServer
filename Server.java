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

public class Server{

	public static ZooKeeper zoo;
	public static boolean isConnected = false;
	public static void main(String[] args){

		org.apache.log4j.BasicConfigurator.configure();

		//String host = "192.168.56.101:2181";
		String host = "192.168.56.101:2181,192.168.56.102:2181,192.168.56.103:2181";
		String inventoryPath = "inventory.txt";
		System.out.printf("starting");
		//Store myStore = new Store();
    // parse the inventory file
   /* try (BufferedReader br = new BufferedReader(new FileReader(new File(inventoryPath))))
    {
    	String line;
    	while((line = br.readLine()) != null)
    	{
    		line = line.trim();
    		if(!line.equals(""))
    		{
    			String[] info = line.split(" ");//\\s+
    			myStore.products.add(info[0], Integer.parseInt(info[1]));
    		}
    	}
    }catch(Exception e)
    {
    	e.printStackTrace();
    }*/

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
		byte[] shoeCount = {20};
		zoo.create("/store/shoes", shoeCount, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		//set watch
		zoo.getData("/command", new CommandWatcher(zoo), null);
			System.out.println("watching command kkkkkkkkkkkkkkkk");
	} catch (KeeperException e){}
		catch (InterruptedException ex){}
		/*for (String thisProduct : myStore.list().keySet()){
	    byte[] data = {myStore.list().get(thisProduct)};
			zoo.create("/store/" + thisProduct, data , ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}

			zoo.create("/user" , ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);*/
		System.out.println("waiting");
		
		try{
		while (true){
			Thread.sleep(1000);
		}
	} catch (InterruptedException e){}
		
	} 


}
