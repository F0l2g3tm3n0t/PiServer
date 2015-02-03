import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONObject;

public class RpiServer {
	public static void main(String[] args) {
		System.out.println("start");
		//receive from client
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				receiveFromClient();
			}
		}).start();
		//receive from pi
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				receiveFromPi();
			}
		}).start();
	}

	protected static void receiveFromPi() {
		// TODO Auto-generated method stub
		ServerSocket serSocket = null;
		Socket socket = null;
		String data = "";
		try{
			serSocket = new ServerSocket(10000);
			while (true) {
				socket = serSocket.accept();
				DataInputStream input = new DataInputStream(socket.getInputStream());
				data = (String)input.readUTF();
				sendDataBroadcast(data, "192.168.42.255", 9999);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendDataBroadcast(String data, String ip, int port) {
		// TODO Auto-generated method stub
		DatagramSocket socket;
		try {
			InetAddress broadcastIP = InetAddress.getByName(ip);
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			byte[] data_byte = data.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(data_byte,data_byte.length,broadcastIP,port);
			socket.send(sendPacket);
			System.out.println("send" + sendPacket.toString());
			socket.close();
		} catch (SocketException e) {
//				 TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected static void receiveFromClient() {
		// TODO Auto-generated method stub
		ServerSocket serSocket = null;
		Socket socket = null;
		String data = "";
		String flag = "";
		try{
			serSocket = new ServerSocket(9999);
			while(true){
				socket = serSocket.accept();
				DataInputStream input = new DataInputStream(socket.getInputStream());
				data = (String)input.readUTF();
				if(data.contains("chatarea")){
					System.out.println("ChatArea From Client");
					System.out.println("Data = " + data.toString());
					sendData(data, getIPList("wlan1"), 10000);
				} else {
					System.out.println("Unknown Data from Client");
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	private static void sendData(String data, ArrayList<String> ipList, int port){
		Socket socket = null;
		DataOutputStream output = null;
		try{
			ipList = getIPList("wlan0");
			for(int i = 0; i < ipList.size(); i++){
				InetAddress ip = InetAddress.getByName(ipList.get(i));
				socket = new Socket(ip, port);
				output = new DataOutputStream(socket.getOutputStream());
				output.writeUTF(data);
				output.flush();
				socket.close();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static ArrayList<String> getIPList(String interfaces){
			String header = "--- Arp ---";
	        ArrayList<String> temp = new ArrayList<String>();
	        try {
	                String arp_command = "arp";
	                Runtime run = Runtime.getRuntime();
	                Process pro = run.exec(arp_command);
	                BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
	                String input = "";
	                while((input = in.readLine()) != null){
	                        if(input.contains(interfaces)){
	                                System.out.println(input);
	                                String[] split = input.split(" ");
	                                System.out.println();
	                                System.out.println(split[0]);
	                                temp.add(split[0]);
	                        }
	                }
	        } catch (Exception e) {
	                System.out.println(header + " " + "Error" + e);
	        }

		return temp;
	}
}
