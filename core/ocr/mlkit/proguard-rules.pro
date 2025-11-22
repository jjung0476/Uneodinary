# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-keep class demo.ocr.camera.utils.CameraUtils {
    public static boolean isLowSpecDevice(android.content.Context);
    public boolean isLowSpecDevice(android.content.Context);
}

-keep class demo.ocr.camera.interfaces.** {*; }
-keep class demo.ocr.camera.camera.CropRectangleView { *; }
-keep class demo.ocr.camera.utils.ImageProcessingGate {*;}

-dontwarn java.lang.invoke.StringConcatFactory

-assumenosideeffects class com.ocr.common.utils.LogUtilsKt {
    public static void debug(...);
    public static void verbose(...);
    public static void info(...);
}