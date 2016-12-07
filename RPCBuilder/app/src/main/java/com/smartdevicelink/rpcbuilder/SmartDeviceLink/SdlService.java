package com.smartdevicelink.rpcbuilder.SmartDeviceLink;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.marshal.JsonRPCMarshaller;
import com.smartdevicelink.proxy.IProxyListener;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.SdlProxyALM;
import com.smartdevicelink.proxy.SdlProxyBuilder;
import com.smartdevicelink.proxy.SdlProxyConfigurationResources;
import com.smartdevicelink.proxy.callbacks.OnServiceEnded;
import com.smartdevicelink.proxy.callbacks.OnServiceNACKed;
import com.smartdevicelink.proxy.constants.Names;
import com.smartdevicelink.proxy.interfaces.IProxyListenerALM;
import com.smartdevicelink.proxy.rpc.AddCommandResponse;
import com.smartdevicelink.proxy.rpc.AddSubMenuResponse;
import com.smartdevicelink.proxy.rpc.Alert;
import com.smartdevicelink.proxy.rpc.AlertManeuverResponse;
import com.smartdevicelink.proxy.rpc.AlertResponse;
import com.smartdevicelink.proxy.rpc.ChangeRegistrationResponse;
import com.smartdevicelink.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteCommandResponse;
import com.smartdevicelink.proxy.rpc.DeleteFileResponse;
import com.smartdevicelink.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteSubMenuResponse;
import com.smartdevicelink.proxy.rpc.DiagnosticMessageResponse;
import com.smartdevicelink.proxy.rpc.DialNumberResponse;
import com.smartdevicelink.proxy.rpc.EndAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.GenericResponse;
import com.smartdevicelink.proxy.rpc.GetDTCsResponse;
import com.smartdevicelink.proxy.rpc.GetVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.GetWayPointsResponse;
import com.smartdevicelink.proxy.rpc.ListFilesResponse;
import com.smartdevicelink.proxy.rpc.OnAudioPassThru;
import com.smartdevicelink.proxy.rpc.OnButtonEvent;
import com.smartdevicelink.proxy.rpc.OnButtonPress;
import com.smartdevicelink.proxy.rpc.OnCommand;
import com.smartdevicelink.proxy.rpc.OnDriverDistraction;
import com.smartdevicelink.proxy.rpc.OnHMIStatus;
import com.smartdevicelink.proxy.rpc.OnHashChange;
import com.smartdevicelink.proxy.rpc.OnKeyboardInput;
import com.smartdevicelink.proxy.rpc.OnLanguageChange;
import com.smartdevicelink.proxy.rpc.OnLockScreenStatus;
import com.smartdevicelink.proxy.rpc.OnPermissionsChange;
import com.smartdevicelink.proxy.rpc.OnStreamRPC;
import com.smartdevicelink.proxy.rpc.OnSystemRequest;
import com.smartdevicelink.proxy.rpc.OnTBTClientState;
import com.smartdevicelink.proxy.rpc.OnTouchEvent;
import com.smartdevicelink.proxy.rpc.OnVehicleData;
import com.smartdevicelink.proxy.rpc.OnWayPointChange;
import com.smartdevicelink.proxy.rpc.PerformAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.PerformInteractionResponse;
import com.smartdevicelink.proxy.rpc.PutFile;
import com.smartdevicelink.proxy.rpc.PutFileResponse;
import com.smartdevicelink.proxy.rpc.ReadDIDResponse;
import com.smartdevicelink.proxy.rpc.RegisterAppInterface;
import com.smartdevicelink.proxy.rpc.ResetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.ScrollableMessageResponse;
import com.smartdevicelink.proxy.rpc.SdlMsgVersion;
import com.smartdevicelink.proxy.rpc.SendLocationResponse;
import com.smartdevicelink.proxy.rpc.SetAppIconResponse;
import com.smartdevicelink.proxy.rpc.SetDisplayLayoutResponse;
import com.smartdevicelink.proxy.rpc.SetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.SetMediaClockTimerResponse;
import com.smartdevicelink.proxy.rpc.ShowConstantTbtResponse;
import com.smartdevicelink.proxy.rpc.ShowResponse;
import com.smartdevicelink.proxy.rpc.SliderResponse;
import com.smartdevicelink.proxy.rpc.SpeakResponse;
import com.smartdevicelink.proxy.rpc.StreamRPCResponse;
import com.smartdevicelink.proxy.rpc.SubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.SubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.SubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.SystemRequestResponse;
import com.smartdevicelink.proxy.rpc.TTSChunk;
import com.smartdevicelink.proxy.rpc.UnsubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.UpdateTurnListResponse;
import com.smartdevicelink.proxy.rpc.enums.AppHMIType;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.enums.HMILevel;
import com.smartdevicelink.proxy.rpc.enums.Language;
import com.smartdevicelink.proxy.rpc.enums.LockScreenStatus;
import com.smartdevicelink.proxy.rpc.enums.SdlDisconnectedReason;
import com.smartdevicelink.transport.BTTransportConfig;
import com.smartdevicelink.transport.TCPTransportConfig;
import com.smartdevicelink.transport.TransportConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

