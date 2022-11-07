package live.labaguettedev.mytransfer.utils;

import android.os.Handler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import live.labaguettedev.mytransfer.presenters.FileSendingPresenter;

public class FileSender {
    private static final int DEFAULT_BUFFER=8000;
    private String path;

    public FileSender(String path) {
        this.path = path;
    }

    public boolean sendFile(OutputStream out, Handler handler, FileSendingPresenter.ISendingScreen sendingScreen) {
        BufferedInputStream bisFile;

        try {
            File f = new File(path);
            long fileSize = f.length();
            if(f.exists()) {
                byte[] buffer = new byte[DEFAULT_BUFFER];
                bisFile = new BufferedInputStream(new FileInputStream(f));
                long currentOffset = 0;
                writeBytes(out, bisFile, fileSize, buffer, currentOffset, handler, sendingScreen);
                bisFile.close();
                return true;
            } else
                return false;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void writeBytes(OutputStream out, BufferedInputStream bisFile, long fileSize, byte[] buffer, long currentOffset,
                            Handler handler, FileSendingPresenter.ISendingScreen sendingScreen) throws IOException {
        double bytesReaded;
        long start = System.currentTimeMillis();
        while((currentOffset < fileSize) && (bytesReaded = bisFile.read(buffer)) > 0) {
            out.write(buffer, 0, (int) bytesReaded); out.flush();

            currentOffset += bytesReaded;
            double finalCurrentOffset = currentOffset;
            long cost = System.currentTimeMillis() - start;
            handler.post(() -> {
                sendingScreen.refreshView((finalCurrentOffset / fileSize) * 100, (finalCurrentOffset / (1024.0*1024)) / (cost / 1000.0));
            });
        }
    }

}
