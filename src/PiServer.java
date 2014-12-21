import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class PiServer {
	private static String data;
	
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
			serSocket = new ServerSocket(10000);
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
		} catch (Exception e) {
			// TODO: handle exception
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
		         	        InetAddress broadcastIP = InetAddress.getByName("1.1.1.255");
		         	        piSocket = new DatagramSocket();
		                    piSocket.setBroadcast(true);
		                    DatagramPacket packet = new DatagramPacket(data_byte, data_byte.length, broadcastIP, 10000);
		                    piSocket.send(packet);
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
