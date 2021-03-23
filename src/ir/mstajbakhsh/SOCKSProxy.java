package ir.mstajbakhsh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class SOCKSProxy extends OnionProxy {
    Thread i2hThread;
    Thread h2iThread;
    Thread serverThread;
    private ServerSocket ss;

    public SOCKSProxy(String Host, int Port, ITunnelStatus eventHandler) {
        super(Host, Port, eventHandler);
    }

    private static boolean available(int port) {
        try (Socket ignored = new Socket("localhost", port)) {
            return false;
        } catch (IOException ignored) {
            return true;
        }
    }

    @Override
    public void createInnerHandler() {
        this._Host = "127.0.0.1";
        Random r = new Random();
        while (ss == null) {
            try {
                _Port = r.nextInt(5000) + 10000;
                if (available(_Port)) {
                    break;
                }
            } catch (Exception ex) {
                continue;
            }
        }
        restoreConnection(_Host, _Port);
    }

    @Override
    public void restoreConnection(String host, int port) {
        _Port = port;
        _Host = host;

        try {
            if (ss != null && ss.isBound()) {
                ss.close();
                eventHandler.onConnectionClosed(this);
            }

            if (serverThread != null && serverThread.isAlive()) {
                serverThread.interrupt();
            }
            ss = new ServerSocket(port);
        } catch (Exception ex) {

        }

        //Server socket is ready!
        eventHandler.onConnectionPrepared(this);

        Address normal = new Address(host, port);
        Address hidden = new Address(_HiddenHost, _HiddenPort);
        Variable.local2onion.put(normal, hidden);
        Variable.onion2local.put(hidden, normal);

        //Connect to hiddenService
        InetSocketAddress hiddenSA = InetSocketAddress.createUnresolved(_HiddenHost, _HiddenPort);
        Proxy p = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(Variable.localTorIP, Variable.localTorPort));
        Socket hiddenSocket = new Socket(p);

        Runnable serverRunnable = () -> {
            try {
                while (true) {
                    Socket innerSocket = ss.accept();
                    if (!hiddenSocket.isConnected()) {
                        hiddenSocket.connect(hiddenSA);
                    }
                    prepareAndStartTunnelThreads(innerSocket, hiddenSocket);
                    eventHandler.onConnectionEstablished(this);
                }
            } catch (IOException ex) {
                eventHandler.onExceptionOccured(this, ex);
                restoreConnection(host, port);
            }
        };
        serverThread = new Thread(serverRunnable, "Server");
        serverThread.start();

    }

    @Override
    public void prepareAndStartTunnelThreads(Socket innerSocket, Socket hiddenSocket) {
        Runnable i2h = () -> {
            try {
                InputStream is = innerSocket.getInputStream();
                OutputStream os = hiddenSocket.getOutputStream();
                while (!innerSocket.isInputShutdown() && !hiddenSocket.isOutputShutdown()) {

                    int i = is.read();
                    if (i != -1) {
                        os.write(i);
                    } else {
                        break;
                    }

                    os.flush();
                }
            } catch (IOException ex) {
                //Do nothing
                eventHandler.onExceptionOccured(this, ex);
                restoreConnection(_Host, _Port);
            }
        };
        i2hThread = new Thread(i2h);

        Runnable h2i = () -> {
            try {
                InputStream is = hiddenSocket.getInputStream();
                OutputStream os = innerSocket.getOutputStream();

                while (!hiddenSocket.isInputShutdown() && !innerSocket.isOutputShutdown()) {
                    int i = is.read();

                    if (i != -1) {
                        os.write(i);
                    } else {
                        break;
                    }

                    os.flush();
                }
            } catch (IOException ex) {
                eventHandler.onExceptionOccured(this, ex);
                //restoreConnection(_Host, _Port);
            }
        };
        //h2iThread = new Thread(h2i, _HiddenHost + "2" + _Port);
        h2iThread = new Thread(h2i);

        i2hThread.start();
        h2iThread.start();
    }

    @Override
    public String getProxyType() {
        return "SOCKS";
    }
}
