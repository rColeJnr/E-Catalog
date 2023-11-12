LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := book-keys
LOCAL_SRC_FILES := book-keys.c
include $(BUILD_SHARED_LIBRARY)