package a9shaonian.com.chatonline.service;

public interface StatusChangeListener {
    void connect();
    void disconnect();
    void error();
    void message(String text);
}
