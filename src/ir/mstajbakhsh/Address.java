package ir.mstajbakhsh;

public class Address {
    String Host;
    int Port;

    public Address(String Host, int Port) {
        this.Host = Host;
        this.Port = Port;
    }

    boolean isOnion() {
        return AddressHelper.getType(Host) == AddressHelper.AddressType.HiddenService;
    }
}
