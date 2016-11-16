package com.smartdevicelink.rpcbuilder;

import android.annotation.TargetApi;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.smartdevicelink.rpcbuilder.Views.Fragments.RBParameterFragment;
import com.smartdevicelink.rpcbuilder.Views.Fragments.RBParameterFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;

public class BuildActivity extends AppCompatActivity {

    private String filename = "Mobile_API.xml";
    private String connectionType = "TCP";
    private String ip_address = "";
    private String port = "";
    private ParserHandler parserHandler = null;
    private RBFunction rbFunction = null;
    private RBStruct rbStruct = null;
    private RBEnum rbEnum = null;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build);

        String callingActivity = null;
        callingActivity = handleIntent(getIntent());

        if(callingActivity.equals("SettingsActivity")){ // Prepare to send RAI, connect to SDL core

            // TODO: Connect to SDl Core

            // add Parameter Fragment
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            RBParameterFragment fragment = new RBParameterFragment();
            fragmentTransaction.add(R.id.activity_build, fragment, "ListRBFuncParams");
            fragmentTransaction.commit();

            parserHandler = parseXML();

            RBFunction RAI_request = null;
            for(RBFunction rb : parserHandler.getRequests()){
                if(rb.name.equals("RegisterAppInterface") && rb.getFunctionId().equals("RegisterAppInterfaceID"))
                    RAI_request = rb;
            }

            if(RAI_request == null) // there is no RAI spec in XML file, go back to Settings
                finish();
            else{
                rbFunction = RAI_request;
                revertTitle();
            }

        }else{ // Display list of all RPC requests

        }
    }

    public void revertTitle(){
        if(rbFunction != null)
            this.setTitle(rbFunction.name);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.build_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send:
                // User chose the "Send" option, send RAI RPC request
                return true;

            case R.id.back:
                finish();

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    // handles extras in intent sent from SettingsActivity
    // always returns String of name of activity that called it
    private String handleIntent(Intent intent){
        if(intent.getStringExtra("from").equals("SettingsActivity")){
            filename = intent.getStringExtra("filename");
            connectionType = intent.getStringExtra("connectionType");
            ip_address = intent.getStringExtra("ip_address");
            port = intent.getStringExtra("port");
        }

        return intent.getStringExtra("from");
    }

    private ParserHandler parseXML(){
        try {
            InputStream inputStream = getAssets().open(filename);

            Parser parser = new Parser();
            parser.parse(inputStream);

            return parser.getParserHandler();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public RBFunction getRBFunction(){ return rbFunction; }
    public RBStruct getRBStruct() { return rbStruct; }
    public RBEnum getRBEnum() {return rbEnum;}

    public void setRBFunction(RBFunction rb){ rbFunction = rb; }
    public void setRBStruct(RBStruct rb) { rbStruct = rb; }
    public void setRBEnum(RBEnum rb) { rbEnum = rb; }

    public ParserHandler getParserHandler() { return parserHandler; }

}
