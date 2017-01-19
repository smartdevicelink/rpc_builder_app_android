package com.smartdevicelink.rpcbuilder.DataModels;

import com.smartdevicelink.rpcbuilder.R;

import org.xml.sax.Attributes;

/**
 * Created by amulle19 on 10/26/16.
 */

public class RBFunction extends RBStruct {
    private String mFunctionId;
    public String getFunctionId() { return mFunctionId; }

    private String mMessageType;
    public String getMessageType() { return mMessageType; }

    public RBFunction(Attributes attributes) {
        super(attributes);
    }

    @Override
    protected void handle(String key, String value) {
        if (key.equals("functionID")) {
            this.mFunctionId = value;
        } else if (key.equals("messagetype")) {
            this.mMessageType = value;
        } else {
            super.handle(key, value);
        }
    }

    private String[] UIFunctionNames = {
            "Show",
            "SendLocation",
            "Slider",
            "Speak",
            "PerformInteraction",
            "PerformAudioPassThru",
            "ScrollableMessage"
    };

    private String[] BulkDataFunctionNames = {
            "PutFile"
    };

    // Returns id value of RPC icon drawable
    public int image(){
        int drawable_id = R.drawable.other_2x;
        if(name.startsWith("Add") || name.startsWith("Create")){
            drawable_id = R.drawable.add_2x;
        }else if(name.startsWith("Delete")){
            drawable_id = R.drawable.delete_2x;
        }else if(name.startsWith("Subscribe")){
            drawable_id = R.drawable.subscribe_2x;
        }else if(name.startsWith("Un")){
            drawable_id = R.drawable.unsubscribe_2x;
        }else if(name.startsWith("Set") || name.startsWith("Alert")){
            drawable_id = R.drawable.ui_2x;
        }else{
            for(String UIFunction : UIFunctionNames){
                if(UIFunction.contains(name)){
                    drawable_id = R.drawable.ui_2x;
                    return drawable_id;
                }
            }
        }

        return drawable_id;
    }

    public boolean requiresBulkData(){
        for(String BulkDataFunctionName : BulkDataFunctionNames){
            if(BulkDataFunctionName.contains(name)){
                return true;
            }
        }
        return false;
    }

}
