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

import com.smartdevicelink.marshal.JsonRPCMarshaller;
import com.smartdevicelink.rpcbuilder.SmartDeviceLink.SdlService;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class BuildActivity extends AppCompatActivity {

    private String filename = "Mobile_API.xml";
    private ParserHandler parserHandler = null;
    private RBFunction rbFunction = null;
    private RBStruct rbStruct = null;
    private RBEnum rbEnum = null;

    public final String LIST_PARAMS_KEY = "LIST_PARAMS";
    public final String LIST_FUNCS_KEY = "LIST_FUNCS";
    public final String LIST_STRUCT_PARAMS_KEY = "LIST_STRUCT_PARAMS";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build);

        String callingActivity = null;
        callingActivity = handleIntent(getIntent());
        parserHandler = parseXML();

        if(callingActivity.equals("SettingsActivity")){ // Prepare to send RAI, connect to SDL core

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
                showFragment(ListParamsFragment.class);
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
            String connectionType = intent.getStringExtra("connectionType");
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

    public void addFragment(Class<?> fragment_type) {
        android.app.FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(fragment_type == ListFuncsFragment.class){
            ListFuncsFragment fragment = new ListFuncsFragment();
            fragmentTransaction.add(R.id.activity_build, fragment, LIST_FUNCS_KEY);
            fragmentTransaction.hide(fragment);
        }else if(fragment_type == ListParamsFragment.class){
            if(rbFunction != null){
                ListParamsFragment fragment = (ListParamsFragment) fragmentManager.findFragmentByTag(LIST_PARAMS_KEY + ":" + rbFunction.name);

                if (fragment == null) {
                    fragment = new ListParamsFragment();
                    fragment.setRBFunction(rbFunction);
                    fragmentTransaction.add(R.id.activity_build, fragment, LIST_PARAMS_KEY + ":" + rbFunction.name);
                }

                fragmentTransaction.hide(fragment);
            }
        }else if(fragment_type == ListStructParamsFragment.class){
            if(rbStruct != null) {
                ListStructParamsFragment fragment = (ListStructParamsFragment) fragmentManager.findFragmentByTag(LIST_STRUCT_PARAMS_KEY + ":" + rbStruct.name.toLowerCase());

                if (fragment == null) {
                    fragment = new ListStructParamsFragment();
                    fragment.setRBStruct(rbStruct);
                    fragmentTransaction.add(R.id.activity_build, fragment, LIST_STRUCT_PARAMS_KEY + ":" + rbStruct.name.toLowerCase());
                }

                fragmentTransaction.hide(fragment);
            }
        }else{
            return; //should not reach here
        }

        fragmentTransaction.commit();
    }

    public void showFragment(Class<?> fragment_type){
        android.app.FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(fragment_type == ListFuncsFragment.class){
            ListFuncsFragment fragment = new ListFuncsFragment();
            fragmentTransaction.add(R.id.activity_build, fragment, LIST_FUNCS_KEY);
            fragmentTransaction.show(fragment);
        }else if(fragment_type == ListParamsFragment.class){
            if(rbFunction != null){
                ListParamsFragment fragment = (ListParamsFragment) fragmentManager.findFragmentByTag(LIST_PARAMS_KEY + ":" + rbFunction.name);

                if (fragment == null) {
                    fragment = new ListParamsFragment();
                    fragment.setRBFunction(rbFunction);
                    fragmentTransaction.add(R.id.activity_build, fragment, LIST_PARAMS_KEY + ":" + rbFunction.name);
                }

                fragmentTransaction.show(fragment);
            }
        }else if(fragment_type == ListStructParamsFragment.class){
            if(rbStruct != null) {
                ListStructParamsFragment fragment = (ListStructParamsFragment) fragmentManager.findFragmentByTag(LIST_STRUCT_PARAMS_KEY + ":" + rbStruct.name.toLowerCase());

                if (fragment == null) {
                    fragment = new ListStructParamsFragment();
                    fragment.setRBStruct(rbStruct);
                    fragmentTransaction.add(R.id.activity_build, fragment, LIST_STRUCT_PARAMS_KEY + ":" + rbStruct.name.toLowerCase());
                }else{
                    fragmentTransaction.show(fragment);
                }
            }
        }else{
            return; //should not reach here
        }

        fragmentTransaction.commit();
    }

    public void hideFragment(Fragment fragment){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(fragment != null){
            fragmentTransaction.hide(fragment);
        }
        fragmentTransaction.commit();
    }

    public void removeFragment(Fragment fragment){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(fragment != null){
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.commit();
    }

    public void sendRPCRequest(Hashtable<String,Object> hash){
        Intent sdlServiceIntent = new Intent(this, SdlService.class);
        try {
            sdlServiceIntent.putExtra("sendRPCRequest", JsonRPCMarshaller.serializeHashtable(hash).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startService(sdlServiceIntent);
    }

}
