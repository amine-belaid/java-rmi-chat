package client;

import common.ClientCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientImpl extends UnicastRemoteObject implements ClientCallback {
    private ChatGUI gui ;
    protected ClientImpl(ChatGUI gui) throws RemoteException {
        this.gui=gui;
    }

    @Override
    public void recevoirMessage(String message) throws RemoteException {
        gui.afficherMessage(message);

    }
}
