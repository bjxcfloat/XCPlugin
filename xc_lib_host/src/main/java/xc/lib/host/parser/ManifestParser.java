package xc.lib.host.parser;

import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import xc.lib.host.parser.ext.ApkCommentReader;
import xc.lib.host.parser.ext.ApkParser;
import xc.lib.host.parser.ext.XmlHandler;


// 从apk包解析配置文件，并读取进来,还有一种方式在预编译时生成一个常量类，比运行期执行要节省时间
public class ManifestParser {

    public static XmlHandler  parseManifest(String manifestStr) {
        XMLReader xmlReader = null;
        XmlHandler handler = new XmlHandler();

        /* 解析字符串 */
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            xmlReader = parser.getXMLReader();
            xmlReader.setContentHandler(handler);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (xmlReader != null) {
            StringReader strReader = null;

            try {
                strReader = new StringReader(manifestStr);
                xmlReader.parse(new InputSource(strReader));
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                if (strReader != null) {
                    strReader.close();
                }
            }
        }
        return handler;
    }

    /**
     * 从 APK 中获取 Manifest 内容
     *
     * @param apkFile apk 文件路径
     * @return apk 中 AndroidManifest 中的内容
     */
    public static String getManifestFromApk(String apkFile) {

        // 先从 Apk comment 中解析 AndroidManifest
        String manifest = ApkCommentReader.readComment(apkFile);
        if (!TextUtils.isEmpty(manifest)) {

            return manifest;
        }

        // 解析失败时，再从 apk 中解析
        ApkParser parser = null;
        try {
            parser = new ApkParser(apkFile);
//            if (LOG) {
//                long begin = System.currentTimeMillis();
//                manifest = parser.getManifestXml();
//                long end = System.currentTimeMillis();
////                LogDebug.d(PLUGIN_TAG, "从 apk 中解析 xml 耗时 " + (end - begin) + " 毫秒");
//            } else {
                manifest = parser.getManifestXml();
//            }
            return manifest;

        } catch (IOException t) {
            t.printStackTrace();
        } finally {
            if (parser != null) {
                try {
                    parser.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }


    public static HashMap<String,List<IntentFilter>> getFilters(String apkpath)
    {
        String manfest =ManifestParser.getManifestFromApk(apkpath);
        XmlHandler xml= ManifestParser.parseManifest(manfest);
        List<ComponentBean> recelist = xml.getReceivers();
        if(recelist!=null&&recelist.size()>0)
        {
            HashMap<String,List<IntentFilter>> hn = new HashMap<>();
            for (ComponentBean bean:recelist)
            {
                hn.put(bean.name,bean.intentFilters);
            }

            return hn;
        }

//        Log.e("asdasd","manfest-"+manfest);
//        Log.e("asdasd","getFilters-"+new Gson().toJson(xml));

        return null;
    }

}
