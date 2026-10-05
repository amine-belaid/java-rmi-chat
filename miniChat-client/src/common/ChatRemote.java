package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatRemote extends Remote {
    boolean registrer (String username , ClientCallback client) throws RemoteException ;
    void envoyerMessage(String username,String message) throws RemoteException ;
    List<String> getUsers () throws RemoteException ;
    List<String> getHistorique(String username) throws RemoteException;
    void logout(String username) throws RemoteException;

    boolean login(String username, String password) throws RemoteException;
    boolean registerUser(String username, String password) throws RemoteException;
}
