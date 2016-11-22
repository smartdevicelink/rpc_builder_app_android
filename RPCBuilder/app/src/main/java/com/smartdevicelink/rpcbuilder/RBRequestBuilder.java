package com.smartdevicelink.rpcbuilder;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.smartdevicelink.proxy.RPCMessage;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.RPCStruct;
import com.smartdevicelink.proxy.SdlProxyBase;
import com.smartdevicelink.proxy.rpc.RegisterAppInterface;
import com.smartdevicelink.rpcbuilder.Views.UIEnumSpinner.RBEnumSpinner;
import com.smartdevicelink.rpcbuilder.Views.UILabel.RBNameLabel;
import com.smartdevicelink.rpcbuilder.Views.UIStructButton.RBStructButton;
import com.smartdevicelink.rpcbuilder.Views.UISwitch.RBSwitch;
import com.smartdevicelink.rpcbuilder.Views.UITextField.RBParamTextField;

import java.util.Hashtable;

/**
 * Created by austinkirk on 11/18/16.
 */

public class RBRequestBuilder {
    private final int 	REGISTER_APP_INTERFACE_CORRELATION_ID = 65529,
            UNREGISTER_APP_INTERFACE_CORRELATION_ID = 65530,
            POLICIES_CORRELATION_ID = 65535;
    private int correlation_id = 1;
    private final String KEY_DISABLED = "DISABLED";

    public RBRequestBuilder(){}

    public Hashtable<String, Object> buildRequest(LinearLayout parameterList, Activity buildActivity){

        Hashtable<String, Object> parameter_table = buildFields(parameterList, buildActivity);

        Hashtable<String, Object> function_table = new Hashtable<String, Object>();
        function_table.put(RPCRequest.KEY_PARAMETERS, parameter_table);
        function_table.put(RPCRequest.KEY_FUNCTION_NAME, ((BuildActivity) buildActivity).getRBFunction().name);
        function_table.put(RPCRequest.KEY_CORRELATION_ID, getCorrelationID(((BuildActivity) buildActivity).getRBFunction().name));

        Hashtable<String, Object> hash = new Hashtable<String, Object>();
        hash.put(RPCRequest.KEY_REQUEST, function_table);

        return hash;
    }

    private int getCorrelationID(String function_name){
        if(function_name.toLowerCase().equals("registerappinterface"))
            return REGISTER_APP_INTERFACE_CORRELATION_ID;
        else if(function_name.toLowerCase().equals("unregisterappinterface"))
            return UNREGISTER_APP_INTERFACE_CORRELATION_ID;
        else
            return correlation_id++;
    }

    private Hashtable<String, Object> buildFields(LinearLayout list, Activity buildActivity){
        Hashtable<String, Object> parameter_table = new Hashtable<String, Object>();
        for(int i = 0; i < list.getChildCount(); i++){
            LinearLayout parameter = (LinearLayout) list.getChildAt(i);

            String name = "no_name_parameter";
            Object value = "null";

            for(int j = 0; j < parameter.getChildCount(); j++){
                View v = parameter.getChildAt(j);
                if(j == 0){
                    if(v instanceof RBNameLabel){
                        name = ((RBNameLabel) v).getText().toString();
                        if(name.contains("*"))
                            name = name.substring(0, name.lastIndexOf("*"));
                        if(!((RBNameLabel) v).isChecked())
                            name = KEY_DISABLED;
                    }
                }else {
                    if (v instanceof RBParamTextField) {
                        value = ((RBParamTextField) v).getText().toString();
                    } else if (v instanceof RBSwitch) {
                        value = ((RBSwitch) v).isChecked();
                    } else if (v instanceof RBStructButton) {
                        FragmentManager fragmentManager =  ((BuildActivity) buildActivity).getFragmentManager();
                        ListStructParamsFragment structFragment = (ListStructParamsFragment) fragmentManager.findFragmentByTag(((BuildActivity) buildActivity).LIST_STRUCT_PARAMS_KEY + ":" + name.toLowerCase());
                        if(structFragment != null){
                            value = buildFields( (LinearLayout) structFragment.getView().findViewById(R.id.param_holder), buildActivity);
                        }else{
                            value = "I am a struct.";
                        }
                    } else if (v instanceof RBEnumSpinner) {
                        value = ((RBEnumSpinner) v).getSelectedItem().toString();
                    }
                }
            }

            if(!value.toString().equals("") && !name.equals(KEY_DISABLED))
                parameter_table.put(name, value);
        }
        return parameter_table;
    }
}
