/**
* Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
* EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
* and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
*/

#include "soil/SOIL.h"
#include "renderer.h"
// include generated arrays
#include "model/banana.h"
#include "model/cat.h"
#include "model/dog.h"
#include "model/frog.h"
#include "model/lion.h"
#include "model/spider.h"
#if defined __APPLE__
#include <OpenGLES/ES3/gl.h>
#endif

#ifdef ANDROID

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
            //初始化设置为空
            orin_word = "";
            //初始化设置为true,表示第一次需要绑定
            flag = true;
        }

        void Renderer::render(const Matrix44F &projectionMatrix, const Matrix44F &cameraview,
                              Vec2F size, const char *word) {
            //模型贴图长宽
            int width, height;
            unsigned char *image;

            glBindBuffer(GL_ARRAY_BUFFER, vbo_vertex);
            //记住，判断的条件是原始的word跟现在识别到的不是同一个，只有满足这个条件，才重新加载贴图，否则不加载
            if (strcmp(word, "frog") == 0 && orin_word != "frog") {
                glBufferData(GL_ARRAY_BUFFER, sizeof(frogVerts), frogVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(frogTexCoords), frogTexCoords, GL_DYNAMIC_DRAW);
                //确定对应模型的贴图
                image = SOIL_load_image("/storage/emulated/0/Download/models/frog.jpg", &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                //记得更新下orin_word值
                orin_word = "frog";
                //一旦更新了，表示就需要重新绑定了
                flag = true;
            } else if (strcmp(word, "spider") == 0 && orin_word != "spider") {
                glBufferData(GL_ARRAY_BUFFER, sizeof(spiderVerts), spiderVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(spiderTexCoords), spiderTexCoords,
                             GL_DYNAMIC_DRAW);
                //确定对应模型的贴图
                image = SOIL_load_image("/storage/emulated/0/Download/models/spider.jpg", &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                orin_word = "spider";
                flag = true;
            } else if (strcmp(word, "banana") == 0 && orin_word != "banana") {
                glBufferData(GL_ARRAY_BUFFER, sizeof(bananaVerts), bananaVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(bananaTexCoords), bananaTexCoords,
                             GL_DYNAMIC_DRAW);
                //确定对应模型的贴图
                image = SOIL_load_image("/storage/emulated/0/Download/models/banana.jpg", &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                orin_word = "banana";
                flag = true;
            } else if (strcmp(word, "cat") == 0 && orin_word != "cat") {
                glBufferData(GL_ARRAY_BUFFER, sizeof(catVerts), catVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(catTexCoords), catTexCoords,
                             GL_DYNAMIC_DRAW);
                //确定对应模型的贴图
                image = SOIL_load_image("/storage/emulated/0/Download/models/cat.jpg", &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                orin_word = "cat";
                flag = true;
            } else if (strcmp(word, "dog") == 0 && orin_word != "dog") {
                glBufferData(GL_ARRAY_BUFFER, sizeof(dogVerts), dogVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(dogTexCoords), dogTexCoords,
                             GL_DYNAMIC_DRAW);
                //确定对应模型的贴图
                image = SOIL_load_image("/storage/emulated/0/Download/models/dog.jpg", &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                orin_word = "dog";
                flag = true;
            } else if (strcmp(word, "lion") == 0 && orin_word != "lion") {
                glBufferData(GL_ARRAY_BUFFER, sizeof(lionVerts), lionVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(lionTexCoords), lionTexCoords,
                             GL_DYNAMIC_DRAW);
                //确定对应模型的贴图
                image = SOIL_load_image("/storage/emulated/0/Download/models/lion.jpg", &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                orin_word = "lion";
                flag = true;
            }

            //重新绑定贴图
            if (flag) {
                glBindTexture(GL_TEXTURE_2D, texture);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE,
                             image);
                glGenerateMipmap(GL_TEXTURE_2D);
                SOIL_free_image_data(image);
                //记得置false,防止多次绑定
                flag = false;
            }

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
            }else if (strcmp(word, "spider") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, spiderNumVerts);
            }else if (strcmp(word, "banana") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, bananaNumVerts);
            }else if (strcmp(word, "cat") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, catNumVerts);
            }else if (strcmp(word, "dog") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, dogNumVerts);
            }else if (strcmp(word, "lion") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, lionNumVerts);
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
