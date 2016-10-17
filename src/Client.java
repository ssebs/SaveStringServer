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
			if (args.length < 1)
			{
				host = InetAddress.getByName("localhost");
			} else
			{
				host = InetAddress.getByName(args[0]);
			}
			sock = new DatagramSocket();

			recieve();

			while (true)
			{
				sendMsg();
			}

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
		} else if (s.equalsIgnoreCase("/killServer"))
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
		} else
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

	private static void recieve()
	{
		receive = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				//now receive reply
				//buffer to receive incoming data
				byte[] buffer = new byte[65536];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				try
				{
					sock.receive(reply);

					byte[] data = reply.getData();
					s = new String(data, 0, reply.getLength());

					//echo the details of incoming data - client ip : client port - client message
					System.out.println(reply.getAddress().getHostAddress() + " : " + reply.getPort() + " - " + s);

				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
		receive.start();

	}

}
