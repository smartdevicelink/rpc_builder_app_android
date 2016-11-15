package com.smartdevicelink.rpcbuilder.Views.UISwitch;

import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.Switch;

import com.smartdevicelink.rpcbuilder.RBBaseObject;
import com.smartdevicelink.rpcbuilder.RBParam;

/**
 * Created by austinkirk on 11/15/16.
 */

public class RBSwitch{

    private Switch mySwitch = null;
    private RBParam rbParam = null;

    public RBSwitch(RBParam parameter, Switch sw){
        mySwitch = sw;
        rbParam = parameter;
        setParameter(parameter);
    }

    public void setParameter(RBParam parameter){
        rbParam = parameter;

        if(rbParam.mDefaultValue != null){
            if(rbParam.mDefaultValue.equals(RBBaseObject.RBTypeBooleanTrueValue)){
                mySwitch.setChecked(true);
            }else if (rbParam.mDefaultValue.equals(RBBaseObject.RBTypeBooleanFalseValue)){
                mySwitch.setChecked(false);
            }
        }
    }

    public Switch getSwitch(){
        return mySwitch;
    }

}