/**
* Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
* EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
* and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
*/

#include "ar.hpp"
#include "renderer.hpp"
#include <jni.h>
#include <GLES2/gl2.h>

#define JNIFUNCTION_NATIVE(sig) Java_com_left_shothappy_views_ARFragment_##sig

extern "C" {
JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(nativeInit(JNIEnv * env, jobject
                                                      object));
JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeDestory(JNIEnv * env, jobject
                                                  object));
JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeInitGL(JNIEnv * env, jobject
                                                  object));
JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeResizeGL(JNIEnv * env, jobject
                                                  object, jint
                                                  w, jint
                                                  h));
JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRender(JNIEnv * env, jobject
                                                  obj));
JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRotationChange(JNIEnv * env, jobject
                                                  obj, jboolean
                                                  portrait));
};

namespace EasyAR {
    namespace samples {

        class HelloAR : public AR {
        public:
            HelloAR();

            virtual void initGL();

            virtual void resizeGL(int width, int height);

            //参数用来选择模型
            virtual void render();

        private:
            Vec2I view_size;
            Renderer renderer;
        };

        HelloAR::HelloAR() {
            view_size[0] = -1;
        }

        void HelloAR::initGL() {
            renderer.init();
            augmenter_ = Augmenter();
        }

        void HelloAR::resizeGL(int width, int height) {
            view_size = Vec2I(width, height);
        }

        void HelloAR::render() {
            glClearColor(0.f, 0.f, 0.f, 1.f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Frame frame = augmenter_.newFrame(tracker_);
            if (view_size[0] > 0) {
                AR::resizeGL(view_size[0], view_size[1]);
                if (camera_ && camera_.isOpened())
                    view_size[0] = -1;
            }
            augmenter_.drawVideoBackground();

            AugmentedTarget::Status status = frame.targets()[0].status();
            if (status == AugmentedTarget::kTargetStatusTracked) {
                Matrix44F projectionMatrix = getProjectionGL(camera_.cameraCalibration(), 0.2f,
                                                             500.f);
                Matrix44F cameraview = getPoseGL(frame.targets()[0].pose());
                ImageTarget target = frame.targets()[0].target().cast_dynamic<ImageTarget>();

                //用来匹配识别到的目标与需要展示的模型
//        if(strcmp(target.name(),char* p2)){}


                renderer.render(projectionMatrix, cameraview, target.size());
            }
        }

    }
}
EasyAR::samples::HelloAR ar;

JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(nativeInit(JNIEnv * , jobject)) {
    bool status = ar.initCamera();
    ar.loadAllFromJsonFile("animals.json");
    ar.loadAllFromJsonFile("fruits.json");
    ar.loadAllFromJsonFile("vegetables.json");
    status &= ar.start();
    return status;
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeDestory(JNIEnv * , jobject)) {
    ar.clear();
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeInitGL(JNIEnv * , jobject)) {
    ar.initGL();
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeResizeGL(JNIEnv * , jobject, jint
                                                  w, jint
                                                  h)) {
    ar.resizeGL(w, h);
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRender(JNIEnv * , jobject)) {
    ar.render();
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRotationChange(JNIEnv * , jobject, jboolean
                                                  portrait)) {
    ar.setPortrait(portrait);
}
