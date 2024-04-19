#include "Toast.h"

Toast::Toast(jobject context, JNIEnv* env){
    this->ctx = context;
    this->env = env;
}
void Toast::toast(jstring msg){
    jclass Toast = env->FindClass( "android/widget/Toast");
    jmethodID methodID=env->GetStaticMethodID(Toast, "makeText","(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;");
    jobject ToastObj = env->CallStaticObjectMethod(Toast,methodID, ctx, msg,0 );
    jmethodID id = env->GetMethodID(Toast, "show", "()V" );
    env->CallVoidMethod( ToastObj, id);
}