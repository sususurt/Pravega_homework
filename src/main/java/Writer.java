
import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.EventWriterConfig;
import io.pravega.client.stream.StreamConfiguration;
import io.pravega.client.stream.impl.JavaSerializer;

import java.net.URI;

public class Writer {

    public static EventStreamWriter<byte[]> getWriter(String url, String scope, String stream) throws Exception {
        URI uri = new URI(url);
        ClientConfig build = ClientConfig.builder().controllerURI(uri).build();
        EventStreamClientFactory streamClientFactory = EventStreamClientFactory.withScope(scope, build);
        EventWriterConfig writerConfig = EventWriterConfig.builder().build();
        return streamClientFactory.createEventWriter(stream, new JavaSerializer<>(), writerConfig);
    }

    public static StreamManager createStream(String url, String scope, String stream)throws  Exception{
        URI uri = new URI(url);
        StreamManager streamManager = StreamManager.create(uri);
        streamManager.createScope(scope);

        StreamConfiguration build = StreamConfiguration.builder().build();
        streamManager.createStream(scope,stream,build);
        return streamManager;
    }
}
