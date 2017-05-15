package irsyadhhs.cs.upi.edu.gopkl3;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by HARVI on 5/11/2017.
 */

public class ProfilesetMenu extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        // change R.layout.yourlayoutfilename for each of your fragments

        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_profileset, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Edit Profile");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.mSearch).setVisible(false);
        menu.findItem(R.id.mRefresh).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
