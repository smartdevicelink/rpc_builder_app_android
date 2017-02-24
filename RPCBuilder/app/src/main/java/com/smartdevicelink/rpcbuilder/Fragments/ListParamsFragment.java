package com.smartdevicelink.rpcbuilder.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.smartdevicelink.marshal.JsonRPCMarshaller;
import com.smartdevicelink.protocol.enums.FunctionID;
import com.smartdevicelink.rpcbuilder.Activities.BuildActivity;
import com.smartdevicelink.rpcbuilder.DataModels.RBFunction;
import com.smartdevicelink.rpcbuilder.DataModels.RBRequestBuilder;
import com.smartdevicelink.rpcbuilder.R;
import com.smartdevicelink.rpcbuilder.Views.RBParamView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Created by austinkirk on 11/15/16.
 */

public class ListParamsFragment extends Fragment {
    private RBFunction request;
    private final String RAI_file = "RAI_file";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_param, container, false);

        BuildActivity buildActivity = (BuildActivity) getActivity();

        buildActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(request != null) {
            ScrollView scrollView = (ScrollView) view.findViewById(R.id.param_scroller);

            RBParamView rb = new RBParamView(getActivity());
            rb.giveRequest(request);
            rb.setId(R.id.param_holder);
            scrollView.addView(rb);

            getActivity().setTitle(request.name);
            setHasOptionsMenu(true);

            if(request.name.equals(FunctionID.REGISTER_APP_INTERFACE.toString())){
                loadRAI(rb);
            }

            TextView textView = (TextView) view.findViewById(R.id.RequiredExplanation);
            textView.bringToFront();
        }

        return view;
    }

    public void setRBFunction(RBFunction rb){ request = rb; }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(request != null && hidden == false) {
            getActivity().setTitle(request.name);
            ((BuildActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

        RBRequestBuilder rbrb = new RBRequestBuilder();
        Hashtable<String,Object> hash =  rbrb.buildRequest((LinearLayout) getView().findViewById(R.id.param_holder), buildActivity);

        // Save Settings if request == RAI
        saveRAI(hash);

        // Remove this fragment, go to list of RPC requests
        buildActivity.hideFragment(this);

        final InputMethodManager imm = (InputMethodManager) buildActivity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.send:
                try {
                    Log.d("Sending RPC", JsonRPCMarshaller.serializeHashtable(hash).toString(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                buildActivity.sendRPCRequest(hash);
                buildActivity.showFragment(ListFuncsFragment.class);
                return true;

            case android.R.id.home:
                if(!buildActivity.getConnectionEstablished()) // If connection does not exist, finish Activity and go back to Settings
                    buildActivity.finish();
                else // otherwise, show the ListFuncFragment
                    buildActivity.showFragment(ListFuncsFragment.class);
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void saveRAI(Hashtable<String,Object> hash){
        if(request.name.equals(FunctionID.REGISTER_APP_INTERFACE.toString())){
            String data = null;
            FileOutputStream outputStream = null;

            try {
                File file = new File(getActivity().getFilesDir(), RAI_file);
                outputStream = new FileOutputStream(file, false); // will overwrite existing data

                if(!file.exists()){
                    file.createNewFile();
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }

            try {
                data = JsonRPCMarshaller.serializeHashtable(hash).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                outputStream.write(data.getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void loadRAI(LinearLayout linearLayout){
        File file = new File(getActivity().getFilesDir(), RAI_file);
        String data = null;
        BufferedReader br = null;
        if(!file.exists())
            return;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            data = sb.toString();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(data != null){
            RBRequestBuilder rbrb = new RBRequestBuilder();
            try {
                rbrb.setRAIfields(linearLayout, (BuildActivity) getActivity(),  JsonRPCMarshaller.deserializeJSONObject(new JSONObject(data)));
            } catch (Exception e) {
                Log.e("LoadRAI", "RAI cache was corrupt, clearing."); // file was corrupt, let's delete it
                file.delete();
            }
        }
    }

}
