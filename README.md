# Tiktok

## 主要改动
- 合并了几个activity 配置暂时没什么问题(除了录视频的activity)
- 主线程更新data的ui可能早于请求网络的线程执行完毕完毕，导致第一次getdata不会更新data  解决方法：延迟更新UI，加上等待动画优化用户体验
- 网络错误/相机获取错误可能导致崩溃 解决方法：catch后不执行后续操作
- 增加“我的”按钮 拉取自己发布的视频
- Menu设置背景进入BackgroundActivity

## 待改进
- CustomCameraActivity正常使用
- videoActivity进度条有点问题
- UploadActivity里的备注(extra——value)的作用？
- 所有的UI
- 第一次登陆让用户起昵称 用SharedPreference保存 之后发布视频用此昵称
- 录制->上传 首页->上传的不同模式

