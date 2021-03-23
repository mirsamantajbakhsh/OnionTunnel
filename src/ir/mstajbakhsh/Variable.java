package ir.mstajbakhsh;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Variable {
    public static String ruleFiles = "rules.txt";

    public static String localTorIP = "127.0.0.1";
    public static int localTorPort = 9150;
    public static int httpProxyPort = 9999;

    public static Map<Address, Address> onion2local = new HashMap<>();
    public static Map<Address, Address> local2onion = new HashMap<>();

    public static int getOpenPort() {
        Random r = new Random();
        int _Port = -1;

        while (_Port == -1) {
            try {
                _Port = r.nextInt(5000) + 10000;
                if (available(_Port)) {
                    break;
                } else {
                    _Port = -1;
                }
            } catch (Exception ex) {
                continue;
            }
        }

        return _Port;
    }

    private static boolean available(int port) {
        try (Socket ignored = new Socket("localhost", port)) {
            return false;
        } catch (IOException ignored) {
            return true;
        }
    }
}
