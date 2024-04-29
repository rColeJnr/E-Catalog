LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := article-keys
LOCAL_SRC_FILES := article-keys.c
include $(BUILD_SHARED_LIBRARY)