package xc.lib.host.parser;

import android.content.IntentFilter;

import java.util.List;

public class ComponentBean {
    public String name;
    public List<IntentFilter> intentFilters;

    @Override
    public String toString() {
        return String.format("{name:%s, intent-filter.size():%s}", name, intentFilters.size());
    }

}
