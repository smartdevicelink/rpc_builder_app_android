# rpc_builder_app_android
The functionality of the app to date:  
__UI__ - Static: settings screen, Dynamic: lists all RPC requests, lists all parameters for an RPC request, lists struct parameters  
__Proxy creation__ - Currently, it creates a proxy using TCP connection and user-input IP address & port (will be updated to add BT option). It creates the proxy using the user input for the initial RegisterAppInterface request.   
__Sending RPC Request__ - Evaluates user input for any RPC request, packages data and sends request via proxy

To use the app:  
1. Run SDL Core in VM, launch the SDL HMI html page  
2. Run the app using an emulator in Android Studio (a Nexus 5 or newer device should suffice)  
3. At the settings screen, you should only have to set the IP Address to the address of your VM  
4. Click the __>__ symbol in the upper right  
5. You can now fill out the options for the initial RAI. Be sure to set the App Name and AppID  
6. Hit the __SEND__ button in the upper right  
7. You should see in the SDL HMI that your app is recognized by core  
8. In the app, you can now select any request, fill out its params, and hit __SEND__ to send it to core  
