package live.labaguettedev.mytransfer.presenters;

import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import live.labaguettedev.mytransfer.utils.FileReceiver;


public class FileReceiverPresenter {

    IReceiverScreen receiverScreen;
    WifiManager wifi;

    ServerSocket server;
    Socket client;

    boolean exit = false;

    public interface IReceiverScreen {
        void startReceiving();
        void finishReceiving();
    }

    public FileReceiverPresenter(IReceiverScreen receiverScreen, WifiManager wifi) {
        this.receiverScreen = receiverScreen;
        this.wifi = wifi;
    }

    public void sendMulticastMessage() {
        WifiManager.MulticastLock multicastLock = wifi.createMulticastLock("multicastLock");
        multicastLock.setReferenceCounted(true);
        multicastLock.acquire();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MulticastSocket multicastSocket = new MulticastSocket(9876);

                        int ipAddress = wifi.getConnectionInfo().getIpAddress();
                        // Convert little-endian to big-endianif needed
                        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                            ipAddress = Integer.reverseBytes(ipAddress);
                        }

                        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

                        String ipAddressString;
                        try {
                            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
                        } catch (UnknownHostException ex) {
                            ipAddressString = "JSP";
                            ex.printStackTrace();
                        }


                        String message = "ANDROID / " + ipAddressString;
                        while (!exit) {
                            DatagramPacket dp = new DatagramPacket(message.getBytes(StandardCharsets.UTF_8), message.length(),
                                    InetAddress.getByName("239.1.1.234"),
                                    9876);

                            multicastSocket.send(dp);
                            System.out.println("SEND " + message);
                            Thread.sleep(1000);
                        }
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            t.start();

            try {
                server = new ServerSocket(5464);
                client = server.accept();
                System.out.println("Connecté");
            } catch (IOException e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                receiverScreen.startReceiving();
            });
        });
    }

    public void cancel() {
        exit = true;
    }

    public void waitForFile() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            try {
                InputStream in = client.getInputStream();
                DataInputStream dataIn = new DataInputStream(in);

                String message = dataIn.readUTF();
                System.out.println("Message reçu : " + message);

                exit = true;

                FileReceiver fr = new FileReceiver(Environment.getExternalStorageDirectory().getPath());
                System.out.println("Démarrage reception du fichier");

                fr.receiveFile(client.getInputStream(), message.substring(0, message.indexOf('/')), Long.parseLong(message.substring(message.indexOf('/') + 1)));
                System.out.println("Fichier reçu");
            } catch (IOException e) {
                e.printStackTrace();
            }
            handler.post(() -> {
                receiverScreen.finishReceiving();
            });
        });
    }
}
