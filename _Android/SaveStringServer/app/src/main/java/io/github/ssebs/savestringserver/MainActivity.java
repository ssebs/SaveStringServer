package io.github.ssebs.savestringserver;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

	private GoogleApiClient client;

	static DatagramSocket sock;
	static int port = 7777;
	static String s;
	static InetAddress host;
	static byte[] b;
	static Thread receive;

	String serverIP;
	String textToSend;
	String replyData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

	}


	protected void submit(View v) {
		serverIP = ((EditText) findViewById(R.id.server_ip)).getText().toString();
		textToSend = ((EditText) findViewById(R.id.txtToSend)).getText().toString();

		Thread init = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (serverIP.length() < 1)
						host = InetAddress.getByName("localhost");
					else
						//host = InetAddress.getByName(serverIP);

						host = InetAddress.getByName("ssebs.ddns.net");

					sock = new DatagramSocket();

					receive();
					sendMsg("mobile|" + textToSend);
				} catch (IOException e) {
					System.err.println("IOException " + e);
				}
			}
		});
		init.start();

	}

	private void receive() {
		receive = new Thread(new Runnable() {
			@Override
			public void run() {
				//now receive reply
				//buffer to receive incoming data
				byte[] buffer = new byte[65536];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				try {
					sock.receive(reply);

					byte[] data = reply.getData();
					replyData = new String(data, 0, reply.getLength());

					//echo the details of incoming data - client ip : client port - client message
					// System.out.println(reply.getAddress().getHostAddress() + " : " + reply.getPort() + " - " + s);

					//Toast.makeText(MainActivity.this, replyData, Toast.LENGTH_SHORT).show();
					// ^^^ This will crash the app

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(MainActivity.this, replyData, Toast.LENGTH_SHORT).show();
						}
					});


				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		receive.start();

	}


	private void sendMsg(String s) {
		//take input and send the packet
		//System.out.print("Enter message to send : ");

		b = s.getBytes();

		try {
			sock.send(new DatagramPacket(b, b.length, host, port));
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
}