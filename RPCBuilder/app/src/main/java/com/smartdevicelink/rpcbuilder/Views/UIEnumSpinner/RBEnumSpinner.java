package com.smartdevicelink.rpcbuilder.Views.UIEnumSpinner;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.smartdevicelink.rpcbuilder.DataModels.RBElement;
import com.smartdevicelink.rpcbuilder.DataModels.RBEnum;

import java.util.ArrayList;

/**
 * Created by austinkirk on 11/16/16.
 */

public class RBEnumSpinner extends Spinner {
    public RBEnumSpinner(Context context){
        super(context);
    }

    public void format(RBEnum rbEnum){
        ArrayList<String> arrayList = new ArrayList<String>();
        for(RBElement e : rbEnum.elements){
            arrayList.add(e.name);
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, arrayList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.setAdapter(spinnerArrayAdapter);
    }
}
