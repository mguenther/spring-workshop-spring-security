# Lab Assignment

Your task is to implement the security constraints necessary for our service landscape. The goal is to protect all critical resources on the resource-server.

Take a look at http://localhost:9090/ to see what the Client Application is showing - it is our goal to see all available data after these tasks!

## Task #1: Secure the Resource Server

Our first task will be to protect all resources that need protection via Spring Security with Basic Auth. There are two public endpoints to - we still want to access `/whoami` as well as the OpenAPI UI if we're not authenticated. 

1. Make sure that our endpoints under `/employee` are protected, while the OpenAPI UI and the `/whoami` endpoint is still available for unauthenticated users. The necessary dependencies are already included.
2. Add users to the Resource Server with the appropriate authorities, so the endpoints are usable again. Test these credentials via the browser and/or the HTTP client.
3. Make sure that we have one user per authority. The authorities are `INTERNAL`, `ACCOUNTING` and `MANAGEMENT`.
4. What happens if a user has more than one (or none) of those authorities? Find out why the API behaves the way it does via browser/HTTP client.

## Task #2: Configure the Client Application to use credentials

With the resource server secured we will no longer be able to show any data in the Client Application. To show the data again we need to configure and use credentials as part of the Client Application.

1. At first make sure that you can access the UI of the Client Application at http://localhost:9090/ 
2. Take a look at the `EmployeeViewController` - how does the Client Application communicate with the Resource Server? 
3. Make the client use one of the credentials introduced in Task 1.2/1.3. What do we expect to see in the Client Application and why?

## Task #3: Secure the Client Application via Google OIDC

The Client Application should now again be able to show the employee data - ideally all data should be visible ... except the logged-in user.  We now want to ensure that only users with a Google or GitHub Account can access the Client Application. We already provided a login entry point in the application listening on `/login`

1. We need an additional dependency in the Client to use the OIDC of Google, the coordinates of the dependency are

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency> 
```

2. With the dependency added the clients need to be configured. The following snippet shows how to register the OAuth 2 clients:

```yaml
spring:
  thymeleaf:
    prefix: classpath:/views/
    suffix: .html
  security:
    oauth2:
      client:
        registration:
          google:
            redirect-uri: http://localhost:9090/login/oauth2/code/google
            client-id: 735658187058-2uo7evmpainngptpc46i77j2g93t2jt5.apps.googleusercontent.com
            client-secret: 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
            scope:
              - email
              - profile
          github:
            redirect-uri: http://localhost:9090/login/oauth2/code/github
            client-id: 64a091a8edc54ea11a0d
            client-secret: a37d47562d152b400bfe7cfd0bf9a742a2c4cea7
            scope:
              - user:email
              - read:user
```
3. Access to the application still works without authentication - which is no wonder, because we never told Spring to actually secure the endpoints. Time to change that! We want to lock down **everything** - except the Login-URL as well as everything in/under the path `/oauth2/`.

Take the snippet below, add it to the appropriate `@Bean` and fill in the missing details.

```java
http.authorizeHttpRequests((auth) -> auth
  // TODO
).oauth2Login(oauth2 ->
  oauth2.loginPage("/login")
    .userInfoEndpoint()
    .userService(new DefaultOAuth2UserService())
);
```

4. If your client is up and running, and you're able to log in via Google/GitHub: does anything seem odd about the redirect-uri? Any ideas what is happening or how to change that?

## Task #4: Allow authentication/authorization via JWT at the resource server

IT-Sec called - they are not thrilled about our Basic Auth integration in the resource server. But the integration of the oauth2 client in the Client Application gave them an idea: let's allow services to authenticate themselves with an JWT. We have an authorization-server standing around anyway ... so why not just use it. Caveat: Of course several systems already integrated with the resource server via Basic Auth. So both authentication mechanisms **must** work in parallel!

1. Take a look at the `token.http` file in the `authorization-server` module. Execute the request, inspect the token at http://jwt.io - how can we leverage that?
2. Configure the Resource Server to accept JWTs. All necessary dependencies are already in place. Use a custom `OncePerRequestFilter` to intercept the request, search for a `Bearer` token in the `Authorization` header and transform token with its scopes into a `Principal`. You don't need to verify the signature of the JWTs or check for expiration to complete this task. But if you want to ... be our guest! To parse a JWT you can use nimbus (See `JWSObject.parse(String token)`) - Nimbus is already part of the application.
3. Try different scopes/tokens with the service and make sure that the results change as expected. You can use the helper requests as given in the `resources.http` file in the `resource-server` module.
4. Last - but not least - we need to adapt our `client-application` module to fetch a token and use it at the Resource Server. Formulate a plan how this could be done without hard-coding a token ... obviously.

## That's it! You've done great!

You have completed all assignments. If you have any further questions or need clarification, please don't hesitate to reach out to us. We're here to help.