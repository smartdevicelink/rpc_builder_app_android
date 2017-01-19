package com.smartdevicelink.rpcbuilder.Views.UIStructButton;


import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.smartdevicelink.rpcbuilder.Activities.BuildActivity;
import com.smartdevicelink.rpcbuilder.Fragments.ListStructParamsFragment;
import com.smartdevicelink.rpcbuilder.DataModels.RBStruct;

/**
 * Created by austinkirk on 11/15/16.
 */

public class RBStructButton extends Button{

    private Context mContext;


    public RBStructButton(Context context){
        super(context);
        mContext = context;
    }

    public void format(RBStruct rbs){
        final RBStruct rbStruct = rbs;
        final String rbName = rbs.name; //if structs are same type, we have to differentiate

        final BuildActivity buildActivity = ((BuildActivity) mContext);
        buildActivity.setRBStruct(rbStruct);
        buildActivity.addFragment(ListStructParamsFragment.class);

        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbStruct.name = rbName;

                buildActivity.hideFragment(buildActivity.getFragmentManager().findFragmentByTag(buildActivity.LIST_PARAMS_KEY + ":" + buildActivity.getRBFunction().name));
                buildActivity.setRBStruct(rbStruct);
                buildActivity.showFragment(ListStructParamsFragment.class);
            }
        });
    }

}
