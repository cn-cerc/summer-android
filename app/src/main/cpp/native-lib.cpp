#include <jni.h>
#include <string>

extern "C"
jstring
Java_com_fmk_fff_efitness_Utils_C_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
