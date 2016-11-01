package com.smartdevicelink.rpcbuilder;

import android.util.Log;

import org.xml.sax.Attributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amulle19 on 10/25/16.
 */

public class RBBaseObject {
    public String name = null;
    public String description = null;
    public final Map<String, String> properties = new HashMap<String, String>();

    public RBBaseObject(Attributes attributes) {
        for (int i = 0; i < attributes.getLength(); i++) {
            handle(attributes.getLocalName(i), attributes.getValue(i));
        }
    }

    public void appendDescription(String string) {
        if (string.length() == 0) {
            return;
        }
        string = string.replaceAll("\\s{2,}", "");
        description = description.concat(string);
    }

    protected void handle(String key, String value) {
        if (key.equals("name")) {
            this.name = value;
        } else {
            properties.put(key, value);
        }
    }
}