import static android.provider.UserDictionary.Words.APP_ID;
import static com.smartdevicelink.proxy.constants.Names.appHMIType;
import static com.smartdevicelink.proxy.constants.Names.appID;
import static com.smartdevicelink.proxy.constants.Names.appName;
import static com.smartdevicelink.proxy.constants.Names.hashID;
import static com.smartdevicelink.proxy.constants.Names.hmiDisplayLanguageDesired;
import static com.smartdevicelink.proxy.constants.Names.isMediaApplication;
import static com.smartdevicelink.proxy.constants.Names.languageDesired;
import static com.smartdevicelink.proxy.constants.Names.ngnMediaScreenAppName;
import static com.smartdevicelink.proxy.constants.Names.sdlMsgVersion;
import static com.smartdevicelink.proxy.constants.Names.ttsName;
import static com.smartdevicelink.proxy.constants.Names.type;
import static com.smartdevicelink.proxy.constants.Names.vrSynonyms;
import static com.smartdevicelink.proxy.rpc.enums.FileType.JSON;

/**
 * Created by austinkirk on 11/1/16.
 */

public class SdlService extends Service implements IProxyListenerALM {
    //The proxy handles communication between the application and SDL
    public final static String CONNECTION_NOTIFICATION_ACTION = "CONNECTION_ACTION";
    private String ip_address = "";
    private String port = "12345";
    private String connectionType = "TCP";
    private Boolean first_HMI_NONE = true;
    private Boolean connectionEstablished = false;
    private String bulkData = null;

    private SdlProxyALM proxy;

    //...

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean forceConnect = intent !=null && intent.getBooleanExtra(TransportConstants.FORCE_TRANSPORT_CONNECTED, false);

        // Handle incoming intent, grab connection information if it is from BuildActivity
        handleIncomingIntent(intent);

