package live.labaguettedev.mytransfer.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import live.labaguettedev.mytransfer.R;
import live.labaguettedev.mytransfer.presenters.DestinationPresenter;
import live.labaguettedev.mytransfer.view.activities.DestinationActivity;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.ViewHolder> {

    private final DestinationActivity activity;
    private final DestinationPresenter destinationPresenter;

    public DestinationAdapter(DestinationActivity activity, DestinationPresenter destinationPresenter) {
        this.activity = activity;
        this.destinationPresenter = destinationPresenter;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements DestinationPresenter.IDestinationItemScreen {
        DestinationActivity activity;
        TextView nameOf;
        ViewHolder(View view, DestinationActivity activity) {
            super(view);
            this.activity = activity;
            this.nameOf = view.findViewById(R.id.name_of);
        }

        @Override
        public void showDestination(String name, String type, String ip) {
            nameOf.setText(name);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_destination, parent, false);
        return new ViewHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        destinationPresenter.showDestinationOn(holder, position);
    }

    @Override
    public int getItemCount() {
        if(destinationPresenter == null) {
            return 0;
        } else {
            return destinationPresenter.getItemCount();
        }
    }
}
