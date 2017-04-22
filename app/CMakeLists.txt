# CMake版本信息
cmake_minimum_required(VERSION 3.4.1)

# 支持-std=gnu++11
set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

# 工程路径
set(pathToProject /Users/left/workspace/ShotHappy)

# 配置加载native依赖
include_directories(${pathToProject}/app/src/main/cpp/include)

# 添加待编译的文件
add_library(ARNative SHARED ${pathToProject}/app/src/main/cpp/model/banana.h
                            ${pathToProject}/app/src/main/cpp/model/bat.h
                            ${pathToProject}/app/src/main/cpp/model/bulldog.h
                            ${pathToProject}/app/src/main/cpp/model/carrot.h
                            ${pathToProject}/app/src/main/cpp/model/cat.h
                            ${pathToProject}/app/src/main/cpp/model/deer.h
                            ${pathToProject}/app/src/main/cpp/model/doberman.h
                            ${pathToProject}/app/src/main/cpp/model/dove.h
                            ${pathToProject}/app/src/main/cpp/model/frog.h
                            ${pathToProject}/app/src/main/cpp/model/grapefruit.h
                            ${pathToProject}/app/src/main/cpp/model/leopard.h
                            ${pathToProject}/app/src/main/cpp/model/lion.h
                            ${pathToProject}/app/src/main/cpp/model/mandarin.h
                            ${pathToProject}/app/src/main/cpp/model/mango.h
                            ${pathToProject}/app/src/main/cpp/model/ostrich.h
                            ${pathToProject}/app/src/main/cpp/model/parakeet.h
                            ${pathToProject}/app/src/main/cpp/model/peach.h
                            ${pathToProject}/app/src/main/cpp/model/penguin.h
                            ${pathToProject}/app/src/main/cpp/model/pig.h
                            ${pathToProject}/app/src/main/cpp/model/raspberry.h
                            ${pathToProject}/app/src/main/cpp/model/redradish.h
                            ${pathToProject}/app/src/main/cpp/model/shark.h
                            ${pathToProject}/app/src/main/cpp/model/spider.h
                            ${pathToProject}/app/src/main/cpp/model/strawberry.h
                            ${pathToProject}/app/src/main/cpp/model/tiger.h
                            ${pathToProject}/app/src/main/cpp/model/wolf.h
                            ${pathToProject}/app/src/main/cpp/soil/image_DXT.h
                            ${pathToProject}/app/src/main/cpp/soil/image_DXT.c
                            ${pathToProject}/app/src/main/cpp/soil/image_helper.h
                            ${pathToProject}/app/src/main/cpp/soil/image_helper.c
                            ${pathToProject}/app/src/main/cpp/soil/stb_image_aug.h
                            ${pathToProject}/app/src/main/cpp/soil/stb_image_aug.c
                            ${pathToProject}/app/src/main/cpp/soil/SOIL.h
                            ${pathToProject}/app/src/main/cpp/soil/SOIL.c
                            ${pathToProject}/app/src/main/cpp/ar.hpp
                            ${pathToProject}/app/src/main/cpp/renderer.h
                            ${pathToProject}/app/src/main/cpp/ar.cc
                            ${pathToProject}/app/src/main/cpp/renderer.cpp
                            ${pathToProject}/app/src/main/cpp/helloar.cc)

# 动态方式加载
add_library(lib_easyar SHARED IMPORTED)
# 引入so文件
set_target_properties(lib_easyar PROPERTIES IMPORTED_LOCATION ${pathToProject}/app/src/main/jniLibs/${ANDROID_ABI}/libEasyAR.so)

target_link_libraries(ARNative
                        lib_easyar
                        GLESv2
                        log)