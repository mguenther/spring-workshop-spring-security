package workshop.spring.security.client;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
public class UserInfoController {

    /*
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OAUTH2_USER')")
    public OAuth2User getUser(Principal principal) {
        var token = (OAuth2AuthenticationToken) principal;
        return token.getPrincipal();
    }
    */
}
