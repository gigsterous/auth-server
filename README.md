# Authentication Service

This is an example authorization server written in Spring Boot 2. It is not meant to be used in production as it is but could be easily modified into a fully functional solution.

This auth server could be extended to provide other resources and act as an resource server or it can be used with an existing resource server and only provide  authentication/authorization by managing user OAuth2 tokens.

If you like this application and have questions or feature requests, feel free to open an issue/PR.

## 🌟 Features

1. Username and password Authentication
2. OAuth2 Access + Refresh Token Provision
3. Registration with e-mail confirmation
4. Basic account management including password change, forgotten password, e-mail change and account deletion
5. Multilingual support
6. Logout including token invalidation
7. Easy SonarQube, Jacoco and Checkstyle intagration for code-quality monitoring
8. Basic unit and integration test coverage with example tests

## 🔧 Installation

This is a [Gradle](https://gradle.org/) project and uses [lombok](https://projectlombok.org/), which needs to be configured in any IDE.

Furthermore, in order to use the e-mail features of this application, the smtp configuration needs to be injected either via `application.properties` or environmental variables. The following variables are required:

- `spring.mail.host`
- `spring.mail.username`
- `spring.mail.password`

## 🚦 Usage

The application can be run using the included Gradle wrapper: `./gradlew bootRun`

Similarly, building the application can be run using `./gradlew clean build`. This step includes also `checkstyle` step which reports all code quality violations and prints them into console and report files. Checkstyle rules can be edited in the configuration file `checkstyle/checkstyle.xml`.

If you wish to use SonarQube for code quality checks and unit test coverage, run `./gradlew sonarqube -Dsonar.host.url=<sonar-url> -Dsonar.login=<sonar-password>`

Spring REST docs are also implemented and basic documentation can be generated using `./gradlew asciidoc`.

In case you have an existing resource server written in Spring Boot and wish to connect it to this authorization server, make sure you have the required Spring Boot security & OAuth2 dependencies and include the following line in your resource server's `application.properties`:

`security.oauth2.resource.userInfoUri=http://localhost:9000/auth/user`

### 🤝 Authentication

To authenticate, call:

```
curl --user 'gigy:secret' \
-d 'grant_type=password&username=john@example.com&password=password' \
-X POST http://localhost:9000/auth/oauth/token
```

A sample response will look like this:

```
{  
   "access_token":"d6ce77cb-28e0-44d1-8d59-ce214822ef4b",
   "token_type":"bearer",
   "refresh_token":"98eb7a5f-5aee-4a96-b173-239401ea78d4",
   "expires_in":3599,
   "scope":"read write"
}
```

### ♻️ Refresh Token

Access Token has a limited validity. Once expired, the Refresh Token can be used in order to obtain a new one without using user's credentials. 

To refresh the Access Token, simply call:

```
curl -i --user 'gigy:secret' \
-d "grant_type=refresh_token&client_id=gigy&client_secret=secret&refresh_token=98eb7a5f-5aee-4a96-b173-239401ea78d4" \
-X POST http://localhost:9000/auth/oauth/token
```

Which will return a new Access Token:

```
{  
   "access_token":"b14d9a0c-450d-4fd9-bd46-d5a70422e4c7",
   "token_type":"bearer",
   "refresh_token":"98eb7a5f-5aee-4a96-b173-239401ea78d4",
   "expires_in":3599,
   "scope":"read write"
}
```

> Notice that the Refresh Token remains the same even after receiving a new Access Token.

### 👤 User Data

To verify that the Access Token works well, we can call the **/user** endpoint method:

```
curl -i -H "Accept: application/json" \
-H "Authorization: Bearer b14d9a0c-450d-4fd9-bd46-d5a70422e4c7" \
-X GET http://localhost:9000/auth/user
```

Which returns:

```
{  
   "id":1,
   "username":"john@example.com",
   "password":"$2a$10$D4OLKI6yy68crm.3imC9X.P2xqKHs5TloWUcr6z5XdOqnTrAK84ri",
   "enabled":true,
   "authorities":[],
   "accountNonExpired":true,
   "accountNonLocked":true,
   "credentialsNonExpired":true
}
```

This method is exposed to all secured services within the whole system. The way it works and returns data is according to Spring Boot standards.

## ⚠️ Error Handling

### 📛 Expired Access Token

When Access Token expires, server will return an error, such as:

```
{  
   "error":"invalid_token",
   "error_description":"Invalid access token: b14d9a0c-450d-4fd9-bd46-d5a70422e4c7"
}
```

This error signals that token is invalid. We can assume that the token has expired and can attempt to get a new one using the Refresh Token.

### ⛔️ Invalid Refresh Token

There are numerous reasons for Refresh Token to be invalidated. When that happens, server will return:

```
{  
   "error":"invalid_grant",
   "error_description":"Invalid refresh token: 98eb7a5f-5aee-4a96-b173-239401ea78d5"
}
```

When this error occurs, user credentials have to be used in order to authenticate the user again.

## 🔖 License

The code is released under the Apache 2.0 license. See [LICENSE](https://github.com/gigsterous/auth-server/blob/master/LICENSE) for details.
