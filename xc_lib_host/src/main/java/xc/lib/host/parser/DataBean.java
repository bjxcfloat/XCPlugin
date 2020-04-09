package xc.lib.host.parser;

import android.os.PatternMatcher;
import android.text.TextUtils;

public class DataBean {
    public String scheme;
    public String host;
    public String port;
    public String mimeType;
    public String path;
    public String pathPattern;
    public String pathPrefix;

    @Override
    public String toString() {
        return String.format(
                "{scheme:%s, host:%s, mimeType:%s, path:%s, pathPattern:%s, pathPrefix:%s, port:%s}", scheme, host, mimeType, pathPattern, pathPrefix, path, port);
    }

    /**
     * 获得 path 匹配类型
     */
    public int getPatternMatcherType() {
        if (TextUtils.isEmpty(pathPattern) && TextUtils.isEmpty(pathPattern)) {
            return PatternMatcher.PATTERN_LITERAL;
        } else if (!TextUtils.isEmpty(pathPrefix)) {
            return PatternMatcher.PATTERN_PREFIX;
        } else {
            return PatternMatcher.PATTERN_SIMPLE_GLOB;
        }
    }
}
