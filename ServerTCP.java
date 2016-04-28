import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

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

public class Server {

  private static int maxlen, N, tcpPort, udpPort;
  public static ZooKeeper zoo;
  private static int orderCount = 0;
  private static boolean isConnected = false;
  public static void main (String[] args) {
    maxlen = 128;
  /*  
    if (args.length != 3) {
      System.out.println("ERROR: Provide 3 arguments");
      System.out.println("\t(1) <tcpPort>: the port number for TCP connection");
      System.out.println("\t(2) <udpPort>: the port number for UDP connection");
      System.out.println("\t(3) <file>: the file of inventory");

      System.exit(-1);
    }
*/
    org.apache.log4j.BasicConfigurator.configure();
		Logger.getLogger("org.apache.zookeeper").setLevel(Level.WARN);

		String host = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
		//String host = "192.168.56.101:2181,192.168.56.102:2181,192.168.56.103:2181";
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
			
			System.out.println("Connection made");

			byte[] placeholder = {'0'};
		try{
	
		zoo.create("/client1", placeholder, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			//zoo.delete("/store/shoes", -1);
		zoo.create("/command" , placeholder, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
	
		
		zoo.create("/store", placeholder, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		

		  //format "customername item quantity"
    	zoo.create("/orders", placeholder, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    	//format "#, #, #"
    	zoo.create("/customers", placeholder, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	} catch (KeeperException e){e.printStackTrace();}
		catch (InterruptedException e){e.printStackTrace();}
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
		}catch (KeeperException e){e.printStackTrace();}
		catch (InterruptedException e){e.printStackTrace();}
		catch(IOException e) {e.printStackTrace();}
		/*for (String thisProduct : myStore.list().keySet()){
	    byte[] data = {myStore.list().get(thisProduct)};
			zoo.create("/store/" + thisProduct, data , ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.);
		}
			zoo.create("/user" , ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);*/
    
    tcpPort = 7050;//Integer.parseInt(args[0]);
    
    TCP t = new TCP();
    t.start();

    System.out.println("TCP listening");
    
  }

  	// services a client UDP request

  	// listen on TCP port and fork to service requests
  	private static class TCP extends Thread{
  		public TCP(){
  			
  		}
	  	public void run(){
	  	// TODO: handle request from clients
	  		try{
		  	    ServerSocket ss = new ServerSocket(tcpPort);
		  		while(true){
		  			Socket s = ss.accept();
		  			TCPResponse t = new TCPResponse(s);
		  			t.start();
		  	  	}
	  		}
	  		catch(Exception e){
	  			e.printStackTrace();
	  		}
	  	}
  	}
  	// handles response for TCP client
  	private static class TCPResponse extends Thread{
  		private Socket s;
  		public TCPResponse(Socket so){
  			s = so;
  		}
	  	public void run(){
	  		if(s == null)
	  			return;

	  	// TODO: handle request from clients
	  		try{
		    	BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		    	PrintStream out = new PrintStream(s.getOutputStream());
	  			String tokens = in.readLine();
		    	
	  			String response = createResponse(tokens);
	  			
	  			out.println(response);
	  			out.flush();
	  			
	  			s.close();
	  		}
	  		catch(Exception e){
	  			System.out.println("2");
	  			e.printStackTrace();
	  		}
	  	}
  	}
  	// handles the general logic for the store
	private static String createResponse(String string){
				String [] tokens = string.split(" ");
				byte[] recievedCommandByte = string.getBytes();

			try{
				if (tokens[0].equals("purchase")){
					String name = tokens[1];
					String item = tokens[2];
					String quantityString = tokens[3];
					String clientName = name;
					int purchasedQuantity = Integer.parseInt(quantityString);
					//no item or run out
					//return null Stat object
					if (zoo.exists("/store/" + item, false) == null){
						String reply = "`Not Available- We do not sell this product";
						byte[] replyByte = reply.getBytes();
						//zoo.setData("/" + clientName, replyByte, -1);
						return reply;
					}
					else if (zoo.getData("/store/" + item, false, null)[0] == 0){
						String reply = "`Not Available- Not Enough Items";
						byte[] replyByte = reply.getBytes();
						//zoo.setData("/" + clientName, replyByte, -1);
						return reply;
					}
					else{
						//change store
						int quantity = (zoo.getData("/store/" + item, false, null)[0]);
						quantity = quantity - purchasedQuantity;
						byte[] byteQuantity = {(byte)quantity};
						zoo.setData("/store/" + item, byteQuantity, -1);
						//change order
						zoo.create("/orders/" + orderCount, recievedCommandByte, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
						
						//customer exsits
						byte[] placeholder = {0};
						if (zoo.exists("/customers/" + clientName, null) != null){
							zoo.create("/customers/" + clientName + "/" + orderCount, placeholder, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
						}
						else{
							zoo.create("/customers/" + clientName, placeholder, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
							zoo.create("/customers/" + clientName + "/" + orderCount, placeholder, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
						}
						orderCount += 1;
						String reply = "Your order has been placed, ## " + name + " " + item + " " + quantity;
						byte[] replyByte = reply.getBytes();
						//zoo.setData("/" + clientName, replyByte, -1);
						//for debugging
						System.out.println(item + " " + quantity);
						return reply;
					}
				}
				else if (tokens[0].equals("cancel")){
					int orderNum = Integer.parseInt(tokens[1]);
					//no such order
					if (zoo.exists("/orders/" + orderNum, false) == null){
						String reply = orderNum + " not found, no such order";
						byte[] replyByte = reply.getBytes();
						zoo.setData("/" + tokens[2], replyByte, -1);
						return reply;
					}
					else{
						String orderType = new String(zoo.getData("/orders/" + orderNum, false, null));
						String[] orderInfo = orderType.split(" ");
						String userName = orderInfo[1];
						String itemType = orderInfo[2];
						int purchasedNum = Integer.parseInt(orderInfo[3]);
						//edit store
						int quantity = (zoo.getData("/store/" + itemType, false, null)[0]);
						quantity = quantity + purchasedNum;
						byte[] byteQuantity = {(byte)quantity};
						zoo.setData("/store/" + itemType, byteQuantity, -1);
						//delete order
						zoo.delete("/orders/" + orderNum, -1);
						//delete customer order
						zoo.delete("/customers/" + userName + "/" + orderNum, -1);
						//reply
						String reply = "Order " + orderNum + " is canceled";
						byte[] replyByte = reply.getBytes();
						zoo.setData("/" + userName, replyByte, -1);
						return reply;
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
					zoo.setData("/" + tokens[1], replyByte, -1);
					return response;
				}
				else if (tokens[0].equals("list")){
					List<String> children = zoo.getChildren("/store", false);
					String response = "";
					for (String thisChild : children){
						int data = zoo.getData("/store/" + thisChild, false, null)[0];
						response += (thisChild + " " + data + "\n");
						
					}
					//send response
					byte[] replyByte = response.getBytes();
					//zoo.setData("/" + tokens[1], replyByte, -1);
					return response;
				}
		} catch (KeeperException e){e.printStackTrace();}
			catch (InterruptedException e){e.printStackTrace();}

		return "the error is " + tokens[0].length();
	}

}