# HomeWork4 - 网络

## 作业内容

1. 使用 HttpUrlConnection 的方法从远程拉取留言板信息
2. 使用 Retrofit 的方法实现提交留言
3. 使用 Socket 的方法实现简单的 Head 请求

## 主要问题

### Url 拼接

- HttpUrlConnection 方法需要进行 Url 进行拼接

![image-20210328161534463](https://typoraim.oss-cn-shanghai.aliyuncs.com/image/image-20210328161534463.png)

这个时候就能感受出这张图的重要性了！

给出的内容是：

![image-20210328161634842](https://typoraim.oss-cn-shanghai.aliyuncs.com/image/image-20210328161634842.png)

所以严格针对某个学号的拼接方法应该是：

`String.format("https://api-sjtu-camp-2021.bytedance.com/homework/invoke/messages?student_id=%s", "123456789012)"`

但是这样导致的问题就是，不能拉取所有人的留言，只能拉取指定学号对象的留言

如果改成：

`String.format("https://api-sjtu-camp-2021.bytedance.com/homework/invoke/messages", student_id)`

即使不指定学号，也能进行留言的拉取

> 此外，第一题的问题还有
>
> `result = new Gson().fromJson(reader, new TypeToken<MessageListResponse>(){}.getType());`
>
> 该行代码中关于数据类型的问题，也是值得注意的

### Retrofit 的使用

1. MultiPart Body

不要简单使用 Body

![image-20210328162117642](https://typoraim.oss-cn-shanghai.aliyuncs.com/image/image-20210328162117642.png)

而是使用 Part，另外在开头加上 Multipart

![image-20210328162147648](https://typoraim.oss-cn-shanghai.aliyuncs.com/image/image-20210328162147648.png)

2. call 的对象

`Call<UploadResponse> call = service.submitMessage(`

比如这里调用的 service 应该是在本文件中之前创建的 retrofit 实例

`service = retrofit.create(IApi.class);`

3. 整个 call 的过程应该放在一个线程中重复调用，比如下面这样

![image-20210328162434748](https://typoraim.oss-cn-shanghai.aliyuncs.com/image/image-20210328162434748.png)

### Socket 的实现

这里需要注意的点是发送的信息，例如此处，发送的信息是content，发送语句是：`os.write(content.getBytes());`

里面的 content 位置就要根据上文中请求内容进行及时更改了！

![image-20210328162627082](https://typoraim.oss-cn-shanghai.aliyuncs.com/image/image-20210328162627082.png)

这样，作业的三个任务就完成了！