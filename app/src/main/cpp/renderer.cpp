/**
* Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
* EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
* and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
*/

#include "soil/SOIL.h"
#include "renderer.h"
// include generated arrays
#include "model/banana.h"
#include "model/bat.h"
#include "model/bulldog.h"
#include "model/cat.h"
#include "model/deer.h"
#include "model/doberman.h"
#include "model/dove.h"
#include "model/frog.h"
#include "model/lion.h"
#include "model/pig.h"
#include "model/shark.h"
#include "model/spider.h"
#include "model/tiger.h"
#include "model/wolf.h"
#include "model/carrot.h"
#include "model/leopard.h"
#include "model/mandarin.h"
#include "model/mango.h"
#include "model/ostrich.h"
#include "model/parakeet.h"
#include "model/peach.h"
#include "model/penguin.h"
#include "model/raspberry.h"
#include "model/strawberry.h"
#include "model/grapefruit.h"
#include "model/redradish.h"

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

std::string model_texture_path = "/sdcard/Android/data/com.left.shothappy/files/textures/";

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
                //记得更新下orin_word值
                orin_word = "frog";
                glBufferData(GL_ARRAY_BUFFER, sizeof(frogVerts), frogVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(frogTexCoords), frogTexCoords, GL_DYNAMIC_DRAW);
                //确定对应模型的贴图
                image = SOIL_load_image((model_texture_path + "frog.jpg").c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                //一旦更新了，表示就需要重新绑定了
                flag = true;
            } else if (strcmp(word, "spider") == 0 && orin_word != "spider") {
                orin_word = "spider";
                glBufferData(GL_ARRAY_BUFFER, sizeof(spiderVerts), spiderVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(spiderTexCoords), spiderTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("spider.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            } else if (strcmp(word, "banana") == 0 && orin_word != "banana") {
                orin_word = "banana";
                glBufferData(GL_ARRAY_BUFFER, sizeof(bananaVerts), bananaVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(bananaTexCoords), bananaTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("banana.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            } else if (strcmp(word, "cat") == 0 && orin_word != "cat") {
                orin_word = "cat";
                glBufferData(GL_ARRAY_BUFFER, sizeof(catVerts), catVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(catTexCoords), catTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("cat.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            } else if (strcmp(word, "lion") == 0 && orin_word != "lion") {
                orin_word = "lion";
                glBufferData(GL_ARRAY_BUFFER, sizeof(lionVerts), lionVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(lionTexCoords), lionTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("lion.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            } else if (strcmp(word, "bat") == 0 && orin_word != "bat") {
                orin_word = "bat";
                glBufferData(GL_ARRAY_BUFFER, sizeof(batVerts), batVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(batTexCoords), batTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("bat.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            } else if (strcmp(word, "pig") == 0 && orin_word != "pig") {
                orin_word = "pig";
                glBufferData(GL_ARRAY_BUFFER, sizeof(pigVerts), pigVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(pigTexCoords), pigTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("pig.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            } else if (strcmp(word, "bulldog") == 0 && orin_word != "bulldog") {
                orin_word = "bulldog";
                glBufferData(GL_ARRAY_BUFFER, sizeof(bulldogVerts), bulldogVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(bulldogTexCoords), bulldogTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("bulldog.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            } else if (strcmp(word, "deer") == 0 && orin_word != "deer") {
                orin_word = "deer";
                glBufferData(GL_ARRAY_BUFFER, sizeof(deerVerts), deerVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(deerTexCoords), deerTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("deer.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            } else if (strcmp(word, "doberman") == 0 && orin_word != "doberman") {
                orin_word = "doberman";
                glBufferData(GL_ARRAY_BUFFER, sizeof(dobermanVerts), dobermanVerts,
                             GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(dobermanTexCoords), dobermanTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("doberman.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            } else if (strcmp(word, "dove") == 0 && orin_word != "dove") {
                orin_word = "dove";
                glBufferData(GL_ARRAY_BUFFER, sizeof(doveVerts), doveVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(doveTexCoords), doveTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("dove.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            } else if (strcmp(word, "shark") == 0 && orin_word != "shark") {
                orin_word = "shark";
                glBufferData(GL_ARRAY_BUFFER, sizeof(sharkVerts), sharkVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(sharkTexCoords), sharkTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("shark.bmp")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            } else if (strcmp(word, "tiger") == 0 && orin_word != "tiger") {
                orin_word = "tiger";
                glBufferData(GL_ARRAY_BUFFER, sizeof(tigerVerts), tigerVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(tigerTexCoords), tigerTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("tiger.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            } else if (strcmp(word, "wolf") == 0 && orin_word != "wolf") {
                orin_word = "wolf";
                glBufferData(GL_ARRAY_BUFFER, sizeof(wolfVerts), wolfVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(wolfTexCoords), wolfTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("wolf.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            }else if (strcmp(word, "carrot") == 0 && orin_word != "carrot") {
                orin_word = "carrot";
                glBufferData(GL_ARRAY_BUFFER, sizeof(carrotVerts), carrotVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(carrotTexCoords), carrotTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("carrot.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            }else if (strcmp(word, "leopard") == 0 && orin_word != "leopard") {
                orin_word = "leopard";
                glBufferData(GL_ARRAY_BUFFER, sizeof(leopardVerts), leopardVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(leopardTexCoords), leopardTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("leopard.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            }else if (strcmp(word, "mandarin") == 0 && orin_word != "mandarin") {
                orin_word = "mandarin";
                glBufferData(GL_ARRAY_BUFFER, sizeof(mandarinVerts), mandarinVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(mandarinTexCoords), mandarinTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("mandarin.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            }else if (strcmp(word, "mango") == 0 && orin_word != "mango") {
                orin_word = "mango";
                glBufferData(GL_ARRAY_BUFFER, sizeof(mangoVerts), mangoVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(mangoTexCoords), mangoTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("mango.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            }else if (strcmp(word, "ostrich") == 0 && orin_word != "ostrich") {
                orin_word = "ostrich";
                glBufferData(GL_ARRAY_BUFFER, sizeof(ostrichVerts), ostrichVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(ostrichTexCoords), ostrichTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("ostrich.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            }else if (strcmp(word, "parakeet") == 0 && orin_word != "parakeet") {
                orin_word = "parakeet";
                glBufferData(GL_ARRAY_BUFFER, sizeof(parakeetVerts), parakeetVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(parakeetTexCoords), parakeetTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("parakeet.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            }else if (strcmp(word, "peach") == 0 && orin_word != "peach") {
                orin_word = "peach";
                glBufferData(GL_ARRAY_BUFFER, sizeof(peachVerts), peachVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(peachTexCoords), peachTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("peach.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            }else if (strcmp(word, "penguin") == 0 && orin_word != "penguin") {
                orin_word = "penguin";
                glBufferData(GL_ARRAY_BUFFER, sizeof(penguinVerts), penguinVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(penguinTexCoords), penguinTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("penguin.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            }else if (strcmp(word, "raspberry") == 0 && orin_word != "raspberry") {
                orin_word = "raspberry";
                glBufferData(GL_ARRAY_BUFFER, sizeof(raspberryVerts), raspberryVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(raspberryTexCoords), raspberryTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("raspberry.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            }else if (strcmp(word, "strawberry") == 0 && orin_word != "strawberry") {
                orin_word = "strawberry";
                glBufferData(GL_ARRAY_BUFFER, sizeof(strawberryVerts), strawberryVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(strawberryTexCoords), strawberryTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("strawberry.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            }else if (strcmp(word, "grapefruit") == 0 && orin_word != "grapefruit") {
                orin_word = "grapefruit";
                glBufferData(GL_ARRAY_BUFFER, sizeof(grapefruitVerts), grapefruitVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(grapefruitTexCoords), grapefruitTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("grapefruit.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
                flag = true;
            }else if (strcmp(word, "red radish") == 0 && orin_word != "red radish") {
                orin_word = "red radish";
                glBufferData(GL_ARRAY_BUFFER, sizeof(redradishVerts), redradishVerts, GL_DYNAMIC_DRAW);
                glBindBuffer(GL_ARRAY_BUFFER, vbo_texcoord);
                glBufferData(GL_ARRAY_BUFFER, sizeof(redradishTexCoords), redradishTexCoords,
                             GL_DYNAMIC_DRAW);
                image = SOIL_load_image((model_texture_path + ("redradish.jpg")).c_str(), &width,
                                        &height, 0, SOIL_LOAD_RGBA);
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
            }else if (strcmp(word, "lion") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, lionNumVerts);
            } else if (strcmp(word, "bat") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, batNumVerts);
            } else if (strcmp(word, "pig") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, pigNumVerts);
            } else if (strcmp(word, "bulldog") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, bulldogNumVerts);
            } else if (strcmp(word, "deer") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, deerNumVerts);
            } else if (strcmp(word, "doberman") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, dobermanNumVerts);
            } else if (strcmp(word, "dove") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, doveNumVerts);
            } else if (strcmp(word, "shark") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, sharkNumVerts);
            } else if (strcmp(word, "tiger") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, tigerNumVerts);
            } else if (strcmp(word, "wolf") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, wolfNumVerts);
            } else if (strcmp(word, "carrot") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, carrotNumVerts);
            }else if (strcmp(word, "leopard") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, leopardNumVerts);
            }else if (strcmp(word, "mandarin") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, mandarinNumVerts);
            }else if (strcmp(word, "mango") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, mangoNumVerts);
            }else if (strcmp(word, "ostrich") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, ostrichNumVerts);
            }else if (strcmp(word, "parakeet") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, parakeetNumVerts);
            }else if (strcmp(word, "peach") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, peachNumVerts);
            }else if (strcmp(word, "penguin") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, penguinNumVerts);
            }else if (strcmp(word, "raspberry") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, raspberryNumVerts);
            }else if (strcmp(word, "strawberry") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, strawberryNumVerts);
            }else if (strcmp(word, "grapefruit") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, grapefruitNumVerts);
            }else if (strcmp(word, "red radish") == 0) {
                glDrawArrays(GL_TRIANGLES, 0, redradishNumVerts);
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
