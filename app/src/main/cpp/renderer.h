/**
* Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
* EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
* and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
*/

#ifndef __EASYAR_SAMPLE_UTILITY_SIMPLERENDERER_H__
#define __EASYAR_SAMPLE_UTILITY_SIMPLERENDERER_H__

#include <android/asset_manager.h>
#include "easyar/matrix.hpp"
#include <GLES2/gl2.h>
#include <string>

namespace EasyAR {
    namespace samples {

        class Renderer {
        public:
            void init();

            void render(const Matrix44F &projectionMatrix, const Matrix44F &cameraview, Vec2F size,
                        const char *word);

        private:
            unsigned int program_box;
            int pos_vertex;
            int pos_texcoord;
            int pos_trans_box;
            int pos_proj_box;
            unsigned int vbo_vertex;
            unsigned int vbo_texcoord;
            unsigned int texture;
            //用来记录之前识别到的单词，目的是为了防止render时不必要的每次都重新加载一次贴图
            std::string orin_word;
            bool flag;//这个值用来判断需不需要重新绑定
        };

        class VideoRenderer {
        public:
            void init();

            void render(const Matrix44F &projectionMatrix, const Matrix44F &cameraview, Vec2F size);

            unsigned int texId();

        private:
            unsigned int program_box;
            int pos_coord_box;
            int pos_tex_box;
            int pos_trans_box;
            int pos_proj_box;
            unsigned int vbo_coord_box;
            unsigned int vbo_tex_box;
            unsigned int vbo_faces_box;
            unsigned int texture_id;
        };
    }
}
#endif
