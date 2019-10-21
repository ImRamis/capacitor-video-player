package com.jeep.plugin.capacitor.videoplayer;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import java.io.File;

import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.content.Intent;
import android.net.Uri;
import android.content.Context;

@NativePlugin(permissionRequestCode = CapacitorVideoPlayer.RequestCodes.Video)
public class CapacitorVideoPlayer extends Plugin {
    private static final String TAG = "CapacitorVideoPlayer";
    private  Context context;

    @PluginMethod()
    public void play(PluginCall call) {
        context = getContext();

        String url = call.getString("url");
        if(url == null) {
            call.reject("Must provide an url");
            return;
        }
        Log.v(TAG,"display url: "+url);
        String http = url.substring(0,4);
        if (http != "http") {
            url = "android.resource://" + context.getPackageName() + "/" + url; // works
            //            url = "content://"+appPath+ "/" + url ;
            Log.v(TAG,"calculated url: "+url);
        }
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
        intent.putExtra("videoUri",uri);
        startActivityForResult(call, intent, RequestCodes.Video);
        saveCall(call);
    }

    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);
        PluginCall savedCall = getSavedCall();

        if (savedCall == null) {
            return;
        }
        JSObject ret = new JSObject();
        if (requestCode == RequestCodes.Video) {
            ret.put("result", data.getBooleanExtra("result", false));
            savedCall.resolve(ret);
            return;
        }
        savedCall.reject("Plugin error");
    }

    public interface RequestCodes {
        int Video = 10001;
    }
}
