package is.chapu.soundcloudcachemodifier;

import java.io.File;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by sebastien on 22/07/16.
 */
public class CacheHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        if (!loadPackageParam.packageName.equals("com.soundcloud.android")) {
            return ;
        }

        XposedBridge.log("Application " + loadPackageParam.packageName + " loaded");

        XposedHelpers.findAndHookMethod("com.soundcloud.android.playback.StreamCacheConfig", loadPackageParam.classLoader, "getStreamCacheSize", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                long current = (long)param.getResult();
                XposedBridge.log("Current cache: " + current);
                param.setResult(524288000);
            }
        });

        XposedHelpers.findAndHookMethod("com.soundcloud.android.playback.StreamCacheConfig", loadPackageParam.classLoader, "getRemainingCacheSpace", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                long remain = (long)param.getResult();
                XposedBridge.log("Cache remaining: " + remain);
            }
        });

        XposedHelpers.findAndHookMethod("com.soundcloud.android.playback.StreamCacheConfig", loadPackageParam.classLoader, "getStreamCacheDirectory", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                File file = (File)param.getResult();
                XposedBridge.log("Cache directory: " + file.getAbsolutePath());
            }
        });
    }
}
