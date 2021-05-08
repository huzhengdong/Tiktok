# 大作业-上传

## 说明

主题为 第5课-网络部分作业内容

主要针对上传 uploadActivity 部分进行了修改

## 方法回顾

参考第5课课件和作业

BaseUrl + 参数变化：

1.  修改 IApi 文件内容，其用于 Retrofit 实例返回
   1. 注意 body 的 类型是 file
2. 修改 UploadActivity 中的相关内容

## 分析

相对来说 UploadActivity 中的内容比较难修改，因为这里面的部分内容是来自用户上传的，所以首先需要改变前端界面，获取一下参数：

student_id：可以来自默认值

user_name：应该在此设定，或者是直接交给用户进行编辑

extra_value：默认是无，这一项也就是备注了

cover_image：请用户选择一张图片（这个要参考一下之前的方法是怎样选择图片的），需要检测图片的大小

video：请用户选择一个视频，需要检测大小（参考上面选择图片的方法，选择一个视频）

### 第5课作业

提交页面：

facebook-drawee view

- 展示封面图片
- id/sd_cover

button 1

- 选择封面按钮
- id/btn_cover

editText 1

- 输入 对方姓名
- id/et_to

editText 2

- 对对方说的话
- id/et_content

button 2

- 提交按钮
- id/btn_submit

针对 editText：使用 `findViewById(R.id.xxx)` 的方式获得文本内容

- toEditText
- contentEditText

针对 coverSD：同样使用 `findViewById(R.id.xxx)`  方法获取coverSD，但使用setImageURI方法对其进行赋值

image 不能像其他元素一样简单地传入 multipartBody 的内容中

### Intent 中的重要方法

intent 本身是跳转方法：

- 显式跳转
- 隐式跳转：不明确指出想要启动哪一个活动，而是指定一系列更为抽象的 action 和 category 等信息来过滤，找到符合条件的 Activity

翻看选择图片的部分，找到了相关语句：

`Intent.ACTION_GET_CONTENT`

- 允许用户选择特殊种类的数据，并返回（比如：照一张照片，或者录一段音）

紧跟着 `intent.setType(type)`

- 指定数据类型，选出合适的应用

`intent.putExtra()` 

- 把要传递的数据暂存在 Intent 中，启动了另一个活动后，只需要把这些数据再从 Intent 中取出就可以了

`Intent.EXTRA_ALLOW_MULTIPLE`

- 仅允许一次选择

`Intent.EXTRA_TITLE`

注意到一个问题，这里使用了 facebook 的 SimpleDraweeView 来进行图片的展示，同样，获取图片时，也用到了其中的 `setImageUri` 的方法，但是如果是视频就不能使用这个方法了吧【仍然可以】

### OnActivityResult

如果有多个 Activity，现在需要从 一个界面跳转到另一个界面执行操作，操作执行完毕后返回时，会返回一些数据

可以利用 `OnActivityResult` 回调函数实现数据交流

用 `RequestCode` 来判断是来自哪一个 Activity

