/**
* Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
* EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
* and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
*/

#include "renderer.hpp"

#if defined __APPLE__
#include <OpenGLES/ES3/gl.h>
#else
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "EasyAR", __VA_ARGS__)
#endif
//顶点着色器源码
const char *box_vert = "#version 330\n"
        "layout (location = 0) in vec3 position;\n"
        "layout (location = 1) in vec3 normal;\n"
        "layout (location = 2) in vec2 texCoords;\n"
        "\n"
        "out vec2 TexCoords;\n"
        "\n"
        "uniform mat4 model;\n"
        "uniform mat4 view;\n"
        "uniform mat4 projection;\n"
        "\n"
        "void main()\n"
        "{\n"
        "    gl_Position = projection * view * model * vec4(position, 1.0f);\n"
        "    TexCoords = texCoords;\n"
        "}";

//像素着色器源码
const char *box_frag = "#version 330\n"
        "\n"
        "in vec2 TexCoords;\n"
        "\n"
        "out vec4 color;\n"
        "\n"
        "uniform sampler2D texture_diffuse1;\n"
        "\n"
        "void main()\n"
        "{    \n"
        "    color = vec4(texture(texture_diffuse1, TexCoords));\n"
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
            // Setup and compile our shaders
            shader.init(box_vert, box_frag);
            // Load models
            ourModel.init("/sdcard/nanosuit/nanosuit.obj");
//            ourModel.init("../../nanosuit/nanosuit.obj");
        }

        void Renderer::render(const Matrix44F &projectionMatrix, const Matrix44F &cameraview,
                              Vec2F size) {

            // Clear the colorbuffer
//            glClearColor(0.05f, 0.05f, 0.05f, 1.0f);
//            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//
//
//            shader.Use();   // <-- Don't forget this one!
//            // Draw the loaded model
//            glm::mat4 model;
//            model = glm::translate(model, glm::vec3(0.0f, -1.75f, 0.0f)); // Translate it down a bit so it's at the center of the scene
//            model = glm::scale(model, glm::vec3(0.2f, 0.2f, 0.2f));	// It's a bit too big for our scene, so scale it down
//            glUniformMatrix4fv(glGetUniformLocation(shader.Program, "model"), 1, GL_FALSE, glm::value_ptr(model));
//            ourModel.Draw(shader);
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
