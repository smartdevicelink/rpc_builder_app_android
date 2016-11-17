package com.smartdevicelink.rpcbuilder.SmartDeviceLink;

/**
 * Created by austinkirk on 11/17/16.
 */

public class SDLConfiguration {
    final String SDLConnectionTypeStringTCP = "TCP";
    final String SDLConnectionTypeStringBT = "BT";
    final String SDLAppTypeStringMedia = "Media";
    final String SDLAppTypeStringNonMedia = "Non-Media";
    final String SDLAppTypeStringNavigation = "Navigation";

    private enum SDLConnectionType{
        SDLConnectionTypeTCP,
        SDLConnectionTypeBT
    };
    private enum SDLAppType{
        SDLAppTypeMedia,
        SDLAppTypeNonMedia,
        SDLAppTypeNavigation,
        SDLAppTypeUnknown
    }

    private SDLConnectionType connectionType;
    private String connectionTypeString;
    private String ipAddress;
    private String port;

    public SDLConfiguration(){
        connectionType = SDLConnectionType.SDLConnectionTypeTCP;
        ipAddress = null;
        port = null;
    }

    public SDLConfiguration(String ip_given, String port_given){
        connectionType = SDLConnectionType.SDLConnectionTypeTCP;
        ipAddress = ip_given;
        port = port_given;
    }

}
