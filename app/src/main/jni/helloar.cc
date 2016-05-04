/**
* Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
* EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
* and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
*/

#include "ar.hpp"
#include "renderer.h"
#include <jni.h>

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
JNIEXPORT jstring JNICALL JNIFUNCTION_NATIVE (nativeGetWord(JNIEnv * env, jobject
                                                      obj));
};

namespace EasyAR {
    namespace samples {

        class HelloAR : public AR {
        public:
            HelloAR();

            //视频相关
            ~HelloAR();

            virtual bool clear();

            virtual void initGL();

            virtual void resizeGL(int width, int height);

            virtual void render();

            //公有，外部可访问
            const char *word;

        private:
            Vec2I view_size;
            Renderer renderer;

            //视频相关
            VideoRenderer *pVideoRenderer[3];
            int tracked_target;
            int active_target;
            int texid[3];
            AR *video;
            VideoRenderer *video_renderer;
        };

        HelloAR::HelloAR() {
            view_size[0] = -1;
            //初始为空
            word = "";

            //视频相关
            tracked_target = 0;
            active_target = 0;
            for (int i = 0; i < 3; ++i) {
                texid[i] = 0;
                pVideoRenderer[i] = new VideoRenderer;
            }
            video = NULL;
            video_renderer = NULL;
        }

        //视频相关
        HelloAR::~HelloAR() {
            for (int i = 0; i < 3; ++i) {
                delete pVideoRenderer[i];
            }
        }

        void HelloAR::initGL() {
            renderer.init();
            augmenter_ = Augmenter();

            //视频相关
            for (int i = 0; i < 3; ++i) {
                pVideoRenderer[i]->init();
                texid[i] = pVideoRenderer[i]->texId();
            }
        }

        void HelloAR::resizeGL(int width, int height) {
            view_size = Vec2I(width, height);
        }

        //视频相关
        bool HelloAR::clear() {
            AR::clear();
            if (video) {
                delete video;
                video = NULL;
                tracked_target = 0;
                active_target = 0;
            }
            return true;
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
                //显示视频的判断
                int id = frame.targets()[0].target().id();
                if (active_target && active_target != id) {
                    video->onLost();
                    delete video;
                    video = NULL;
                    tracked_target = 0;
                    active_target = 0;
                }
                if (!tracked_target) {
                    if (video == NULL) {
                        if (frame.targets()[0].target().name() == std::string("friend") &&
                            texid[0]) {
                            video = new AR;
                            video->openStreamingVideo("http://www.gardenofvisual.com/static/videos/friend.mp4", texid[0]);
                            video_renderer = pVideoRenderer[0];
                        }
                        else if (frame.targets()[0].target().name() == std::string("mouse") &&
                                 texid[1]) {
                            video = new AR;
                            video->openStreamingVideo("http://www.gardenofvisual.com/static/videos/mouse.mp4", texid[1]);
                            video_renderer = pVideoRenderer[1];
                        }
                        else if (frame.targets()[0].target().name() == std::string("dumb") &&
                                 texid[2]) {
                            video = new AR;
                            video->openStreamingVideo(
                                    "http://file.bmob.cn/M03/22/46/oYYBAFcJ1h2AAWuHAL71_sMJ6DI259.mp4",
                                    texid[2]);
                            video_renderer = pVideoRenderer[2];
                        }
                    }
                    if (video) {
                        video->onFound();
                        tracked_target = id;
                        active_target = id;
                    }
                }

                Matrix44F projectionMatrix = getProjectionGL(camera_.cameraCalibration(), 0.2f,
                                                             500.f);
                Matrix44F cameraview = getPoseGL(frame.targets()[0].pose());
                ImageTarget target = frame.targets()[0].target().cast_dynamic<ImageTarget>();

                //记得把单词拿一下
                word = target.name();

                //如果视频检测到了，就不要3d模型渲染了
                if (tracked_target) {
                    video->update();
                    video_renderer->render(projectionMatrix, cameraview, target.size());
                } else {
                    //非视频，3d模型渲染
                    renderer.render(projectionMatrix, cameraview, target.size(), word);
                }
            }
            else {
                //失去识别体时记得把word置空
                word = "";
                if (tracked_target) {
                    video->onLost();
                    tracked_target = 0;
                }
            }
        }
    }
}
EasyAR::samples::HelloAR ar;

JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(nativeInit(JNIEnv * env, jobject)) {
    bool status = ar.initCamera();
    ar.loadAllFromJsonFile("animals.json");
    ar.loadAllFromJsonFile("fruits.json");
    ar.loadAllFromJsonFile("vegetables.json");

    ar.loadAllFromJsonFile("targets.json");
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

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRender(JNIEnv *env , jobject assetManager)) {
    ar.render();
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRotationChange(JNIEnv * , jobject, jboolean
                                                  portrait)) {
    ar.setPortrait(portrait);
}

JNIEXPORT jstring JNICALL JNIFUNCTION_NATIVE (nativeGetWord(JNIEnv * env, jobject)) {
    //返回word，供java层调用
    return env->NewStringUTF(ar.word);
}

