
package xc.lib.host.gradle


class XcPluginConfig {

    // 是否使用AppCompat主题
    def useAppCompat = false
    def standardActivityPitCount = 10
    def singleTopActivityPitCount = 5
    def singleTaskActivityPitCount = 5
    def singleInstanceActivityPitCount = 4
    def servicePitCount = 5
    def contentProviderPitCount = 2
    // 宿主contentprovider类库名
    def hostLibPackageName = 'xc.lib.host.contentprovider'

}