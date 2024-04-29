#include <jni.h>


JNIEXPORT jstring JNICALL
Java_com_rick_movie_screen_1movie_trending_1movie_1detatils_TrendingMovieDetailsViewModelKt_getTMDBKey(
        JNIEnv *env, jclass clazz) {
    return (*env)->NewStringUTF(env, "9b25e27e722ef7a92d3e6bd98977bc09");
}

JNIEXPORT jstring JNICALL
Java_com_rick_movie_screen_1movie_trending_1movie_1search_TrendingMovieSearchViewModelKt_getTmdbKey(
        JNIEnv *env, jclass clazz) {
    return (*env)->NewStringUTF(env, "9b25e27e722ef7a92d3e6bd98977bc09");
}