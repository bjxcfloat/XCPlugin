package xc.lib.host.parser.ext;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public  abstract class AbstractApkParser implements Closeable {

    private static final String MANIFEST_FILE = "AndroidManifest.xml";

    private String manifestXml;

    /**
     * return decoded AndroidManifest.xml
     *
     * @return decoded AndroidManifest.xml
     */
    public String getManifestXml() throws IOException {
        if (this.manifestXml == null) {
            parseManifestXml();
        }
        return this.manifestXml;
    }

    /**
     * parse manifest.xml, get manifestXml as xml text.
     */
    private void parseManifestXml() throws IOException {
        XmlTranslator xmlTranslator = new XmlTranslator();

        byte[] data = getFileData(MANIFEST_FILE);
        if (data == null) {
            throw new ParserException("Manifest file not found");
        }
        transBinaryXml(data, xmlTranslator);
        this.manifestXml = xmlTranslator.getXml();
    }

    /**
     * read file in apk into bytes
     */
    public abstract byte[] getFileData(String path) throws IOException;

    private void transBinaryXml(byte[] data, XmlStreamer xmlStreamer) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        BinaryXmlParser binaryXmlParser = new BinaryXmlParser(buffer);
        binaryXmlParser.setXmlStreamer(xmlStreamer);
        binaryXmlParser.parse();
    }

    @Override
    public void close() throws IOException {
    }
}
