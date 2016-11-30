package com.smartdevicelink.rpcbuilder.DataModels;

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
}
