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
import android.widget.LinearLayout;
import android.widget.ListView;

import com.smartdevicelink.rpcbuilder.BuildActivity;
import com.smartdevicelink.rpcbuilder.R;
import com.smartdevicelink.rpcbuilder.RBFunction;
import com.smartdevicelink.rpcbuilder.RBParam;
import com.smartdevicelink.rpcbuilder.RBParamView;
import com.smartdevicelink.rpcbuilder.RBStruct;

/**
 * Created by austinkirk on 11/15/16.
 */

public class RBStructFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_param, container, false);

        BuildActivity buildActivity = (BuildActivity) getActivity();
        RBStruct request = buildActivity.getRBStruct();

        if(request != null) {
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.param_holder);

            for (RBParam p : request.getParams()) {
                RBParamView rb = new RBParamView(getActivity());
                rb.setLayoutParams(lparams);
                rb.addParam(p);
                linearLayout.addView(rb);
            }

            setHasOptionsMenu(true);
        }

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

                fragmentTransaction.hide(fragmentManager.findFragmentByTag("ListRBStructParams:" + ((BuildActivity) getActivity()).getRBStruct().name));
                fragmentTransaction.show(fragmentManager.findFragmentByTag("ListRBFuncParams"));

                fragmentTransaction.commit();

                ((BuildActivity) getActivity()).revertTitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
