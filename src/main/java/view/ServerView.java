package view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import controller.ServidorController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerView extends Application {
    
    private TextArea areaTextoLogs;
    private ServerSocket soqueteServidor;
    private Thread threadAceitarConexoes;
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage palcoPrincipal) {
        palcoPrincipal.setTitle("Servidor Chat");

        Label rotuloPorta = new Label("Porta do Servidor:");
        TextField textoPorta = new TextField();
        textoPorta.setPromptText("Ex: 8080");
        
        // Permite apenas números no campo de porta
        textoPorta.textProperty().addListener((observable, valorAntigo, novoValor) -> {
            if (!novoValor.matches("\\d*")) {
                textoPorta.setText(novoValor.replaceAll("[^\\d]", ""));
            }
        });

        Button botaoIniciar = new Button("Iniciar Servidor");
        
        HBox caixaTopo = new HBox(10, rotuloPorta, textoPorta, botaoIniciar);
        caixaTopo.setPadding(new Insets(10));

        areaTextoLogs = new TextArea();
        areaTextoLogs.setEditable(false);
        VBox.setVgrow(areaTextoLogs, Priority.ALWAYS);

        VBox raiz = new VBox(10, caixaTopo, areaTextoLogs);
        raiz.setPadding(new Insets(10));

        // Redirecionar logs
        redirecionarSaidaSistema();

        botaoIniciar.setOnAction(e -> {
            String textoPortaInserida = textoPorta.getText();
            if (textoPortaInserida.isEmpty()) {
                System.err.println("Por favor, digite a porta do servidor.");
                return;
            }
            int porta = Integer.parseInt(textoPortaInserida);
            iniciarServidor(porta);
            botaoIniciar.setDisable(true);
            textoPorta.setDisable(true);
        });

        Scene cena = new Scene(raiz, 600, 400);
        palcoPrincipal.setScene(cena);
        palcoPrincipal.setOnCloseRequest(e -> {
            try {
                if (soqueteServidor != null && !soqueteServidor.isClosed()) {
                    soqueteServidor.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Platform.exit();
            System.exit(0);
        });
        palcoPrincipal.show();
    }

    private void redirecionarSaidaSistema() {
        OutputStream fluxoSaida = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                Platform.runLater(() -> areaTextoLogs.appendText(String.valueOf((char) b)));
            }
            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                final String texto = new String(b, off, len);
                Platform.runLater(() -> areaTextoLogs.appendText(texto));
            }
        };
        System.setOut(new PrintStream(fluxoSaida, true));
        System.setErr(new PrintStream(fluxoSaida, true));
    }

    private void iniciarServidor(int porta) {
        threadAceitarConexoes = new Thread(() -> {
            try {
                soqueteServidor = new ServerSocket(porta);
                System.out.println("Servidor iniciado na porta: " + porta);
                System.out.println("Aguardando conexoes...");

                while (!soqueteServidor.isClosed()) {
                    Socket soqueteCliente = soqueteServidor.accept();
                    System.out.println("----------------------------------------------------------------");
                    System.out.println("Conectado com sucesso " + soqueteCliente.getInetAddress().getHostAddress());
                    
                    // Lança uma nova Thread para tratar esse cliente
                    new Thread(() -> processarRequisicao(soqueteCliente)).start();
                }
            } catch (IOException e) {
                if (soqueteServidor != null && !soqueteServidor.isClosed()) {
                    System.err.println("Erro ao iniciar servidor ou aceitar conexao: " + e.getMessage());
                } else {
                    System.out.println("Servidor encerrado.");
                }
            }
        });
        threadAceitarConexoes.setDaemon(true);
        threadAceitarConexoes.start();
    }

    private static void processarRequisicao(Socket soqueteCliente){
        try {
            ServidorController servidorController = new ServidorController();
            Gson gson = new Gson();
            BufferedReader entrada = new BufferedReader(new InputStreamReader(soqueteCliente.getInputStream()));
            String op = null;
            
            while(true) {
                String stringRequisicao = entrada.readLine();
                if (stringRequisicao == null) {
                    System.out.println("Cliente desconectado " + soqueteCliente.getInetAddress().getHostAddress());
                    ServidorController.removerSessao(soqueteCliente);
                    soqueteCliente.close();
                    break;
                }
                
                JsonObject requisicao = null;
                try {
                    requisicao = gson.fromJson(stringRequisicao, JsonObject.class);
                } catch (Exception e) {
                    System.out.println("Erro ao converter requisicao (JSON Invalido): " + stringRequisicao);
                    continue;
                }
                
                if (requisicao == null) {
                    System.out.println("Erro: Requisicao recebida eh nula. Ignorando processamento.");
                    continue;
                }
                if (requisicao.has("op")) {
                    op = requisicao.get("op").getAsString();
                } else {
                    System.out.println("O JSON recebido não contém a chave 'op'.");
                    continue;
                }
                
                switch (op) {
                
                case "cadastrarUsuario":
                    System.out.println("|----------------------------------------------------------------");
                    System.out.println("|operacao cadastrar recebida com sucesso");
                    System.out.println("|" + stringRequisicao );
                    System.out.println("|----------------------------------------------------------------");
                    servidorController.cadastrarUsuario(soqueteCliente, requisicao);
                    break;
                
                case "login":
                    System.out.println("|----------------------------------------------------------------");
                    System.out.println("|operacao login recebida com sucesso");
                    System.out.println("|" + stringRequisicao );
                    System.out.println("|----------------------------------------------------------------");
                    servidorController.logarUsuario(soqueteCliente, requisicao);
                    break;
                    
                case "logout":
                    System.out.println("|----------------------------------------------------------------");
                    System.out.println("|operacao logout recebida com sucesso");
                    System.out.println("|" + stringRequisicao );
                    System.out.println("|----------------------------------------------------------------");
                    servidorController.logout(soqueteCliente, requisicao);
                    break;
                    
                case "consultarUsuario":
                    System.out.println("|----------------------------------------------------------------");
                    System.out.println("|operacao consultar recebida com sucesso");
                    System.out.println("|" + stringRequisicao );
                    System.out.println("|----------------------------------------------------------------");
                    servidorController.consultarUsuario(soqueteCliente, requisicao);
                    break;
                    
                case "atualizarUsuario":
                    System.out.println("|----------------------------------------------------------------");
                    System.out.println("|operacao atualizar recebida com sucesso");
                    System.out.println("|" + stringRequisicao );
                    System.out.println("|----------------------------------------------------------------");
                    servidorController.atualizarUsuario(soqueteCliente, requisicao);
                    break;
                    
                case "deletarUsuario":
                    System.out.println("|----------------------------------------------------------------");
                    System.out.println("|operacao deletar recebida com sucesso");
                    System.out.println("|" + stringRequisicao );
                    System.out.println("|----------------------------------------------------------------");
                    servidorController.deletarUsuario(soqueteCliente, requisicao);
                    break;
                    
                case "consultarUsuariosAdmin":
                    System.out.println("|----------------------------------------------------------------");
                    System.out.println("|operacao consultar usuarios como admnistrador recebida com sucesso");
                    System.out.println("|" + stringRequisicao );
                    System.out.println("|----------------------------------------------------------------");
                    servidorController.consultarTodosUsuarios(soqueteCliente, requisicao);
                    break;
                    
                case "consultarUsuarioAdmin":
                    System.out.println("|----------------------------------------------------------------");
                    System.out.println("|operacao consultar um usuario como admnistrador recebida com sucesso");
                    System.out.println("|" + stringRequisicao );
                    System.out.println("|----------------------------------------------------------------");
                    servidorController.consultarUsuarioAdmin(soqueteCliente, requisicao);
                    break;
                    
                case "atualizarUsuarioAdmin":
                    System.out.println("|----------------------------------------------------------------");
                    System.out.println("|operacao atualizar usuario como admnistrador recebida com sucesso");
                    System.out.println("|" + stringRequisicao );
                    System.out.println("|----------------------------------------------------------------");
                    servidorController.atualizarUsuarioAdmin(soqueteCliente, requisicao);
                    break;
                    
                case "deletarUsuarioAdmin":
                    System.out.println("|----------------------------------------------------------------");
                    System.out.println("|operacao deletar usuario como admnistrador recebida com sucesso");
                    System.out.println("|" + stringRequisicao );
                    System.out.println("|----------------------------------------------------------------");
                    servidorController.deletarUsuarioAdmin(soqueteCliente, requisicao);
                    break;
                    
                case "enviarMensagem":
                    System.out.println("|----------------------------------------------------------------");
                    System.out.println("|operacao enviarMensagem recebida com sucesso");
                    System.out.println("|" + stringRequisicao );
                    System.out.println("|----------------------------------------------------------------");
                    servidorController.enviarMensagem(soqueteCliente, requisicao);
                    break;

                case "listarUsuariosLogados":
                    System.out.println("|----------------------------------------------------------------");
                    System.out.println("|operacao listarUsuariosLogados recebida com sucesso");
                    System.out.println("|" + stringRequisicao );
                    System.out.println("|----------------------------------------------------------------");
                    servidorController.listarUsuariosLogados(soqueteCliente, requisicao);
                    break;
                    
                default:
                    System.out.println("Operacao desconhecida: " + op);
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao atender cliente " + soqueteCliente.getInetAddress().getHostAddress() + ": " + e.getMessage());
            ServidorController.removerSessao(soqueteCliente);
        }
    }
}
