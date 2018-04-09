package com.smartdevicelink.rpcbuilder.Activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.smartdevicelink.rpcbuilder.R;
import com.smartdevicelink.rpcbuilder.SmartDeviceLink.SdlService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by austinkirk on 3/21/18.
 */

public class LogActivity extends AppCompatActivity {
	final static int COLOR_OUTGOING = Color.WHITE;
	final static int COLOR_INCOMING = Color.BLUE;
	ScrollView loggerText;
	private String title = "Communication Log";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);
		setTitle(title);

		loggerText = (ScrollView) findViewById(R.id.log);
		readFromLogFile(loggerText);
	}

	@Override
	protected void onResume() {
		super.onResume();
		loggerText.post(new Runnable() {
			@Override
			public void run() {
				loggerText.fullScroll(View.FOCUS_DOWN);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void readFromLogFile(ScrollView scrollView){
		File file = new File(this.getFilesDir(), BuildActivity.LOG_FILENAME);
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
				sb.append("\n\r");
				line = br.readLine();
			}
			data = sb.toString();
			if(!data.isEmpty()){
				logTextToScroll(data, true);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void logTextToScroll(String s, Boolean outgoing){
		if(s.isEmpty())
			return;

		TextView t = new TextView(this);
		s += "\n";
		t.setText(s);
		t.setTextColor(outgoing ? COLOR_OUTGOING : COLOR_INCOMING);
		loggerText.addView(t);
	}

	private void clearLog(){
		for(int i = 0; i < loggerText.getChildCount(); i++){
			TextView t = (TextView) loggerText.getChildAt(i);
			t.setText("");
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.log_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
			case R.id.clear:
				File file = new File(this.getFilesDir(), BuildActivity.LOG_FILENAME);
				if(file.exists())
					file.delete();
				clearLog();
				return true;

			default:
				// If we got here, the user's action was not recognized.
				// Invoke the superclass to handle it.
				return super.onOptionsItemSelected(item);

		}
	}


}
