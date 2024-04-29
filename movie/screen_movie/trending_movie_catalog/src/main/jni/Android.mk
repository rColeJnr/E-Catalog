LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := movie-keys
LOCAL_SRC_FILES := movie-keys.c
include $(BUILD_SHARED_LIBRARY)