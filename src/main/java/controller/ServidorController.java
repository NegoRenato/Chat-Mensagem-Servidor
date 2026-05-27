package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import model.Usuario;

public class ServidorController {
	private Gson gson = new Gson();
	private List<Usuario> usuarios = null;

	public ServidorController() {
		Usuario usuario = new Usuario();
		usuario.setNome("admin");
		usuario.setUsuario("admin");
		usuario.setSenha("123456");
		this.usuarios = new ArrayList<Usuario>();
		this.usuarios.add(usuario);
	}
	
	public void cadastrarUsuario(Socket socket, JsonObject request) {
		String nome = request.has("nome") ? request.get("nome").getAsString() : null;
		String usuario = request.has("usuario") ? request.get("usuario").getAsString() : null;
		String senha = request.has("senha") ? request.get("senha").getAsString() : null;
		boolean validado = true;
		
		try {
			PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
			for (Usuario usuario1 : usuarios) {
				if(usuario1.getUsuario().equals(usuario)) {
					JsonObject response = new JsonObject();
					response.addProperty("resposta", "401");
					response.addProperty("mensagem", "usuario ja cadastrado no sistema");
					
					System.out.println("usuario ja existe no sistema");
					
					String stringResponse = gson.toJson(response);
					
					System.out.println("enviando para o cliente: " + stringResponse);
					
					saida.println(stringResponse);
					
					return;
				}
			}
			
			if(usuario.length() < 5) {
				validado = false;
				JsonObject response = new JsonObject();
				response.addProperty("resposta", "401");
				response.addProperty("mensagem", "nome de usuario deve ter mais que 5 caracteres");
				
				System.out.println("usuario deve ter mais que 5 letras");
				
				String stringResponse = gson.toJson(response);
				
				System.out.println("enviando para o cliente: " + stringResponse);

				
				saida.println(stringResponse);
				return;
			}
			else if(usuario.length() > 20) {
				validado = false;
				JsonObject response = new JsonObject();
				response.addProperty("resposta", "401");
				response.addProperty("mensagem", "nome deve ter menos que 20 caracteres");
				
				System.out.println("usuario deve ter menos que 20 letras");
				
				String stringResponse = gson.toJson(response);
				
				System.out.println("enviando para o cliente: " + stringResponse);
				
				saida.println(stringResponse);
			}
			else if(!usuario.matches("^[a-zA-Z0-9]+$")) {
				validado = false;
				JsonObject response = new JsonObject();
				response.addProperty("resposta", "401");
				response.addProperty("mensagem", "nome de usuario nao pode conter espaço e caracteres especiais");
				
				System.out.println("nome de usuario invalido");
				
				String stringResponse = gson.toJson(response);
				
				System.out.println("enviando para o cliente: " + stringResponse);
				
				saida.println(stringResponse);
			}
			else if(nome.isEmpty()) {
				validado = false;
				JsonObject response = new JsonObject();
				response.addProperty("resposta", "401");
				response.addProperty("mensagem", "campo nome não pode ficar em branco");
				
				System.out.println("campo nome nulo");
				
				String stringResponse = gson.toJson(response);
				
				System.out.println("enviando para o cliente: " + stringResponse);
				
				saida.println(stringResponse);
				return;
			}
			else if(!senha.matches("^\\d{6}$")) {
				validado = false;
				JsonObject response = new JsonObject();
				response.addProperty("resposta", "401");
				response.addProperty("mensagem", "senha invalida");
				
				System.out.println("campo senha invalido");
				
				String stringResponse = gson.toJson(response);
				
				System.out.println("enviando para o cliente: " + stringResponse);
				
				saida.println(stringResponse);
				return;
			}
			
			if(validado) {
				Usuario novoUsuario = new Usuario();
				
				novoUsuario.setNome(nome);
				novoUsuario.setUsuario(usuario);
				novoUsuario.setSenha(senha);
				this.usuarios.add(novoUsuario);
				
				JsonObject response = new JsonObject();
				response.addProperty("resposta", "200");
				response.addProperty("mensagem", "usuario cadastrado com sucesso");
				
				System.out.println("requisição de cadastro aceita");
				consultarUsuarios();
				
				String stringResponse = gson.toJson(response);
				
				System.out.println("enviando para o cliente: " + stringResponse);
				
				saida.println(stringResponse);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void consultarUsuarios() {
		int contador = 1;
		for (Usuario usuario : usuarios) {
			System.out.println("usuario[" + contador + "] " + "nome: " + usuario.getNome() + "| usuario: " + usuario.getUsuario() + " | senha: " +
					usuario.getSenha() + " | token: " + usuario.getToken());
			System.out.println("---------------------------------------------------------------------------------------------------------------------");
			contador++;
		}
	}
	
	public void logarUsuario(Socket socket, JsonObject request) {
		String usuario = request.has("usuario") ? request.get("usuario").getAsString() : null;
		String senha = request.has("senha") ? request.get("senha").getAsString() : null;
		boolean validado = true;
		
		try {
			PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
			if(usuario.length() < 5) {
				validado = false;
				JsonObject response = new JsonObject();
				response.addProperty("resposta", "401");
				response.addProperty("mensagem", "nome do usuario deve ter mais que 5 caracteres");
				
				System.out.println("usuario deve ter mais que 5 letras");
				
				String stringResponse = gson.toJson(response);
				
				System.out.println("enviando para o cliente: " + stringResponse);
				
				saida.println(stringResponse);
				
				return;
				
			}else if(usuario.length() > 20) {
				validado = false;
				JsonObject response = new JsonObject();
				response.addProperty("resposta", "401");
				response.addProperty("mensagem", "nome do usuario deve ter menos que 20 caracteres");
				
				System.out.println("usuario deve ter menos que 20 letras");
				
				String stringResponse = gson.toJson(response);
				
				System.out.println("enviando para o cliente: " + stringResponse);
				
				saida.println(stringResponse);
				return;
				
			}else if(!senha.matches("^\\d{6}$")) {
				validado = false;
				JsonObject response = new JsonObject();
				response.addProperty("resposta", "401");
				response.addProperty("mensagem", "senha invalida");
				
				System.out.println("campo senha invalido");
				
				String stringResponse = gson.toJson(response);
				
				System.out.println("enviando para o cliente: " + stringResponse);
				
				saida.println(stringResponse);
				return;
			}
			if(validado) {
				for (Usuario usuario2 : usuarios) {
					if(usuario2.getUsuario().equals(usuario) && usuario2.getSenha().equals(senha)) {
						System.out.println("usuario existe no sistema");
						System.out.println("verificando se existe um token atrelado ao usuario");
						if(usuario2.getToken() == null && usuario2.getUsuario() != "admin") {
							JsonObject response = new JsonObject();
							String token = "usr_" + usuario;
							usuario2.setToken(token);
							
							response.addProperty("resposta", "200");
							response.addProperty("token", token);
							
							System.out.println("usuario nao possui um token logado");
							
							String stringResponse = gson.toJson(response);
							
							System.out.println("enviando para o clienter " + stringResponse);
							
							saida.println(stringResponse);
							
							return;
						}else if (usuario2.getToken() == null && usuario2.getUsuario().equals("admin")){
							JsonObject response = new JsonObject();
							String token = "adm";
							usuario2.setToken(token);
							
							response.addProperty("resposta", "200");
							response.addProperty("token_admin", token);
							
							System.out.println("usuario nao possui um token logado");
							
							String stringResponse = gson.toJson(response);
							
							System.out.println("enviando para o cliente" + stringResponse);
							
							saida.println(stringResponse);
							
							return;
						}else
						{
							JsonObject response = new JsonObject();
							response.addProperty("resposta", "401");
							response.addProperty("mensagem", "usuario ja esta logado");
							
							System.out.println("usuario logado no sistema");
							
							String stringResponse = gson.toJson(response);
							
							System.out.println("enviando para o cliente: " + stringResponse);
							
							saida.println(stringResponse);
							
							return;
						}
					}
				}
			}
		JsonObject response = new JsonObject();
		response.addProperty("resposta", "401");
		response.addProperty("mensagem", "usuario nao esta cadastrado no sistema");
				
		System.out.println("usuario nao existe no sistema");
				
		String stringResponse = gson.toJson(response);
				
		System.out.println("enviando para o cliente: " + stringResponse);
				
		saida.println(stringResponse);	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void logout(Socket socket, JsonObject request) {
		String token = request.has("token") ? request.get("token").getAsString() : null;
		
		try {
			PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
			JsonObject response = new JsonObject();
			
			for (Usuario usuario : usuarios) {
				if (usuario.getToken() != null && usuario.getToken().equals(token)) {
					usuario.setToken(null); // Remove o token para deslogar
					
					response.addProperty("resposta", "200");
					response.addProperty("mensagem", "Logout bem sucedido");
					System.out.println("usuario deslogado com sucesso");
					
					System.out.println("envinado para o cliente: " + gson.toJson(response));
					
					saida.println(gson.toJson(response));
					return;
				}
			}
			
			response.addProperty("resposta", "401");
			response.addProperty("mensagem", "Logout mal sucedido: Token invalido ou usuario nao logado");
			System.out.println("tentativa de logout falhou");
			saida.println(gson.toJson(response));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void consultarUsuario(Socket socket, JsonObject request) {
		String token = request.has("token") ? request.get("token").getAsString() : null;
		
		try {
			PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
			JsonObject response = new JsonObject();
			
			for (Usuario usuario : usuarios) {
				if (usuario.getToken() != null && usuario.getToken().equals(token)) {
					response.addProperty("resposta", "200");
					response.addProperty("mensagem", "Consulta bem sucedida");
					response.addProperty("usuario", usuario.getUsuario());
					response.addProperty("nome", usuario.getNome());
					System.out.println("consulta de usuario realizada com sucesso");
					
					saida.println(gson.toJson(response));
					
					System.out.println("enviando para o cliente: " + gson.toJson(response));
					
					return;
				}
			}
			
			response.addProperty("resposta", "401");
			response.addProperty("mensagem", "Consulta mal sucedida: Token invalido");
			System.out.println("tentativa de consulta falhou");
			saida.println(gson.toJson(response));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void atualizarUsuario(Socket socket, JsonObject request) {
		String token = request.has("token") ? request.get("token").getAsString() : null;
		String nome = request.has("nome") ? request.get("nome").getAsString() : null;
		String senha = request.has("senha") ? request.get("senha").getAsString() : null;
		
		try {
			PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
			JsonObject response = new JsonObject();
			
			for (Usuario usuario : usuarios) {
				if (usuario.getToken() != null && usuario.getToken().equals(token)) {
					
					// Validações parecidas com as de cadastro
					if(nome == null || nome.isEmpty()) {
						response.addProperty("resposta", "401");
						response.addProperty("mensagem", "Atualizacao mal sucedida: campo nome nulo");
						saida.println(gson.toJson(response));
						
						System.out.println("enviando para o cliente: " + gson.toJson(response));
						return;
					}
					if(senha == null || !senha.matches("^\\d{6}$")) {
						response.addProperty("resposta", "401");
						response.addProperty("mensagem", "Atualizacao mal sucedida: senha invalida");
						saida.println(gson.toJson(response));
						
						System.out.println("enviando para o cliente: " + gson.toJson(response));
						return;
					}
					
					usuario.setNome(nome);
					usuario.setSenha(senha);
					
					response.addProperty("resposta", "200");
					response.addProperty("mensagem", "Atualizacao bem sucedida");
					System.out.println("usuario atualizado com sucesso");
					consultarUsuarios();
					
					saida.println(gson.toJson(response));
					System.out.println("enviando para o cliente: " + gson.toJson(response));
					return;
				}
			}
			
			response.addProperty("resposta", "401");
			response.addProperty("mensagem", "Atualizacao mal sucedida: Token invalido");
			System.out.println("tentativa de atualizacao falhou");
			saida.println(gson.toJson(response));
			System.out.println("enviado para o cliente: " + gson.toJson(response));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deletarUsuario(Socket socket, JsonObject request) {
		String token = request.has("token") ? request.get("token").getAsString() : null;
		
		try {
			PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
			JsonObject response = new JsonObject();
			
			for (int i = 0; i < usuarios.size(); i++) {
				Usuario usuario = usuarios.get(i);
				if (usuario.getToken() != null && usuario.getToken().equals(token)) {
					usuarios.remove(i);
					
					response.addProperty("resposta", "200");
					response.addProperty("mensagem", "Exclusao bem sucedida");
					System.out.println("usuario excluido com sucesso");
					consultarUsuarios();
					
					saida.println(gson.toJson(response));
					
					System.out.println("enviando para o cliente:" + gson.toJson(response));
					return;
				}
			}
			response.addProperty("resposta", "401");
			response.addProperty("mensagem", "Exclusao mal sucedida: Token invalido");
			System.out.println("tentativa de exclusao falhou");
			saida.println(gson.toJson(response));
			System.out.println("enviando para o cliente: " + gson.toJson(response));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void consultarTodosUsuarios(Socket socket, JsonObject request) {
	    String token = request.has("token_admin") ? request.get("token_admin").getAsString() : null;
	    
	    try {
	        PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
	        JsonObject response = new JsonObject();

	        if("adm".equals(token)) { 
	            response.addProperty("resposta", "200");
	            response.addProperty("mensagem", "Consulta bem sucedida");  

	            JsonArray listaUsuariosJson = new JsonArray();
	            
	            for (Usuario usuario : usuarios) {
	                JsonObject userJson = new JsonObject();
	                userJson.addProperty("usuario", usuario.getUsuario());
	                userJson.addProperty("nome", usuario.getNome());
	                listaUsuariosJson.add(userJson);
	            }

	            response.add("lista_usuarios", listaUsuariosJson);
	            
	            System.out.println("consulta de usuario realizada com sucesso");
	            System.out.println("enviando para o cliente: " + gson.toJson(response));
	            
	            saida.println(gson.toJson(response));
	        } else {
	            response.addProperty("resposta", "401");
	            response.addProperty("mensagem", "Deve ser adm para consultar a lista");
	            System.out.println("tentativa de consulta adm falhou");
	            
	            System.out.println("enviando para o cliente: " + gson.toJson(response));
	            saida.println(gson.toJson(response));
	        }
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void consultarUsuarioAdmin(Socket socket, JsonObject request) {
		String token = request.has("token_admin") ? request.get("token_admin").getAsString() : null;
		String usuario = request.has("usuario") ? request.get("usuario").getAsString() : null;
		boolean achou = false;
		
		try {
			PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
			JsonObject response = new JsonObject();
			if("adm".equals(token)) {
				for (Usuario usuario2 : usuarios) {
					if(usuario2.getUsuario().equals(usuario)) {
						achou = true;
						response.addProperty("resposta", "200");
						response.addProperty("mensagem", "Consulta bem sucedida");
						response.addProperty("usuario", usuario2.getUsuario());
						response.addProperty("nome", usuario2.getNome());
						System.out.println("consulta de usuario realizada com sucesso");
						
						saida.println(gson.toJson(response));
						
						System.out.println("enviando para o cliente: " + gson.toJson(response));
						
						return;
					}
				}
			}else {
				response.addProperty("resposta", "401");
				response.addProperty("mensagem", "Consulta mal sucedida: Token invalido");
				System.out.println("tentativa de consulta falhou");
				saida.println(gson.toJson(response));
				
				return;
			}
			
			if(!achou) {
				response.addProperty("resposta", "401");
				response.addProperty("mensagem", "usuario nao encontrado");
				System.out.println("tentativa de consulta falhou");
				saida.println(gson.toJson(response));
				
				return;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void atualizarUsuarioAdmin(Socket socket, JsonObject request) {
		try {
			String token = request.has("token_admin") ? request.get("token_admin").getAsString() : null;
			String nome = request.has("nome") ? request.get("nome").getAsString() : null;
			String usuario = request.has("usuario") ? request.get("usuario").getAsString() : null;
			String senha = request.has("senha") ? request.get("senha").getAsString() : null;
			PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
			JsonObject response = new JsonObject();
			boolean validado = true;
			boolean achou = true;
			
			if("adm".equals(token)) {
				for (Usuario usuario2 : usuarios) {
					if(usuario2.getUsuario().equals(usuario)) {
						System.out.println("usuario encontrado\n");
						System.out.println("começando processo de validação");
						if(nome.isEmpty()) {
							validado = false;
							response.addProperty("resposta", "401");
							response.addProperty("mensagem", "campo nome não pode ficar em branco");
							
							System.out.println("campo nome nulo");
							
							String stringResponse = gson.toJson(response);
							
							System.out.println("enviando para o cliente: " + stringResponse);
							
							saida.println(stringResponse);
							return;
						}
						else if(!senha.matches("^\\d{6}$")) {
							validado = false;
							response.addProperty("resposta", "401");
							response.addProperty("mensagem", "senha invalida");
							
							System.out.println("campo senha invalido");
							
							String stringResponse = gson.toJson(response);
							
							System.out.println("enviando para o cliente: " + stringResponse);
							
							saida.println(stringResponse);
							return;
						}
						
						if(validado) {
							usuario2.setNome(nome);
							usuario2.setSenha(senha);
							
							response.addProperty("resposta", "200");
							response.addProperty("mensagem", "usuario atualizado com sucesso");
							
							System.out.println("requisição de atualização aceita");
							consultarUsuarios();
							
							String stringResponse = gson.toJson(response);
							
							System.out.println("enviando para o cliente: " + stringResponse);
							
							saida.println(stringResponse);
							
							return;
						}
					}
				}
			}else {
				response.addProperty("resposta", "401");
				response.addProperty("mensagem", "Consulta mal sucedida: Token invalido");
				System.out.println("tentativa de consulta falhou");
				saida.println(gson.toJson(response));
				
				return;
			}
			
			if(!achou) {
				response.addProperty("resposta", "401");
				response.addProperty("mensagem", "usuario nao encontrado");
				System.out.println("tentativa de consulta falhou");
				saida.println(gson.toJson(response));
				
				return;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void deletarUsuarioAdmin(Socket socket, JsonObject request) {
		try {
			String token = request.has("token_admin") ? request.get("token_admin").getAsString() : null;
			String usuario = request.has("usuario") ? request.get("usuario").getAsString() : null;
			PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
			JsonObject response = new JsonObject();
			boolean achou = true;
			
			if ("adm".equals(token)) {
				for (Usuario usuario2 : usuarios) {
					if(usuario2.getUsuario().equals(usuario)) {
						usuarios.remove(usuario2);
						
						response.addProperty("resposta", "200");
						response.addProperty("mensagem", "Exclusao bem sucedida");
						System.out.println("usuario excluido com sucesso");
						consultarUsuarios();
						
						saida.println(gson.toJson(response));
						
						System.out.println("enviando para o cliente:" + gson.toJson(response));
						return;
					}
				}
			}else {
				response.addProperty("resposta", "401");
				response.addProperty("mensagem", "Consulta mal sucedida: Token invalido");
				System.out.println("tentativa de consulta falhou");
				saida.println(gson.toJson(response));
				
				return;
			}
			
			if(!achou) {
				response.addProperty("resposta", "401");
				response.addProperty("mensagem", "usuario nao encontrado");
				System.out.println("tentativa de consulta falhou");
				saida.println(gson.toJson(response));
				
				return;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
