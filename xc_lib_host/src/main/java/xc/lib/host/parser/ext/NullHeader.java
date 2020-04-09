package xc.lib.host.parser.ext;

public class NullHeader extends ChunkHeader {
    public NullHeader(int chunkType, int headerSize, long chunkSize) {
        super(chunkType, headerSize, chunkSize);
    }
}
