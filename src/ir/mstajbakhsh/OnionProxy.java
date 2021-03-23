package ir.mstajbakhsh;

import java.net.Socket;

public class OnionProxy implements IProxy {
    String _Host;
    String _HiddenHost;
    int _Port;
    int _HiddenPort;
    ITunnelStatus eventHandler;

    public OnionProxy(String Host, int Port, ITunnelStatus eventHandler) {
        this._Host = Host;
        this._HiddenHost = Host;
        this._Port = Port;
        this._HiddenPort = Port;
        this.eventHandler = eventHandler;

        if (getType() == AddressHelper.AddressType.HiddenService) {
            createInnerHandler();
        }
    }

    @Override
    public void prepareAndStartTunnelThreads(Socket innerSocket, Socket hiddenSocket) {

    }

    @Override
    public void restoreConnection(String host, int port) {

    }

    @Override
    public void createInnerHandler() {

    }

    public int getPort() {
        return _Port;
    }

    public String getHost() {
        return _Host;
    }

    public AddressHelper.AddressType getType() {
        return AddressHelper.getType(getHost());
    }

    @Override
    public String toString() {
        return getHost() + ":" + getPort() + "<=>" + _HiddenHost + ":" + _HiddenPort;
    }

    public String getProxyType() {
        return "";
    }
}
