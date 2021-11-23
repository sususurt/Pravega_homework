import io.pravega.client.admin.ReaderGroupManager;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.EventRead;
import io.pravega.client.stream.EventStreamReader;
import io.pravega.client.stream.EventStreamWriter;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Chat implements Runnable {
    // thread is used to running the reader
    private Thread thread;
    // whether download the file
    protected boolean requestDownload = false;
    // whether the chat is going on
    protected boolean isChatting = true;
    // Names
    protected String selfName, peerName, groupName, streamName;
    // default settings
    protected String controllerURI = "tcp://127.0.0.1:9090";
    protected String scopeName = "ChatRoom";
    protected String file_scopeName = "FileTransfer";
    // pravega apis
    protected StreamManager streamManager, file_streamManager;
    protected ReaderGroupManager readerGroupManager, file_readerGroupManager;
    protected EventStreamWriter<byte[]> writer, file_writer;
    protected EventStreamReader<byte[]> reader, file_reader;

    /***
     * Constructor
     * @param mode 1->One to One chat; 2->Group chat
     *
     * One to One mode:
     *            create the stream name by adding selfName's hashcode
     *            and peerName's hashcode together, make sure that only
     *            these pair of user can chat in this stream.
     * Group chat mode:
     *            creat the stream name by the group name, which means
     *            that anyone knows the group name can join this stream.
     * intro:
     *             Creating two streams, one is used for chatting, and
     *             the other is used for file transfer.
     */
    public Chat(String selfName, String peerName, String groupName, int mode) throws Exception {
        if (mode == 1) {
            this.peerName = peerName;
            this.streamName = (selfName.hashCode() + peerName.hashCode()) + "";
        } else {
            this.groupName = groupName;
            this.streamName = groupName;
        }
        this.selfName = selfName;
        this.streamManager = Writer.createStream(controllerURI, scopeName, streamName);
        this.file_streamManager = Writer.createStream(controllerURI, file_scopeName, streamName);
        this.writer = Writer.getWriter(controllerURI, scopeName, streamName);
        this.file_writer = Writer.getWriter(controllerURI, file_scopeName, streamName);
        this.readerGroupManager = Reader.createReaderGroup(controllerURI, scopeName, streamName, selfName);
        this.file_readerGroupManager = Reader.createReaderGroup(controllerURI, file_scopeName, streamName, selfName);
        this.reader = Reader.createReader(controllerURI, scopeName, selfName, selfName);
        this.file_reader = Reader.createReader(controllerURI, file_scopeName, selfName, selfName);
        System.out.println("Chat Room: " + streamName + " Entered.");
        System.out.println("==================================");
    }

    /***
     *  Strat chatting:
     *  create a thread to read the chat stream
     *  get user input:
     *      1. with prefix: upload@xxxx(path) -> upload file
     *      2. download: download the uploaded file
     *      3. bye: quit
     *      4. other input: sent a message into the stream
     */
    public void startChat() throws Exception {
        startReading();
        while (isChatting) {
            Scanner scanner = new Scanner(System.in);
            String userInput = scanner.nextLine();
            if (userInput.startsWith("upload@")) {
                try {
                    uploadFile(userInput);
                    writeData(writer, selfName + " has upload a file.");
                } catch (FileNotFoundException e) {
                    System.out.println("Invalid path.");
                }
            } else if (userInput.equalsIgnoreCase("download")) {
                requestDownload = true;
            } else if (userInput.equalsIgnoreCase("bye")) {
                isChatting = false;
            } else {
                writeData(writer, selfName + ":" + userInput);
            }
        }
    }

    /***
     *  read events from chat steam
     */
    public void readData(EventStreamReader<byte[]> reader) {
        EventRead<byte[]> event = reader.readNextEvent(500);
        if (event.getEvent() != null) {
            System.out.println(byteToString(event.getEvent()));
        }
    }

    /***
     *  convert the string message to byte[]
     *  write the byte[] into the chat stream
     */
    public void writeData(EventStreamWriter<byte[]> writer, String message) throws Exception {
        CompletableFuture<Void> future = writer.writeEvent(stringToByte(message));
        future.get();
    }

    /***
     *  get a pathName, convert the file into byte[],
     *  write the byte[] into the file transefer stream
     */
    public void uploadFile(String pathName) throws Exception {
        File file = new File(pathName.substring(7));
        InputStream input = new FileInputStream(file);
        byte[] byt = new byte[input.available()];
        input.read(byt);
        file_writer.writeEvent(byt);
    }

    /***
     *  read the file transfer stream,
     *  convert the events(byte[]) to file
     */
    public void receiveFile(EventStreamReader<byte[]> reader) throws Exception {
        EventRead<byte[]> event = reader.readNextEvent(500);
        if (event.getEvent() != null) {
            OutputStream output = new FileOutputStream("receivedFile");
            output.write(event.getEvent());
            System.out.println("File download success.");
            requestDownload = false;
        } else {
            System.out.println("No new file to download.");
        }
    }

    /***
     * close writers, readers and readerGroupManagers
     * delete the readerGroups
     */
    public void close() {
        writer.close();
        file_writer.close();
        reader.close();
        file_reader.close();
        readerGroupManager.deleteReaderGroup(selfName);
        file_readerGroupManager.deleteReaderGroup(selfName);
        readerGroupManager.close();
        file_readerGroupManager.close();
    }

    // thread to read the stream
    @Override
    public void run() {
        try {
            while (isChatting) {
                readData(reader);
                if (requestDownload) {
                    receiveFile(file_reader);
                }
                Thread.sleep(500);
            }
            close();
        } catch (Exception e) {
            System.out.println("Thread " + selfName + " error.");
        }
        System.out.println("Thread " + selfName + " exiting.");
    }

    public void startReading() {
        if (thread == null) {
            thread = new Thread(this, selfName);
            thread.start();
        }
    }

    /*Utility Functions*/

    // Convert string to byte[]
    public byte[] stringToByte(String msg) {
        return msg.getBytes();
    }
    // Convert byte[] to String
    public String byteToString(byte[] bytes) {
        return new String(bytes);
    }
}
