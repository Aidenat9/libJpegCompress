# libJpegCompress

###简介说明

Android系统中的skia引擎是阉割的skia版本，对jpeg的处理基于libjpeg开源库，对png的处理则是基于libpng。 早期的Android系统由于cpu吃紧。将libjpeg中的最优哈夫曼编码关闭了。直到7.0才打开。

所以我们在7.0之前压缩图片可以采用自行编译libjpeg来生产so文件使用最优化夫曼编码。
