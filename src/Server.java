import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;

public class Server
{
	static PrintWriter fileOut;

	public static void main(String args[])
	{
		DatagramSocket sock = null;
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				try
				{
					Thread.sleep(200);
					System.out.println("Shouting down ...");
					//some cleaning up code...
					fileOut.flush();
					fileOut.close();
					echo("Server Closed.");
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		try
		{
			//1. creating a server socket, parameter is local port number
			sock = new DatagramSocket(7777);
			//buffer to receive incoming data
			byte[] buffer = new byte[65536];
			DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);

			//2. Wait for an incoming data
			echo("Server socket created. Waiting for incoming data...");

			fileOut = new PrintWriter(new FileOutputStream(new File("recieved.txt")), true);

			if (!new File("received.txt").isFile())
			{
				// Check if output file exists
				fileOut.println("RECEIVED DATA" + System.lineSeparator() + "==============");
				fileOut.flush();
//				fileOut.close();
			}

//			fileOut = new PrintWriter(new FileOutputStream(new File("recieved.txt")), true);

			//communication loop
			while (true)
			{
				sock.receive(incoming);
				byte[] data = incoming.getData();
				String s = new String(data, 0, incoming.getLength());
				echo(s);

				//echo("after recieve");

				if (s.startsWith("mobile|"))
				{
					fileOut.append(""+LocalDateTime.now()+"		");
					fileOut.append(s.substring(7) + System.lineSeparator());
					fileOut.flush();
					
					String sendBack = "Your message was sent successfully from Server: " + InetAddress.getLocalHost();
					DatagramPacket dp = new DatagramPacket(sendBack.getBytes(), sendBack.getBytes().length,
							incoming.getAddress(), incoming.getPort());

					sock.send(dp);
					
					if (s.toLowerCase().contains("/killserver"))
					{
						fileOut.append(LocalDateTime.now() + System.lineSeparator());
						break;
					} else if (s.toLowerCase().contains("/eraseserver"))
					{
						fileOut.println("RECEIVED DATA" + System.lineSeparator() + "==============");
					}
				}

				if (s.equalsIgnoreCase("/killServer"))
				{
					//					fileOut = new PrintWriter("recieved.txt");
					fileOut.append(""+LocalDateTime.now()+"		");
					fileOut.append(s + System.lineSeparator());
					
					break;
				}//else if (s.equalsIgnoreCase("/eraseServer"))
				//				{
				//					fileOut = new PrintWriter("recieved.txt");
				//					fileOut.append(s + System.lineSeparator());
				//					fileOut.println("RECEIVED DATA" + System.lineSeparator() + "==============");
				//					fileOut.flush();
				//					fileOut.close();
				//				}

				//				fileOut = new PrintWriter("recieved.txt");
				fileOut.append(""+LocalDateTime.now()+"		");
				fileOut.append(s + System.lineSeparator());
				fileOut.flush();
				//				fileOut.close();
			}
			fileOut.flush();
			fileOut.close();
			echo("Server Closed.");
		}

		catch (IOException e)
		{
			System.err.println("IOException " + e);
		}

	}

	//simple function to echo data to terminal
	public static void echo(String msg)
	{
		System.out.println(msg);
	}

	private static String readFile(String path) throws IOException
	{
		String ret = "";
		try (BufferedReader br = new BufferedReader(new FileReader(path)))
		{
			String line = null;
			while ((line = br.readLine()) != null)
			{
				ret += line + System.lineSeparator();
			}
		}
		return ret;
	}

}
