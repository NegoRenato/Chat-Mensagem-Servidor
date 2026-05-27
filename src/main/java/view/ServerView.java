package view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import controller.ServidorController;

public class ServerView {
	public static void main(String[] args) {
		Socket clientSocket = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int serverPort = 0;
		
		try {
			System.out.println("Digite a Porta do Servidor");
			serverPort = Integer.parseInt(br.readLine());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.err.println("VALOR INVALIDO PARA PORTA DO SERVER" + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try (ServerSocket serverSocket = new ServerSocket(serverPort);){
			while(true) {
				System.out.println("aguardando conexão");
				
				clientSocket = serverSocket.accept();
				
				System.out.println("----------------------------------------------------------------");
				System.out.println("Conectado com sucesso " + clientSocket.getInetAddress().getHostAddress());
				processarRequest(clientSocket);
			}
		} catch (NumberFormatException e) {
            System.err.println("a porta deve conter apenas numeros" + e.getMessage());
            
		} catch (IOException e) {
            System.err.println("Erro ao atender cliente: " + e.getMessage());
            
		}
	}
	
	private static void processarRequest(Socket clientSocket){
		try {
			ServidorController servidorController = new ServidorController();
			Gson gson = new Gson();
			BufferedReader entrada;
			entrada = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String op = null;
			
			while(true) {
				String stringRequest = entrada.readLine();
				JsonObject request = gson.fromJson(stringRequest, JsonObject.class);
				if (request == null) {
				    System.out.println("Erro: Request recebido é nulo. Ignorando processamento.");
				    return;
				}
				if (request.has("op")) {
					op = request.get("op").getAsString();
				} else {
				    System.out.println("O JSON é válido, mas não contém a chave 'sua_chave'.");
				}
				
				switch (op) {
				
				case "cadastrarUsuario":
					System.out.println("|----------------------------------------------------------------");
					System.out.println("|operação cadastrar recebida com sucesso");
					System.out.println("|" + stringRequest );
					System.out.println("|----------------------------------------------------------------");
					servidorController.cadastrarUsuario(clientSocket, request);
					break;
				
				case "login":
					System.out.println("|----------------------------------------------------------------");
					System.out.println("|operação login recebida com sucesso");
					System.out.println("|" + stringRequest );
					System.out.println("|----------------------------------------------------------------");
					servidorController.logarUsuario(clientSocket, request);
					break;
					
				default:
					break;
				
				case "logout":
					System.out.println("|----------------------------------------------------------------");
					System.out.println("|operação logout recebida com sucesso");
					System.out.println("|" + stringRequest );
					System.out.println("|----------------------------------------------------------------");
					servidorController.logout(clientSocket, request);
					break;
					
				case "consultarUsuario":
					System.out.println("|----------------------------------------------------------------");
					System.out.println("|operação consultar recebida com sucesso");
					System.out.println("|" + stringRequest );
					System.out.println("|----------------------------------------------------------------");
					servidorController.consultarUsuario(clientSocket, request);
					break;
					
				case "atualizarUsuario":
					System.out.println("|----------------------------------------------------------------");
					System.out.println("|operação atualizar recebida com sucesso");
					System.out.println("|" + stringRequest );
					System.out.println("|----------------------------------------------------------------");
					servidorController.atualizarUsuario(clientSocket, request);
					break;
					
				case "deletarUsuario":
					System.out.println("|----------------------------------------------------------------");
					System.out.println("|operação deletar recebida com sucesso");
					System.out.println("|" + stringRequest );
					System.out.println("|----------------------------------------------------------------");
					servidorController.deletarUsuario(clientSocket, request);
					break;
					
				case "consultarUsuariosAdmin":
					System.out.println("|----------------------------------------------------------------");
					System.out.println("|operação consultar usuarios como admnistrador recebida com sucesso");
					System.out.println("|" + stringRequest );
					System.out.println("|----------------------------------------------------------------");
					servidorController.consultarTodosUsuarios(clientSocket, request);
					break;
					
				case "consultarUsuarioAdmin":
					System.out.println("|----------------------------------------------------------------");
					System.out.println("|operação consultar um usuario como admnistrador recebida com sucesso");
					System.out.println("|" + stringRequest );
					System.out.println("|----------------------------------------------------------------");
					servidorController.consultarUsuarioAdmin(clientSocket, request);
					break;
					
				case "atualizarUsuarioAdmin":
					System.out.println("|----------------------------------------------------------------");
					System.out.println("|operação atualizar usuario como admnistrador recebida com sucesso");
					System.out.println("|" + stringRequest );
					System.out.println("|----------------------------------------------------------------");
					servidorController.atualizarUsuarioAdmin(clientSocket, request);
					break;
					
				case "deletarUsuarioAdmin":
					System.out.println("|----------------------------------------------------------------");
					System.out.println("|operação atualizar usuario como admnistrador recebida com sucesso");
					System.out.println("|" + stringRequest );
					System.out.println("|----------------------------------------------------------------");
					servidorController.deletarUsuarioAdmin(clientSocket, request);
					break;
				}
			}
		} catch (IOException e) {
            System.err.println("Erro ao atender cliente: " + e.getMessage());
		}
	}
}
