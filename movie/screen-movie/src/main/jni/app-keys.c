#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_rick_screen_1movie_MovieCatalogViewModelKt_getKey(JNIEnv *env, jobject instance) {
    return (*env)->  NewStringUTF(env, "r2hGllYW5TVGnqspbO8u3li1Un4AlQgQ");
}