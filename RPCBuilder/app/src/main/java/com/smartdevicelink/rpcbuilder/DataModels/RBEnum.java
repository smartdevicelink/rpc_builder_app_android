package com.smartdevicelink.rpcbuilder.DataModels;

import org.xml.sax.Attributes;

import java.util.Vector;

/**
 * Created by amulle19 on 10/25/16.
 */

public class RBEnum extends RBBaseObject {
    public final Vector<RBElement> elements = new Vector<RBElement>();

    public RBEnum(Attributes attributes) {
        super(attributes);
    }

    public void addElement(RBElement element) {
        elements.add(element);
    }
}
