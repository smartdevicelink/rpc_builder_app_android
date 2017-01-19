package com.smartdevicelink.rpcbuilder.DataModels;

import org.xml.sax.Attributes;

import java.util.Vector;

/**
 * Created by amulle19 on 10/26/16.
 */

public class RBStruct extends RBElement {
    private final Vector<RBParam> mParams = new Vector<RBParam>();

    public RBStruct(Attributes attributes) {
        super(attributes);
    }

    public void addParameter(RBParam parameter) {
        mParams.add(parameter);
    }

    public Vector<RBParam> getParams(){ return mParams; }
}
