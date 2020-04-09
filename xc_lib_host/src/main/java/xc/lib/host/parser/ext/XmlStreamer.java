package xc.lib.host.parser.ext;

public interface XmlStreamer {

    void onStartTag(XmlNodeStartTag xmlNodeStartTag);

    void onEndTag(XmlNodeEndTag xmlNodeEndTag);

    void onNamespaceStart(XmlNamespaceStartTag tag);

    void onNamespaceEnd(XmlNamespaceEndTag tag);
}
