package com.smartdevicelink.rpcbuilder.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.smartdevicelink.rpcbuilder.Activities.BuildActivity;
import com.smartdevicelink.rpcbuilder.DataModels.RBFunction;
import com.smartdevicelink.rpcbuilder.R;
import com.smartdevicelink.rpcbuilder.Views.RBFuncView;
import com.smartdevicelink.rpcbuilder.Views.RBParamView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

/**
 * Created by austinkirk on 11/17/16.
 */

public class ListFuncsFragment extends Fragment {
    private Vector<RBFunction> requests;
    private String title = "RPC Requests";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_param, container, false);

        final BuildActivity buildActivity = (BuildActivity) getActivity();
        requests = buildActivity.getParserHandler().getRequests();
        buildActivity.setTitle(title);

        setRetainInstance(true); //make retainable

        if(!requests.isEmpty()){
            ScrollView scrollView = (ScrollView) view.findViewById(R.id.param_scroller);

            RBFuncView rbFuncView = new RBFuncView(getActivity());
            scrollView.addView(rbFuncView.setFuncs(requests));

            setHasOptionsMenu(true);
        }

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden == false)
            getActivity().setTitle(title);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_request_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.back:
                getActivity().finish();

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}

