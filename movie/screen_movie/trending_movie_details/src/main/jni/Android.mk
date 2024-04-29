LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := movie-details-keys
LOCAL_SRC_FILES := movie-details-keys.c
include $(BUILD_SHARED_LIBRARY)