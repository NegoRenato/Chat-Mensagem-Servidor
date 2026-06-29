package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import model.Usuario;
import model.SessaoCliente;

public class ServidorController {
	private Gson gson = new Gson();
	
	// Lista de usuários compartilhada de forma estática por todas as Threads de conexão
	private static final List<Usuario> usuarios = new ArrayList<>();
	
	// Mapa estático de sessões ativas: Chave é "IP:Porta" do cliente conectado -> Valor é o objeto SessaoCliente
	private static final Map<String, SessaoCliente> sessoesAtivas = new ConcurrentHashMap<>();

	static {
		Usuario admin = new Usuario();
		admin.setNome("admin");
		admin.setUsuario("admin");
		admin.setSenha("123456");
		usuarios.add(admin);
	}

	public ServidorController() {
		// Construtor vazio; inicialização estática dos usuários principais já realizada.
	}
	
	// Helper para obter a chave IP:Porta única de cada conexão
	private String obterChaveConexao(Socket socket) {
		return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
	}
	
	// Método estático de utilidade para limpar sessões de clientes desconectados
	public static void removerSessao(Socket socket) {
		if (socket == null) return;
		String chave = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
		SessaoCliente sessaoRemovida = sessoesAtivas.remove(chave);
		if (sessaoRemovida != null) {
			System.out.println("Sessao limpa automaticamente para o IP:Porta " + chave + " (Usuario: " + sessaoRemovida.getUsuario() + ")");
		}
	}
	
