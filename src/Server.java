import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;

public class Server
{
	private static PrintWriter fileOut;
	
	//TODO: Make literals in to variables.

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
					System.out.println("Shutting down ...");
					fileOut.flush();
					fileOut.close();
					echo("Server Closed.");
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		});
		try
		{
			//Creating a server socket, parameter is local port number
			sock = new DatagramSocket(7777);
			//buffer to receive incoming data
			byte[] buffer = new byte[65536];
			DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);

			//Wait for an incoming data
			echo("Server socket created. Waiting for incoming data...");

			//Making a PrintWriter to log the data that comes in. true refers to append mode. 
			fileOut = new PrintWriter(new FileOutputStream(new File("recieved.txt")), true);

			if (!new File("received.txt").isFile())
			{
				// Check if output file exists, if not it will create this.
				fileOut.println("RECEIVED DATA" + System.lineSeparator() + "==============");
				fileOut.flush();
			}

			//communication loop. Loop can be broken using <CTRL> + <C>, or from a client entering "/killserver"
			while (true)
			{
				// Will wait for the UDP socket to get a signal, then receive the data.
				// Hopefully they don't happen at same time? Make sync
				sock.receive(incoming);
				byte[] data = incoming.getData();
				
				// Save data as String.
				// TODO: Make this not make a String every time.
				String s = new String(data, 0, incoming.getLength());
				
				echo(s);

				if (s.startsWith("mobile|"))
				{
					fileOut.append("" + LocalDateTime.now() + "		");
					fileOut.append(s + System.lineSeparator());
					fileOut.flush();

					String sendBack = "Your message was sent successfully to Server: " + InetAddress.getLocalHost();
					DatagramPacket dp = new DatagramPacket(sendBack.getBytes(), sendBack.getBytes().length,
							incoming.getAddress(), incoming.getPort());

					sock.send(dp);

					if (s.toLowerCase().contains("/killserver"))
					{
						fileOut.append(LocalDateTime.now() + System.lineSeparator());
						break;
					} else if (s.toLowerCase().contains("/eraseserver"))
					{
						//TODO: Fix this, as of now it only appends. 
						fileOut.println("RECEIVED DATA" + System.lineSeparator() + "==============");
					}
				} else
				{

					if (s.equalsIgnoreCase("/killServer"))
					{
						// Arbitrary spaces are for formatting, they match the mobile version.
						fileOut.append("" + LocalDateTime.now() + "		");
						fileOut.append("       " + s + System.lineSeparator());
						break;
					} else if (s.toLowerCase().contains("/eraseserver"))
					{
						//TODO: Fix this, as of now it only appends. 
						fileOut.println("RECEIVED DATA" + System.lineSeparator() + "==============");
					}
					// Arbitrary spaces are for formatting, they match the mobile version.
					fileOut.append("" + LocalDateTime.now() + "		");
					fileOut.append("       " + s + System.lineSeparator());
					fileOut.flush();
				}
			} // End communication loop
		}

		catch (IOException e)
		{
			System.err.println("IOException " + e);
		}

	}

	// Shorter version of Java's Println.
	public static void echo(String msg)
	{
		System.out.println(msg);
	}
}