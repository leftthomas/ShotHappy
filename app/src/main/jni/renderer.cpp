/**
* Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
* EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
* and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
*/

#include "soil/SOIL.h"
#include "renderer.h"
#include <string.h>
// include generated arrays
//#include "model/banana.h"
//#include "model/cat.h"
//#include "model/dog.h"
//#include "model/elephant.h"
#include "model/frog.h"
//#include "model/lion.h"
#include "model/spider.h"
#if defined __APPLE__
#include <OpenGLES/ES3/gl.h>
#endif

#ifdef ANDROID

#include <android/log.h>
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "EasyAR", __VA_ARGS__)
#else
#define LOGI(...) printf(__VA_ARGS__)
#endif

const char *box_vert = "uniform mat4 trans;\n"
        "uniform mat4 proj;\n"
        "attribute vec4 vertex;\n"
        "attribute vec2 texcoord;\n"
        "varying vec2 vtexcoord;\n"
        "void main()\n"
        "{\n"
        "    vtexcoord = texcoord;\n"
        "    gl_Position = proj*trans*vertex;\n"
        "}";

const char* box_frag= "varying vec2 vtexcoord;\n"
        "uniform sampler2D texture;"
        "void main()\n"
        "{\n"
        "    gl_FragColor = texture2D(texture, vtexcoord);\n"
        "}";

const char *box_video_vert = "uniform mat4 trans;\n"
        "uniform mat4 proj;\n"
        "attribute vec4 coord;\n"
        "attribute vec2 texcoord;\n"
        "varying vec2 vtexcoord;\n"
        "\n"
        "void main(void)\n"
        "{\n"
        "    vtexcoord = texcoord;\n"
        "    gl_Position = proj*trans*coord;\n"
        "}\n"
        "\n";

const char *box_video_frag = "#ifdef GL_ES\n"
        "precision highp float;\n"
        "#endif\n"
        "varying vec2 vtexcoord;\n"
        "uniform sampler2D texture;\n"
        "\n"
        "void main(void)\n"
        "{\n"
        "    gl_FragColor = texture2D(texture, vtexcoord);\n"
        "}\n"
        "\n";

namespace EasyAR {
    namespace samples {

        void Renderer::init() {
            program_box = glCreateProgram();
            GLuint vertShader = glCreateShader(GL_VERTEX_SHADER);
            glShaderSource(vertShader, 1, &box_vert, 0);
            glCompileShader(vertShader);
            GLuint fragShader = glCreateShader(GL_FRAGMENT_SHADER);
            glShaderSource(fragShader, 1, &box_frag, 0);
            glCompileShader(fragShader);
            glAttachShader(program_box, vertShader);
            glAttachShader(program_box, fragShader);
            glLinkProgram(program_box);
            glUseProgram(program_box);
            pos_vertex = glGetAttribLocation(program_box, "vertex");
            pos_texcoord = glGetAttribLocation(program_box, "texcoord");
            pos_trans_box = glGetUniformLocation(program_box, "trans");
            pos_proj_box = glGetUniformLocation(program_box, "proj");

            glGenBuffers(1, &vbo_vertex);

            // TexCoord attribute
            glGenBuffers(1, &vbo_texcoord);

            // Load and create a texture
            glGenTextures(1, &texture);
            glBindTexture(GL_TEXTURE_2D,
                          texture); // All upcoming GL_TEXTURE_2D operations now have effect on this texture object
            // Set the texture wrapping parameters
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
                            GL_CLAMP_TO_EDGE);    // Set texture wrapping to GL_REPEAT (usually basic wrapping method)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            // Set texture filtering parameters
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            // Load image, create texture and generate mipmaps
            int width, height;
            //确定对应模型的贴图
            char *imageaddress = "/storage/emulated/0/Download/models/frog.jpg";
//            strcat(imageaddress, word);
//            strcat(imageaddress, ".jpg");

//            LOGI("imageaddress: %s\n", imageaddress);

