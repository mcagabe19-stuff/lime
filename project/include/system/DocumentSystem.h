#ifndef LIME_SYSTEM_DOCUMENTSYSTEM_H
#define LIME_SYSTEM_DOCUMENTSYSTEM_H

#include <system/CFFI.h>
#include <system/JNI.h>
#include <utils/QuickVec.h>
#include <utils/Bytes.h>
#include <jni.h>
#include <pthread.h>
#include <android/log.h>
#include <string>
#include <iostream>

namespace lime
{

    class DocumentSystem
    {

    public:
        DocumentSystem(const char *treeUri);
        ~DocumentSystem();

        const QuickVec<unsigned char> readBytes(const char *path);
        void writeBytes(const char *path, Bytes *data);

    private:
        jobject javaObject;
    };

}

#endif