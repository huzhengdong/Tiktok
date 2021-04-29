# 大作业-上传

## 说明

主体为 第5课-网络部分作业内容

主要针对上传 uploadActivity 部分进行了修改

## 方法回顾

参考第5课课件和作业

BaseUrl + 参数变化：

1.  修改 IApi 文件内容，其用于 Retrofit 实例返回
    1. 注意 body 的 类型是 file
2.  修改 UploadActivity 中的相关内容

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

### OnActivityResult

如果有多个 Activity，现在需要从 一个界面跳转到另一个界面执行操作，操作执行完毕后返回时，会返回一些数据

可以利用 `OnActivityResult` 回调函数实现数据交流

用 `RequestCode` 来判断是来自哪一个 Activity

> 示例：
>
> [onActivityResult()的用法_我不是油饼的博客-CSDN博客_onactivityresult](https://blog.csdn.net/weixin_41008021/article/details/90346700)

注意到一个问题，这里使用了 facebook 的 SimpleDraweeView 来进行图片的展示，同样，获取图片时，也用到了其中的 `setImageUri` 的方法，但是如果是视频就不能使用这个方法了吧

# 其他

`ContextCompat.getExternalFilesDirs`

将绝对路径返回到应用程序，可以防止其拥有的永久文件的所有外部存储设备上的应用程序特定目录的绝对路径

