# Tiktok

## 主要改动
- 将Activity改为Fragment
- ”我的“增加用户id和昵称的显示，可修改昵称
- 初次使用时根据时间戳生成id，id和name都用SharedPreferences保存
- recycleView一次滑动一页 不会停在中间


## 待改进
- Camera的pressYes函数涉及到Fragment的转换 还没有实现
- 手动切换每个Fragment的时候状态有点问题
- 两个人完全同时使用时会导致id一样

