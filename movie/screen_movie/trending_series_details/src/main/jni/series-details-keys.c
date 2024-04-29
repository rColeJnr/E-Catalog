#include <jni.h>


JNIEXPORT jstring JNICALL
Java_com_rick_movie_screen_1movie_trending_1series_1details_TrendingSeriesDetailsViewModelKt_getTMDBKey(
        JNIEnv *env, jclass clazz) {
    return (*env)->NewStringUTF(env, "9b25e27e722ef7a92d3e6bd98977bc09");
}

JNIEXPORT jstring JNICALL
Java_com_rick_movie_screen_1movie_trending_1series_1search_TrendingSeriesSearchViewModelKt_getTmdbKey(
        JNIEnv *env, jclass clazz) {
    return (*env)->NewStringUTF(env, "9b25e27e722ef7a92d3e6bd98977bc09");
}