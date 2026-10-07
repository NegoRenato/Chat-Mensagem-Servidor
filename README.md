# 💬 Protocolo Cliente-Servidor (Sistema de Chat)

## Visão geral (Tema)
O projeto consiste em um aplicativo de troca de mensagens baseado na arquitetura cliente-servidor. O Servidor recebe uma requisição do Cliente, que processa as chamadas e retorna a resposta adequada. A comunicação é realizada via sockets utilizando o protocolo TCP, o que garante a confiabilidade e a entrega ordenada dos pacotes (diferentemente do UDP).

  O Servidor espera a conexão de um ou mais cliente na porta porta que lhe foi especificada.

## Funcionalidades
O Servidor possui as seguintes funcionalidades.
Aguardando Conexão: Quando o usuario iniciar o servidor, ele devera especificar o numero da porta, apos especificado o servidor fica esperando de uma ate tres conexao do cliente.
Processar Requisições: Apos um Cliente logado o Servidor processa as seguintes requisições.
  Cadastrar Usuario: Quando o cliente manda a requisição de cadastrar um usuario, o servidor processa a requisição e analisa se o cadastro esta tudo nos conforme         avaliando se o cliente compriu as regras de que, o nome de usuario nao pode conter caracteres especiais e deve ter mais de 5 caracteres e ate 20,se o nome comum        possui de 5 ate 20 caracteres e se a senha e numerica e ate 6 caracteres.

  Login: Quando o cliente manda a requisição de login, o servidor processa a requisição e analisa se esta cumprindo as regras de nome de usuario e senha, e se estiver    tudo certo ele, consulta se aquele usuario existe no sistema, caso não exista uma mensagem de erro e retornada a ele, caso exista o servidor retorna uma mensagem de    sucesso e seu token.

  Consultar Usuario: Quando o cliente manda a requisição de consultar usuario, o servidor processa a requisição e analisa se o token que foi lhe mandado esta ativo no    sistema e se ele corresponde ao usuario, caso nao cumpra as regras uma mensagem de erro e retornado, caso ele cumpra as regras uma mensagem de sucesso e retornado ao   usuarios e seus dados.

  Atualizar Usuario: Quando o cliente manda a requisição de atualizar usuario, o servidor processa a requisição e analisa se o token que foi lhe mandado esta ativo no    sistema e se ele corresponde ao usuario, caso nao cumpra as regras uma mensagem de erro e retornado, caso ele cumpra as regras o servidor analisa se o que o cliente    pediu para alterar cumpre as regras de validação, caso nao cumpra uma mensagem de erro e retornado, caso cumpra e retornado uma mensagem de sucesso e altera aquilo     que o cliente especificou.

  Deletar Usuario: Quando o cliente manda a requisição de excluir usuario, o servidor processa a requisição e analisa se o token que foi lhe mandado esta ativo no        sistema e se ele corresponde ao usuario, caso nao cumpra as regras uma mensagem de erro e retornado, caso ele cumpra as regras o servidor exclui o cadastro do          usuario do sistema.

  Logout: Quando o cliente manda a requisição de logout, o servidor processa a requsição e analisa se token que foi lhe mandado esta ativo no sistema e se ele            corresponde ao usuario, caso nao cumpra as regras uma mensagem de erro e retornado, caso ele cumpra as regras o servidor o usuario e deslogado do sistema e o token     e desativado.

  Enviar Mensagem: Quando o cliente manda a requisição de enviar mensagem, o servidor processa a requisição e analisa se token que foi lhe mandado esta ativo no          sistema e se ele corresponde ao usuario, caso nao cumpra as regras uma mensagem de erro e retornado, caso ele cumpra as regras o servidor envia a mensagem do           remetente para o destinatario.

  Listar usuarios Logados: Quando o cliente manda a requisição de enviar mensagem, o servidor processa a requisição e analisa se token que foi lhe mandado esta ativo     no sistema e se ele corresponde ao usuario, caso nao cumpra as regras uma mensagem de erro e retornado, caso ele cumpra as regras o servidor retorna os usuarios        logados.
