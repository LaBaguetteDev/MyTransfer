package live.labaguettedev.mytransfer.view.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Objects;

import live.labaguettedev.mytransfer.R;
import live.labaguettedev.mytransfer.presenters.FilePresenter;
import live.labaguettedev.mytransfer.view.activities.DestinationActivity;
import live.labaguettedev.mytransfer.view.activities.MainActivity;

public class SendFragment extends Fragment implements FilePresenter.ISelectedFileScreen {

    View view;
    Button selectFile;
    Button sendFile;
    TextView fileText;

    private FilePresenter filePresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_send, container, false);


        selectFile = view.findViewById(R.id.select_file);
        sendFile = view.findViewById(R.id.send_file);
        fileText = view.findViewById(R.id.file_text);

        selectFile.setOnClickListener(view -> openFileDialog());
        sendFile.setOnClickListener(view -> sendingFile());

        filePresenter = new FilePresenter(this);

        return view;
    }

    // SELECTION DE FICHIER
    // Sélectionner un fichier à envoyer
    ActivityResultLauncher<Intent> sActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        fileObject(view.getContext(), uri);
                        sendFile.setEnabled(true);
                    }
                }
            }
    );

    private void fileObject(Context context, Uri contentUri) {
        String res = null;
        if (contentUri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                    double size = cursor.getDouble(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE));
                    String path = contentUri.getPath();
                    filePresenter.setSelectedFile(path, name, size);
                }
            } finally {
                assert cursor != null;
                cursor.close();
            }
        }
    }

    // Ouvrir le dialogue d'ouverture de fichier
    private void openFileDialog() {
        Intent data = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        data.setType("*/*");
        data = Intent.createChooser(data, "Choisir un fichier");
        sActivityResultLauncher.launch(data);

    }

    @Override
    public void showSelectedFile(String path, String name, double size) {
        fileText.setText(path + " / " + name + " / " + size);
    }

    // ENVOI DE FICHIER
    public void sendingFile() {
        Activity currentActivity = getActivity();
        Intent intent = new Intent(currentActivity, DestinationActivity.class);
        currentActivity.startActivity(intent);
    }

}