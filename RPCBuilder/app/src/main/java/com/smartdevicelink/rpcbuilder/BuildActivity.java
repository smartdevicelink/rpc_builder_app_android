package com.smartdevicelink.rpcbuilder;

import android.annotation.TargetApi;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.smartdevicelink.rpcbuilder.SmartDeviceLink.SDLConfiguration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class BuildActivity extends AppCompatActivity {

    private String filename = "Mobile_API.xml";
    private SDLConfiguration sdlConfiguration;
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
        parserHandler = parseXML();

        if(callingActivity.equals("SettingsActivity")){ // Prepare to send RAI, connect to SDL core

            // TODO: Connect to SDl Core

            RBFunction RAI_request = null;
            for(RBFunction rb : parserHandler.getRequests()){
                if(rb.name.equals("RegisterAppInterface") && rb.getFunctionId().equals("RegisterAppInterfaceID"))
                    RAI_request = rb;
            }

            if(RAI_request == null) { // there is no RAI spec in XML file, go back to Settings
                Toast.makeText(this, "No RegisterAppInterface in XML file", Toast.LENGTH_SHORT);
                finish();
            }else{
                rbFunction = RAI_request;
                showRBParameterFragment();
            }

        }else{ // Display list of all RPC requests

        }
    }

    // handles extras in intent sent from SettingsActivity
    // always returns String of name of activity that called it
    private String handleIntent(Intent intent){
        if(intent.getStringExtra("from").equals("SettingsActivity")){
            filename = intent.getStringExtra("filename");
            String ip_address = intent.getStringExtra("ip_address");
            String port = intent.getStringExtra("port");
            sdlConfiguration = new SDLConfiguration(ip_address, port);
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

    public void OnRequestSelected(RBFunction rb){
        rbFunction = rb;

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

    public void showRBStructFragment() {
        android.app.FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(rbFunction != null){
            Fragment fragmentA = fragmentManager.findFragmentByTag("ListRBFuncParams:" + rbFunction.name);
            if(fragmentA != null)
                fragmentTransaction.hide(fragmentA);
        }

        if(rbStruct != null) {
            RBStructFragment fragmentB = (RBStructFragment) fragmentManager.findFragmentByTag("ListRBStructParams:" + rbStruct.name);

            if (fragmentB == null) {
                fragmentB = new RBStructFragment();
                fragmentTransaction.add(R.id.activity_build, fragmentB, "ListRBStructParams:" + rbStruct.name);
            }

            fragmentTransaction.show(fragmentB);
        }
        fragmentTransaction.commit();
    }

    public void showRBParameterFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        RBFuncFragment fragmentA = (RBFuncFragment) fragmentManager.findFragmentByTag("ListRBRequests");
        if(fragmentA != null)
            fragmentTransaction.remove(fragmentA);

        if(rbStruct != null) {
            RBStructFragment fragmentB = (RBStructFragment) fragmentManager.findFragmentByTag("ListRBStructParams:" + rbStruct.name);
            if(fragmentB != null)
                fragmentTransaction.hide(fragmentB);
        }

        if(rbFunction != null){
            RBParameterFragment fragmentC = (RBParameterFragment) fragmentManager.findFragmentByTag("ListRBFuncParams:" + rbFunction.name);

            if (fragmentC == null) {
                fragmentC = new RBParameterFragment();
                fragmentTransaction.add(R.id.activity_build, fragmentC, "ListRBFuncParams:" + rbFunction.name);
            }
            
            fragmentTransaction.show(fragmentC);
        }
        fragmentTransaction.commit();
    }

    public void showRBFuncFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(rbFunction != null){
            RBParameterFragment fragmentA = (RBParameterFragment) fragmentManager.findFragmentByTag("ListRBFuncParams:" + rbFunction.name);
            if(fragmentA != null)
                fragmentTransaction.remove(fragmentA);
        }

        RBFuncFragment fragmentB = new RBFuncFragment();
        fragmentTransaction.add(R.id.activity_build, fragmentB, "ListRBRequests");
        fragmentTransaction.commit();
    }

}
