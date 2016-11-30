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

import com.smartdevicelink.rpcbuilder.R;

import java.io.IOException;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

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
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    public void onProceedSelected() {
        Spinner fileSpinner = (Spinner) findViewById(R.id.file_spinner);
        String filename = fileSpinner.getSelectedItem().toString();

        Spinner connectionSpinner = (Spinner) findViewById(R.id.connection_spinner);
        String connectionType = connectionSpinner.getSelectedItem().toString();

        EditText ip_address_et = (EditText) findViewById(R.id.ip_address);
        String ip_address = ip_address_et.getText().toString();

        EditText port_et = (EditText) findViewById(R.id.port);
        String port = port_et.getText().toString();

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
}