> 示例：
>
> [onActivityResult()的用法_我不是油饼的博客-CSDN博客_onactivityresult](https://blog.csdn.net/weixin_41008021/article/details/90346700)
>
> [Android Activity中onActivityResult的使用笔记_Brainbg的博客-CSDN博客_onactivityresult](https://blog.csdn.net/u014720022/article/details/93324292)【这篇文章将的更详细一点】

从第二篇文章的学习后了解到：

- `onActivityResult` 和 `startActivityForResult` 是一一对应的
- 在本代码中，`startAcivityForResult` 是在 `getFile` 函数当中，将 `requestCode` 继续传下去，从而在 `onActivityResult` 中实现数据的判断

`Activity.Result_OK`等是系统预设好的几种标识符，所以不能算 Context

> [Activity.RESULT_OK_战略观察者的博客-CSDN博客](https://blog.csdn.net/baohanqing/article/details/19243103)

## 视频压缩

目前找到的资料做一个总结：

1. ffmpeg - 基本认定效果不好

   > [Android本地视频压缩方案_weixin_34132768的博客-CSDN博客](https://blog.csdn.net/weixin_34132768/article/details/92024004)

2. SiliCompressor - 应该是当前效果比较好的一个选择

   > 参考：
   >
   > [使用开源库SiliCompressor对视频进行压缩处理_抬头仰望放肆的微笑v的博客-CSDN博客](https://blog.csdn.net/qq_31207845/article/details/84105204)
   >
   > [Android视频压缩并且上传 - 黑帅-quan - 博客园 (cnblogs.com)](https://www.cnblogs.com/wzqnxd/p/10038881.html)【本文是参考关键】

   1. 但是再在这个过程中涉及两个问题：
      1. 该方法变换时要选择的是 filepath，而不是 videoUri（所以需要 Uri 转 filepath）
      2. 最后压缩后也是 filepath，而不是 Uri（所以需要 filepath 转 Uri）

3. 其他

   1. [视频压缩_卡布里多的博客-CSDN博客](https://blog.csdn.net/qq_35198779/article/details/115181055?utm_medium=distribute.pc_relevant.none-task-blog-baidujs_title-1&spm=1001.2101.3001.4242)
   2. [fishwjy/VideoCompressor: A High-performance video compressor for Android using Hardware decoding and encoding API(MediaCodec). (github.com)](https://github.com/fishwjy/VideoCompressor)

### Uri 与 Path

系统性总结类型文档：

> [Android视频播放，选择，压缩，上传 - 简书 (jianshu.com)](https://www.jianshu.com/p/78b7176c041e)

本文确实是非常全面，在第2部分-显示视频第一帧部分，讲到了一点内容：

可以使用`  Uri videoUri = FileProvider.getUriForFile(_mActivity, AppUtils.getAppPackageName() + ".fileprovider", new File(videoPath));` 实现 视频 Uri 的获取

关于其中的 `FileProvider.getUriForFile()`函数，可以参考

> [关于 Android 7.0 适配中 FileProvider 部分的总结_滴水穿石，点石为金-CSDN博客](https://blog.csdn.net/growing_tree/article/details/71190741)

此外，还看到一篇文章：

> [android获取video uri的方法_阿Gogo-CSDN博客](https://blog.csdn.net/wl724120268/article/details/78582343)
>
> 也是关于从 filepath 获取 Uri 的方法

最后一篇文章，很厉害：

> [Android中的Uri详解_yy的博客-CSDN博客_android uri](https://blog.csdn.net/sinat_37205087/article/details/102815247)
>
> 涵盖内容有：
>
> - uri、file、path 之间的互相转换
>
> 可以看出，从 uri 到 path 是最复杂的一步，其他都不是太难

### 主推 SiliCompressor

要输入三个量：

1. context，目前使用了 uploadActivity.this

2. filepath，用[Android视频压缩并且上传 - 黑帅-quan - 博客园 (cnblogs.com)](https://www.cnblogs.com/wzqnxd/p/10038881.html)所说的方法，将 Uri 转为 filePath

3. 输出文件的路径，这个目前是最大问题，因为需要找一个确实存在的路径，上文说如果不知道写什么，就写`Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()`，初看确实会有点懵，仔细查找发现是找到系统提供的公共目录的路径

   ```java
   //获取系统提供的公共目录的路径，例如：DIRECTORY_MUSIC , DIRECTORY_PICTURES，这些目录都是以DIRECTORY开头的
   File directory_pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
   ```

   参考：

   > [Android开发：Environment类（外部存储状态及路径获取）的接口详解_FDGFGFDGFD的博客-CSDN博客](https://blog.csdn.net/FDGFGFDGFD/article/details/80434520)

但是出现问题：

```bash
E/AndroidRuntime: FATAL EXCEPTION: Thread-4
    Process: com.bytedance.sjtu2021, PID: 4586
    java.lang.IllegalArgumentException
        at android.media.MediaMetadataRetriever.setDataSource(MediaMetadataRetriever.java:70)
        at com.iceteck.silicompressorr.videocompression.MediaController.convertVideo(MediaController.java:272)
        at com.iceteck.silicompressorr.SiliCompressor.compressVideo(SiliCompressor.java:333)
        at com.iceteck.silicompressorr.SiliCompressor.compressVideo(SiliCompressor.java:316)
        at com.bytedance.practice5.UploadActivity$4.run(UploadActivity.java:143)
```

但是并未获得有效的解决方法，因为这个问题 `java.lany.IllegalArgumentException` 是比较通用的问题，而不是仅在这种情况下会出现的问题

> 相关问题检索：
>
> 有一个类似的问题，出现在文件路径过程中：[android file path 问题_weixin_34348174的博客-CSDN博客](https://blog.csdn.net/weixin_34348174/article/details/92714621)
>
> - 这里描述出错原因是 file_path 中出现了路径分隔符，可以从这个角度对代码进行考虑

### Context 问题

到底在 `onActivityResult` 中应该使用什么作为上下文 Context 的参数呢？

1. 使用 `UploadActivity.this` 吗？

   1. 从结果来看，这样不行，最终返回的 path 是 null
   2. 查询一番后认为，context 应该是没有问题的，就是它

   但是有一个问题，之前没有太在意：

   ```bash
   E/CursorWindow: Failed to read row 0, column -1 from a CursorWindow which has 1 rows, 6 columns.
   ```

   似乎是关于数据库方面的问题，有文章指出是数据太大了

2. 什么是 `getContentResolver()`

3. 什么是 `getContext()`

> [Android开发：getContentResolver的使用_AFull的博客-CSDN博客_getcontentresolver](https://blog.csdn.net/daniel80110_1020/article/details/55260510)
>
> [Android获取Context（任意位置任意地方，全局上下文）_周健彬的博客-CSDN博客_activity 获取上下文](https://blog.csdn.net/zjb369542423/article/details/50719046)

### class 问题

在 2.2.4 版本中，新加入了转换方法，可以从 Uri 直接转换，不需要再转换为 path 了，这是利好

不过新问题是 `java.lang.NoClassDefFoundError: Failed resolution of: Lcom/googlecode/mp4parser/util/Matrix;`

简单说就是找不到这个类，那么要明确：

1. 这是什么类
2. 为什么会出现在这里

解决方法：

> 参考文章
>
> [java.lang.NoClassDefFoundError: Failed resolution of: Lcom/googlecode/mp4parser/util/Matrix-开源项目-CSDN问答](https://ask.csdn.net/questions/1477916)
>
> 只需要添加
>
> You need to add `implementation 'com.googlecode.mp4parser:isoparser:1.1.22'`
>
> 就可以了

压缩成功

### 替换 Uri

把新的文件传给最后的上传位置

新问题：`W/System.err: java.io.FileNotFoundException: No content provider: /storage/emulated/0/Android/data/com.bytedance.sjtu2021/files/Movies/20210504_150559.mp4`

但是，实际上该文件是存在的，所以是发生了什么问题？

是 Uri 不对

比如手机原始存储文件的Uri是：

`content://com.android.providers.media.documents/document/video%3A450942`

但是，仅仅凭借 `Uri.parse(path)` 转换的 Uri 为：

`/storage/emulated/0/Android/data/com.bytedance.sjtu2021/files/Movies/20210504_151809.mp4`

其实这个内容就是 path，完全没有改变，只不过数据类型从 String 变为了 Uri

如果想要调用 `readDataFromUri(Uri)`，那么必须要转成下面的样子：

`file:///storage/emulated/0/Android/data/com.bytedance.sjtu2021/files/Movies/20210504_151809.mp4`， 这样的uri就可以**上传成功！**

仔细比较，就是增加了一点点内容：`file://`

> 此处，我找到这个可以成功上传的Uri，是通过如下方法探索的：
>
> 1. path 转 file（new File 类型的数据）
> 2. file 转 Uri（这样的 Uri 是肯定可以上传的，就是上面的样子）

不过可能存在一点问题，就是：

- 浏览器无法播放
- PotPlayer 可以播放（下载后播放），但是提示 音频解码器错误，应该使用 AAC 解码器

**可以看到数据确实小了很多：从9M+压缩到了645K**

![image-20210504154249082](https://typoraim.oss-cn-shanghai.aliyuncs.com/image/image-20210504154249082.png)



### 等待时间

当文件比较大时，压缩会需要一点时间

这个时候路径就会是空的，那么读到的数据也是空的

怎么样保证压缩完毕，然后再读取路径呢？

> 参考1：[安卓开发中如何实现主线程等待子线程执行_曹同学的博客-CSDN博客_android 主线程等待子线程](https://blog.csdn.net/crh170/article/details/90060811)

这种方法直接使用一个 callback 的方法修改 flag，以达到目的

直接说结论：不行。

会导致问题：`E/tmessages: Surface frame wait timed out`，前面板会无响应而关闭

> 参考2：[主线程如何等待子线程的结束？（非阻塞等待）-CSDN论坛](https://bbs.csdn.net/topics/310243308)

给出了两种方法

（1）主线程WaitForSingleObject()子线程

（2）子线程退出时，给主线程发送PostThreadMessage消息

能够做到子线程完成后发送消息，接下来需要根据消息进行合适的程序调用：

（1）比如在没有压缩完成前，提交按钮是灰色的

（2）可以添加一点动画，在提交完成前，转圈圈表示正在压缩

#### 目前的方法

1. 添加了一个 lottie 动画
2. 添加了 Handler，用来接收子线程信息，并修改主线程UI
3. 当子线程在进行压缩时，主线程中：
   1. lottie 动画展示出来 【使用 FramView 的覆盖特性 + `view.bringToFront()`方法修改控件的层次】 
   2. 背景为灰色，略带透明 【`v.getBackground().setAlpha(100);//0~255透明度值`】
   3. “提交”按钮禁用【`btn_v.setEnabled(false);`】
4. 当压缩完毕，lottie动画消失，提交按钮释放

## 其他

`ContextCompat.getExternalFilesDirs`

将绝对路径返回到应用程序，可以防止其拥有的永久文件的所有外部存储设备上的应用程序特定目录的绝对路径

## 功能整理

1. **分离压缩功能，可选择压缩或者不压缩**
   1. 如果没有压缩，直接使用 Uri，而不是使用传递过来的 path 路径
   2. 如果压缩，在压缩过程中涉及 path 路径的转换
2. **压缩部分写成函数：** compress 函数，可以直接进行调用
3. **增加按钮动效，避免误操作：**
   1. 点击压缩按钮时，会临时禁用提交、压缩按钮，压缩按钮文字改为“正在压缩”，视频部分显示压缩动画，压缩完毕之后更改为“压缩完毕”，保持禁用
   2. 点击提交按钮时，会临时禁用提交按钮，按钮文字显示为正在提交
   3. 提交完成后会直接进行跳转回首页，同时整个页面内容清空，所以不必再设计



