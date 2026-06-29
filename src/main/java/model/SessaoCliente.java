package model;

import java.net.Socket;

public class SessaoCliente {
    private String token;
    private String usuario;
    private String ip;
    private int porta;
    private Socket socket;

    public SessaoCliente(String token, String usuario, String ip, int porta, Socket socket) {
        this.token = token;
        this.usuario = usuario;
        this.ip = ip;
        this.porta = porta;
        this.socket = socket;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
