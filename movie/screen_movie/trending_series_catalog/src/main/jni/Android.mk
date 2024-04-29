LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := series-keys
LOCAL_SRC_FILES := series-keys.c
include $(BUILD_SHARED_LIBRARY)