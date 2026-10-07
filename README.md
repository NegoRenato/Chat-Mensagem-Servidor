# 💬 Protocolo Cliente-Servidor (Sistema de Chat)

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![JSON](https://img.shields.io/badge/json-5E5C5C?style=for-the-badge&logo=json&logoColor=white)

## 📌 Visão Geral
Este projeto consiste em um aplicativo de troca de mensagens baseado na arquitetura **cliente-servidor**. 

A comunicação é realizada via **Sockets** utilizando o protocolo **TCP**, o que garante a confiabilidade e a entrega ordenada dos pacotes de dados. O Servidor aguarda conexões em uma porta específica, recebe requisições dos Clientes, processa as chamadas e retorna a resposta adequada.

---

## ⚙️ Funcionalidades do Servidor

O sistema suporta múltiplas operações, gerenciadas pelo Servidor através de validações rigorosas e autenticação por tokens.

### 🔌 Inicialização e Conexão
- **Aguardando Conexão:** Ao iniciar, o usuário define a porta de operação. O Servidor fica então em estado de escuta, suportando de 1 a 3 conexões simultâneas de clientes.

### 🔄 Processamento de Requisições
Após a conexão, o Servidor é capaz de processar as seguintes requisições:

| Requisição | Descrição e Regras de Validação |
| :--- | :--- |
| 📝 **Cadastrar Usuário** | Valida se o *nome de usuário* possui entre 6 e 20 caracteres e não contém caracteres especiais. Verifica se o *nome comum* tem entre 5 e 20 caracteres e se a *senha* é estritamente numérica com até 6 dígitos. |
| 🔐 **Login** | Verifica as credenciais fornecidas. Se o usuário existir e os dados estiverem corretos, retorna uma mensagem de sucesso junto com um **Token de Autenticação**. Caso contrário, retorna um erro. |
| 🔍 **Consultar Usuário** | Exige um Token ativo válido. Se autenticado, retorna os dados cadastrais do usuário correspondente. |
| ✏️ **Atualizar Usuário** | Exige um Token ativo válido. Valida os novos dados enviados de acordo com as regras de cadastro. Se aprovado, atualiza as informações no sistema e retorna sucesso. |
| 🗑️ **Deletar Usuário** | Exige um Token ativo válido. Se autenticado, exclui permanentemente o cadastro do usuário do sistema. |
| 🚪 **Logout** | Exige um Token ativo válido. Desconecta o usuário do sistema e invalida (desativa) o seu Token. |
| ✉️ **Enviar Mensagem** | Exige um Token ativo válido. Processa a mensagem enviada pelo remetente e a encaminha para o destinatário correto. |
| 👥 **Listar Logados** | Exige um Token ativo válido. Retorna uma lista com todos os usuários que estão online no sistema no momento. |

---

## 🚀 Como Executar o Projeto
 ### VsCode
 |Execute o comando > git clone https://github.com/NegoRenato/Chat-Mensagem-Servidor| 
 |apos executar o comando abra o vscode na pasta clonada, abra o terminal e execute o seguinte comando > cd protocolo-cliente-servidor| 
 |depois execute o comando para compilar o codigo > javac *.java| 
 |e então execute o comando para rodar a aplicação > java Servidor|

### 1️⃣ Pré-requisitos
Certifique-se de ter o **Java JDK 11** (ou superior) instalado na sua máquina. Para verificar, abra o terminal e digite:
```bash
java -version
