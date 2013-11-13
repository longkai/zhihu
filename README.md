知乎阅读
========

## 介绍
这个app是一个阅读类型的应用，使用了www.zhihu.com/read的数据，可以看到，内容基本上是一样的= =

使用时需要联网，每次用户主动更新都会缓存数据，用户可以离线查看

受支持的版本为2.2以上

本项目在github上的主页：https://github.com/longkai/zhihu

## 使用技术
1. android tartget-api = 19
2. google volley 异步网络连接
3. android support-v7 appcompat 支持2.2以上的actionbar功能
4. ...

## 开发环境
1. android studio 0.3.4
2. jdk 1.7_10
3. android sdk 19 kitkat
4. gradle 1.8 (非android自带的wrapper，本地安装的）

## 依赖 （均为当时最新版本）
1. android sdk 19
2. google volley
3. android support-v4 19.0
4. android support-v7 appcompat 19.0
5. yuejia（自己的一个类库）

## 文件结构
```
settings.xml        所有包含的子项目配置在这里
build.gradle        根项目的构建文件，空的
gradlew*            命令行构建脚本，直接在命令行上构建项目

volley/             google volley 类库，直接采用源代码来构建，未包含，需另下载，见下面"如何构建”
    build.gradle    volley项目构建配置

yuejia/             个人的一个类库，基本上没什么内容= =，未包含，需另下载，见下面"如何构建”
    build.gradle    yuejia项目构建配置（依赖support-v4）

zhihu/              最终构建app的项目，包含
    build.gralde    最终项目的构建配置（依赖了以上两个项目，以及support-v4与support-v7 appcompat）
```

**所有的src/main/java表示android源代码**

**所有的src/main/res表示android资源文件**

## 如何构建（首先确定自己达到了开发环境的要求，gradle非必需）
1. 下载volley的源码 http://android.googlesource.com/platform/frameworks/volley
2. 下载yuejia的源吗 https://github.com/longkai/yuejia
3. 推荐使用Android Studio或者Intellij Idea，版本最新最好，然后将根项目import进去就好
4. 使用命令行，根据你的系统运行根目录下的gradlew*脚本即可
5. 使用eclipse，这个如果你能明白eclipse是如何进行多项目构建的，那么看看每个项目下的build.gradle基本上就ok了，实际上eclipse的构建是比较复杂的

**以上所说的构建说明只是简单的介绍，如果有不明白的地方最好多google一下或者联系作者im.longkai@gmail.com**

## License
```
The MIT License (MIT)
Copyright (c) 2013 longkai(龙凯)
The software shall be used for good, not evil.
```