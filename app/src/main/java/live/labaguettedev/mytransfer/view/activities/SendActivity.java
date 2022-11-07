package live.labaguettedev.mytransfer.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;

import live.labaguettedev.mytransfer.R;
import live.labaguettedev.mytransfer.model.Destination;
import live.labaguettedev.mytransfer.model.SelectedFile;
import live.labaguettedev.mytransfer.presenters.FileSendingPresenter;

public class SendActivity extends AppCompatActivity implements FileSendingPresenter.ISendingScreen {

    SelectedFile f;
    Destination d;
    FileSendingPresenter fileSendingPresenter;

    TextView textPercentage;
    TextView textSpeed;
    ProgressBar progressBar;

    Button finishBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        Intent intent = getIntent();

        f = (SelectedFile) intent.getSerializableExtra("FILE");
        d = (Destination) intent.getSerializableExtra("DESTINATION");
        textPercentage = findViewById(R.id.text_percentage);
        textSpeed = findViewById(R.id.text_speed);
        progressBar = findViewById(R.id.send_progress);
        finishBtn = findViewById(R.id.finish_btn);

        finishBtn.setOnClickListener(view -> finishActivity());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fileSendingPresenter = new FileSendingPresenter(f, d, this);
        fileSendingPresenter.sendingFile();
    }

    @Override
    public void refreshView(double percentage, double speed) {
        String sPercentage = new DecimalFormat("#").format(percentage);
        String sSpeed = new DecimalFormat("#.#").format(speed);
        textPercentage.setText(sPercentage + " %");
        textSpeed.setText(sSpeed + " MB/s");
        progressBar.setProgress(Integer.parseInt(sPercentage));
    }

    @Override
    public void finishTransfer() {
        finishBtn.setEnabled(true);
    }

    public void finishActivity() {
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }
}