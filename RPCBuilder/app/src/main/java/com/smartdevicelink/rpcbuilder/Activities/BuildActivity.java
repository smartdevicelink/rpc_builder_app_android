package com.smartdevicelink.rpcbuilder.Activities;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.smartdevicelink.marshal.JsonRPCMarshaller;
import com.smartdevicelink.rpcbuilder.DataModels.RBEnum;
import com.smartdevicelink.rpcbuilder.DataModels.RBFunction;
import com.smartdevicelink.rpcbuilder.DataModels.RBStruct;
import com.smartdevicelink.rpcbuilder.Fragments.ListFuncsFragment;
import com.smartdevicelink.rpcbuilder.Fragments.ListParamsFragment;
import com.smartdevicelink.rpcbuilder.Fragments.ListStructParamsFragment;
import com.smartdevicelink.rpcbuilder.Parser.Parser;
import com.smartdevicelink.rpcbuilder.Parser.ParserHandler;
import com.smartdevicelink.rpcbuilder.R;
import com.smartdevicelink.rpcbuilder.SmartDeviceLink.SdlReceiver;
import com.smartdevicelink.rpcbuilder.SmartDeviceLink.SdlService;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

public class BuildActivity extends AppCompatActivity {
    public final int FILE_PICKER_SUCCESS = 12;
    private Boolean connectionEstablished = false;

    private String filename = "Mobile_API_4.1.xml";
    private String ip_address = "";
    private String port = "12345";
    private String connectionType = "TCP";
    private String bulkData = null;

    private ParserHandler parserHandler = null;
    private RBFunction rbFunction = null;
    private RBStruct rbStruct = null;
    private RBEnum rbEnum = null;

    private ProgressDialog progress;

    public final String LIST_PARAMS_KEY = "LIST_PARAMS";
    public final String LIST_FUNCS_KEY = "LIST_FUNCS";
    public final String LIST_STRUCT_PARAMS_KEY = "LIST_STRUCT_PARAMS";

    ConnectionReceiver connectionReceiver;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build);

        // Line for making sure EditText is not entered unless explicitly clicked on
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        String callingActivity = null;
        callingActivity = handleIncomingIntent(getIntent()); //Handle incoming intent, grab connection information if it is from SettingsActivity
        parserHandler = parseXML();

        if(callingActivity.equals("SettingsActivity")){ // Prepare to send RAI, connect to SDL core

            // Loop through parsed XML looking for RAI request
            RBFunction RAI_request = null;
            for(RBFunction rb : parserHandler.getRequests()){
                if(rb.name.equals("RegisterAppInterface") && rb.getFunctionId().equals("RegisterAppInterfaceID"))
                    RAI_request = rb;
            }

            if(RAI_request == null) { // there is no RAI spec in XML file, go back to Settings
                Toast.makeText(this, "No RegisterAppInterface function in XML file", Toast.LENGTH_SHORT);
                finish();
            }else{
                rbFunction = RAI_request; // Set the current function to be RAI
                showFragment(ListParamsFragment.class); // Show the parameters for RAI
            }

        }else{ // Currently not called by another Activity

        }
    }

    @Override
    protected void onDestroy() {
        Intent sdlServiceIntent = new Intent(this, SdlService.class);
        stopService(sdlServiceIntent); // make sure Sdl Service is stopped.
        connectionEstablished = false;
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        // Register a Receiver to be notified when Sdl Connection is valid
        connectionReceiver = new ConnectionReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SdlService.CONNECTION_NOTIFICATION_ACTION);
        registerReceiver(connectionReceiver, intentFilter);

        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(connectionReceiver);

        super.onStop();
    }

    private class ConnectionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            if(Boolean.parseBoolean(arg1.getStringExtra("connectionEstablished")) == true){
                connectionEstablished = true;
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Connected to Core.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "Disconnected from Core.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case FILE_PICKER_SUCCESS:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        Log.d("ISTREAM BYTES AVAIL:", ""+inputStream.available());

                        bulkData = uri.toString();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // handles extras in Intent sent from SettingsActivity
    // always returns String of name of activity that called it
    private String handleIncomingIntent(Intent intent){
        if(intent.getStringExtra("from").equals("SettingsActivity")){
            filename = intent.getStringExtra("filename");
            ip_address = intent.getStringExtra("ip_address");
            port = intent.getStringExtra("port");
            connectionType = intent.getStringExtra("connectionType");
        }

        return intent.getStringExtra("from");
    }

    private Intent handleOutgoingIntent(Intent intent){
        intent.putExtra("from", "BuildActivity");
        intent.putExtra("ip_address", ip_address);
        intent.putExtra("port", port);
        intent.putExtra("connectionType", connectionType);
        return intent;
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
    public Boolean getConnectionEstablished(){
        return connectionEstablished;
    }

    public void setRBFunction(RBFunction rb){ rbFunction = rb; }
    public void setRBStruct(RBStruct rb) { rbStruct = rb; }
    public void setRBEnum(RBEnum rb) { rbEnum = rb; }

    public ParserHandler getParserHandler() { return parserHandler; }

    // Adds a hidden Fragment to the current Activity
    public void addFragment(Class<?> fragment_type) {
        android.app.FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(fragment_type == ListFuncsFragment.class){

            ListFuncsFragment fragment = (ListFuncsFragment) fragmentManager.findFragmentByTag(LIST_FUNCS_KEY);

            if(fragment == null){
                fragment = new ListFuncsFragment();
                fragmentTransaction.add(R.id.activity_build, fragment, LIST_FUNCS_KEY);
            }
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

    // Shows a Fragment that may or may not have been previously added to Activity
    public void showFragment(Class<?> fragment_type){
        android.app.FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(fragment_type == ListFuncsFragment.class){
            ListFuncsFragment fragment = (ListFuncsFragment) fragmentManager.findFragmentByTag(LIST_FUNCS_KEY);

            if(fragment == null){
                fragment = new ListFuncsFragment();
                fragmentTransaction.add(R.id.activity_build, fragment, LIST_FUNCS_KEY);
            }
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
        if(!connectionEstablished){
            SdlReceiver.queryForConnectedService(this);

            String message = connectionType.equals("TCP") ? "Please wait... If not connected after 15s, app will close." : "Connecting via Bluetooth...";

            progress = ProgressDialog.show(this, "Connecting to Core",
                    message, true);
            progress.setCanceledOnTouchOutside(false);

            Timer timer = new Timer();
            if(connectionType.equals("TCP")) {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!connectionEstablished) {
                            progress.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    android.os.Process.killProcess(android.os.Process.myPid()); // Force quits after 15 seconds of no connection
                                }
                            });
                            finish();
                        }
                    }
                }, 15 * 1000);
            }else{
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!connectionEstablished) {
                            progress.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Could not connect to core... check Bluetooth settings.", Toast.LENGTH_LONG).show();
                                    progress.dismiss();
                                }
                            });
                            finish();
                        }
                    }
                }, 15 * 1000);
            }
        }

        Intent sdlServiceIntent = new Intent(this, SdlService.class);
        try {
            sdlServiceIntent.putExtra("sendRPCRequest", JsonRPCMarshaller.serializeHashtable(hash).toString());
            if(rbFunction.requiresBulkData())
                sdlServiceIntent.putExtra("bulkData", bulkData);
            handleOutgoingIntent(sdlServiceIntent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startService(sdlServiceIntent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
