#include <stdio.h>
#include <jni.h>

#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

using namespace std;
using namespace cv;

extern "C"
{
    jboolean Java_com_fenchtose_asyncamera_AsynCamPreview_convertGray(
        JNIEnv* env, jobject thiz, jint width, jint height,
        jbyteArray NV21FrameData, jintArray outPixels)
    {
        jbyte * pNV21FrameData = env->GetByteArrayElements(NV21FrameData, 0);
        jint * poutPixels = env->GetIntArrayElements(outPixels, 0);
        
        Mat mNV(height, width, CV_8UC1, (unsigned char*)pNV21FrameData);
        Mat mBgra(height, width, CV_8UC4, (unsigned char*) poutPixels);

        cvtColor(mNV, mBgra, CV_GRAY2RGBA);

        env->ReleaseByteArrayElements(NV21FrameData, pNV21FrameData, 0);
        env->ReleaseIntArrayElements(outPixels, poutPixels, 0);

        return true;
    }
}