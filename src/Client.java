import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client
{
	static boolean isRunning = true;

	static Scanner scan = new Scanner(System.in);
	static DatagramSocket sock;
	static int port = 7777;
	static String s;
	static InetAddress host;
	static byte[] b;
	static Thread receive;

	public static void main(String args[])
	{
		try
		{
			// Conditional checks if there's a flag, if so it will use it as an IP 
			//		to connect to, otherwise it will default to 'localhost'
			if (args.length < 1)
				host = InetAddress.getByName("localhost");
			else
				host = InetAddress.getByName(args[0]);

			// Create UDP socket 
			sock = new DatagramSocket();

			// Start receive loop thread
			recieve();

			while (isRunning)
				sendMsg();

		} catch (IOException e)
		{
			System.err.println("IOException " + e);
		}
	}

	private static void sendMsg()
	{
		//take input and send the packet
		System.out.print("Enter message to send : ");
		String s = scan.nextLine();

		if (s.equalsIgnoreCase("/kill") || s.equalsIgnoreCase("/stop") || s.equalsIgnoreCase("/exit")
				|| s.equalsIgnoreCase("/quit"))
		{
			/*
			 * FIRST CHECK
			 * Will check if you want to exit the Client
			 * 
			 */
			System.out.println("Exiting...");
			try
			{
				// Will end receive loop
				isRunning = false;
				// Will stop loop. TODO: Figure out if this is needed.
				receive.interrupt();
				Thread.sleep(200);
				System.exit(0);

			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		/*
		 * SECOND CHECK
		 * Will check if you want to stop the server, will also exit Client
		 * 
		 */
		else if (s.equalsIgnoreCase("/killServer"))
		{
			b = s.getBytes();

			try
			{
				sock.send(new DatagramPacket(b, b.length, host, port));
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			System.out.println("Exiting...");
			try
			{
				receive.interrupt();
				Thread.sleep(200);
				System.exit(0);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		/*
		 * THIRD CHECK
		 * Will send data from System.in to server using socket.
		 * 
		 */
		else
		{

			b = s.getBytes();

			try
			{
				sock.send(new DatagramPacket(b, b.length, host, port));
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}

	/**
	 * This will receive data back from the UDP Server.
	 * Once received, it will echo it out.
	 * Needs to be in seperate thread so it can run in the background while the 
	 * 		main thread listens for input
	 * */
	private static void recieve()
	{
		receive = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				//buffer to receive incoming data
				byte[] buffer = new byte[65536];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

				try
				{
					while (isRunning)
					{
						sock.receive(reply);
						byte[] data = reply.getData();
						s = new String(data, 0, reply.getLength());
						//echo the details of incoming data - client ip : client port - client message
						System.out.println(reply.getAddress().getHostAddress() + " : " + reply.getPort() + " - " + s);
					}

				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
		receive.start();
	}

}
