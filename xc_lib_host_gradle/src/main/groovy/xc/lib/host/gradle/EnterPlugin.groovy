package xc.lib.host.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class EnterPlugin implements Plugin<Project> {

    def project
    def appID


    @Override
    void apply(Project project) {

        this.project = project
        project.extensions.create(Constant.pluginConfig, XcPluginConfig)
        XcPluginConfig config = project[Constant.pluginConfig]

        project.afterEvaluate {
            project.android.applicationVariants.all { variant ->
                variant.outputs.each { output ->
                    output.processResources.doFirst { pm ->

                        def generateBuildConfigTask = VariantCompat.getGenerateBuildConfigTask(variant)
                        // 获取包名
                        appID = generateBuildConfigTask.appPackageName
                        println("appID-" + appID)

                        String manifestPath = output.processResources.manifestFile;
                        def manifestContent = new File(manifestPath).getText()


                        def newConfig = getActivityPits(config)
                        newConfig = getMetaData(config, newConfig)
                        newConfig = getServicePits(config, newConfig)
                        newConfig = getContentProviderPits(config, newConfig)

//                        Node xml = new XmlParser().parseText(manifestContent)
//                        xml.application[0].appendNode("meta-data", ['android:name': 'channel', 'android:value': '123213'])
//                        def serialize = XmlUtil.serialize(xml)
                        def regex = '<\\s*/\\s*application\\s*>'
                        def matcher = (manifestContent =~ regex)
                        manifestContent = matcher.replaceAll(newConfig + '\r\n' + "</application>")

                        println(manifestContent)
                        new File(manifestPath).write(manifestContent)
                    }
                }
            }
        }


    }

    def getMetaData(XcPluginConfig config, def activityies) {
        def regexMetaDataName = '\\{\\$name\\}'
        def regexMetaVal = '\\{\\$val\\}'
        def matcherMetaData = (Constant.formatMetadata =~ regexMetaDataName)

        def metaStr = matcherMetaData.replaceAll(Constant.plugin_standardActivityPitCount)
        def matcherMetaVal = (metaStr =~ regexMetaVal)
        metaStr = matcherMetaVal.replaceAll(config.standardActivityPitCount.toString())

        activityies += "\r\n" + metaStr

        metaStr = matcherMetaData.replaceAll(Constant.plugin_singleTopActivityPitCount)
        matcherMetaVal = (metaStr =~ regexMetaVal)
        metaStr = matcherMetaVal.replaceAll(config.singleTopActivityPitCount.toString())

        activityies += "\r\n" + metaStr

        metaStr = matcherMetaData.replaceAll(Constant.plugin_singleTaskActivityPitCount)
        matcherMetaVal = (metaStr =~ regexMetaVal)
        metaStr = matcherMetaVal.replaceAll(config.singleTaskActivityPitCount.toString())

        activityies += "\r\n" + metaStr

        metaStr = matcherMetaData.replaceAll(Constant.plugin_singleInstanceActivityPitCount)
        matcherMetaVal = (metaStr =~ regexMetaVal)
        metaStr = matcherMetaVal.replaceAll(config.singleInstanceActivityPitCount.toString())

        activityies += "\r\n" + metaStr

        metaStr = matcherMetaData.replaceAll(Constant.plugin_isUseAppCompat)
        matcherMetaVal = (metaStr =~ regexMetaVal)
        metaStr = matcherMetaVal.replaceAll(config.useAppCompat.toString())

        activityies += "\r\n" + metaStr

        metaStr = matcherMetaData.replaceAll(Constant.plugin_servicePitCount)
        matcherMetaVal = (metaStr =~ regexMetaVal)
        metaStr = matcherMetaVal.replaceAll(config.servicePitCount.toString())

        activityies += "\r\n" + metaStr

        metaStr = matcherMetaData.replaceAll(Constant.plugin_contentProviderPitCount)
        matcherMetaVal = (metaStr =~ regexMetaVal)
        metaStr = matcherMetaVal.replaceAll(config.contentProviderPitCount.toString())

        activityies += "\r\n" + metaStr

        return activityies
    }
    def getContentProviderPits(XcPluginConfig config, def activityies) {

        def regexClassName = '\\{\\$provider\\}'
        def matcherClassName = (Constant.formatContentProvider =~ regexClassName)
        def regexAuthority = '\\{\\$authorities\\}'


        for (def i = 0; i < config.contentProviderPitCount; i++) {

            def tempStr
            def classname = config.hostLibPackageName + '.ContentProviderPit' + i
            tempStr = matcherClassName.replaceAll(classname)
            def matherAuthority =  (tempStr =~ regexAuthority)
            tempStr = matherAuthority.replaceAll("privoders"+i)
            activityies += '\r\n' + tempStr

        }

        return activityies
    }
    def getServicePits(XcPluginConfig config, def activityies) {

        def regexClassName = '\\{\\$name\\}'
        def matcherClassName = (Constant.formatService =~ regexClassName)

        for (def i = 0; i < config.servicePitCount; i++) {

            def tempStr
            def classname = appID + '.s_' + i
            tempStr = matcherClassName.replaceAll(classname)
            activityies += '\r\n' + tempStr

        }

        return activityies
    }

    def getActivityPits(XcPluginConfig config) {
        def activityies = ''
        def regexClassName = '\\{\\$className\\}'
        def regexLauchMode = '\\{\\$launchMode\\}'
        def matcherClassName = (Constant.formatActivity =~ regexClassName)
        // 输出standard的Activity
        for (def i = 0; i < config.standardActivityPitCount; i++) {

            def tempStr
            def classname = appID + '.a_' + i
            tempStr = matcherClassName.replaceAll(classname)
            def matcherLauchMode = (tempStr =~ regexLauchMode)
            tempStr = matcherLauchMode.replaceAll(Constant.standard)
            activityies += '\r\n' + tempStr

        }
        // 输出singleTop的Activity
        for (def i = 0; i < config.singleTopActivityPitCount; i++) {

            def tempStr
            def classname = appID + '.b_' + i
            tempStr = matcherClassName.replaceAll(classname)
            def matcherLauchMode = (tempStr =~ regexLauchMode)
            tempStr = matcherLauchMode.replaceAll(Constant.singleTop)
            activityies += '\r\n' + tempStr

        }
        // 输出singleTask的Activity
        for (def i = 0; i < config.singleTaskActivityPitCount; i++) {

            def tempStr
            def classname = appID + '.c_' + i
            tempStr = matcherClassName.replaceAll(classname)
            def matcherLauchMode = (tempStr =~ regexLauchMode)
            tempStr = matcherLauchMode.replaceAll(Constant.singleTask)
            activityies += '\r\n' + tempStr

        }
        // 输出singleInstance的Activity
        for (def i = 0; i < config.singleInstanceActivityPitCount; i++) {

            def tempStr
            def classname = appID + '.d_' + i
            tempStr = matcherClassName.replaceAll(classname)
            def matcherLauchMode = (tempStr =~ regexLauchMode)
            tempStr = matcherLauchMode.replaceAll(Constant.singleInstance)
            activityies += '\r\n' + tempStr

        }

        return activityies
    }

}
