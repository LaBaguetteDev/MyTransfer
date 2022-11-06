package live.labaguettedev.mytransfer.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import live.labaguettedev.mytransfer.view.activities.MainActivity;
import live.labaguettedev.mytransfer.view.fragments.ReceiveFragment;
import live.labaguettedev.mytransfer.view.fragments.SendFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {


    public ViewPagerAdapter(@NonNull FragmentActivity fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new ReceiveFragment();
        }

        return new SendFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
