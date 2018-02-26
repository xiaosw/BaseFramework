# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

# link: http://blog.csdn.net/wzzfeitian/article/details/40963457/
# 预定义变量
# PROJECT_SOURCE_DIR 工程的根目录
# PROJECT_BINARY_DIR 运行cmake命令的目录,通常是${PROJECT_SOURCE_DIR}/build
# CMAKE_INCLUDE_PATH 环境变量,非cmake变量
# CMAKE_LIBRARY_PATH 环境变量
# CMAKE_CURRENT_SOURCE_DIR 当前处理的CMakeLists.txt所在的路径
# CMAKE_CURRENT_BINARY_DIR target编译目录
# 使用ADD_SURDIRECTORY(src bin)可以更改此变量的值
# SET(EXECUTABLE_OUTPUT_PATH <新路径>)并不会对此变量有影响,只是改变了最终目标文件的存储路径
# CMAKE_CURRENT_LIST_FILE 输出调用这个变量的CMakeLists.txt的完整路径
# CMAKE_CURRENT_LIST_LINE 输出这个变量所在的行
# # CMAKE_MODULE_PATH 定义自己的cmake模块所在的路径
# SET(CMAKE_MODULE_PATH ${PROJECT_SOURCE_DIR}/cmake),然后可以用INCLUDE命令来调用自己的模块
# EXECUTABLE_OUTPUT_PATH 重新定义目标二进制可执行文件的存放位置
# LIBRARY_OUTPUT_PATH 重新定义目标链接库文件的存放位置
# PROJECT_NAME 返回通过PROJECT指令定义的项目名称
# CMAKE_ALLOW_LOOSE_LOOP_CONSTRUCTS 用来控制IF ELSE语句的书写方式

# 系统信息
# CMAKE_MAJOR_VERSION cmake主版本号,如2.8.6中的2
# CMAKE_MINOR_VERSION cmake次版本号,如2.8.6中的8
# CMAKE_PATCH_VERSION cmake补丁等级,如2.8.6中的6
# CMAKE_SYSTEM 系统名称,例如Linux-2.6.22
# CAMKE_SYSTEM_NAME 不包含版本的系统名,如Linux
# CMAKE_SYSTEM_VERSION 系统版本,如2.6.22
# CMAKE_SYSTEM_PROCESSOR 处理器名称,如i686
# UNIX 在所有的类UNIX平台为TRUE,包括OS X和cygwin
# WIN32 在所有的win32平台为TRUE,包括cygwin

# 开关选项
# BUILD_SHARED_LIBS 控制默认的库编译方式。如果未进行设置,使用ADD_LIBRARY时又没有指定库类型,默认编译生成的库都是静态库 （可在t3中稍加修改进行验证）
# CMAKE_C_FLAGS 设置C编译选项
# CMAKE_CXX_FLAGS 设置C++编译选项

# custom add
set(so-name xiaosw)
#设置生成的so动态库最后输出的路径
# message(----------------------- ${PROJECT_SOURCE_DIR})
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/libs/${ANDROID_ABI})

#设置头文件搜索路径（和此txt同个路径的头文件无需设置），可选
#INCLUDE_DIRECTORIES(${CMAKE_CURRENT_SOURCE_DIR}/common)

#指定用到的系统库或者NDK库或者第三方库的搜索路径，可选。
#LINK_DIRECTORIES(/usr/local/lib)


file(GLOB native_srcs src/main/cpp/*.cpp
                      src/main/cpp/*.c
                      ../core/src/main/cpp/*.cpp
                      ../core/src/main/cpp/*.c)
add_library( # Sets the name of the library.
             ${so-name}

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             ${native_srcs})

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       ${so-name}

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )