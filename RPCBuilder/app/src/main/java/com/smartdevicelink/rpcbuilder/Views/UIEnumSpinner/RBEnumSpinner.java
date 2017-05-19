package com.smartdevicelink.rpcbuilder.Views.UIEnumSpinner;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.smartdevicelink.rpcbuilder.DataModels.RBElement;
import com.smartdevicelink.rpcbuilder.DataModels.RBEnum;

import java.util.ArrayList;

/**
 * Created by austinkirk on 11/16/16.
 */

public class RBEnumSpinner extends android.support.v7.widget.AppCompatSpinner {
    private boolean mRequiresArray = false;
    private final String[] enumsRequiringArray = {"GlobalProperty"};

    public RBEnumSpinner(Context context){
        super(context);
    }

    public void format(RBEnum rbEnum){
        for(String s : enumsRequiringArray){
            if(rbEnum.name.equals(s)){
                mRequiresArray = true;
            }
        }

        ArrayList<String> arrayList = new ArrayList<String>();
        for(RBElement e : rbEnum.elements){
            arrayList.add(e.name);
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, arrayList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.setAdapter(spinnerArrayAdapter);
    }

    public boolean requiresArray(){
        return mRequiresArray;
    }
}
