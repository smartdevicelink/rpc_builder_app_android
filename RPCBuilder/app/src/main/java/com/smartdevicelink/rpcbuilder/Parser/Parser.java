package com.smartdevicelink.rpcbuilder.Parser;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by amulle19 on 10/25/16.
 */

public class Parser {
    private XMLReader mXmlReader;
    private ParserHandler mParserHandler;

    public void parse(InputStream inputStream) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        mXmlReader = parser.getXMLReader();
        mParserHandler  = new ParserHandler();
        mXmlReader.setContentHandler(mParserHandler);

        mXmlReader.parse(new InputSource(inputStream));
    }

    public ParserHandler getParserHandler(){
        return mParserHandler;
    }
}

