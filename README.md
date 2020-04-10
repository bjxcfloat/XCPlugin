# XCPlugin是一款插件框架，特点如下：

1，支持Android 2.3以上

2，支持插件的安装，卸载

3，系统注入点只有一处，仅Hook了classloader类，减少了兼容问题

4，插件完美支持四大组件：Activity,Service,Broadcast,ContentProvider

5，支持插件自定义Application，静态广播


项目结构介绍：

App:宿主项目

plugin1:插件样例1

xc_lib_common：最顶层的公共类库

宿主和插件类库都要依赖这个common组件

xc_lib_host：宿主类库，App依赖xc_lib_host，xc_lib_host依赖xc_lib_common

宿主项目要依赖这个宿主类库

xc_lib_host_gradle：宿主依赖gradle类库，App依赖xc_lib_host_gradle

宿主项目要依赖这个类库，主要是对宿主的配置文件做处理，比如：增加Actiity坑位，Service坑位，ContentProvider坑位等

xc_lib_plugin:插件项目要依赖这个类库，包含一些需要插件对象继承的一些基类等


原理介绍：

Activity，Service原理：通过在宿主项目埋设坑位，骗过系统，让系统认为是合法的业务组件，然后在ClassLoader实例化对象的时候会从坑位管理器找到真实的插件类加载，在插件找不到会跳到宿主项目继续寻找并加载。

插件类加载器与宿主类加载是可以互相调用的，目的就是找到对应的资源正常加载业务对象正常运行。具体看参看源码，或者与我联系。

Contentprovider是通过代理类方式实现。具体看参看源码，或者与我联系。


使用介绍：

PluginManager为插件总入口

插件安装：

 PluginManager.getInstance().install(newApk);
 
 newApk：Apk绝对地址
 
 插件卸载：
 
 PluginManager.getInstance().unInstall(插件包名);
 
 Activity:
 
 插件Activity需继承PluginAppCompatActivity或者其他基类，根据业务需求自行配置。
 
 插件内打开Activity可以直接按正常写法即可：startActivity(),如果要打开的Activity类不在当前插件，则intent里可以配置字符串类。
 
 Service:
 
 插件的service需继承PluginService，并在插件的manifest配置文件写好配置即可使用
 
 Broadcast:
 
 插件广播按正常写法即可，支持插件manfest静态配置
 
 ContentProvider:
 
 插件contentprovider需继承PluginContentProvider
 
 插件的Authority需调用基类getAuthority()，客户端定制的Authority后移到第二个参数，举例：com.plugin.test是插件的Authority
 
   matcher.addURI(getAuthority(), "com.plugin.test/delete", 1);
   
 自Android 7.0后系统禁止应用向外部公开file://URI ，因此需要FileProvider来向外界传递URI
     <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="com.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_path" />
        </provider>
 将上述类似配置信息放到宿主项目，并在res目录增加xml相关配置文件，插件使用的时候需使用在宿主配置的对应authorities即可正常访问。
  if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(this, "com.fileprovider", outputImage);
   } else {
            imageUri = Uri.fromFile(outputImage);
   }
 
 项目内代码都是同步执行，如需异步执行，自行配置即可，目前还在优化项目提高兼容性与性能。有问题欢迎联系我：bjxcfloat@163.com,谢谢！
 
 
 
 
 


