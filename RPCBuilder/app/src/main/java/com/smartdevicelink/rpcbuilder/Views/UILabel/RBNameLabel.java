package com.smartdevicelink.rpcbuilder.Views.UILabel;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.smartdevicelink.rpcbuilder.RBParam;

/**
 * Created by austinkirk on 11/16/16.
 */

public class RBNameLabel extends TextView {
    private Boolean enabled = true;

    public RBNameLabel(Context context){
        super(context);
    }

    public void format(RBParam rbParam){
        if(rbParam.mIsMandatory == null) {
            rbParam.mIsMandatory = false; // set to false by default
        }else if(rbParam.mIsMandatory == false){
            enabled = false;
        }
        this.setText( rbParam.mIsMandatory ? rbParam.name + "*" : rbParam.name );
        this.setClickable( rbParam.mIsMandatory ? false : true);
        this.setTextColor( enabled ? Color.WHITE : Color.GRAY);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                enabled = !enabled;
                ((TextView) view).setTextColor( enabled ? Color.WHITE : Color.GRAY);
            }
        });
    }

    public Boolean isChecked(){return enabled;}
}
