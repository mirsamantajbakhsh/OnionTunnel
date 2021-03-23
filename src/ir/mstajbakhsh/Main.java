package ir.mstajbakhsh;

import ir.mstajbakhsh.newHTTPProxy.Proxy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static ITunnelStatus eventHandler = new ITunnelStatus() {
        @Override
        public void onConnectionEstablished(OnionProxy OnionProxy) {
            System.out.println("[+] Connection established " + OnionProxy.toString());
        }

        @Override
        public void onConnectionClosed(OnionProxy OnionProxy) {
            System.out.println("[+] Connection closed " + OnionProxy.toString());
        }

        @Override
        public void onExceptionOccured(OnionProxy OnionProxy, Exception ex) {
            System.out.println("[+] Connection dropped " + OnionProxy.toString() + " (Cause: " + ex.getMessage().replaceAll("\r\n", " "));
        }

        @Override
        public void onConnectionPrepared(OnionProxy OnionProxy) {
            System.out.println("[+] Local " + OnionProxy.getProxyType() + " server is ready " + OnionProxy.toString());
        }
    };
    private static Map<String, Integer> mapping = new HashMap<>();

    public static void main(String[] args) throws IOException {
        loadRules();
        startThreads();
    }

    private static void startThreads() {
        mapping.forEach((onion, port) -> {
            Runnable r = () -> {
                new SOCKSProxy(onion, port, eventHandler);
                //new HTTPProxy(onion, port, eventHandler);
            };
            new Thread(r).start();
        });

        Runnable r2 = () -> {
            //Run http proxy
            Variable.httpProxyPort = Variable.getOpenPort();
            Proxy p = new Proxy(Variable.httpProxyPort, eventHandler);
            p.listen();
        };
        new Thread(r2).start();
    }

    private static void loadRules() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(Variable.ruleFiles));
        String line;
        while ((line = br.readLine()) != null) {
            try {
                String[] parts = line.split(":");
                mapping.put(parts[0], Integer.parseInt(parts[1]));
            } catch (Exception ex) {
                System.out.println("[-] Error in loading " + line);
            }
        }
    }
}
