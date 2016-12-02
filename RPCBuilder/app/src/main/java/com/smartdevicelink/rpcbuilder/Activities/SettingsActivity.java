package com.smartdevicelink.rpcbuilder.Activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.smartdevicelink.marshal.JsonRPCMarshaller;
import com.smartdevicelink.protocol.enums.FunctionID;
import com.smartdevicelink.rpcbuilder.DataModels.RBRequestBuilder;
import com.smartdevicelink.rpcbuilder.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class SettingsActivity extends AppCompatActivity {
    private final String Settings_file = "Settings_file";
    private String filename;
    private String connectionType;
    private String ip_address;
    private String port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        setTitle("Settings");

        AssetManager assetManager = getAssets();
        ArrayList<String> spec_files = new ArrayList<String>();

        try{
            for(String s : assetManager.list("")){
                if(s.contains(".xml"))
                    spec_files.add(s);
            }
        }catch(IOException e){
            Log.e("Failure", "No Spec files in assets folder.");
        }

        Spinner connectionSpinner = (Spinner) findViewById(R.id.connection_spinner);
        Spinner fileSpinner = (Spinner) findViewById(R.id.file_spinner);

        ArrayAdapter<CharSequence> cs_adapter = ArrayAdapter.createFromResource(this, R.array.connection_methods, android.R.layout.simple_spinner_item);
        ArrayAdapter<String> fs_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spec_files);

        cs_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fs_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        connectionSpinner.setAdapter(cs_adapter);
        fileSpinner.setAdapter(fs_adapter);

        loadSettings();
    }

    @Override
    protected void onDestroy() {
        saveSettings();
        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    public void onProceedSelected() {
        Spinner fileSpinner = (Spinner) findViewById(R.id.file_spinner);
        filename = fileSpinner.getSelectedItem().toString();

        Spinner connectionSpinner = (Spinner) findViewById(R.id.connection_spinner);
        connectionType = connectionSpinner.getSelectedItem().toString();

        EditText ip_address_et = (EditText) findViewById(R.id.ip_address);
        ip_address = ip_address_et.getText().toString();

        EditText port_et = (EditText) findViewById(R.id.port);
        port = port_et.getText().toString();

        saveSettings();

        Intent intent = new Intent(this, BuildActivity.class);
        intent.putExtra("from", "SettingsActivity");
        intent.putExtra("filename", filename);
        intent.putExtra("connectionType", connectionType);
        intent.putExtra("ip_address", ip_address);
        intent.putExtra("port", port);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.proceed:
                // User chose the "Next" item, parse XML and prep to send RegisterAppInterface
                onProceedSelected();

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveSettings(){
        Hashtable<String,Object> hash = new Hashtable<String, Object>();
        hash.put("filename", filename);
        hash.put("connectionType", connectionType);
        hash.put("ip_address", ip_address);
        hash.put("port", port);

        String data = null;
        FileOutputStream outputStream = null;

        try {
            File file = new File(this.getFilesDir(), Settings_file);
            outputStream = new FileOutputStream(file, false); // will overwrite existing data

            if(!file.exists()){
                file.createNewFile();
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        try {
            data = JsonRPCMarshaller.serializeHashtable(hash).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            outputStream.write(data.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSettings(){
        File file = new File(this.getFilesDir(), Settings_file);
        String data = null;
        BufferedReader br = null;
        if(!file.exists())
            return;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            data = sb.toString();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(data != null){
            try {
                Hashtable<String, Object> hash = JsonRPCMarshaller.deserializeJSONObject(new JSONObject(data));
                Spinner connectionSpinner = (Spinner) findViewById(R.id.connection_spinner);
                EditText ipAddress = (EditText) findViewById(R.id.ip_address);
                EditText portNumber = (EditText) findViewById(R.id.port);

                connectionSpinner.setSelection(((ArrayAdapter<String>) connectionSpinner.getAdapter()).getPosition((String) hash.get("connectionType")));
                ipAddress.setText((String) hash.get("ip_address"));
                portNumber.setText((String) hash.get("port"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
