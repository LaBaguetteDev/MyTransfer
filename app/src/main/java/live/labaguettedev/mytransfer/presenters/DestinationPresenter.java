package live.labaguettedev.mytransfer.presenters;

import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import live.labaguettedev.mytransfer.model.Destination;

public class DestinationPresenter {

    private List<Destination> destination = new ArrayList<>();
    IDestinationScreen destinationScreen;
    WifiManager wifi;

    public interface IDestinationItemScreen {
        void showDestination(String name, String type, String ip);
    }

    public interface IDestinationScreen {
        void refreshView(List<Destination> destinationList);
        void refreshView();
    }

    public DestinationPresenter(IDestinationScreen destinationScreen, WifiManager wifi) {
        this.destinationScreen = destinationScreen;
        this.wifi = wifi;

        searchForDestination();
    }

    public void setDestination(List<Destination> destination) {
        this.destination = destination;
    }

    private void searchForDestination() {
        WifiManager.MulticastLock multicastLock = wifi.createMulticastLock("multicastLock");
        multicastLock.setReferenceCounted(true);
        multicastLock.acquire();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        AtomicReference<String> received = new AtomicReference<>("");
        AtomicReference<String> ipAddr = new AtomicReference<>("");
        executorService.execute(() -> {
            try {
                MulticastSocket socket = new MulticastSocket(9876);
                InetAddress group = InetAddress.getByName("239.1.1.234");
                socket.joinGroup(group);

                DatagramPacket packet;
                byte[] buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                received.set(new String(packet.getData(), 0, buf.length));
                ipAddr.set(packet.getAddress().toString());

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            handler.post(() -> {
                declareDestination(received.get(), ipAddr.get());
                destinationScreen.refreshView();
            });
        });
    }

    public void declareDestination(String received, String ip) {
        String name = received.substring(received.indexOf('/') + 2, received.length());
        String type = received.substring(0, received.indexOf('/') - 1);
        Destination d = new Destination(ip, name, type);
        destination.add(d);
        destinationScreen.refreshView(destination);
    }

    public void showDestinationOn(IDestinationItemScreen holder, int position) {
        Destination d = destination.get(position);
        holder.showDestination(d.getName(), d.getType(), d.getIp());
    }

    public int getItemCount() {
        if(destination == null) {
            return 0;
        } else {
            return destination.size();
        }
    }

    public Destination getDestination() {
        return destination.get(0);
    }
}
