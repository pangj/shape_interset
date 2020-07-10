package bpdts.gov.uk.map.controller;

import bpdts.gov.uk.map.model.User;
import bpdts.gov.uk.map.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * rest api controller for users
 */

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    UserService userService;

    /**
     * {@inheritDoc}
     * An example of a complete uri of this rest api endpoint is http://localhost:8080/api/v1/londoners
     */
    @GetMapping("/londoners")
    public List<User> getAllLondons() {
        return userService.findUsersNearByEsIndex();
    }

}
