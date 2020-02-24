package com.rnunity;

import android.view.View;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import javax.annotation.Nullable;


public class RNUnity extends ReactContextBaseJavaModule  {
    private static ReactApplicationContext reactContext;

    public RNUnity(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNUnity";
    }

    private void sendEvent(ReactContext reactContext,  String eventName, @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @ReactMethod
    public void initializeSDK(String gameID) {
        final UnityAdsListener myAdsListener = new UnityAdsListener ();
        UnityAds.initialize (getCurrentActivity(), gameID, myAdsListener, true);
    }

    @ReactMethod
    public void isInitialized(final Callback callback) {
        callback.invoke(UnityAds.isInitialized());
    }

    @ReactMethod
    public void getPlacementState(String placementID, final Callback callback) {
        if (placementID.equals(null) || placementID == "") {
            callback.invoke(UnityAds.getPlacementState().toString());
        } else {
            callback.invoke(UnityAds.getPlacementState(placementID).toString());
        }
    }

    @ReactMethod
    public void show(String placementID) {
        if (placementID.equals(null) || placementID == "")   {
            if (UnityAds.isReady()) {
                UnityAds.show(getCurrentActivity());
            }
        } else {
            if (UnityAds.isReady(placementID)) {
                UnityAds.show(getCurrentActivity(), placementID);
            }
        }
    }


    // Implement the IUnityAdsListener interface methods:
    private class UnityAdsListener implements IUnityAdsListener {

        @Override
        public void onUnityAdsReady (String placementId) {
            WritableMap params = Arguments.createMap();
            params.putString("placementId", placementId);
            sendEvent(reactContext,"onReady", params);
        }

        @Override
        public void onUnityAdsStart (String placementId) {
            WritableMap params = Arguments.createMap();
            params.putString("placementId", placementId);
            sendEvent(reactContext,"onStart", params);
        }

        @Override
        public void onUnityAdsFinish (String placementId, UnityAds.FinishState finishState) {
            WritableMap params = Arguments.createMap();
            params.putString("placementId", placementId);
            params.putString("finishState", String.valueOf(finishState));
            sendEvent(reactContext,"onFinish", params);
        }

        @Override
        public void onUnityAdsError (UnityAds.UnityAdsError error, String message) {
            WritableMap params = Arguments.createMap();
            params.putString("placementId", String.valueOf(error));
            params.putString("finishState", String.valueOf(message));
            sendEvent(reactContext,"onError", params);
        }
    }

}
