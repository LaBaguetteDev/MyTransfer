package live.labaguettedev.mytransfer.view.fragments;

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
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;

import live.labaguettedev.mytransfer.R;
import live.labaguettedev.mytransfer.model.Destination;
import live.labaguettedev.mytransfer.presenters.FilePresenter;
import live.labaguettedev.mytransfer.utils.FileUtils;
import live.labaguettedev.mytransfer.view.activities.DestinationActivity;
import live.labaguettedev.mytransfer.view.activities.SendActivity;

public class SendFragment extends Fragment implements FilePresenter.ISelectedFileScreen {

    View view;
    Button selectFileBtn;
    Button searchDestinationBtn;
    Button sendFileBtn;
    TextView fileText;

    private FilePresenter filePresenter;
    private Destination d;

    ActivityResultLauncher<Intent> selectFile;
    ActivityResultLauncher<Intent> getDestination;
    ActivityResultLauncher<Intent> sendFileActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_send, container, false);


        selectFileBtn = view.findViewById(R.id.select_file);
        searchDestinationBtn = view.findViewById(R.id.search_destination);
        sendFileBtn = view.findViewById(R.id.send_file);
        fileText = view.findViewById(R.id.file_text);

        selectFileBtn.setOnClickListener(view -> openFileDialog());
        searchDestinationBtn.setOnClickListener(view -> searchDestinator());
        sendFileBtn.setOnClickListener(view -> sendFile());

        filePresenter = new FilePresenter(this);

        selectFile = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Uri uri = data.getData();
                            fileObject(view.getContext(), uri);
                            searchDestinationBtn.setEnabled(true);
                        }
                    }
                }
        );

        getDestination = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::getDestination);
        sendFileActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::getSendFileResult);

        return view;
    }

    private void fileObject(Context context, Uri contentUri) {
        String res = null;
        if (contentUri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                    double size = cursor.getDouble(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE));
                    File f = FileUtils.getFile(requireContext(), contentUri);
                    filePresenter.setSelectedFile(f.getPath(), name, size);
                }
            } catch (IOException e) {
                e.printStackTrace();
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
        selectFile.launch(data);

    }

    @Override
    public void showSelectedFile(String path, String name, double size) {
        fileText.setText(path + " / " + name + " / " + size);
    }

    // ENVOI DE FICHIER
    public void searchDestinator() {
        Activity currentActivity = getActivity();
        Intent intent = new Intent(currentActivity, DestinationActivity.class);
        getDestination.launch(intent);
    }

    private void getDestination(ActivityResult result) {
        d = (Destination) result.getData().getSerializableExtra("DESTINATION");
        sendFileBtn.setVisibility(View.VISIBLE);
        sendFileBtn.setEnabled(true);
        searchDestinationBtn.setVisibility(View.GONE);
        searchDestinationBtn.setEnabled(false);
    }

    private void sendFile() {
        Activity currentActivity = getActivity();
        Intent intent = new Intent(currentActivity, SendActivity.class);
        intent.putExtra("FILE", filePresenter.getSelectedFile());
        intent.putExtra("DESTINATION", d);
        sendFileActivity.launch(intent);
    }

    private void getSendFileResult(ActivityResult result) {
        filePresenter.removeFile();
        fileText.setText("");
        sendFileBtn.setVisibility(View.GONE);
        sendFileBtn.setEnabled(false);
        searchDestinationBtn.setVisibility(View.VISIBLE);
        searchDestinationBtn.setEnabled(false);
    }
}
