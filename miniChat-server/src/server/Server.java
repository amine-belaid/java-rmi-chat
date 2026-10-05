package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static void main(String[] args) throws RemoteException, MalformedURLException {
        System.out.println("start server ...");
        try {
            LocateRegistry.createRegistry(9005);
            String url = "rmi://localhost:9005/chat";
            ChatServiceImpl imp = new ChatServiceImpl();
            Naming.rebind(url, imp);

        }catch (RemoteException ex) {
            throw new RuntimeException(ex);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }

    }
}
