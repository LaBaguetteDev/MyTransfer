package live.labaguettedev.mytransfer.view.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import live.labaguettedev.mytransfer.R;
import live.labaguettedev.mytransfer.presenters.FileReceiverPresenter;

public class ReceiveFragment extends Fragment implements FileReceiverPresenter.IReceiverScreen {

    View view;

    Button sendSignalBtn;
    Button cancelBtn;

    ProgressBar signalProgressBar;
    TextView sendSignalText;

    FileReceiverPresenter fileReceiverPresenter;

    ActivityResultLauncher<Intent> storageActivityResultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_receive, container, false);

        sendSignalBtn = view.findViewById(R.id.send_signal_btn);
        cancelBtn = view.findViewById(R.id.cancel_btn);

        sendSignalBtn.setOnClickListener(view -> sendSignal());
        cancelBtn.setOnClickListener(view -> cancel());

        signalProgressBar = view.findViewById(R.id.signal_progressbas);
        sendSignalText = view.findViewById(R.id.send_signal_text);

        fileReceiverPresenter = new FileReceiverPresenter(this,
                (WifiManager) requireContext().getSystemService(Context.WIFI_SERVICE));


        storageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if(!Environment.isExternalStorageManager()) {
                                //TODO : Gérer le refus de la permission
                            }
                        }
                    }
                }
        );

        return view;
    }

    private void cancel() {
        signalProgressBar.setVisibility(View.GONE);
        sendSignalText.setVisibility(View.GONE);
        sendSignalBtn.setEnabled(true);
        sendSignalBtn.setVisibility(View.VISIBLE);
        cancelBtn.setEnabled(false);
        cancelBtn.setVisibility(View.GONE);

        fileReceiverPresenter.cancel();
    }

    private void sendSignal() {
        if(checkPermission()) {
            signalProgressBar.setVisibility(View.VISIBLE);
            sendSignalText.setVisibility(View.VISIBLE);
            sendSignalBtn.setEnabled(false);
            sendSignalBtn.setVisibility(View.GONE);
            cancelBtn.setEnabled(true);
            cancelBtn.setVisibility(View.VISIBLE);

            fileReceiverPresenter.sendMulticastMessage();
        } else {
            requestPermission();
        }

    }

    private void requestPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }

    private boolean checkPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int write = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            return write == PackageManager.PERMISSION_DENIED && read == PackageManager.PERMISSION_DENIED;
        }
    }

    @Override
    public void startReceiving() {
        sendSignalText.setText("Réception du fichier en cours");
        cancelBtn.setEnabled(false);

        fileReceiverPresenter.waitForFile();
    }

    @Override
    public void finishReceiving() {
        sendSignalText.setText("Fichier reçu");
        signalProgressBar.setVisibility(View.VISIBLE);
        sendSignalBtn.setEnabled(false);
        sendSignalBtn.setVisibility(View.GONE);
        cancelBtn.setEnabled(true);
        cancelBtn.setVisibility(View.VISIBLE);
    }
}