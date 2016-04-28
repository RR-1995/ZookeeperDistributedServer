import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.*;
import java.util.*;

// client class
public class Client {
  public static void main (String[] args) {
    String hostAddress;
    Socket tcpServer;
    BufferedReader in;
    PrintStream out;
    InetAddress i;
    int tcpPort;
    int maxlen = 128;
    int timeoutValue = 100;
    String my_name = "client1";

   	Scanner keyb = new Scanner(System.in);
    ArrayList<InetSocketAddress> servers = new ArrayList<InetSocketAddress>();

    // read in servers list
    int numServers = 1;//Integer.parseInt(keyb.next());
   // for(int x = 0; x < numServers; x++){
    	String val[] = "127.0.0.1:7050".split(":");
    	servers.add(new InetSocketAddress(val[0], Integer.parseInt(val[1])));
    //}
    //keyb.nextLine();

    // which server to try to connect to
    int current_server = 0;

	try{
	    i = servers.get(current_server).getAddress();
	    tcpPort = servers.get(current_server).getPort();
	    while(keyb.hasNextLine()) {
	    	try{
		      String cmd = keyb.nextLine() + " " + my_name;
		     // System.out.println(cmd);
		      
		      if(cmd.length() == 0)
		      	continue;

		      String[] tokens = cmd.split(" ");
		      
		      byte[] buf = cmd.getBytes();
		      
		      if (tokens[0].equals("purchase")) {	    	 
		      } else if (tokens[0].equals("cancel")) {
		      } else if (tokens[0].equals("search")) {
		      } else if (tokens[0].equals("list")) {
		      } else {
		        System.out.println("ERROR: No such command");
		        continue;
		      }
			  
			  boolean timeout = true;

			  // try again on another server if timeout
			  while(timeout){
			  	  try{
			    	  tcpServer = new Socket(i, tcpPort);
			    	  tcpServer.setSoTimeout(timeoutValue);
			    	  
			    	  in = new BufferedReader(new InputStreamReader(tcpServer.getInputStream()));
			    	  out = new PrintStream(tcpServer.getOutputStream());
			    	  
			    	  long startTime = System.nanoTime();
			    	  out.println(cmd);
			    	  out.flush();
			    	
			    	  System.out.println("Server Response:");
			    	  
			    	  String response = in.readLine();
			    	  while(response != null){
				    	  System.out.println(response);
				    	  response = in.readLine();
			    	  }

			    	  long stopTime = System.nanoTime();

			    	  System.out.println((stopTime - startTime) / 1000000 + " ms");

			    	  timeout = false;
		    	  }

		    	  	// for exceptions, switch servers
				    catch(SocketTimeoutException e){
				    	while(current_server == 0){}
		  				current_server++;
		  				if(current_server == servers.size()){
		  					System.out.println("Server Timeout, switching servers");
		  					System.out.println("All Servers Down");
		  					return;
		  				}
	    				i = servers.get(current_server).getAddress();
	    				tcpPort = servers.get(current_server).getPort();
		  				System.out.println("Server Timeout, switching servers");
					}			    
					catch(ConnectException e){
		  				current_server++;
		  				if(current_server == servers.size()){
		  					System.out.println("Server Crashed, switching servers");
		  					System.out.println("All Servers Down");
		  					return;
		  				}
	    				i = servers.get(current_server).getAddress();
	    				tcpPort = servers.get(current_server).getPort();
		  				System.out.println("Server Crashed, switching servers");
					}
	    	  }
		    }  	
		    catch(Exception e){
		    	e.printStackTrace();
		    }
	    }
    }

    catch(Exception e){
		e.printStackTrace();
    }
  }
}
