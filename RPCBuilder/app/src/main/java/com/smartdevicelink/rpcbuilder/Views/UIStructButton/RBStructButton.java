package com.smartdevicelink.rpcbuilder.Views.UIStructButton;


import android.app.FragmentTransaction;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.smartdevicelink.rpcbuilder.BuildActivity;
import com.smartdevicelink.rpcbuilder.R;
import com.smartdevicelink.rpcbuilder.RBStruct;
import com.smartdevicelink.rpcbuilder.RBStructFragment;

/**
 * Created by austinkirk on 11/15/16.
 */

public class RBStructButton extends Button{

    Context mContext;

    public RBStructButton(Context context){
        super(context);
        mContext = context;
    }

    public void format(final RBStruct rbStruct){
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ((BuildActivity) mContext).setRBStruct(rbStruct);
                ((BuildActivity) mContext).showRBStructFragment();
            }
        });
    }

}