	public void cadastrarUsuario(Socket socket, JsonObject requisicao) {
		String nome = requisicao.has("nome") ? requisicao.get("nome").getAsString() : null;
		String usuario = requisicao.has("usuario") ? requisicao.get("usuario").getAsString() : null;
		String senha = requisicao.has("senha") ? requisicao.get("senha").getAsString() : null;
		boolean validado = true;
		
		try {
			PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
			synchronized (usuarios) {
				for (Usuario usuario1 : usuarios) {
					if(usuario1.getUsuario().equals(usuario)) {
						JsonObject resposta = new JsonObject();
						resposta.addProperty("resposta", "401");
						resposta.addProperty("mensagem", "usuario ja cadastrado no sistema");
						
						System.out.println("usuario ja existe no sistema");
						
						String stringResposta = gson.toJson(resposta);
						System.out.println("enviando para o cliente: " + stringResposta);
						saida.println(stringResposta);
						return;
					}
				}
			}
			
			if(usuario.length() < 5) {
				validado = false;
				JsonObject resposta = new JsonObject();
				resposta.addProperty("resposta", "401");
				resposta.addProperty("mensagem", "nome de usuario deve ter mais que 5 caracteres");
				System.out.println("usuario deve ter mais que 5 letras");
				String stringResposta = gson.toJson(resposta);
				System.out.println("enviando para o cliente: " + stringResposta);
				saida.println(stringResposta);
				return;
			}
			else if(usuario.length() > 20) {
				validado = false;
				JsonObject resposta = new JsonObject();
				resposta.addProperty("resposta", "401");
				resposta.addProperty("mensagem", "nome deve ter menos que 20 caracteres");
				System.out.println("usuario deve ter menos que 20 letras");
				String stringResposta = gson.toJson(resposta);
				System.out.println("enviando para o cliente: " + stringResposta);
				saida.println(stringResposta);
				return;
			}
			else if(!usuario.matches("^[a-zA-Z0-9]+$")) {
				validado = false;
				JsonObject resposta = new JsonObject();
				resposta.addProperty("resposta", "401");
				resposta.addProperty("mensagem", "nome de usuario nao pode conter espaço e caracteres especiais");
				System.out.println("nome de usuario invalido");
				String stringResposta = gson.toJson(resposta);
				System.out.println("enviando para o cliente: " + stringResposta);
				saida.println(stringResposta);
				return;
			}
			else if(nome == null || nome.isEmpty()) {
				validado = false;
				JsonObject resposta = new JsonObject();
				resposta.addProperty("resposta", "401");
				resposta.addProperty("mensagem", "campo nome não pode ficar em branco");
				System.out.println("campo nome nulo");
				String stringResposta = gson.toJson(resposta);
				System.out.println("enviando para o cliente: " + stringResposta);
				saida.println(stringResposta);
				return;
			}
			else if(!senha.matches("^\\d{6}$")) {
				validado = false;
				JsonObject resposta = new JsonObject();
				resposta.addProperty("resposta", "401");
				resposta.addProperty("mensagem", "senha invalida");
				System.out.println("campo senha invalido");
				String stringResposta = gson.toJson(resposta);
				System.out.println("enviando para o cliente: " + stringResposta);
				saida.println(stringResposta);
				return;
			}
			
			if(validado) {
				Usuario novoUsuario = new Usuario();
				novoUsuario.setNome(nome);
				novoUsuario.setUsuario(usuario);
				novoUsuario.setSenha(senha);
				synchronized (usuarios) {
					usuarios.add(novoUsuario);
				}
				
				JsonObject resposta = new JsonObject();
				resposta.addProperty("resposta", "200");
				resposta.addProperty("mensagem", "usuario cadastrado com sucesso");
				System.out.println("requisicao de cadastro aceita");
				consultarUsuarios();
				
				String stringResposta = gson.toJson(resposta);
				System.out.println("enviando para o cliente: " + stringResposta);
				saida.println(stringResposta);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void consultarUsuarios() {
		int contador = 1;
		synchronized (usuarios) {
			for (Usuario usuario : usuarios) {
				System.out.println("usuario[" + contador + "] nome: " + usuario.getNome() + " | usuario: " + usuario.getUsuario() + " | senha: " +
						usuario.getSenha());
				System.out.println("---------------------------------------------------------------------------------------------------------------------");
				contador++;
			}
		}
	}
	
	public void logarUsuario(Socket socket, JsonObject requisicao) {
		String usuario = requisicao.has("usuario") ? requisicao.get("usuario").getAsString() : null;
		String senha = requisicao.has("senha") ? requisicao.get("senha").getAsString() : null;
		boolean validado = true;
		
		try {
			PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
			if(usuario == null || usuario.length() < 5) {
				validado = false;
				JsonObject resposta = new JsonObject();
				resposta.addProperty("resposta", "401");
				resposta.addProperty("mensagem", "nome do usuario deve ter mais que 5 caracteres");
				System.out.println("usuario deve ter mais que 5 letras");
				String stringResposta = gson.toJson(resposta);
				System.out.println("enviando para o cliente: " + stringResposta);
				saida.println(stringResposta);
				return;
			}else if(usuario.length() > 20) {
				validado = false;
				JsonObject resposta = new JsonObject();
				resposta.addProperty("resposta", "401");
				resposta.addProperty("mensagem", "nome do usuario deve ter menos que 20 caracteres");
				System.out.println("usuario deve ter menos que 20 letras");
				String stringResposta = gson.toJson(resposta);
				System.out.println("enviando para o cliente: " + stringResposta);
				saida.println(stringResposta);
				return;
			}else if(senha == null || !senha.matches("^\\d{6}$")) {
				validado = false;
				JsonObject resposta = new JsonObject();
				resposta.addProperty("resposta", "401");
				resposta.addProperty("mensagem", "senha invalida");
				System.out.println("campo senha invalido");
				String stringResposta = gson.toJson(resposta);
				System.out.println("enviando para o cliente: " + stringResposta);
				saida.println(stringResposta);
				return;
			}
			
			if(validado) {
				String chaveConexao = obterChaveConexao(socket);
				
				// Requisito: Limite de 3 usuários logados simultaneamente no servidor
				boolean jaLogadoNestaConexao = sessoesAtivas.containsKey(chaveConexao);
				
				if (!jaLogadoNestaConexao && sessoesAtivas.size() >= 3) {
					JsonObject resposta = new JsonObject();
					resposta.addProperty("resposta", "401");
					resposta.addProperty("mensagem", "Limite de 3 usuarios logados simultaneamente atingido.");
					System.out.println("Falha no login: Limite de 3 usuarios concorrentes atingido.");
					saida.println(gson.toJson(resposta));
					return;
				}
				
				synchronized (usuarios) {
					for (Usuario usuario2 : usuarios) {
						if(usuario2.getUsuario().equals(usuario) && usuario2.getSenha().equals(senha)) {
							System.out.println("usuario existe no sistema");
							System.out.println("verificando se existe um token atrelado ao usuario");
							
							// Verificar se o usuário já não está logado em alguma outra conexão/IP
							boolean jaLogadoEmOutraConexao = false;
							for (SessaoCliente sessao : sessoesAtivas.values()) {
								if (sessao.getUsuario().equals(usuario)) {
									jaLogadoEmOutraConexao = true;
									break;
								}
							}
							
							if (jaLogadoEmOutraConexao) {
								JsonObject resposta = new JsonObject();
								resposta.addProperty("resposta", "401");
								resposta.addProperty("mensagem", "usuario ja esta logado");
								System.out.println("usuario ja logado no sistema");
								saida.println(gson.toJson(resposta));
								return;
							}
							
							if(!usuario2.getUsuario().equals("admin")) {
								JsonObject resposta = new JsonObject();
								String token = "usr_" + usuario;
								
								// Salva a sessão mapeada por IP:Porta para segurança de conexão
								sessoesAtivas.put(chaveConexao, new SessaoCliente(token, usuario, socket.getInetAddress().getHostAddress(), socket.getPort(), socket));
								
								resposta.addProperty("resposta", "200");
								resposta.addProperty("token", token);
								System.out.println("usuario nao possui um token logado");
								
								String stringResposta = gson.toJson(resposta);
								System.out.println("enviando para o cliente: " + stringResposta);
								saida.println(stringResposta);
								return;
							} else if (usuario2.getUsuario().equals("admin")){
								JsonObject resposta = new JsonObject();
								String token = "adm";
								
								// Salva a sessão mapeada por IP:Porta para segurança de conexão
								sessoesAtivas.put(chaveConexao, new SessaoCliente(token, usuario, socket.getInetAddress().getHostAddress(), socket.getPort(), socket));
								
								resposta.addProperty("resposta", "200");
								resposta.addProperty("token", token);
								System.out.println("usuario nao possui um token logado");
								
								String stringResposta = gson.toJson(resposta);
								System.out.println("enviando para o cliente: " + stringResposta);
								saida.println(stringResposta);
								return;
							}
						}
					}
				}
			}
			JsonObject resposta = new JsonObject();
			resposta.addProperty("resposta", "401");
			resposta.addProperty("mensagem", "usuario nao esta cadastrado no sistema");
			System.out.println("usuario nao existe no sistema");
			String stringResposta = gson.toJson(resposta);
			System.out.println("enviando para o cliente: " + stringResposta);
			saida.println(stringResposta);	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void logout(Socket socket, JsonObject requisicao) {
		String token = requisicao.has("token") ? requisicao.get("token").getAsString() : null;
		
		try {
			PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
			JsonObject resposta = new JsonObject();
			
			String chaveConexao = obterChaveConexao(socket);
			SessaoCliente sessao = sessoesAtivas.get(chaveConexao);
			
			if (sessao != null && sessao.getToken().equals(token)) {
				// Remove do mapa de sessões seguras baseadas em IP/Porta
				sessoesAtivas.remove(chaveConexao);
				
				resposta.addProperty("resposta", "200");
				resposta.addProperty("mensagem", "Logout bem sucedido");
				System.out.println("usuario deslogado com sucesso");
				saida.println(gson.toJson(resposta));
				return;
			}
			
			resposta.addProperty("resposta", "401");
			resposta.addProperty("mensagem", "Logout mal sucedido: Token invalido ou usuario nao logado");
			System.out.println("tentativa de logout falhou");
			saida.println(gson.toJson(resposta));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void consultarUsuario(Socket socket, JsonObject requisicao) {
		String token = requisicao.has("token") ? requisicao.get("token").getAsString() : null;
		
		try {
			PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
			JsonObject resposta = new JsonObject();
			
			String chaveConexao = obterChaveConexao(socket);
			SessaoCliente sessao = sessoesAtivas.get(chaveConexao);
			
			if (sessao != null && sessao.getToken().equals(token)) {
				synchronized (usuarios) {
					for (Usuario usuario : usuarios) {
						if (usuario.getUsuario().equals(sessao.getUsuario())) {
							resposta.addProperty("resposta", "200");
							resposta.addProperty("mensagem", "Consulta bem sucedida");
							resposta.addProperty("usuario", usuario.getUsuario());
							resposta.addProperty("nome", usuario.getNome());
							System.out.println("consulta de usuario realizada com sucesso");
							saida.println(gson.toJson(resposta));
							System.out.println("enviando para o cliente: " + gson.toJson(resposta));
							return;
						}
					}
				}
			}
			
			resposta.addProperty("resposta", "401");
			resposta.addProperty("mensagem", "Consulta mal sucedida: Token invalido");
			System.out.println("tentativa de consulta falhou");
			saida.println(gson.toJson(resposta));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void atualizarUsuario(Socket socket, JsonObject requisicao) {
		String token = requisicao.has("token") ? requisicao.get("token").getAsString() : null;
		String nome = requisicao.has("nome") ? requisicao.get("nome").getAsString() : null;
		String senha = requisicao.has("senha") ? requisicao.get("senha").getAsString() : null;
		
		try {
			PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
			JsonObject resposta = new JsonObject();
			
			String chaveConexao = obterChaveConexao(socket);
			SessaoCliente sessao = sessoesAtivas.get(chaveConexao);
			
			if (sessao != null && sessao.getToken().equals(token)) {
				synchronized (usuarios) {
					for (Usuario usuario : usuarios) {
						if (usuario.getUsuario().equals(sessao.getUsuario())) {
							
							if(nome == null || nome.isEmpty()) {
								resposta.addProperty("resposta", "401");
								resposta.addProperty("mensagem", "Atualizacao mal sucedida: campo nome nulo");
								saida.println(gson.toJson(resposta));
								System.out.println("enviando para o cliente: " + gson.toJson(resposta));
								return;
							}
							if(senha == null || !senha.matches("^\\d{6}$")) {
								resposta.addProperty("resposta", "401");
								resposta.addProperty("mensagem", "Atualizacao mal sucedida: senha invalida");
								saida.println(gson.toJson(resposta));
								System.out.println("enviando para o cliente: " + gson.toJson(resposta));
								return;
							}
							
							usuario.setNome(nome);
							usuario.setSenha(senha);
							
							resposta.addProperty("resposta", "200");
							resposta.addProperty("mensagem", "Atualizacao bem sucedida");
							System.out.println("usuario atualizado com sucesso");
							consultarUsuarios();
							
							saida.println(gson.toJson(resposta));
							System.out.println("enviando para o cliente: " + gson.toJson(resposta));
							return;
						}
					}
				}
			}
			
			resposta.addProperty("resposta", "401");
			resposta.addProperty("mensagem", "Atualizacao mal sucedida: Token invalido");
			System.out.println("tentativa de atualizacao falhou");
			saida.println(gson.toJson(resposta));
			System.out.println("enviado para o cliente: " + gson.toJson(resposta));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deletarUsuario(Socket socket, JsonObject requisicao) {
		String token = requisicao.has("token") ? requisicao.get("token").getAsString() : null;
		
		try {
			PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
			JsonObject resposta = new JsonObject();
			
			String chaveConexao = obterChaveConexao(socket);
			SessaoCliente sessao = sessoesAtivas.get(chaveConexao);
			
			if (sessao != null && sessao.getToken().equals(token)) {
				synchronized (usuarios) {
					for (int i = 0; i < usuarios.size(); i++) {
						Usuario usuario = usuarios.get(i);
						if (usuario.getUsuario().equals(sessao.getUsuario())) {
							usuarios.remove(i);
							
							// Limpa sessão ativa
							sessoesAtivas.remove(chaveConexao);
							
							resposta.addProperty("resposta", "200");
							resposta.addProperty("mensagem", "Exclusao bem sucedida");
							System.out.println("usuario excluido com sucesso");
							consultarUsuarios();
							
							saida.println(gson.toJson(resposta));
							System.out.println("enviando para o cliente:" + gson.toJson(resposta));
							return;
						}
					}
				}
			}
			resposta.addProperty("resposta", "401");
			resposta.addProperty("mensagem", "Exclusao mal sucedida: Token invalido");
			System.out.println("tentativa de exclusao falhou");
			saida.println(gson.toJson(resposta));
			System.out.println("enviando para o cliente: " + gson.toJson(resposta));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void consultarTodosUsuarios(Socket socket, JsonObject requisicao) {
	    String token = requisicao.has("token") ? requisicao.get("token").getAsString() : null;
	    
	    try {
	        PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
	        JsonObject resposta = new JsonObject();
	        
	        String chaveConexao = obterChaveConexao(socket);
	        SessaoCliente sessao = sessoesAtivas.get(chaveConexao);

	        if(sessao != null && "adm".equals(token) && sessao.getToken().equals(token)) { 
	            resposta.addProperty("resposta", "200");
	            resposta.addProperty("mensagem", "Consulta bem sucedida");  

	            JsonArray listaUsuariosJson = new JsonArray();
	            
	            synchronized (usuarios) {
	                for (Usuario usuario : usuarios) {
	                    JsonObject userJson = new JsonObject();
	                    userJson.addProperty("usuario", usuario.getUsuario());
	                    userJson.addProperty("nome", usuario.getNome());
	                    listaUsuariosJson.add(userJson);
	                }
	            }

	            resposta.add("lista_usuarios", listaUsuariosJson);
	            
	            System.out.println("consulta de usuario realizada com sucesso");
	            System.out.println("enviando para o cliente: " + gson.toJson(resposta));
	            
	            saida.println(gson.toJson(resposta));
	        } else {
	            resposta.addProperty("resposta", "401");
	            resposta.addProperty("mensagem", "Deve ser adm para consultar a lista");
	            System.out.println("tentativa de consulta adm falhou");
	            System.out.println("enviando para o cliente: " + gson.toJson(resposta));
	            saida.println(gson.toJson(resposta));
	        }
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void consultarUsuarioAdmin(Socket socket, JsonObject requisicao) {
		String token = requisicao.has("token") ? requisicao.get("token").getAsString() : null;
		String usuario = requisicao.has("usuario") ? requisicao.get("usuario").getAsString() : null;
		boolean achou = false;
		
		try {
			PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
			JsonObject resposta = new JsonObject();
			
			String chaveConexao = obterChaveConexao(socket);
			SessaoCliente sessao = sessoesAtivas.get(chaveConexao);
			
			if(sessao != null && "adm".equals(token) && sessao.getToken().equals(token)) {
				synchronized (usuarios) {
					for (Usuario usuario2 : usuarios) {
						if(usuario2.getUsuario().equals(usuario)) {
							achou = true;
							resposta.addProperty("resposta", "200");
							resposta.addProperty("mensagem", "Consulta bem sucedida");
							resposta.addProperty("usuario", usuario2.getUsuario());
							resposta.addProperty("nome", usuario2.getNome());
							System.out.println("consulta de usuario realizada com sucesso");
							
							saida.println(gson.toJson(resposta));
							System.out.println("enviando para o cliente: " + gson.toJson(resposta));
							return;
						}
					}
				}
			}else {
				resposta.addProperty("resposta", "401");
				resposta.addProperty("mensagem", "Consulta mal sucedida: Token invalido");
				System.out.println("tentativa de consulta falhou");
				saida.println(gson.toJson(resposta));
				return;
			}
			
			if(!achou) {
				resposta.addProperty("resposta", "401");
				resposta.addProperty("mensagem", "usuario nao encontrado");
				System.out.println("tentativa de consulta falhou");
				saida.println(gson.toJson(resposta));
				return;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void atualizarUsuarioAdmin(Socket socket, JsonObject requisicao) {
		try {
			String token = requisicao.has("token") ? requisicao.get("token").getAsString() : null;
			String nome = requisicao.has("nome") ? requisicao.get("nome").getAsString() : null;
			String usuario = requisicao.has("usuario") ? requisicao.get("usuario").getAsString() : null;
			String senha = requisicao.has("senha") ? requisicao.get("senha").getAsString() : null;
			PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
			JsonObject resposta = new JsonObject();
			boolean validado = true;
			boolean achou = false;
			
			String chaveConexao = obterChaveConexao(socket);
			SessaoCliente sessao = sessoesAtivas.get(chaveConexao);
			
			if(sessao != null && "adm".equals(token) && sessao.getToken().equals(token)) {
				synchronized (usuarios) {
					for (Usuario usuario2 : usuarios) {
						if(usuario2.getUsuario().equals(usuario)) {
							achou = true;
							System.out.println("usuario encontrado\n");
							System.out.println("comecando processo de validacao");
							if(nome == null || nome.isEmpty()) {
								validado = false;
								resposta.addProperty("resposta", "401");
								resposta.addProperty("mensagem", "campo nome nao pode ficar em branco");
								System.out.println("campo nome nulo");
								String stringResposta = gson.toJson(resposta);
								System.out.println("enviando para o cliente: " + stringResposta);
								saida.println(stringResposta);
								return;
							}
							else if(senha == null || !senha.matches("^\\d{6}$")) {
								validado = false;
								resposta.addProperty("resposta", "401");
								resposta.addProperty("mensagem", "senha invalida");
								System.out.println("campo senha invalido");
								String stringResposta = gson.toJson(resposta);
								System.out.println("enviando para o cliente: " + stringResposta);
								saida.println(stringResposta);
								return;
							}
							
							if(validado) {
								usuario2.setNome(nome);
								usuario2.setSenha(senha);
								
								resposta.addProperty("resposta", "200");
								resposta.addProperty("mensagem", "usuario atualizado com sucesso");
								System.out.println("requisicao de atualizacao aceita");
								consultarUsuarios();
								
								String stringResposta = gson.toJson(resposta);
								System.out.println("enviando para o cliente: " + stringResposta);
								saida.println(stringResposta);
								return;
							}
						}
					}
				}
			}else {
				resposta.addProperty("resposta", "401");
				resposta.addProperty("mensagem", "Consulta mal sucedida: Token invalido");
				System.out.println("tentativa de consulta falhou");
				saida.println(gson.toJson(resposta));
				return;
			}
			
			if(!achou) {
				resposta.addProperty("resposta", "401");
				resposta.addProperty("mensagem", "usuario nao encontrado");
				System.out.println("tentativa de consulta falhou");
				saida.println(gson.toJson(resposta));
				return;
			}
		} catch (Exception e) {
			// handle exception
		}
	}
	
	public void deletarUsuarioAdmin(Socket socket, JsonObject requisicao) {
		try {
			String token = requisicao.has("token") ? requisicao.get("token").getAsString() : null;
			String usuario = requisicao.has("usuario") ? requisicao.get("usuario").getAsString() : null;
			PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
			JsonObject resposta = new JsonObject();
			boolean achou = false;
			
			String chaveConexao = obterChaveConexao(socket);
			SessaoCliente sessao = sessoesAtivas.get(chaveConexao);
			
			if (sessao != null && "adm".equals(token) && sessao.getToken().equals(token)) {
				synchronized (usuarios) {
					for (Usuario usuario2 : usuarios) {
						if(usuario2.getUsuario().equals(usuario)) {
							achou = true;
							usuarios.remove(usuario2);
							
							// Se o usuário excluído estava logado, limpa a sessão dele
							for (Map.Entry<String, SessaoCliente> entrada : sessoesAtivas.entrySet()) {
								if (entrada.getValue().getUsuario().equals(usuario)) {
									sessoesAtivas.remove(entrada.getKey());
									break;
								}
							}
							
							resposta.addProperty("resposta", "200");
							resposta.addProperty("mensagem", "Exclusao bem sucedida");
							System.out.println("usuario excluido com sucesso");
							consultarUsuarios();
							
							saida.println(gson.toJson(resposta));
							System.out.println("enviando para o cliente:" + gson.toJson(resposta));
							return;
						}
					}
				}
			}else {
				resposta.addProperty("resposta", "401");
				resposta.addProperty("mensagem", "Consulta mal sucedida: Token invalido");
				System.out.println("tentativa de consulta falhou");
				saida.println(gson.toJson(resposta));
				return;
			}
			
			if(!achou) {
				resposta.addProperty("resposta", "401");
				resposta.addProperty("mensagem", "usuario nao encontrado");
				System.out.println("tentativa de consulta falhou");
				saida.println(gson.toJson(resposta));
				return;
			}
		} catch (Exception e) {
			// handle exception
		}
	}

	// Requisito: Nova Operação 'enviarMensagem'
	// Parâmetros no JSON de entrada: {"op" : "enviarMensagem", "token": "token", "destinatario": "usuario", "mensagem": "mensagem"}
	// Resposta encaminhada ao destinatário: {"op":"receberMensagem", "remetente": "usuario", "mensagem": "texto_mensagem"}
	public void enviarMensagem(Socket socket, JsonObject requisicao) {
		String token = requisicao.has("token") ? requisicao.get("token").getAsString() : null;
		String destinatario = requisicao.has("destinatario") ? requisicao.get("destinatario").getAsString() : null;
		String mensagem = requisicao.has("mensagem") ? requisicao.get("mensagem").getAsString() : null;
		
		try {
			PrintWriter saidaRemetente = new PrintWriter(socket.getOutputStream(), true);
			JsonObject resposta = new JsonObject();
			
			// 1. Validar se o remetente está devidamente logado nesta conexão IP:Porta
			String chaveConexao = obterChaveConexao(socket);
			SessaoCliente sessaoRemetente = sessoesAtivas.get(chaveConexao);
			
			if (sessaoRemetente == null || !sessaoRemetente.getToken().equals(token)) {
				resposta.addProperty("resposta", "401");
				resposta.addProperty("mensagem", "Envio de mensagem recusado: Remetente nao autenticado ou token invalido");
				System.out.println("Tentativa de envio de mensagem sem autenticacao valida na conexao: " + chaveConexao);
				saidaRemetente.println(gson.toJson(resposta));
				return;
			}
			
			String remetenteUsuario = sessaoRemetente.getUsuario();
			
			// Mensagem para todos
			if ("/todos".equals(destinatario)) {
				int enviados = 0;
				for (SessaoCliente s : sessoesAtivas.values()) {
					// Nao envia pra ele mesmo (opcional, mas geralmente eh bom)
					if (!s.getUsuario().equals(remetenteUsuario)) {
						try {
							PrintWriter saidaDestinatario = new PrintWriter(s.getSocket().getOutputStream(), true);
							JsonObject msgParaDestinatario = new JsonObject();
							msgParaDestinatario.addProperty("op", "receberMensagem");
							msgParaDestinatario.addProperty("remetente", remetenteUsuario);
							msgParaDestinatario.addProperty("mensagem", mensagem);
							saidaDestinatario.println(gson.toJson(msgParaDestinatario));
							enviados++;
						} catch (Exception e) {
							System.err.println("Erro ao enviar para " + s.getUsuario());
						}
					}
				}
				resposta.addProperty("resposta", "200");
				resposta.addProperty("mensagem", "Mensagem enviada para " + enviados + " usuario(s) com sucesso");
				saidaRemetente.println(gson.toJson(resposta));
				return;
			}
			
			// 2. Validar se o usuário destinatário existe no banco do sistema (somente para unicast)
			boolean destinatarioExiste = false;
			synchronized (usuarios) {
				for (Usuario u : usuarios) {
					if (u.getUsuario().equals(destinatario)) {
						destinatarioExiste = true;
						break;
					}
				}
			}
			
			if (!destinatarioExiste) {
				resposta.addProperty("resposta", "401");
				resposta.addProperty("mensagem", "Envio de mensagem recusado: Destinatario nao existe");
				System.out.println("Falha ao enviar msg: Destinatario " + destinatario + " nao existe no sistema");
				saidaRemetente.println(gson.toJson(resposta));
				return;
			}
			
			// 3. Validar se o destinatário está ativamente logado no servidor
			SessaoCliente sessaoDestinatario = null;
			for (SessaoCliente s : sessoesAtivas.values()) {
				if (s.getUsuario().equals(destinatario)) {
					sessaoDestinatario = s;
					break;
				}
			}
			
			if (sessaoDestinatario == null) {
				resposta.addProperty("resposta", "401");
				resposta.addProperty("mensagem", "Envio de mensagem recusado: Destinatario nao esta logado");
				System.out.println("Falha ao enviar msg: Destinatario " + destinatario + " nao esta logado no momento");
				saidaRemetente.println(gson.toJson(resposta));
				return;
			}
			
			// 4. Encaminhar mensagem para o destinatário logado
			try {
				Socket soqueteDestinatario = sessaoDestinatario.getSocket();
				PrintWriter saidaDestinatario = new PrintWriter(soqueteDestinatario.getOutputStream(), true);
				
				JsonObject msgParaDestinatario = new JsonObject();
				msgParaDestinatario.addProperty("op", "receberMensagem");
				msgParaDestinatario.addProperty("remetente", remetenteUsuario);
				msgParaDestinatario.addProperty("mensagem", mensagem);
				
				saidaDestinatario.println(gson.toJson(msgParaDestinatario));
				System.out.println("Mensagem encaminhada com sucesso de [" + remetenteUsuario + "] para [" + destinatario + "]");
				
				// Responder sucesso para o remetente que enviou
				resposta.addProperty("resposta", "200");
				resposta.addProperty("mensagem", "Mensagem enviada com sucesso");
				saidaRemetente.println(gson.toJson(resposta));
				
			} catch (IOException e) {
				System.err.println("Erro ao escrever no Socket do destinatário: " + e.getMessage());
				resposta.addProperty("resposta", "401");
				resposta.addProperty("mensagem", "Erro interno de rede ao enviar para o destinatario");
				saidaRemetente.println(gson.toJson(resposta));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void listarUsuariosLogados(Socket socket, JsonObject requisicao) {
		String token = requisicao.has("token") ? requisicao.get("token").getAsString() : null;
		
		try {
			PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
			JsonObject resposta = new JsonObject();
			
			String chaveConexao = obterChaveConexao(socket);
			SessaoCliente sessaoRemetente = sessoesAtivas.get(chaveConexao);
			
			if (sessaoRemetente == null || !sessaoRemetente.getToken().equals(token)) {
				resposta.addProperty("resposta", "401");
				resposta.addProperty("mensagem", "Erro: usuario nao autenticado ou token invalido");
				System.out.println("Tentativa de listar usuarios sem autenticacao");
				saida.println(gson.toJson(resposta));
				return;
			}
			
			JsonArray listaUsuariosLogadosJson = new JsonArray();
			for (SessaoCliente s : sessoesAtivas.values()) {
				listaUsuariosLogadosJson.add(s.getUsuario());
			}
			
			resposta.addProperty("resposta", "200");
			resposta.add("lista_usuarios", listaUsuariosLogadosJson);
			
			System.out.println("Lista de usuarios logados requisitada por: " + sessaoRemetente.getUsuario());
			saida.println(gson.toJson(resposta));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}