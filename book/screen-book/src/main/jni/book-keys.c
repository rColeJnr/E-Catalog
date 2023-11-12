#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_rick_screen_1book_bestseller_1screen_BestsellerViewModelKt_getNYKey(JNIEnv *env, jclass clazz) {
    return (*env)-> NewStringUTF(env, "r2hGllYW5TVGnqspbO8u3li1Un4AlQgQ");
}