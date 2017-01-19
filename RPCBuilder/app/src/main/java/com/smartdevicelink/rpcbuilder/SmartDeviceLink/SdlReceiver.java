package com.smartdevicelink.rpcbuilder.SmartDeviceLink;

import android.content.Context;
import android.content.Intent;

import com.smartdevicelink.transport.SdlBroadcastReceiver;

/**
 * Created by austinkirk on 11/1/16.
 */

public class SdlReceiver extends SdlBroadcastReceiver {

    @Override
    public void onSdlEnabled(Context context, Intent intent) {
        //Use the provided intent but set the class to the SdlService
        intent.setClass(context, SdlService.class);
        context.startService(intent);
    }


    @Override
    public Class<? extends SdlRouterService> defineLocalSdlRouterClass() {
        //Return a local copy of the SdlRouterService located in your project
        return com.smartdevicelink.rpcbuilder.SmartDeviceLink.SdlRouterService.class;
    }
}
