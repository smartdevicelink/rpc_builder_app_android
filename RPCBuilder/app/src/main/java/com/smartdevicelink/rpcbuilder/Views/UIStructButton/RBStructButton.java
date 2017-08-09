package com.smartdevicelink.rpcbuilder.Views.UIStructButton;


import android.content.Context;
import android.view.View;

import com.smartdevicelink.rpcbuilder.Activities.BuildActivity;
import com.smartdevicelink.rpcbuilder.DataModels.RBStruct;
import com.smartdevicelink.rpcbuilder.Fragments.ListStructParamsFragment;
import com.smartdevicelink.rpcbuilder.Views.RBFuncView;
import com.smartdevicelink.rpcbuilder.Views.RBParamView;
import com.smartdevicelink.rpcbuilder.Views.RBStructParamView;

/**
 * Created by austinkirk on 11/15/16.
 */

public class RBStructButton extends android.support.v7.widget.AppCompatButton{

    private Context mContext;
    private boolean isArray;
    private View parent;

    public RBStructButton(Context context){
        super(context);
        mContext = context;
    }

    public void format(RBStruct rbs, final View parent){
        final RBStruct rbStruct = rbs;
        final String rbName = rbs.name; //if structs are same type, we have to differentiate
        this.parent = parent;

        final BuildActivity buildActivity = ((BuildActivity) mContext);
        buildActivity.setRBStruct(rbStruct);
        buildActivity.addFragment(ListStructParamsFragment.class);

        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rbStruct.name = rbName;

                if(parent.getClass().equals(RBStructParamView.class)){
	                buildActivity.hideFragment(buildActivity.getFragmentManager().findFragmentByTag(buildActivity.LIST_STRUCT_PARAMS_KEY + ":" + buildActivity.getRBStruct().name.toLowerCase()));
                }else if(parent.getClass().equals(RBParamView.class)){
                    buildActivity.hideFragment(buildActivity.getFragmentManager().findFragmentByTag(buildActivity.LIST_PARAMS_KEY + ":" + buildActivity.getRBFunction().name));
                }
	            buildActivity.setRBStruct(rbStruct);
	            buildActivity.showFragment(ListStructParamsFragment.class);
            }
        });
    }

    public boolean isArray(){
        return isArray;
    }

    public void setArray(boolean a){
        isArray = a;
    }

}
