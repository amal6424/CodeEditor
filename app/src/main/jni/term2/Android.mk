LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
APP_PLATFORM := android-26
LOCAL_MODULE := libjackpal-termexec2
LOCAL_SRC_FILES := process.cpp
LOCAL_LDLIBS := -llog
LOCAL_CPPFLAGS := -Wall -std=c++11
include $(BUILD_SHARED_LIBRARY)