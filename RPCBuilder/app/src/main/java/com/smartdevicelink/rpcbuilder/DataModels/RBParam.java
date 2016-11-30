package com.smartdevicelink.rpcbuilder.DataModels;

import org.xml.sax.Attributes;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by amulle19 on 10/26/16.
 */

public class RBParam extends RBEnum {
    public String mType;
    public String mDefaultValue;
    public Number mMinValue;
    public Number mMaxValue;
    public Number mMaxLength;
    public Boolean mRequiresArray;
    public Boolean mIsMandatory;

    public RBParam(Attributes attributes) {
        super(attributes);
    }

    @Override
    protected void handle(String key, String value) {
        try {
            if (key.equals("type")) {
                mType = value;
            } else if (key.equals("defvalue")) {
                mDefaultValue = value;
            } else if (key.equals("mandatory")) {
                mIsMandatory = Boolean.valueOf(value);
            } else if (key.equals("array")) {
                mRequiresArray = Boolean.valueOf(value);
            } else if (key.equals("minvalue")) {
                mMinValue = NumberFormat.getInstance().parse(value);
            } else if (key.equals("maxvalue")) {
                mMaxValue = NumberFormat.getInstance().parse(value);
            } else if (key.equals("maxlength")) {
                mMaxLength = NumberFormat.getInstance().parse(value);
            } else {
                super.handle(key, value);
            }
        } catch (ParseException e) {
            e.printStackTrace();

        }
    }
}
