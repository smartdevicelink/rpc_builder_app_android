package com.smartdevicelink.rpcbuilder.DataModels;

import android.app.Activity;
import android.app.FragmentManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.rpcbuilder.Activities.BuildActivity;
import com.smartdevicelink.rpcbuilder.Fragments.ListStructParamsFragment;
import com.smartdevicelink.rpcbuilder.R;
import com.smartdevicelink.rpcbuilder.Views.UIEnumSpinner.RBEnumSpinner;
import com.smartdevicelink.rpcbuilder.Views.UILabel.RBNameLabel;
import com.smartdevicelink.rpcbuilder.Views.UIStructButton.RBStructButton;
import com.smartdevicelink.rpcbuilder.Views.UISwitch.RBSwitch;
import com.smartdevicelink.rpcbuilder.Views.UITextField.RBParamTextField;
import com.smartdevicelink.util.CorrelationIdGenerator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Created by austinkirk on 11/18/16.
 */

public class RBRequestBuilder {
    private final int 	REGISTER_APP_INTERFACE_CORRELATION_ID = 65529,
            UNREGISTER_APP_INTERFACE_CORRELATION_ID = 65530,
            POLICIES_CORRELATION_ID = 65535;
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
            return UNREGISTER_APP_INTERFACE_CORRELATION_ID; // This is not allowed
        else
            return CorrelationIdGenerator.generateId();
    }

    private Hashtable<String, Object> buildFields(LinearLayout list, Activity buildActivity){
        Hashtable<String, Object> parameter_table = new Hashtable<String, Object>();

        for(int i = 0; i < list.getChildCount(); i++){
            LinearLayout parameter = (LinearLayout) list.getChildAt(i);

            String name = "no_name_parameter";
            Object value = null;

            for(int j = 0; j < parameter.getChildCount(); j++){
                View v = parameter.getChildAt(j);
                if(j == 0){
                    if(v instanceof RBNameLabel){
                        name = ((RBNameLabel) v).getRBParamName();
                        if(!((RBNameLabel) v).isChecked())
                            name = KEY_DISABLED;
                    }
                }else {
                    if (v instanceof RBParamTextField) {
                        if(((RBParamTextField) v).getType().equals("Integer")){
                            try {
                                value = Integer.parseInt(((RBParamTextField) v).getText().toString());
                            }catch (NumberFormatException e){
                                value = ((RBParamTextField) v).getText().toString();
                            }
                        }else if(((RBParamTextField) v).getType().equals("Long")){
	                        try {
		                        value = Long.parseLong(((RBParamTextField) v).getText().toString());
	                        }catch (NumberFormatException e){
		                        value = ((RBParamTextField) v).getText().toString();
	                        }
                        }else if(((RBParamTextField) v).getType().equals("Float")){
	                        try {
		                        value = Float.parseFloat(((RBParamTextField) v).getText().toString());
	                        }catch (NumberFormatException e){
		                        value = ((RBParamTextField) v).getText().toString();
	                        }
                        }else if(((RBParamTextField) v).getType().equals("Double")){
	                        try {
		                        value = Double.parseDouble(((RBParamTextField) v).getText().toString());
	                        }catch (NumberFormatException e){
		                        value = ((RBParamTextField) v).getText().toString();
	                        }
                        }else {
                            value = ((RBParamTextField) v).getText().toString();
                        }

                        if(((RBParamTextField) v).requiresArray()){
                            ArrayList array = new ArrayList<>(1);
                            array.add(value);
                            value = array;
                        }
                    } else if (v instanceof RBSwitch) {
                        value = ((RBSwitch) v).isChecked();
                    } else if (v instanceof RBStructButton) {
                        FragmentManager fragmentManager =  ((BuildActivity) buildActivity).getFragmentManager();
                        ListStructParamsFragment structFragment = (ListStructParamsFragment) fragmentManager.findFragmentByTag(((BuildActivity) buildActivity).LIST_STRUCT_PARAMS_KEY + ":" + name.toLowerCase());
                        if(structFragment != null){
                            if(((RBStructButton) v).isArray()){
                                value = new Vector<Object>();
                                ((Vector<Object>) value).add(buildFields((LinearLayout) structFragment.getView().findViewById(R.id.param_holder), buildActivity));
                            }else{
                                value = buildFields((LinearLayout) structFragment.getView().findViewById(R.id.param_holder), buildActivity);
                            }
                        }
                    } else if (v instanceof RBEnumSpinner) {
                        value = ((RBEnumSpinner) v).getSelectedItem().toString();
                        if(((RBEnumSpinner) v).requiresArray()){
                            ArrayList array = new ArrayList<>(1);
                            array.add(value);
                            value = array;
                        }
                    }
                }
            }

            if(value!=null && !name.equals(KEY_DISABLED))
                parameter_table.put(name, value);
        }

        return parameter_table;
    }

    //This function and its helper allows you to set values for Views in LinearLayouts based on a hash
    //Currently only used when reloading RAI
    public void setRAIfields(LinearLayout list, Activity buildActivity, Hashtable<String, Object> hash){
        Hashtable<String, Object> parameter_table = (Hashtable<String, Object>) ((Hashtable<String, Object>) hash.get(RPCRequest.KEY_REQUEST)).get(RPCRequest.KEY_PARAMETERS);
        setRAIfields_helper(list, buildActivity, parameter_table);
    }

    private void setRAIfields_helper(LinearLayout list, Activity buildActivity, Hashtable<String, Object> hash){
        for(int i = 0; i < list.getChildCount(); i++){
            LinearLayout parameter = (LinearLayout) list.getChildAt(i);

            String name = "no_name_parameter";
            Object value = null;

            for(int j = 0; j < parameter.getChildCount(); j++){
                View v = parameter.getChildAt(j);
                if(j == 0){
                    if(v instanceof RBNameLabel){
                        name = ((RBNameLabel) v).getRBParamName();
                        if(hash.containsKey(name)){
                            value = hash.get(name);
                        }
                    }
                }else if (value != null){
                    if (v instanceof RBParamTextField) {
                        if(((RBParamTextField) v).getType().equals("Integer")){
                            ((RBParamTextField) v).setText("" + (Integer) value);
                        }else {
                            ((RBParamTextField) v).setText((String) value);
                        }
                    } else if (v instanceof RBSwitch) {
                        ((RBSwitch) v).setChecked((Boolean) value);
                    } else if (v instanceof RBStructButton) {
                        FragmentManager fragmentManager =  ((BuildActivity) buildActivity).getFragmentManager();
                        ListStructParamsFragment structFragment = (ListStructParamsFragment) fragmentManager.findFragmentByTag(((BuildActivity) buildActivity).LIST_STRUCT_PARAMS_KEY + ":" + name.toLowerCase());
                        if(structFragment != null){
                            setRAIfields_helper( (LinearLayout) structFragment.getView().findViewById(R.id.param_holder), buildActivity, (Hashtable<String, Object>) value);
                        }
                    } else if (v instanceof RBEnumSpinner) {
                        String val = (String) value;
                        ((RBEnumSpinner) v).setSelection( ((ArrayAdapter<String>) ((RBEnumSpinner) v).getAdapter()).getPosition(((String) value)));
                    }
                }
            }
        }
    }
}
