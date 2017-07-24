# summer-android
通用android客户端

使用说明
---
1、develop分支为主要的迭代branch，为app的主要分支，app的所有修改请基于develop分支进行

2、遇到的bug，请及时修复并 push 到 develop 分支

3、微信支付的类必须位于与应用id一致的package下

4、H5页面直接调用安卓信息
  > 1)、安卓 SInterface 定义相关的js直接调用对象
    2)、JSobj 供web端js调用标识，请修改成jsAndroid，统一规范
