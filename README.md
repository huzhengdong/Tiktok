# Tiktok

## 主要改动
- MainActivity有三个Fragment 布局完全相同，分别为
   - HomeFragment 展示所有人的视频信息
   - FriendFragment 展示用户关注的人的视频信息
   - MineFragment 展示用户自己发布的视频信息   
- 其他三个重新改为Activity 暂时设置为单例模式
- 在根据时间戳生成id后，使用SecurityRandom在末尾加上三位随机数，减小id重复的概率
- MineActivity增加了关注功能，输入用户id即可添加关注，使用SharedPreference保存


## 待改进
- 点某些item时进度条离奇消失（同样的代码，虚拟机有手机上没有）
- 所有的UI
- activity的模式

