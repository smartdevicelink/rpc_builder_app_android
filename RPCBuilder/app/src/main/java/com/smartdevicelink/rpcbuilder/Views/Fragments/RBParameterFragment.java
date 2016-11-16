package com.smartdevicelink.rpcbuilder.Views.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.smartdevicelink.rpcbuilder.BuildActivity;
import com.smartdevicelink.rpcbuilder.R;
import com.smartdevicelink.rpcbuilder.RBParamAdapter;

/**
 * Created by austinkirk on 11/15/16.
 */

public class RBParameterFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_param, container, false);

        ListView listView = (ListView) view.findViewById(R.id.param_list);
        if(listView.getAdapter() == null)
            listView.setAdapter(((BuildActivity) getActivity()).getListParamAdapter());

        return view;
    }

}
