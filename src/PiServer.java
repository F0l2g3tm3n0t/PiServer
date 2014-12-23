import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONArray;


public class PiServer extends JSONParser{
	private static String data;
	private static JSONParser jsonparser = new JSONParser();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("start Receive From Client Server");
				receiveFromClient();
			}
		}).start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("start Receive From PI Server");
				receiveFromPi();
			}
		}).start();
	}

	private static void receiveFromPi() {
		// TODO Auto-generated method stub
		ServerSocket serSocket = null;
		Socket socket =null;
		DatagramSocket clientSocket = null;
			try {
				serSocket = new ServerSocket(11111);
				while(true) {
					try {
						socket = serSocket.accept();
						DataInputStream input = new DataInputStream(socket.getInputStream());
						data = (String)input.readUTF();
			            System.out.println(data);
			            if(data != null){
			            	 byte[] data_byte = data.getBytes();
			                 try {
			         	        InetAddress broadcastIP = InetAddress.getByName("192.168.42.255");
			         	        clientSocket = new DatagramSocket();
			                    clientSocket.setBroadcast(true);
			                    DatagramPacket packet = new DatagramPacket(data_byte, data_byte.length, broadcastIP, 9999);
			                    clientSocket.send(packet);
			         	        clientSocket.close();
			                 } catch (Exception e) {
			                    e.printStackTrace();
			         	        if(clientSocket != null) {
			         		        clientSocket.close();
			         	        }
			                 }
			            }
					} catch (IOException e) {
						e.printStackTrace();
					} 
				} //end while(true)
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
	}

	@SuppressWarnings("resource")
	private static void receiveFromClient() {
		// TODO Auto-generated method stub
		ServerSocket serSocket = null;
		Socket socket = null;	
		DatagramSocket piSocket = null;
		try {
			serSocket = new ServerSocket(9999);
			while(true) {
				try {
					socket = serSocket.accept();
					DataInputStream input = new DataInputStream(socket.getInputStream());
					data = (String)input.readUTF();
		            System.out.println(data);
		            if(data != null){
		            	 byte[] data_byte = data.getBytes();
		                 try {
		         	        //InetAddress broadcastIP = InetAddress.getByName("1.1.1.255");
		         	        piSocket = new DatagramSocket();
		                    piSocket.setBroadcast(true);
		                    JSONArray json  = jsonparser.getNeighbor(piSocket.getLocalAddress().getHostAddress());
		                    if(json.toString() != null) {
		                    	for(int i = 0; i < json.length(); i++){
		                    		DatagramPacket packet = new DatagramPacket(data_byte, data_byte.length, InetAddress.getByName(json.get(i).toString()), 11111);
				                    piSocket.send(packet);
		                    	}
		                    }
		         	        piSocket.close();
		                 } catch (Exception e) {
		                    e.printStackTrace();
		         	        if(piSocket != null) {
		         		        piSocket.close();
		         	        }
		                 }
		            }
				} catch (IOException e) {
					e.printStackTrace();
				} 
			} //end while(true)
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	} //end method receiveFromClient() and broadcast to PI
	
	
}
