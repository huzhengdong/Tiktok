# Tiktok

## 主要改动
- MainActivity有三个Fragment 布局完全相同，均加入刷新功能，分别为
   - HomeFragment 展示所有人的视频信息
   - FriendFragment 展示用户关注的人的视频信息
   - MineFragment 展示用户自己发布的视频信息   
- 其他三个重新改为Activity 暂时设置为单例模式
- 在根据时间戳生成id后，使用SecurityRandom在末尾加上三位随机数，减小id重复的概率
- MineActivity增加了关注功能，输入用户id即可添加关注，使用SharedPreference保存
- 四个Activity均为单例模式，在某些功能跳转到其他activity时加finish，否则始终保持原来的状态
- 增加不压缩选项
- 优化刷新功能
- 限制昵称长度
- 录制视频点叉后不保存
- 首页增加关注功能

