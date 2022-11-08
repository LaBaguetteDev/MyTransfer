package live.labaguettedev.mytransfer.presenters;

import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import live.labaguettedev.mytransfer.model.Destination;
import live.labaguettedev.mytransfer.model.SelectedFile;
import live.labaguettedev.mytransfer.utils.FileSender;

public class FileSendingPresenter {

    public interface ISendingScreen {
        void refreshView(double percentage, double speed);
        void finishTransfer();
    }

    ISendingScreen sendingScreen;
    private SelectedFile selectedFile;
    private Destination destination;

    private Socket connection;


    public FileSendingPresenter(SelectedFile f, Destination d, ISendingScreen sendingScreen) {
        this.selectedFile = f;
        this.destination = d;
        this.sendingScreen = sendingScreen;
    }

    public void sendingFile() {
        File f = new File(selectedFile.getPath());

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            try {
                connection = new Socket(destination.getIp().substring(1), 5464);

                OutputStream out = connection.getOutputStream();
                DataOutputStream dataOut = new DataOutputStream(out);
                dataOut.writeUTF(selectedFile.getName() + "/" + f.length());

                FileSender fs = new FileSender(f.getPath());
                fs.sendFile(connection.getOutputStream(), handler, sendingScreen);

                f.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }


            handler.post(() -> {
                sendingScreen.refreshView(100, 0);
                sendingScreen.finishTransfer();
            });
        });
    }
}
