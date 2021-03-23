package ir.mstajbakhsh;

public interface ITunnelStatus {
    void onConnectionEstablished(OnionProxy OnionProxy);
    void onConnectionClosed(OnionProxy OnionProxy);
    void onExceptionOccured(OnionProxy OnionProxy, Exception ex);
    void onConnectionPrepared(OnionProxy OnionProxy);
}
