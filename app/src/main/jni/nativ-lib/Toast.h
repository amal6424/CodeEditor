#ifndef TOAST_H
#define TOAST_H

#include <jni.h>
class Toast{
    private:
        jobject ctx;
        JNIEnv* env;
    public:
        Toast(jobject context, JNIEnv* env);
        void toast(jstring msg);
};
#endif