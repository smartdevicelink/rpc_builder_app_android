package com.smartdevicelink.rpcbuilder;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.smartdevicelink.rpcbuilder.BuildActivity;
import com.smartdevicelink.rpcbuilder.R;
import com.smartdevicelink.rpcbuilder.RBFunction;
import com.smartdevicelink.rpcbuilder.RBParam;
import com.smartdevicelink.rpcbuilder.RBParamView;

/**
 * Created by austinkirk on 11/15/16.
 */

public class RBParameterFragment extends Fragment {
    private RBFunction request;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_param, container, false);

        BuildActivity buildActivity = (BuildActivity) getActivity();
        request = buildActivity.getRBFunction();

        if(request != null) {
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.param_holder);

            for (RBParam p : request.getParams()) {
                RBParamView rb = new RBParamView(getActivity());
                rb.setLayoutParams(lparams);
                rb.addParam(p);
                linearLayout.addView(rb);
            }

            getActivity().setTitle(request.name);
            setHasOptionsMenu(true);
        }

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        getActivity().setTitle(request.name);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.build_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ((BuildActivity) getActivity()).setRBFunction(request);
        // handle item selection
        switch (item.getItemId()) {
            case R.id.send:
                // TODO: User chose the "Send" option, send RPC request
                // Remove this fragment, go to list of RPC requests
                ((BuildActivity) getActivity()).showRBFuncFragment();
                return true;

            case R.id.back:
                // Remove this fragment, go to list of RPC requests
                ((BuildActivity) getActivity()).showRBFuncFragment();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
