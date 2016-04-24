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

public class CommandWatcher implements Watcher{
	static ZooKeeper zoo;

	public CommandWatcher(ZooKeeper zooParam){
		zoo = zooParam;
		System.out.println("constructed");
	}
	
	@Override
	public void process(WatchedEvent e){
		try{
		byte[] recievedCommandByte = zoo.getData("/command", false, null);
				String recievedCommand = new String(recievedCommandByte);
				System.out.println(recievedCommand);
				String[] tokens = recievedCommand.split(" ");
				if (tokens[0].equals("purchase")){
					String name = tokens[1];
	        String item = tokens[2];
					String quantityString = tokens[3];
					String clientName = tokens[4];
					int purchasedQuantity = Integer.parseInt(quantityString);
					//ignore item not exsist or run out for now
					//ignore User and order data for now
					int quantity = (zoo.getData("/store/" + item, false, null)[0]);
					quantity = quantity - purchasedQuantity;
					byte[] byteQuantity = {(byte)quantity};
					zoo.setData("/store/" + item, byteQuantity, -1);
					String reply = "Your order has been placed, ## " + name + " " + item + " " + quantity;
					byte[] replyByte = reply.getBytes();
					zoo.setData("/" + clientName, replyByte, -1);
					//set watcher again
					zoo.getData("/command", new CommandWatcher(zoo), null);
					//for debugging
					System.out.println(item + " " + quantity);
			}
		} catch (KeeperException ex){}
			catch (InterruptedException ex){}
					
	}
	
	
}

