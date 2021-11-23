
import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.admin.ReaderGroupManager;
import io.pravega.client.stream.*;
import io.pravega.client.stream.impl.JavaSerializer;

import java.net.URI;

public class Reader {
    public static ReaderGroupManager createReaderGroup(String url, String scope, String stream, String groupName) throws Exception {
        URI uri = new URI(url);
        ReaderGroupManager readerGroupManager = ReaderGroupManager.withScope(scope, uri);
        ReaderGroupConfig build = ReaderGroupConfig.builder().stream(scope + "/" + stream).build();
        readerGroupManager.createReaderGroup(groupName, build);
        return readerGroupManager;
    }

    public static EventStreamReader<byte[]> createReader(String url, String scope, String readerId, String groupName) throws Exception {
        URI uri = new URI(url);
        ClientConfig clientConfig = ClientConfig.builder().controllerURI(uri).build();
        EventStreamClientFactory streamClientFactory = EventStreamClientFactory.withScope(scope, clientConfig);
        ReaderConfig readerConfig = ReaderConfig.builder().build();
        EventStreamReader<byte[]> reader = streamClientFactory.createReader(readerId, groupName, new JavaSerializer<>(), readerConfig);
        return reader;
    }

}
