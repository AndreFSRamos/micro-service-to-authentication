# micro-service-to-authentication

## Descrição

Micro serviço desenvolvido com a linguagem Java 18 e o framework Spring Boot 3.2.2, para autenticar usuários por meio de "username" e "password" e também o "refresh token".
O projeto faz uso das bibliotecas JWT, Spring Security e configurações de CORS para garantir a segurança da autenticação, também contém testes de integração usando testcontainers para validar a integridade de cada ponto antes da construção e com uma documentação disponibilizada pelo Swagger-UI.

## Requisitos

### Funcionais
- RF001: Autenticar com username e password.
- RF002: Criar novo usuário.
- RF003: Alterar password.
- RF004: Alterar perfil do usuário.
- RF005: Desabilitar/Habilitar usuário.
- RF006: Buscar todos os usuário.
- RF007: Buscar usuário por ID.
- RF008: Deletar usuário pelo ID.
- RF009: Validar Token.
- RF010: Reautenticar com refresh token.

### Não Funcionais
- RNF001: Somente os endpoints de Autenticação e Reautenticação são liberados para qualquer requisição.
- RNF002: Os perfis de usuário devem ser ADMIN, MANAGER e COMMON, seguindo essa hierarquia.
- RNF003: Adicionar um usuário ADMIN primário, o qual não deve ser possível desabilitar ou deletar nem alterar o perfil.
- RNF004: Somente será possível alterar password, perfil e desabilitar um usuário, com outra usuário que tenha o perfil ADMIN.
- RNF005: Somente os usuário com perfil ADMIN e MANAGER podem criar um novo usuário e efetuar buscas por todos os usuários ou por ID.
- RNF006: Qual novo usuário criado deve ter seu perfil como COMMON.
- RNF007: Todos os endpoints com exceção dos endpoints descritos no RNF001, devem somente aceitar requisições com token valido, origem na requisição iguais às predefinidas e com perfil do usuário valido.
- RNF008: Aplicação deve ter uma documentação para desenvolvedores.

## Recursos Necessários

Para executar a aplicação locamente você vai precisar:

- [JDK 18](https://www.oracle.com/java/technologies/javase/jdk18-archive-downloads.html)
- [Maven 3.9.6](https://maven.apache.org)
- [Docker](https://www.docker.com/) (OBS: O Docker é usado apenas para executar os testes)
- [MySQL 8](https://dev.mysql.com/downloads/mysql/)

## Rodando a aplicação localmente

- Clone o projeto para sua máquina.
- Importe para a sua IDE.
- Faça o download das dependências.
- Com o MySQL já instalado, crie uma database para o projeto, com um mome de usa preferência.
- Configure as variáveis de ambiente da aplicação na sua IDE conforme o arquivo "application-local.yml".
- Faça o RUN da aplicação.
- Para rodar os testes de integração, é necessário que o Docker esteja instalado e rodando.

Durante a inicialização da aplicação, já será adicionado um primeiro usuário ADMIN para iniciar as autenticações. Com esse usuário, você pode criar novos usuários.
As credências desse usuário são:
  - Username: admin
  - Password: admin123
    
OBS: O password pode e deve ser alterado.

Você pode consultar a documentação dos endpoints no Swagger-UI no link "http://localhost:{A PORTA QUE VC DEFINIL}/swagger-ui/index.html

## Deploy da aplicação

Para efetuar o deploy é bem simple, basta executar o comando maven abaixo na pasta raiz do projeto.
```shell
mvn package
```
Porém, há alguns detalhes para verificar antes.
- O Docker deve estar ligado, pois no momento build, todos os testes de integração são validados.
- O comando "mvn package" deve ser executado no terminal da sua IDE, pois as variáveis de ambiente estão nela, e elas serão necessárias para os testes de integração. 
- Para executar o comando, é necessário que as suas variáveis de ambiente do MAVEN e JAVA estejam configuradas corretamente no seu sistema operacional.

Se tudo correr bem, será gerado o arquivo "ms-auth-0.0.1-SNAPSHOT.jar" dentro da pasta "target" na raiz do projeto.

## Rodando a aplicação do Docker:

NECESSÁRIO A ETAPA DE "Deploy da aplicação" TER SIDO CONCLUÍDA COM SUCESSO.

- Inicie o seu Docker.
- Execute o comando abaixo no terminal da sua IDE, na pasta raiz do projeto, para gerar uma imagem.
```docker
docker build -t NOME_QUE_VOCE_QUIZER .
```
- Agora execute o comando a baixo para criar um contêiner, lembre-se de preencher as variáveis de ambiente.
```docker
docker run  --name NOME_DO_CONTAINNER \
            --restart always \
            -e HOST_URL_DATABASE= IP DA DATABASE \
            -e PORT_DATABASE= PORTA DA DATBASE \
            -e DATABASE= NOME DATABASE \
            -e DATA_BASE_USERNAME= USERNAME DATABASE \
            -e DATA_BASE_PASS= PASSWORD DATABASE \
            -e ORIGEN_PATTERNS= TODOS OS LINK DE ACESSO SEPARADO POR "," E SEM ESPAÇOS \
            -e SERVER_PORT= PORTA QUE VAI FICAR DISPONIVEL A APLICAÇÃO \
            -e JWT_SECRET= CHAVE SECRETA PARA GERAÇÂO DO TOKEN \
            -e EXPIRE_LENGTH_ACCESS_TOKEN= TEMPO DE EXPIRAÇÃO DO ACCESS TOKEN EM MILISEGUNDOS \
            -e EXPIRE_LENGTH_REFRESH_TOKEN= TEMPO DE EXPIRAÇÃO DO REFRESH TOKEN EM MILISEGUNDOS \
            -p 3100:3100 <- DEVE SER O MESMMO QUE O SERVER_PORT \
            -d \
            NOME DA IMAGEM QUE VOCÊ INFORMOR NO docker build
```

## Copyright

Desenvolvido por [André Ramos](https://andrefsramos.tech/) | [Linkedin](https://www.linkedin.com/in/andrefsramos-tech/).
 
