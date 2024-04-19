#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <malloc.h>

#include "Toast.h"
extern "C" {
    
    
    JNIEXPORT void JNICALL Java_peepu_codeeditor_MainActivity_toast(JNIEnv* env, jobject clz, jobject ctx, jstring msg){
    
    Toast toast(ctx,env);
    toast.toast(msg);
}
}