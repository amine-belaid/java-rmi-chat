package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallback extends Remote {
    void recevoirMessage(String message) throws RemoteException ;
}
