package com.smartdevicelink.rpcbuilder.Parser;

import android.util.Log;

import com.smartdevicelink.rpcbuilder.DataModels.RBBaseObject;
import com.smartdevicelink.rpcbuilder.DataModels.RBElement;
import com.smartdevicelink.rpcbuilder.DataModels.RBEnum;
import com.smartdevicelink.rpcbuilder.DataModels.RBFunction;
import com.smartdevicelink.rpcbuilder.DataModels.RBParam;
import com.smartdevicelink.rpcbuilder.DataModels.RBStruct;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ParserHandler extends DefaultHandler {

    private static final String DescriptionKey = "description";
    private static final String DesignDescriptionKey = "designdescription";
    private static final String ElementKey = "element";
    private static final String EnumKey = "enum";
    private static final String FunctionKey = "function";
    private static final String InterfaceKey = "interface";
    private static final String IssueKey = "issue";
    private static final String ParamKey = "param";
    private static final String StructKey = "struct";
    private static final String TodoKey = "todo";
    private static final String RequestKey = "request";
    private static final String ResponseKey = "response";

    private String mCurrentTag;

    private Vector<Object> mTagsContainer = new Vector<Object>();

    private Vector<RBElement> mElementsContainer = new Vector<RBElement>();
    public Vector<RBElement> getElements() {
        return mElementsContainer;
    }

    private Vector<RBFunction> mFunctionsContainer = new Vector<RBFunction>();

    private Vector<RBFunction> mRequestsContainer = new Vector<RBFunction>();
    public Vector<RBFunction> getRequests() {
        return mRequestsContainer;
    }

    private Vector<RBFunction> mResponsesContainer= new Vector<RBFunction>();
    public Vector<RBFunction> getResponses() {
        return mResponsesContainer;
    }

    private Map<String, RBEnum> mEnumsDictionary = new HashMap<String, RBEnum>();
    public Map<String, RBEnum> getEnums() { return mEnumsDictionary; }

    private Map<String, RBStruct> mStructsDictionary= new HashMap<String, RBStruct>();
    public Map<String, RBStruct> getStructs() { return mStructsDictionary; }

    @Override
    public void startDocument() throws SAXException {
        mTagsContainer = new Vector<Object>();
        mElementsContainer = new Vector<RBElement>();
        mRequestsContainer = new Vector<RBFunction>();
        mResponsesContainer = new Vector<RBFunction>();
        mFunctionsContainer = new Vector<RBFunction>();
        mEnumsDictionary = new HashMap<String, RBEnum>();
        mStructsDictionary = new HashMap<String, RBStruct>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        mCurrentTag = localName;
        if (localName.equals(InterfaceKey)
                || localName.equals(DescriptionKey)
                || localName.equals(DesignDescriptionKey)
                || localName.equals(IssueKey)
                || localName.equals(TodoKey)) {
            return; // no-op
        } else if (localName.equals(EnumKey)) {
            mTagsContainer.add(new RBEnum(attributes));
        } else if (localName.equals(ElementKey)) {
            mTagsContainer.add(new RBElement(attributes));
        } else if (localName.equals(StructKey)) {
            mTagsContainer.add(new RBStruct(attributes));
        } else if (localName.equals(ParamKey)) {
            mTagsContainer.add(new RBParam(attributes));
        } else if (localName.equals(FunctionKey)) {
            mTagsContainer.add(new RBFunction(attributes));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals(InterfaceKey)
                || localName.equals(DesignDescriptionKey)
                || localName.equals(IssueKey)
                || localName.equals(TodoKey)) {
            return; // no-op
        }

        mCurrentTag = null;

        Object lastObject = mTagsContainer.lastElement();
        mTagsContainer.removeElement(lastObject);

        if(lastObject.getClass() != String.class) {

            if (localName.equals(EnumKey)) {
                RBEnum enumObj = (RBEnum) lastObject;
                mEnumsDictionary.put(enumObj.name, enumObj);
                return;
            } else if (localName.equals(FunctionKey)) {
                RBFunction functionObj = (RBFunction) lastObject;
                if (functionObj.getMessageType().equals("request")) {
                    mRequestsContainer.add(functionObj);
                } else if (functionObj.getMessageType().equals("response")) {
                    mResponsesContainer.add(functionObj);
                }
                mFunctionsContainer.add(functionObj);
            } else if (localName.equals(StructKey)) {
                RBStruct structObj = (RBStruct) lastObject;
                mStructsDictionary.put(structObj.name, structObj);
            } else if (localName.equals(ElementKey)) {
                RBElement elementObj = (RBElement) lastObject;
                mElementsContainer.add(elementObj);
            }
        }

        if (mTagsContainer.size() > 0) {
            addObjectToParent(lastObject, mTagsContainer.lastElement());
        } else {
            Log.d("EndElement", "here");
        }

        Log.d("EndElement", "URI: " + uri + " localName: " + localName + " qName: " + qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (start == 0 && length == 1 && ch[0] == '\n') {
            return;
        }

        String text = new String(ch, start, length).trim();

        if (text.length() == 0) {
            return;
        }

        if(mCurrentTag.equals(DescriptionKey))
            mTagsContainer.add(text);
        Log.d("Characters", "Text: " + text);
    }

    private void addObjectToParent(Object object, Object parent) {
        if (object.getClass() == String.class) {
            String objectString = (String)object;
            if (parent.getClass() == String.class) {
                String parentString = (String)parent;
                mTagsContainer.remove(parent);
                String newString = parentString.concat(" "+objectString);
                addObjectToParent(newString, mTagsContainer.lastElement());
            } else if (parent.getClass() == RBElement.class) {
                RBElement elementObj = (RBElement)parent;
                elementObj.objectDescription = objectString;
            } else if (parent.getClass() == RBEnum.class) {
                RBEnum enumObj = (RBEnum)parent;
                enumObj.objectDescription = objectString;
            } else if (parent.getClass() == RBFunction.class){
                RBFunction funcObj = (RBFunction) parent;
                funcObj.objectDescription = objectString;
            }
        } else if (object.getClass() == RBElement.class) {
            if (parent.getClass() == RBEnum.class
                    || parent.getClass() == RBParam.class) {
                RBEnum enumObj = (RBEnum)parent;
                RBElement element = (RBElement)object;
                enumObj.addElement(element);
            }
        } else if (object.getClass() == RBParam.class) {
            if (parent.getClass() == RBStruct.class
                    || parent.getClass() == RBFunction.class) {
                RBParam paramObj = (RBParam)object;
                RBStruct structObj = (RBStruct)parent;
                structObj.addParameter(paramObj);
            }
        }
    }
}