            unsigned char *image = SOIL_load_image(imageaddress, &width, &height, 0,
                                                   SOIL_LOAD_RGBA);
            LOGI("load image: %s\n", SOIL_last_result());

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE,
                         image);
            glGenerateMipmap(GL_TEXTURE_2D);
            SOIL_free_image_data(image);
        }

        void Renderer::render(const Matrix44F &projectionMatrix, const Matrix44F &cameraview,
                              Vec2F size, const char *word) {
            //模型贴图长宽
            int width, height;
            unsigned char *image;

            glBindBuffer(GL_ARRAY_BUFFER, vbo_vertex);
            if (strcmp(word, "frog") == 0) {
                glBufferData(GL_ARRAY_BUFFER, sizeof(frogVerts), frogVerts, GL_STATIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(frogTexCoords), frogTexCoords, GL_STATIC_DRAW);
                //确定对应模型的贴图
                image = SOIL_load_image("/storage/emulated/0/Download/models/frog.jpg", &width,
                                        &height, 0, SOIL_LOAD_RGBA);
            } else if (strcmp(word, "spider") == 0) {
                glBufferData(GL_ARRAY_BUFFER, sizeof(spiderVerts), spiderVerts, GL_STATIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(spiderTexCoords), spiderTexCoords,
                             GL_STATIC_DRAW);
                //确定对应模型的贴图
                image = SOIL_load_image("/storage/emulated/0/Download/models/spider.jpg", &width,
                                        &height, 0, SOIL_LOAD_RGBA);
            }

            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE,
                            image);
            SOIL_free_image_data(image);


            // Render
            glEnable(GL_DEPTH_TEST);
            glUseProgram(program_box);

            glBindBuffer(GL_ARRAY_BUFFER, vbo_vertex);
            glVertexAttribPointer(pos_vertex, 3, GL_FLOAT, GL_FALSE, 0, 0);
            glEnableVertexAttribArray(pos_vertex);

            glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
            glVertexAttribPointer(pos_texcoord, 2, GL_FLOAT, GL_FALSE, 0, 0);
            glEnableVertexAttribArray(pos_texcoord);

            glUniformMatrix4fv(pos_trans_box, 1, 0, cameraview.data);
            glUniformMatrix4fv(pos_proj_box, 1, 0, projectionMatrix.data);

            // Bind Texture
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture);

            // Draw the triangle
            if (strcmp(word, "frog") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, frogNumVerts);
            } else if (strcmp(word, "spider") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, spiderNumVerts);
            }
        }

        void VideoRenderer::init() {
            program_box = glCreateProgram();
            GLuint vertShader = glCreateShader(GL_VERTEX_SHADER);
            glShaderSource(vertShader, 1, &box_video_vert, 0);
            glCompileShader(vertShader);
            GLuint fragShader = glCreateShader(GL_FRAGMENT_SHADER);
            glShaderSource(fragShader, 1, &box_video_frag, 0);
            glCompileShader(fragShader);
            glAttachShader(program_box, vertShader);
            glAttachShader(program_box, fragShader);
            glLinkProgram(program_box);
            glUseProgram(program_box);
            pos_coord_box = glGetAttribLocation(program_box, "coord");
            pos_tex_box = glGetAttribLocation(program_box, "texcoord");
            pos_trans_box = glGetUniformLocation(program_box, "trans");
            pos_proj_box = glGetUniformLocation(program_box, "proj");

            glGenBuffers(1, &vbo_coord_box);
            glBindBuffer(GL_ARRAY_BUFFER, vbo_coord_box);
            const GLfloat cube_vertices[4][3] = {{1.0f / 2,  1.0f / 2,  0.f},
                                                 {1.0f / 2,  -1.0f / 2, 0.f},
                                                 {-1.0f / 2, -1.0f / 2, 0.f},
                                                 {-1.0f / 2, 1.0f / 2,  0.f}};
            glBufferData(GL_ARRAY_BUFFER, sizeof(cube_vertices), cube_vertices, GL_DYNAMIC_DRAW);

            glGenBuffers(1, &vbo_tex_box);
            glBindBuffer(GL_ARRAY_BUFFER, vbo_tex_box);
            const GLubyte cube_vertex_texs[4][2] = {{0, 0},
                                                    {0, 1},
                                                    {1, 1},
                                                    {1, 0}};
            glBufferData(GL_ARRAY_BUFFER, sizeof(cube_vertex_texs), cube_vertex_texs,
                         GL_STATIC_DRAW);

            glGenBuffers(1, &vbo_faces_box);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo_faces_box);
            const GLushort cube_faces[] = {3, 2, 1, 0};
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(cube_faces), cube_faces, GL_STATIC_DRAW);

            glUniform1i(glGetUniformLocation(program_box, "texture"), 0);
            glGenTextures(1, &texture_id);
            glBindTexture(GL_TEXTURE_2D, texture_id);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        }

        void VideoRenderer::render(const Matrix44F &projectionMatrix, const Matrix44F &cameraview,
                                   Vec2F size) {
            glBindBuffer(GL_ARRAY_BUFFER, vbo_coord_box);
            const GLfloat cube_vertices[4][3] = {{size[0] / 2,  size[1] / 2,  0.f},
                                                 {size[0] / 2,  -size[1] / 2, 0.f},
                                                 {-size[0] / 2, -size[1] / 2, 0.f},
                                                 {-size[0] / 2, size[1] / 2,  0.f}};
            glBufferData(GL_ARRAY_BUFFER, sizeof(cube_vertices), cube_vertices, GL_DYNAMIC_DRAW);

            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_DEPTH_TEST);
            glUseProgram(program_box);
            glBindBuffer(GL_ARRAY_BUFFER, vbo_coord_box);
            glEnableVertexAttribArray(pos_coord_box);
            glVertexAttribPointer(pos_coord_box, 3, GL_FLOAT, GL_FALSE, 0, 0);
            glBindBuffer(GL_ARRAY_BUFFER, vbo_tex_box);
            glEnableVertexAttribArray(pos_tex_box);
            glVertexAttribPointer(pos_tex_box, 2, GL_UNSIGNED_BYTE, GL_FALSE, 0, 0);
            glUniformMatrix4fv(pos_trans_box, 1, 0, cameraview.data);
            glUniformMatrix4fv(pos_proj_box, 1, 0, projectionMatrix.data);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo_faces_box);
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture_id);
            glDrawElements(GL_TRIANGLE_FAN, 4, GL_UNSIGNED_SHORT, 0);
            glBindTexture(GL_TEXTURE_2D, 0);
        }

        unsigned int VideoRenderer::texId() {
            return texture_id;
        }

    }
}
