# Sistema Mega 2 - Projeto de Gestão

## Descrição
Este é um sistema de gestão desenvolvido em Java com Spring Boot, Thymeleaf e PostgreSQL.  
O projeto está em desenvolvimento e ainda não está concluído, mas já possui funcionalidades básicas implementadas, principalmente para o perfil de gerente.

---

## Requisitos para rodar o projeto

- Java JDK 17 ou superior instalado
- Maven instalado
- PostgreSQL instalado e em execução
- IDE recomendada: IntelliJ IDEA, Eclipse ou VSCode com suporte a Java/Spring

---

## Configuração do Banco de Dados

1. Certifique-se que o PostgreSQL está rodando na sua máquina.
2. Crie um banco de dados chamado `loja`.
3. O projeto está configurado para conectar ao banco `loja` no endereço `localhost:5432` com o usuário `postgres` e senha `1990`.  
   Caso queira alterar, modifique as configurações no arquivo `ConfiguracaoBancoDeDados.java`.

---

## Como executar

1. Clone o repositório do projeto.
2. Importe o projeto na sua IDE.
3. Execute a aplicação Spring Boot pela classe principal (geralmente `Sistemameg2Application.java`).
4. A aplicação iniciará na porta `8081`.
use o localhost:8081/auth/registro ou login para acessar o sistema.
---

## Acesso ao sistema e testes

- Abra seu navegador e acesse:  
- Você verá a tela de **login (autenticação)**.
- Se quiser criar novos usuários, há a opção de registro disponível na tela de login.

---

## Usuários para testes

| Email                      | Senha | Perfil   | Observações                                   |
|----------------------------|-------|----------|-----------------------------------------------|
| wandersongerente@gmail.com  | 1990  | Gerente  | Pode acessar o painel completo, incluindo listar usuários |
| wandersonvendedor@gmail.com | 1990  | Vendedor | Perfil com menos funcionalidades (em desenvolvimento) |

> **Importante:** Apenas o usuário com perfil de gerente está com a funcionalidade de listar usuários implementada até o momento.

---

## Observações finais

Este projeto está em desenvolvimento contínuo e algumas funcionalidades ainda serão implementadas e melhoradas.

Se houver dúvidas, estou à disposição para ajudar na configuração e testes.

---

Obrigado pela atenção!  
Wanderson  
