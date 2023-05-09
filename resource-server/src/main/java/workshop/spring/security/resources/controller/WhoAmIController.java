package workshop.spring.security.resources.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/whoami")
public class WhoAmIController {

    private final static Logger log = LoggerFactory.getLogger(WhoAmIController.class);

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object currentUser(Principal principal) {
        log.info("Current principal is '{}'", principal);
        return principal;
    }


    @GetMapping(path = "authenticated", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public Object loggedInUsed(Principal principal) {
        log.info("Current logged in principal is '{}'", principal);
        return principal;
    }

}
