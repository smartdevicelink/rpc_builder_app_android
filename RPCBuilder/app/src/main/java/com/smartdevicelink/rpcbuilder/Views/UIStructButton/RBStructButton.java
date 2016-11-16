package com.smartdevicelink.rpcbuilder.Views.UIStructButton;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toolbar;

import com.smartdevicelink.rpcbuilder.BuildActivity;
import com.smartdevicelink.rpcbuilder.Parser;
import com.smartdevicelink.rpcbuilder.R;
import com.smartdevicelink.rpcbuilder.RBBaseObject;
import com.smartdevicelink.rpcbuilder.RBParam;
import com.smartdevicelink.rpcbuilder.RBParamAdapter;
import com.smartdevicelink.rpcbuilder.RBStruct;
import com.smartdevicelink.rpcbuilder.Views.Fragments.RBParameterFragment;
import com.smartdevicelink.rpcbuilder.Views.Fragments.RBParameterFragment;
import com.smartdevicelink.rpcbuilder.Views.Fragments.RBStructFragment;

import java.util.List;

/**
 * Created by austinkirk on 11/15/16.
 */

public class RBStructButton{
    private RBStruct rbStruct = null;
    private Button myButton = null;

    public RBStructButton(RBStruct rb, Button b, final RBParamAdapter rbParamAdapter){
        rbStruct = rb;
        myButton = b;
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BuildActivity) rbParamAdapter.getContext()).setListLvl2ParamAdapter( new RBParamAdapter(rbParamAdapter.getContext(), rbStruct.getParams(), rbParamAdapter.getParserHandler()) );

                android.app.FragmentManager fragmentManager = ((Activity) rbParamAdapter.getContext()).getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.detach(fragmentManager.findFragmentByTag("ListRBFuncParams"));
                RBStructFragment fragment = (RBStructFragment) fragmentManager.findFragmentByTag("ListRBStructParams");

                if(fragment == null){
                    fragment = new RBStructFragment();
                    fragmentTransaction.add(R.id.activity_build, fragment, "ListRBStructParams");
                }else{
                    fragmentTransaction.attach(fragment);
                }

                fragmentTransaction.commit();

                ((Activity) rbParamAdapter.getContext()).setTitle(rbStruct.name);
            }
        });
    }

    public Button getButton(){
        return myButton;
    }

}
