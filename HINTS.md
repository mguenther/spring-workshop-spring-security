# Hints

**Spoiler Alert**

We encourage you to work on the assignment yourself or together with your peers. However, situations may present themselves to you where you're stuck on a specific assignment. Thus, this document contains a couple of hints that ought to guide you through a specific task of the lab assignment.

In any case, don't hesitate to talk to us if you're stuck on a given problem!

## Task #1: Secure the Resource Server

1. The `WebSecurityConfig` does not define any matchers for securing the endpoints yet. In order to allow the public endpoints `/whoami` and the Swagger-UI to be accessible but the rest to be protected we have to adapt the `SecureFilterChain` like this: 

```java
http.authorizeHttpRequests(authz ->
                authz.requestMatchers("/swagger-ui.html", "/swagger-ui/*", "/v3/**", "/whoami").permitAll()
                        .requestMatchers("/**").authenticated()
        )
        .httpBasic()
```

2. The easiest way to do this is to configure a `UserDetailsService` bean with an `InMemoryUserDetailsManager` and static credentials. You could also externalize the credentials, but to complete the task it is good enough to hardcode them.
3. We can add one user per authority to the `InMemoryUserDetailsManager` like this:
```java
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password("{noop}user")
                .authorities("INTERNAL")
                .build();

        UserDetails accounting = User.builder()
                .username("accounting")
                .password("{noop}accounting")
                .authorities("ACCOUNTING")
                .build();

        UserDetails management = User.builder()
                .username("management")
                .password("{noop}management")
                .authorities("MANAGEMENT")
                .build();

        return new InMemoryUserDetailsManager(user, accounting, management);
    }
```
4. We use the `JsonView` feature to just return the subset of information the user is allowed to see. The hierarchy of Views is `Public` -> `Internal` -> `Accounting` -> `Management`. So to get all properties from the API we need to use a user with the `MANAGEMENT` authority.

## Task #2: Configure the Client Application to use credentials

2. We use a `RestTemplate` Bean, that is configured in the `WebClientConfiguration` class - so for any centralized client changes we can customize the Bean.
3. We have several ways to make our `RestTemplate` use the appropriate credentials. We could hard-code the `Authorization` Header - which obviously is not a production ready approach, but enough to pass the task:
```java
    return new RestTemplateBuilder()
            .uriTemplateHandler(new DefaultUriBuilderFactory(config.getResourceUrl()))
            .defaultHeader("Authorization", "Basic base64encodedcredentials")
            .build();
```
To encode your credentials the correct way you can use a utility page like https://www.blitter.se/utils/basic-authentication-header-generator/

Regarding the changes in the UI: We will see more/less data, depending on our highest authority. The Statistics will only be visible with the `MANAGEMENT` authority.

## Task #3: Secure the Client Application via Google OIDC

3. We need to enforce authentication while still permitting access to the login-form as well as the necessary `/oauth2/` callbacks. So we need a `SecurityFilterChain` configuration that looks like this:

```java
http.authorizeHttpRequests((auth) -> auth
  .requestMatchers("/oauth2/**", "/login").permitAll()
  .anyRequest().authenticated()
).oauth2Login(oauth2 ->
  oauth2.loginPage("/login")
    .userInfoEndpoint()
    .userService(new DefaultOAuth2UserService())
);
```

4. We have no custom redirect after a successful login. The default redirect works just fine but has a `/source-url?continue` pattern, which is just ugly. One intended way to change that is to register a success-handler on the oauth2 process and do the redirect ourselves.

## Task #4: Allow authentication/authorization via JWT at the resource server

1.  We need a `SecurityChainFilter` that intercepts the request, reads the `Authorization` header and extracts the information of the JWT to transform them into a valid `SecurityContext`.
2. The necessary Filter might look something like the following snippet. This is a very primitive and stupid implementation that doesn't check anything except that it's a readable JWT. So feel free to improve on this!
```java
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws IOException, ServletException {
        String authorizationHeader = httpServletRequest.getHeader("Authorization");

        if (hasBearerToken(authorizationHeader)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        UsernamePasswordAuthenticationToken token = createToken(authorizationHeader);

        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private boolean hasBearerToken(String authorizationHeader) {
        return authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ");
    }

    private UsernamePasswordAuthenticationToken createToken(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return parseToken(token);
    }


    private UsernamePasswordAuthenticationToken parseToken(String token) {
        JWSObject jws;

        try {
            jws = JWSObject.parse(token);

            // Obviously we usually have to verify the signature against the JWKs of the issuer
            // but for this demo we skip this part
            var claims = JWTClaimsSet.parse(jws.getPayload().toJSONObject());
            return new UsernamePasswordAuthenticationToken(
                    new OAuth2User(claims.getSubject(), claims.getIssuer()),
                    null,
                    scopesToAuthorities(claims.getStringListClaim("scope")));

        } catch (ParseException e) {
            throw new BadCredentialsException("Invalid token", e);
        }
    }

    private List<GrantedAuthority> scopesToAuthorities(List<String> scopes) {
        if (scopes == null) {
            return new ArrayList<>();
        }

        return scopes.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
```

Additionally, we introduced a custom `Principal` to hold the properties we want to use. This is quite common - there's a reason the principal is of type `Object`
```java
public class OAuth2User {

    private final String name;

    private final String issuer;

    public OAuth2User(String name, String issuer) {
        this.name = name;
        this.issuer = issuer;
    }

    public String getName() {
        return name;
    }

    public String getIssuer() {
        return issuer;
    }
}
```

Finally, we need to register the filter with the `SecurityFilterChain`.
```yaml
(...)
.httpBasic()
.and()
.addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
```

4. No hint for that - you are on your own for that final one. 

## That's it! You've done great!

You have completed all assignments. If you have any further questions or need clarification, please don't hesitate to reach out to us. We're here to help.