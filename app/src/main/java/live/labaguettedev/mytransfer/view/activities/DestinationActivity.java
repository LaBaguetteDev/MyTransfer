package live.labaguettedev.mytransfer.view.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import live.labaguettedev.mytransfer.R;
import live.labaguettedev.mytransfer.model.Destination;
import live.labaguettedev.mytransfer.presenters.DestinationPresenter;
import live.labaguettedev.mytransfer.presenters.FilePresenter;
import live.labaguettedev.mytransfer.view.adapter.DestinationAdapter;

public class DestinationActivity extends AppCompatActivity implements DestinationPresenter.IDestinationScreen {

    DestinationPresenter destinationPresenter;
    FilePresenter filePresenter;

    RecyclerView recyclerView;

    ProgressBar pb;
    TextView textView;
    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        pb = findViewById(R.id.progressBar);
        textView = findViewById(R.id.text_search);
        confirm = findViewById(R.id.confirm_btn);

        confirm.setOnClickListener(view -> sendBackResult());

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new DestinationAdapter(this, destinationPresenter));

        filePresenter = (FilePresenter) getIntent().getSerializableExtra("FILEPRESENTER");
        destinationPresenter = new DestinationPresenter(this, (WifiManager) getSystemService(Context.WIFI_SERVICE));
    }

    private void sendBackResult() {
        Intent data = new Intent();
        Destination destination = destinationPresenter.getDestination();
        data.putExtra("DESTINATION", destination);
        setResult(RESULT_OK, data);

        finish();
    }

    @Override
    public void refreshView(List<Destination> destinationList) {
        destinationPresenter.setDestination(destinationList);
        recyclerView.setAdapter(new DestinationAdapter(this, destinationPresenter));
    }

    @Override
    public void refreshView() {
        pb.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
        confirm.setEnabled(true);
    }

}