        // Are we sending an RPC request? If so, grab the Hashtable associated with it
        Hashtable<String, Object> hash = null;
        if(intent.getStringExtra("sendRPCRequest") != null){
            try {
                hash = JsonRPCMarshaller.deserializeJSONObject(new JSONObject(intent.getStringExtra("sendRPCRequest")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String name = null;
        Hashtable<String, Object> parameters = null;

        // Specific case to establish proxy when sending first RAI
        if(proxy == null && hash != null) {
            try{
                if (hash.containsKey("request")) {
                    if (((Hashtable<String, Object>) hash.get("request")).containsKey("name"))
                        name = (String) ((Hashtable<String, Object>) hash.get("request")).get("name");
                    if (((Hashtable<String, Object>) hash.get("request")).containsKey("parameters"))
                        parameters = (Hashtable<String, Object>) ((Hashtable<String, Object>) hash.get("request")).get("parameters");

                    if (parameters != null && name != null) {
                        if (name.equals("RegisterAppInterface")) {
                            String rai_appID = "123", rai_appName = "appName";
                            Boolean rai_isMediaApplication = false;
                            if (parameters.containsKey(appID))
                                rai_appID = (String) parameters.get(appID);
                            if (parameters.containsKey(appName))
                                rai_appName = (String) parameters.get(appName);
                            if (parameters.containsKey(isMediaApplication))
                                rai_isMediaApplication = (Boolean) parameters.get(isMediaApplication);

                            SdlProxyBuilder.Builder sdlProxyBuilder = new SdlProxyBuilder.Builder(this, rai_appID,
                                    rai_appName, rai_isMediaApplication, this);

                            if(connectionType.equals("BT"))
                                sdlProxyBuilder.setTransportType(new BTTransportConfig(true));
                            else
                                sdlProxyBuilder.setTransportType(new TCPTransportConfig(Integer.parseInt(port), ip_address, true));

                            if (hash.containsKey(sdlMsgVersion))
                                sdlProxyBuilder.setSdlMessageVersion(new SdlMsgVersion((Hashtable<String, Object>) hash.get(sdlMsgVersion)));
                            if (hash.containsKey(ttsName)) {
                                Vector<TTSChunk> ttsChunks = new Vector<TTSChunk>();
                                ttsChunks.add(new TTSChunk((Hashtable<String, Object>) hash.get(ttsName)));
                            }
                            if (hash.containsKey(ngnMediaScreenAppName))
                                sdlProxyBuilder.setShortAppName((String) hash.get(ngnMediaScreenAppName));
                            if (hash.containsKey(vrSynonyms)) {
                                Vector<String> vector = new Vector<String>();
                                vector.add((String) hash.get(vrSynonyms));
                                sdlProxyBuilder.setVrSynonyms(vector);
                            }
                            if (hash.containsKey(languageDesired)) {
                                sdlProxyBuilder.setLangDesired((Language) hash.get(languageDesired));
                            }
                            if (hash.containsKey(hmiDisplayLanguageDesired))
                                sdlProxyBuilder.setHMILangDesired((Language) hash.get(hmiDisplayLanguageDesired));
                            if (hash.containsKey(appHMIType)) {
                                Vector<AppHMIType> appHMITypes = new Vector<AppHMIType>();
                                appHMITypes.add((AppHMIType) hash.get(appHMIType));
                                sdlProxyBuilder.setVrAppHMITypes(appHMITypes);
                            }
                            if (hash.containsKey(hashID)) {
                                sdlProxyBuilder.setAppResumeDataHash((String) hash.get(hashID));
                            }

                            proxy = sdlProxyBuilder.build();
                        }
                    }
                }
            } catch (SdlException e) {
                //There was an error creating the proxy
                if (proxy == null) {
                    //Stop the SdlService
                    stopSelf();
                }
            }
        }else{
            if(forceConnect){ //TODO: Check purpose of this
                proxy.forceOnConnected();
            }
            if(hash != null) { // Proxy is established, sending a general RPC Request
                try {
                    RPCRequest rpcRequest = new RPCRequest((hash));
                    if(bulkData != null) {
                        Uri uri = Uri.parse(bulkData);
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        rpcRequest.setBulkData(getBytes(inputStream));
                    }
                    proxy.sendRPCRequest(rpcRequest);
                } catch (SdlException e) {
                    e.printStackTrace();
                    proxy = null;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //use START_STICKY because we want the SDLService to be explicitly started and stopped as needed.
        return START_STICKY;
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private String handleIncomingIntent(Intent intent){
        if(intent.getStringExtra("from").equals("BuildActivity")){
            ip_address = intent.getStringExtra("ip_address");
            port = intent.getStringExtra("port");
            connectionType = intent.getStringExtra("connectionType");
            bulkData = intent.getStringExtra("bulkData");
        }

        return intent.getStringExtra("from");
    }

    @Override
    public void onDestroy() {
        //Dispose of the proxy

        if (proxy != null) {
            try {
                proxy.dispose();
            } catch (SdlException e) {
                e.printStackTrace();
            } finally {
                proxy = null;
            }
        }

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onProxyClosed(String info, Exception e, SdlDisconnectedReason reason) {
        //Stop the service
        stopSelf();

        if(reason == SdlDisconnectedReason.LANGUAGE_CHANGE) {
            if(connectionType.equals("BT")){ // if on Bluetooth
                Looper.prepare();
                SdlReceiver.queryForConnectedService(this);
            }else{ //else, on TCP
                Intent sdlServiceIntent = new Intent(this, SdlService.class);
                startService(sdlServiceIntent);
            }
        }
    }

    @Override
    public void onServiceEnded(OnServiceEnded serviceEnded) {

    }

    @Override
    public void onServiceNACKed(OnServiceNACKed serviceNACKed) {

    }

    @Override
    public void onOnStreamRPC(OnStreamRPC notification) {

    }

    @Override
    public void onStreamRPCResponse(StreamRPCResponse response) {

    }

    @Override
    public void onError(String info, Exception e) {

    }

    @Override
    public void onGenericResponse(GenericResponse response) {

    }

    @Override
    public void onOnCommand(OnCommand notification) {

    }

    @Override
    public void onAddCommandResponse(AddCommandResponse response) {

    }

    @Override
    public void onAddSubMenuResponse(AddSubMenuResponse response) {

    }

    @Override
    public void onCreateInteractionChoiceSetResponse(CreateInteractionChoiceSetResponse response) {

    }

    @Override
    public void onAlertResponse(AlertResponse response) {

    }

    @Override
    public void onDeleteCommandResponse(DeleteCommandResponse response) {

    }

    @Override
    public void onDeleteInteractionChoiceSetResponse(DeleteInteractionChoiceSetResponse response) {

    }

    @Override
    public void onDeleteSubMenuResponse(DeleteSubMenuResponse response) {

    }

    @Override
    public void onPerformInteractionResponse(PerformInteractionResponse response) {

    }

    @Override
    public void onResetGlobalPropertiesResponse(ResetGlobalPropertiesResponse response) {

    }

    @Override
    public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse response) {

    }

    @Override
    public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse response) {

    }

    @Override
    public void onShowResponse(ShowResponse response) {

    }

    @Override
    public void onSpeakResponse(SpeakResponse response) {

    }

    @Override
    public void onOnButtonEvent(OnButtonEvent notification) {

    }

    @Override
    public void onOnButtonPress(OnButtonPress notification) {

    }

    @Override
    public void onSubscribeButtonResponse(SubscribeButtonResponse response) {

    }

    @Override
    public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse response) {

    }

    @Override
    public void onOnPermissionsChange(OnPermissionsChange notification) {

    }

    @Override
    public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse response) {

    }

    @Override
    public void onUnsubscribeVehicleDataResponse(UnsubscribeVehicleDataResponse response) {

    }

    @Override
    public void onGetVehicleDataResponse(GetVehicleDataResponse response) {

    }

    @Override
    public void onOnVehicleData(OnVehicleData notification) {

    }

    @Override
    public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse response) {

    }

    @Override
    public void onEndAudioPassThruResponse(EndAudioPassThruResponse response) {

    }

    @Override
    public void onOnAudioPassThru(OnAudioPassThru notification) {

    }

    @Override
    public void onPutFileResponse(PutFileResponse response) {

    }

    @Override
    public void onDeleteFileResponse(DeleteFileResponse response) {

    }

    @Override
    public void onListFilesResponse(ListFilesResponse response) {

    }

    @Override
    public void onSetAppIconResponse(SetAppIconResponse response) {
        if(response.getSuccess() && response.getCorrelationID() == 2){
            Log.d("DEBUG", "App Icon successfully set.");
        }
    }

    @Override
    public void onScrollableMessageResponse(ScrollableMessageResponse response) {

    }

    @Override
    public void onChangeRegistrationResponse(ChangeRegistrationResponse response) {

    }

    @Override
    public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse response) {

    }

    @Override
    public void onOnLanguageChange(OnLanguageChange notification) {

    }

    @Override
    public void onOnHashChange(OnHashChange notification) {

    }

    @Override
    public void onSliderResponse(SliderResponse response) {

    }

    @Override
    public void onOnDriverDistraction(OnDriverDistraction notification) {

    }

    @Override
    public void onOnTBTClientState(OnTBTClientState notification) {

    }

    @Override
    public void onOnSystemRequest(OnSystemRequest notification) {

    }

    @Override
    public void onSystemRequestResponse(SystemRequestResponse response) {

    }

    @Override
    public void onOnKeyboardInput(OnKeyboardInput notification) {

    }

    @Override
    public void onOnTouchEvent(OnTouchEvent notification) {

    }

    @Override
    public void onDiagnosticMessageResponse(DiagnosticMessageResponse response) {

    }

    @Override
    public void onReadDIDResponse(ReadDIDResponse response) {

    }

    @Override
    public void onGetDTCsResponse(GetDTCsResponse response) {

    }

    @Override
    public void onOnLockScreenNotification(OnLockScreenStatus notification) {
    }

    @Override
    public void onDialNumberResponse(DialNumberResponse response) {

    }

    @Override
    public void onSendLocationResponse(SendLocationResponse response) {

    }

    @Override
    public void onShowConstantTbtResponse(ShowConstantTbtResponse response) {

    }

    @Override
    public void onAlertManeuverResponse(AlertManeuverResponse response) {

    }

    @Override
    public void onUpdateTurnListResponse(UpdateTurnListResponse response) {

    }

    @Override
    public void onServiceDataACK(int dataSize) {

    }

    @Override
    public void onGetWayPointsResponse(GetWayPointsResponse response) {

    }

    @Override
    public void onSubscribeWayPointsResponse(SubscribeWayPointsResponse response) {

    }

    @Override
    public void onUnsubscribeWayPointsResponse(UnsubscribeWayPointsResponse response) {

    }

    @Override
    public void onOnWayPointChange(OnWayPointChange notification) {

    }

    @Override
    public void onOnHMIStatus(OnHMIStatus notification) {

        switch(notification.getHmiLevel()) {
            case HMI_FULL:
                //send welcome message, addcommands, subscribe to buttons ect
                break;
            case HMI_LIMITED:
                break;
            case HMI_BACKGROUND:
                break;
            case HMI_NONE:
                if(first_HMI_NONE){
                    first_HMI_NONE = false;
                    connectionEstablished = true;

                    //Toast.makeText(getApplicationContext(), "Connected to Core.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();
                    intent.setAction(CONNECTION_NOTIFICATION_ACTION);
                    intent.putExtra("connectionEstablished", connectionEstablished.toString());
                    sendBroadcast(intent);
                }
                break;
            default:
                return;
        }
    }
    //...
}
