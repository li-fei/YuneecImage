/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_yuneec_image_dll_YuneecGuide */

#ifndef _Included_com_yuneec_image_dll_YuneecGuide
#define _Included_com_yuneec_image_dll_YuneecGuide
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_yuneec_image_dll_YuneecGuide
 * Method:    DLL_ADD
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_yuneec_image_dll_YuneecGuide_DLL_1ADD
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_yuneec_image_dll_YuneecGuide
 * Method:    DLL_SUB
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_yuneec_image_dll_YuneecGuide_DLL_1SUB
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_yuneec_image_dll_YuneecGuide
 * Method:    guideToRGB
 * Signature: ([BII)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_yuneec_image_dll_YuneecGuide_guideToRGB
  (JNIEnv *, jobject, jbyteArray, jint, jint);

/*
 * Class:     com_yuneec_image_dll_YuneecGuide
 * Method:    guideGrayTemper
 * Signature: (S[BIIIISSIS)F
 */
JNIEXPORT jfloat JNICALL Java_com_yuneec_image_dll_YuneecGuide_guideGrayTemper
  (JNIEnv *, jobject, jshort, jbyteArray, jint, jint, jint, jint, jshort, jshort, jint, jshort);

#ifdef __cplusplus
}
#endif
#endif
