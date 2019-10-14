# reader
一款在任阅基础上改进的网络小说阅读器📕,采用Kt编写，支持追书、看书。拥有语音朗读，仿真翻页等功能。相比原作者:

    - 重写代码逻辑，优化代码结构,Md设计风格
    
    + 使用郭神的LitePal框架存储
    
    * 语音朗读、插画等功能都进行了实现
    
    - 适配了安卓9.0,使用AndroidX控件,对刘海屏等异形屏幕有了很好的支持
    
注: 之前项目采用Java编写，目前正在重构转KT代码,之后会上传Kt源代码。欢迎大家提问题。本人一定尽快修复

## 使用技术 
Kt、AndroidX、Retrofit、Okhttp3、Glide、LitePal等

## TIP
- 目前书源为轻小说书源,书中大多带了插图,因此屏蔽了tts语音朗读功能,之后会切换成正常小说书源并添加语音朗读功能

+ 因为书籍为爬取的数据,服务器在美国,所以国内监管比较严的时候可能需要翻墙或切换网络去多尝试几次。

* 同时请各位大神手下留情不要攻击或者爬取数据,供大家学习使用。之后会切换成网络多数源。

## 项目截图

**主页**

<img src="https://github.com/390057892/reader/blob/master/screenshot/%E9%A6%96%E9%A1%B5.jpg?raw=true" width="280" alt="首页"/> <img src="https://github.com/390057892/reader/blob/master/screenshot/%E6%8E%A8%E8%8D%90.jpg?raw=true" width="280" alt="推荐"/><img src="https://github.com/390057892/reader/blob/master/screenshot/%E4%B9%A6%E5%BA%93.jpg?raw=true" width="280" alt="书库"/> <img src="https://github.com/390057892/reader/blob/master/screenshot/%E8%AE%BE%E7%BD%AE.png?raw=true" width="280" alt="设置"/> <img src="https://github.com/390057892/reader/blob/master/screenshot/%E6%90%9C%E7%B4%A2.png?raw=true" width="280" alt="搜索"/>

</br>

**阅读页**

<img src="https://github.com/390057892/reader/blob/master/screenshot/%E9%98%85%E8%AF%BB%E9%A1%B5.png" width="280" alt="阅读页"/> <img src="https://github.com/390057892/reader/blob/master/screenshot/%E9%98%85%E8%AF%BB%E9%A1%B5%E8%8F%9C%E5%8D%95.png" width="280" alt="菜单"/> <img src="https://github.com/390057892/reader/blob/master/screenshot/%E4%B9%A6%E7%B1%8D%E7%9B%AE%E5%BD%95.png" width="280" alt="目录"/> <img src="https://github.com/390057892/reader/blob/master/screenshot/%E6%8F%92%E9%A1%B51.jpg" width="280" alt="插页"/> <img src="https://github.com/390057892/reader/blob/master/screenshot/%E6%8F%92%E9%A1%B52.jpg" width="280" alt="插页2"/>

## LICENSE

```
Copyright (C) zhaolijun, Open source codes for study only.
Do not use for commercial purpose.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```