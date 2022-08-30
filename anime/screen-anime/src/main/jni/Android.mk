LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := app-keys
LOCAL_SRC_FILES := app-keys.c
include $(BUILD_SHARED_LIBRARY)