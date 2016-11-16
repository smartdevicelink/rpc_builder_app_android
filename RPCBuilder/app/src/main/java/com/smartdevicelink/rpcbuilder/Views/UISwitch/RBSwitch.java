package com.smartdevicelink.rpcbuilder.Views.UISwitch;

import android.content.Context;
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

public class RBSwitch extends Switch{


    public RBSwitch(Context context){
        super(context);
    }

    public void format(RBParam rbParam){

        if(rbParam.mDefaultValue != null){
            if(rbParam.mDefaultValue.equals(RBBaseObject.RBTypeBooleanTrueValue)){
                this.setChecked(true);
            }else if (rbParam.mDefaultValue.equals(RBBaseObject.RBTypeBooleanFalseValue)){
                this.setChecked(false);
            }
        }
    }

}