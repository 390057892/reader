# Reader
一款网络小说阅读器📕,采用Kt编写，支持追书、看书。支持TTS语音朗读，仿真翻页等功能。相比old分支:

    - 重写代码逻辑，优化代码结构,Md设计风格

    + sql分层处理，MVVM 架构

    * 语音朗读、插画、国际化、黑夜模式等功能都进行了实现

    - 适配了安卓10,使用AndroidX控件,对刘海屏等异形屏幕有了很好的支持

注: old分支之前项目采用Java编写,之后用studio一键转的Kotlin代码,没有kotlin特有的语法糖之类，
如果有同学想看java代码,old分支应该更容易懂。master分支为全新代码架构,欢迎大家提问题。本人一定尽快修复

## 使用技术
Kt、AndroidX、Retrofit、Okhttp3、Glide、LitePal、MVVM、协程等

## TIP
- 当前书源为轻小说书源,书中大多带了插图。(不提供正常小说的原因有版权问题,同时防止有人直接用代码改个名字就去上架)

+ 因为书籍为爬取的数据,服务器在美国,所以国内监管比较严的时候可能需要翻墙或切换网络去多尝试几次。

* 同时请大家不要攻击或者爬取数据,供大家学习使用。

## 项目截图


| <img src="https://github.com/390057892/reader/blob/master/screenshot/%E9%A6%96%E9%A1%B5.jpg?raw=true" width="280" alt="首页"/> | <img src="https://github.com/390057892/reader/blob/master/screenshot/%E6%8E%A8%E8%8D%90.jpg?raw=true" width="280" alt="推荐"/>  |  <img src="https://github.com/390057892/reader/blob/master/screenshot/%E4%B9%A6%E5%BA%93.jpg?raw=true" width="280" alt="书库"/>  |
| --- | --- | --- |
| <img src="https://github.com/390057892/reader/blob/master/screenshot/%E8%AE%BE%E7%BD%AE.png?raw=true" width="280" alt="设置"/> | <img src="https://github.com/390057892/reader/blob/master/screenshot/%E6%90%9C%E7%B4%A2.png?raw=true" width="280" alt="搜索"/> | <img src="https://github.com/390057892/reader/blob/master/screenshot/%E9%98%85%E8%AF%BB%E9%A1%B5.png" width="280" alt="阅读页"/> |
| <img src="https://github.com/390057892/reader/blob/master/screenshot/%E9%98%85%E8%AF%BB%E9%A1%B5%E8%8F%9C%E5%8D%95.png" width="280" alt="菜单"/> |  <img src="https://github.com/390057892/reader/blob/master/screenshot/%E4%B9%A6%E7%B1%8D%E7%9B%AE%E5%BD%95.png" width="280" alt="目录"/>  | <img src="https://github.com/390057892/reader/blob/master/screenshot/%E6%8F%92%E9%A1%B51.jpg" width="280" alt="插页"/>  |
| <img src="https://github.com/390057892/reader/blob/master/screenshot/%E6%8F%92%E9%A1%B52.jpg" width="280" alt="插页2"/> | <img src="https://github.com/390057892/reader/blob/master/screenshot/night.jpg" width="280" alt="夜间"/> | <img src="https://github.com/390057892/reader/blob/master/screenshot/edit.jpg" width="280" alt="编辑"/> |



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

