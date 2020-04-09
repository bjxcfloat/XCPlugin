package xc.lib.host.gradle


class Constant {
    static def plugin_standardActivityPitCount = "plugin_standardActivityPitCount"
    static def plugin_singleTopActivityPitCount = "plugin_singleTopActivityPitCount"
    static def plugin_singleTaskActivityPitCount = "plugin_singleTaskActivityPitCount"
    static def plugin_singleInstanceActivityPitCount = "plugin_singleInstanceActivityPitCount"
    static def plugin_isUseAppCompat = "plugin_isUseAppCompat"
    static def plugin_servicePitCount = "plugin_servicePitCount"
    static def plugin_contentProviderPitCount = "plugin_contentProviderPitCount"
    static def standard = "standard"
    static def singleTop = "singleTop"
    static def singleTask = "singleTask"
    static def singleInstance = "singleInstance"
    static def formatActivity = '<activity android:name="{$className}"  android:launchMode="{$launchMode}" android:screenOrientation="portrait" android:configChanges="keyboard|keyboardHidden|orientation|screenSize" android:exported="false" />'
    static def formatMetadata = '<meta-data android:name="{$name}"  android:value="{$val}" />'
    static def formatService = '<service android:name="{$name}"   android:exported="false"  android:enabled="true" />'
    static def formatContentProvider = ' <provider  android:name="{$provider}"  android:authorities="{$authorities}"  android:enabled="true"  android:exported="true"></provider>'
    static def pluginConfig = 'xcPluginConfig'

}