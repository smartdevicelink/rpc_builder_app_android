package com.smartdevicelink.rpcbuilder.Views.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.smartdevicelink.rpcbuilder.BuildActivity;
import com.smartdevicelink.rpcbuilder.R;

/**
 * Created by austinkirk on 11/15/16.
 */

public class RBStructFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_param, container, false);

        ListView listView = (ListView) view.findViewById(R.id.param_list);
        listView.setAdapter(((BuildActivity) getActivity()).getListLvl2ParamAdapter());

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.second_level_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.back_from_second:
                android.app.FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.detach(fragmentManager.findFragmentByTag("ListRBStructParams"));
                fragmentTransaction.attach(fragmentManager.findFragmentByTag("ListRBFuncParams"));

                fragmentTransaction.commit();

                ((BuildActivity) getActivity()).revertTitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
