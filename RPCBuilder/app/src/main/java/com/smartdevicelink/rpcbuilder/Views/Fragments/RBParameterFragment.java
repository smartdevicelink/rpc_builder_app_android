package com.smartdevicelink.rpcbuilder.Views.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_param, container, false);

        BuildActivity buildActivity = (BuildActivity) getActivity();
        RBFunction request = buildActivity.getRBFunction();
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.param_holder);

        for(RBParam p : request.getParams() ){
            RBParamView rb = new RBParamView(getActivity());
            rb.setLayoutParams(lparams);
            rb.addParam(p);
            linearLayout.addView(rb);
        }

        return view;
    }

}
