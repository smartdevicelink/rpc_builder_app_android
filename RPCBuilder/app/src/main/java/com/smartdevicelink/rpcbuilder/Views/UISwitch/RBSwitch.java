package com.smartdevicelink.rpcbuilder.Views.UISwitch;

import android.content.Context;
import android.widget.Switch;

import com.smartdevicelink.rpcbuilder.DataModels.RBBaseObject;
import com.smartdevicelink.rpcbuilder.DataModels.RBParam;

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