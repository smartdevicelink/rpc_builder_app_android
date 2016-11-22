package com.smartdevicelink.rpcbuilder;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.smartdevicelink.marshal.JsonRPCMarshaller;
import com.smartdevicelink.proxy.RPCMessage;
import com.smartdevicelink.rpcbuilder.BuildActivity;
import com.smartdevicelink.rpcbuilder.R;
import com.smartdevicelink.rpcbuilder.RBFunction;
import com.smartdevicelink.rpcbuilder.RBParam;
import com.smartdevicelink.rpcbuilder.RBParamView;
import com.smartdevicelink.rpcbuilder.SmartDeviceLink.SdlService;

import org.json.JSONException;

import java.util.Arrays;
import java.util.Hashtable;

/**
 * Created by austinkirk on 11/15/16.
 */

public class ListParamsFragment extends Fragment {
    private RBFunction request;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_param, container, false);

        BuildActivity buildActivity = (BuildActivity) getActivity();

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

    public void setRBFunction(RBFunction rb){ request = rb; }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(request != null && hidden == false)
            getActivity().setTitle(request.name);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.build_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        BuildActivity buildActivity = (BuildActivity) getActivity();
        buildActivity.setRBFunction(request);
        // handle item selection
        switch (item.getItemId()) {
            case R.id.send:
                // TODO: User chose the "Send" option, send RPC request

                // Remove this fragment, go to list of RPC requests
                buildActivity.hideFragment(this);
                buildActivity.showFragment(ListFuncsFragment.class);
                RBRequestBuilder rbrb = new RBRequestBuilder();
                Hashtable<String,Object> hash =  rbrb.buildRequest((LinearLayout) getView().findViewById(R.id.param_holder), buildActivity);
                try {
                    Log.d("Before", JsonRPCMarshaller.serializeHashtable(hash).toString(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                buildActivity.sendRPCRequest(hash);
                return true;

            case R.id.back:
                // Remove and hide this fragment, go back to list of requests (RBfunctions)
                buildActivity.hideFragment(this);
                buildActivity.showFragment(ListFuncsFragment.class);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
