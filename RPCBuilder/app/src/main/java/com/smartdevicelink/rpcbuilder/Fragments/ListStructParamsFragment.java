package com.smartdevicelink.rpcbuilder.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.smartdevicelink.rpcbuilder.Activities.BuildActivity;
import com.smartdevicelink.rpcbuilder.DataModels.RBParam;
import com.smartdevicelink.rpcbuilder.DataModels.RBStruct;
import com.smartdevicelink.rpcbuilder.R;
import com.smartdevicelink.rpcbuilder.Views.RBParamView;

/**
 * Created by austinkirk on 11/15/16.
 */

public class ListStructParamsFragment extends Fragment {

    private RBStruct request;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_param, container, false);

        BuildActivity buildActivity = (BuildActivity) getActivity();

        if(request != null) {
            ScrollView scrollView = (ScrollView) view.findViewById(R.id.param_scroller);

            RBParamView rb = new RBParamView(getActivity());
            rb.giveRequest(request);
            rb.setId(R.id.param_holder);
            scrollView.addView(rb);

            setHasOptionsMenu(true);
        }

        return view;
    }

    public void setRBStruct(RBStruct rb){request = rb;}

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(request != null && hidden == false)
            getActivity().setTitle(request.name);
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
                ((BuildActivity) getActivity()).hideFragment(this);
                ((BuildActivity) getActivity()).showFragment(ListParamsFragment.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
