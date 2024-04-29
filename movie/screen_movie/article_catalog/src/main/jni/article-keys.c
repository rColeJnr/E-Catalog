#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_rick_movie_screen_1movie_article_1catalog_ArticleViewModelKt_getNYKey(JNIEnv *env,
                                                                               jclass clazz) {
    return (*env)->NewStringUTF(env, "r2hGllYW5TVGnqspbO8u3li1Un4AlQgQ");
}

JNIEXPORT jstring JNICALL
Java_com_rick_movie_screen_1movie_article_1search_ArticleSearchViewModelKt_getNyKey(JNIEnv *env,
                                                                                    jclass clazz) {
    return (*env)->NewStringUTF(env, "r2hGllYW5TVGnqspbO8u3li1Un4AlQgQ");
}