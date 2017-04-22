/**
* Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
* EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
* and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
*/

#include "ar.hpp"
#include "renderer.h"
#include <jni.h>
#include <android/log.h>
#define JNIFUNCTION_NATIVE(sig) Java_com_left_shothappy_ARActivity_##sig
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "EasyAR", __VA_ARGS__)
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
JNIEXPORT void JNICALL JNIFUNCTION_NATIVE (nativeUpdateRewards(JNIEnv * env, jclass
                                                   type, jobjectArray
                                                   rewards));
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
            //用来存放长度，c语言太差了，连取长度的函数都没
            int rewards_length;
            //用来存放从java层传过来的当前用的奖励视频数据
            const char *rewards[10];

            //用来判断当前的视频是不是已经在当前用户的奖励列表里
            bool iscontainreward(const char *name) {
                for (int i = 0; i < rewards_length; i++) {
                    if (strcmp(rewards[i], name) == 0) {
                        return true;
                    }
                }
                return false;
            }

        private:
            Vec2I view_size;
            Renderer renderer;

            //视频相关
            VideoRenderer *pVideoRenderer[9];
            int tracked_target;
            int active_target;
            int texid[9];
            AR *video;
            VideoRenderer *video_renderer;
        };

        HelloAR::HelloAR() {
            view_size[0] = -1;
            //初始为空
            word = "";
            rewards_length = 0;
            //视频相关
            tracked_target = 0;
            active_target = 0;
            for (int i = 0; i < 9; ++i) {
                texid[i] = 0;
                pVideoRenderer[i] = new VideoRenderer;
            }
            video = NULL;
            video_renderer = NULL;
        }

        //视频相关
        HelloAR::~HelloAR() {
            for (int i = 0; i < 9; ++i) {
                delete pVideoRenderer[i];
            }
        }

        void HelloAR::initGL() {
            renderer.init();
            augmenter_ = Augmenter();

            //视频相关
            for (int i = 0; i < 9; ++i) {
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
                        if (frame.targets()[0].target().name() == std::string("2017-04-22") &&
                            texid[0]) {
                            video = new AR;
                            if (iscontainreward(frame.targets()[0].target().name()))
                                video->openStreamingVideo(
                                        "http://bmob-cdn-929.b0.upaiyun.com/2017/04/22/96c7a4f44069f75080c3fd4e5691c185.mp4",
                                        texid[0]);
                            video_renderer = pVideoRenderer[0];
                        } else if (
                                frame.targets()[0].target().name() == std::string("2017-04-23") &&
                                texid[1]) {
                            video = new AR;
                            if (iscontainreward(frame.targets()[0].target().name()))
                                video->openStreamingVideo(
                                        "http://bmob-cdn-929.b0.upaiyun.com/2017/04/22/6543d8ff40a01913805695d63e9e4601.mp4",
                                        texid[1]);
                            video_renderer = pVideoRenderer[1];
                        } else if (
                                frame.targets()[0].target().name() == std::string("2017-04-24") &&
                                texid[2]) {
                            video = new AR;
                            if (iscontainreward(frame.targets()[0].target().name()))
                            video->openStreamingVideo(
                                    "http://bmob-cdn-929.b0.upaiyun.com/2017/04/22/9594e31a405ca98080b9e67705328fa5.mp4",
                                    texid[2]);
                            video_renderer = pVideoRenderer[2];
                        } else if (
                                frame.targets()[0].target().name() == std::string("2017-04-25") &&
                                texid[3]) {
                            video = new AR;
                            if (iscontainreward(frame.targets()[0].target().name()))
                            video->openStreamingVideo(
                                    "http://bmob-cdn-929.b0.upaiyun.com/2016/05/07/3d0a15ed40275ebe80edbfec82ca8d20.MP4",
                                    texid[3]);
                            video_renderer = pVideoRenderer[3];
                        } else if (
                                frame.targets()[0].target().name() == std::string("2017-04-26") &&
                                texid[4]) {
                            video = new AR;
                            if (iscontainreward(frame.targets()[0].target().name()))
                            video->openStreamingVideo(
                                    "http://bmob-cdn-929.b0.upaiyun.com/2017/04/22/345664f840b9419e8000ab5723d459ff.mp4",
                                    texid[4]);
                            video_renderer = pVideoRenderer[4];
                        } else if (
                                frame.targets()[0].target().name() == std::string("2017-04-27") &&
                                texid[5]) {
                            video = new AR;
                            if (iscontainreward(frame.targets()[0].target().name()))
                            video->openStreamingVideo(
                                    "http://bmob-cdn-929.b0.upaiyun.com/2017/04/22/5ef896f240230ff18083d022ddec78bf.mp4",
                                    texid[5]);
                            video_renderer = pVideoRenderer[5];
                        } else if (
                                frame.targets()[0].target().name() == std::string("2017-04-28") &&
                                texid[6]) {
                            video = new AR;
                            if (iscontainreward(frame.targets()[0].target().name()))
                            video->openStreamingVideo(
                                    "http://bmob-cdn-929.b0.upaiyun.com/2017/04/22/dbc60ef340a575c980e4ed2feda4574c.mp4",
                                    texid[6]);
                            video_renderer = pVideoRenderer[6];
                        } else if (
                                frame.targets()[0].target().name() == std::string("2017-04-29") &&
                                texid[7]) {
                            video = new AR;
                            if (iscontainreward(frame.targets()[0].target().name()))
                            video->openStreamingVideo(
                                    "http://bmob-cdn-929.b0.upaiyun.com/2017/04/22/111db2c540a03cb9806e4e7703351228.mp4",
                                    texid[7]);
                            video_renderer = pVideoRenderer[7];
                        } else if (
                                frame.targets()[0].target().name() == std::string("2017-04-30") &&
                                texid[8]) {
                            video = new AR;
                            if (iscontainreward(frame.targets()[0].target().name()))
                            video->openStreamingVideo(
                                    "http://bmob-cdn-929.b0.upaiyun.com/2017/04/22/5cc342854091d58480874c4dcffe8bb9.mp4",
                                    texid[8]);
                            video_renderer = pVideoRenderer[8];
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
                    //有才放
                    if (iscontainreward(word))
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

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeUpdateRewards(JNIEnv * env, jclass
                                                  type,
                                                          jobjectArray
                                                  rewards)) {
    //把jobjectArray数组中的值取出来
    int size = env->GetArrayLength(rewards);//得到数组的长度值
    ar.rewards_length = size;
    LOGI("rewards size: %d \n", ar.rewards_length);

    for (int i = 0; i < size; i++) {
        jstring obj = (jstring) env->GetObjectArrayElement(rewards, i);
        const char *chars = env->GetStringUTFChars(obj, NULL);//将jstring类型转换成char类型输出
        ar.rewards[i] = chars;
        LOGI("load rewards: %s \n", ar.rewards[i]);
    }
}