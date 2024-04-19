LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
APP_PLATFORM := android-26
LOCAL_MODULE := libnative-lib
LOCAL_SRC_FILES := native-lib.cpp Toast.cpp
LOCAL_LIBRARIES := -landroid -llog
LOCAL_CPPFLAGS := -Wall -std=c++11
include $(BUILD_SHARED_LIBRARY)


