package ir.mstajbakhsh;

import java.net.Socket;

public interface IProxy {
    void prepareAndStartTunnelThreads(Socket innerSocket, Socket hiddenSocket);
    //TODO change to Address
    void restoreConnection(String host, int port);
    void createInnerHandler();
}
