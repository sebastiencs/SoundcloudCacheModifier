package is.chapu.soundcloudcachemodifier;

import java.io.File;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by sebastien on 22/07/16.
 */
public class CacheHook implements IXposedHookLoadPackage {

    private int cacheSize = 500 * 1024 * 1024;
    private static XSharedPreferences pref = null;
    private static boolean prefLoaded = false;

    public void loadCacheSize() {
        if (this.pref == null) {
            this.pref = new XSharedPreferences("is.chapu.soundcloudcachemodifier", "pref_sd");
        }

        if (!this.prefLoaded && pref.getFile().exists()) {
            this.pref.makeWorldReadable();
            this.pref.reload();
            this.prefLoaded = true;
        }

        if (this.prefLoaded && this.pref.hasFileChanged()) {
            pref.reload();
        }

        this.cacheSize = pref.getInt("size_cache", 500);
        this.cacheSize *= 1024 * 1024;
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        if (!loadPackageParam.packageName.equals("com.soundcloud.android")) {
            return ;
        }

        XposedBridge.log("Application " + loadPackageParam.packageName + " loaded");

        XposedHelpers.findAndHookConstructor("com.soundcloud.android.playback.StreamCacheConfig", loadPackageParam.classLoader, "com.soundcloud.android.utils.TelphonyBasedCountryProvider", File.class, "com.soundcloud.android.utils.IOUtils", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                File dir = (File) param.args[1];
                XposedBridge.log("Directory cache from Constructor: " + dir.getAbsolutePath());
            }
        });

        XposedHelpers.findAndHookMethod("com.soundcloud.android.playback.StreamCacheConfig", loadPackageParam.classLoader, "getStreamCacheSize", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                long current = (long) param.getResult();
                XposedBridge.log("Current Cache: " + current);
                loadCacheSize();
                XposedBridge.log("New Cache: " + cacheSize);
                param.setResult(cacheSize);
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
