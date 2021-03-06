package zookeeper;
// import zookeeper classes
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

public class CommandWatcher implements Watcher{
	static ZooKeeper zoo;
	static int commandCount = 0;
	public CommandWatcher(ZooKeeper zooParam){
		zoo = zooParam;
	}
	
	@Override
	public void process(WatchedEvent e){
		try{
			zoo.create("/commandNum/" + commandCount, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			commandCount += 1;
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
					//no item or run out
					//return null Stat object
					if (zoo.exists("/store/" + item, false) == null){
						String reply = "`Not Available- We do not sell this product";
						byte[] replyByte = reply.getBytes();
						zoo.setData("/" + clientName, replyByte, -1);
					}
					else{
						//change store
						int quantity = Integer.parseInt(new String(zoo.getData("/store/" + item, false, null)));
						if(purchasedQuantity > quantity)
						{
							String reply = "`Not Available- Not Enough Items";
							byte[] replyByte = reply.getBytes();
							zoo.setData("/" + clientName, replyByte, -1);
						}
						else 
						{
							quantity = quantity - purchasedQuantity;
							byte[] byteQuantity = String.valueOf(quantity).getBytes();
							zoo.setData("/store/" + item, byteQuantity, -1);
							//change order
							int orderCount = Integer.parseInt(new String(zoo.getData("/orderNum", false, null)));
							zoo.create("/orders/" + orderCount, recievedCommandByte, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
							
							//customer exists
							byte[] placeholder = {0};
							if (zoo.exists("/customers/" + name, null) == null){
								zoo.create("/customers/" + name, placeholder, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
							}
							zoo.create("/customers/" + name + "/" + orderCount, placeholder, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
							orderCount += 1;
							zoo.setData("/orderNum", ("" + orderCount).getBytes(), -1);
							String reply = "Your order has been placed, ## " + name + " " + item + " " + purchasedQuantity;
							byte[] replyByte = reply.getBytes();
							zoo.setData("/" + clientName, replyByte, -1);
							//for debugging
							System.out.println(item + " " + quantity);
						}
						
					}
				}
				else if (tokens[0].equals("cancel")){
					int orderNum = Integer.parseInt(tokens[1]);
					//no such order
					if (zoo.exists("/orders/" + orderNum, false) == null){
						String reply = orderNum + " not found, no such order";
						byte[] replyByte = reply.getBytes();
						zoo.setData("/" + tokens[2], replyByte, -1);
					}
					else{
						String orderType = new String(zoo.getData("/orders/" + orderNum, false, null));
						String[] orderInfo = orderType.split(" ");
						String userName = orderInfo[1];
						String itemType = orderInfo[2];
						int purchasedNum = Integer.parseInt(orderInfo[3]);
						//edit store
						int quantity = Integer.parseInt(new String(zoo.getData("/store/" + itemType, false, null)));
						quantity = quantity + purchasedNum;
						byte[] byteQuantity = String.valueOf(quantity).getBytes();
						zoo.setData("/store/" + itemType, byteQuantity, -1);
						//delete order
						zoo.delete("/orders/" + orderNum, -1);
						//delete customer order
						zoo.delete("/customers/" + userName + "/" + orderNum, -1);
						//reply
						String reply = "Order " + orderNum + " is canceled";
						byte[] replyByte = reply.getBytes();
						zoo.setData("/" + tokens[2], replyByte, -1);
					}
				}
				else if (tokens[0].equals("search")){
					List<String> children = zoo.getChildren("/customers/" + tokens[1], false);
					String response = "";
					for (String thisChild : children){
						String data = new String(zoo.getData("/orders/" + thisChild, false, null));
						String[] dataTokens = data.split(" ");
						response += (thisChild + " " + dataTokens[2] + " " +  dataTokens[3] + "\n");
						
					}
					//send response
					byte[] replyByte = response.getBytes();
					zoo.setData("/" + tokens[2], replyByte, -1);
				}
				else if (tokens[0].equals("list")){
					List<String> children = zoo.getChildren("/store", false);
					String response = "";
					for (String thisChild : children){
						String data = new String(zoo.getData("/store/" + thisChild, false, null));
						response += (thisChild + " " + data + "\n");
						
					}
					//send response
					byte[] replyByte = response.getBytes();
					zoo.setData("/" + tokens[1], replyByte, -1);
					
				}
		} catch (KeeperException ex){commandCount += 1; System.out.println(ex.getMessage());} 
		catch (Exception ex){System.out.println(ex.getMessage());}
		
		try{
			//set watcher again
			zoo.getData("/command", new CommandWatcher(zoo), null);
			System.out.println("watch set");
		} catch (Exception ex){System.out.println(ex.getMessage());}
					
	}
	
	
}

