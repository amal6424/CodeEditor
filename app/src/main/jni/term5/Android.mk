LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
APP_PLATFORM := android-26
LOCAL_MODULE := libjackpal-androidterm5
LOCAL_SRC_FILES := fileCompat.cpp termExec.cpp common.cpp
LOCAL_LDLIBS := -llog
LOCAL_CPPFLAGS := -Wall -std=c++11
include $(BUILD_SHARED_LIBRARY)
