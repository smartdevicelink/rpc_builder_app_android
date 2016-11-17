package com.smartdevicelink.rpcbuilder;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

/**
 * Created by austinkirk on 11/17/16.
 */

public class RBFuncFragment extends Fragment {
    private Vector<RBFunction> requests;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.list_request_fragment, container, false);

        final BuildActivity buildActivity = (BuildActivity) getActivity();
        requests = buildActivity.getParserHandler().getRequests();
        buildActivity.setTitle(R.string.app_name);

        if(!requests.isEmpty()){
            final ArrayList<String> arrayList = new ArrayList<String>();
            for(RBFunction req : requests){
                arrayList.add(req.name);
            }
            //Sort into alphabetical order
            Collections.sort(arrayList);
            ArrayAdapter<String> listArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayList);
            ListView requestList = (ListView) view.findViewById(R.id.request_list);
            requestList.setAdapter(listArrayAdapter);
            requestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    for(RBFunction rb : buildActivity.getParserHandler().getRequests()){
                        if(rb.name.equals(arrayList.get(i))){
                            buildActivity.setRBFunction(rb);
                            buildActivity.showRBParameterFragment();
                        }
                    }
                }
            });

            setHasOptionsMenu(true);
        }

        return view;
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

