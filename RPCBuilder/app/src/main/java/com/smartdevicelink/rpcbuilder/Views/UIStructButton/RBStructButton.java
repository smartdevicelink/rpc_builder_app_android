package com.smartdevicelink.rpcbuilder.Views.UIStructButton;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.smartdevicelink.rpcbuilder.BuildActivity;
import com.smartdevicelink.rpcbuilder.R;
import com.smartdevicelink.rpcbuilder.RBStruct;
import com.smartdevicelink.rpcbuilder.Views.Fragments.RBStructFragment;

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

                android.app.FragmentManager fragmentManager = ((BuildActivity) mContext).getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.hide(fragmentManager.findFragmentByTag("ListRBFuncParams"));
                RBStructFragment fragment = (RBStructFragment) fragmentManager.findFragmentByTag("ListRBStructParams:"+rbStruct.name);

                if(fragment == null){
                    fragment = new RBStructFragment();
                    fragmentTransaction.add(R.id.activity_build, fragment, "ListRBStructParams:" + rbStruct.name);
                }

                fragmentTransaction.show(fragment);
                fragmentTransaction.commit();

                ((BuildActivity) mContext).setTitle(rbStruct.name);
                ((BuildActivity) mContext).setRBStruct(rbStruct);
            }
        });
    }

}
