# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncommenxt the following
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

# Fix for Retrofit issue https://github.com/square/retrofit/issues/3751
# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
# ---------------------------------------------------------------------------------------------
# E-Catalog. Gson reads and writes classes by their field names (API responses, and the lists Room
# stores as JSON through the type converters), and most fields have no @SerializedName: keep the
# model classes and their field names so R8 neither renames nor strips them.
-keep class com.rick.ecatalogs.**.network.model.** { *; }
-keep class com.rick.ecatalogs.**.model.** { *; }
-keep class com.rick.ecatalogs.data.translation.Translation* { *; }

# Gson's TypeToken subclasses (e.g. object : TypeToken<List<Author>>() {}) need their generic
# signature at run time; R8 full mode strips it otherwise.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
-keepattributes InnerClasses,EnclosingMethod

# Retrofit service interfaces are only reached through a proxy.
-keep,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# WorkManager stores worker class names (DelegatingWorker looks them up by name), and those stay
# queued across app updates: keep the names stable.
-keepnames class * extends androidx.work.ListenableWorker

# Navigation arguments and saved state find their classes by name.
-keepnames class * implements android.os.Parcelable
-keepnames class * implements java.io.Serializable

# DataStore's proto (protobuf lite) reads its fields reflectively.
-keep class * extends com.google.protobuf.GeneratedMessageLite { <fields>; }
