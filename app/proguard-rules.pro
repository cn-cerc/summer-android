# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
#        and specify the fully qualified class name to the JavaScript interface
#        class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#-keepclassmembers class cn.xx.xx.Activity$AppAndroid {
#  public *;
#}
#-keepattributes *Annotation*
#-keepattributes *JavascriptInterface*
#
#
#-dontoptimize
#-dontpreverify
#-dontwarn cn.jpush.**
#-keep class cn.jpush.** { *; }
#-dontwarn cn.jiguang.**
#-keep class cn.jiguang.** { *; }
#
#-dontwarn com.alibaba.fastjson.**
#-keep class com.alibaba.fastjson.** { *; }
#
#-keepclassmembers class * {
#   public <init> (org.json.JSONObject);
#}
#-keepclassmembers class cn.cerc.summer.android.MyConfig {
#   *;
#}
#-keepclassmembers class cn.cerc.summer.android.Activity.StartActivity {
#   *;
#}
#-keepclassmembers class cn.cerc.summer.android.Activity.MainActivity {
#    *;
# }