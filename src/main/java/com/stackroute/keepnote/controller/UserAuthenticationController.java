package com.stackroute.keepnote.controller;

import com.stackroute.keepnote.exception.UserNotFoundException;
import com.stackroute.keepnote.model.User;
import com.stackroute.keepnote.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/*
 * As in this assignment, we are working with creating RESTful web service, hence annotate
 * the class with @RestController annotation.A class annotated with @Controller annotation
 * has handler methods which returns a view. However, if we use @ResponseBody annotation along
 * with @Controller annotation, it will return the data directly in a serialized 
 * format. Starting from Spring 4 and above, we can use @RestController annotation which 
 * is equivalent to using @Controller and @ResposeBody annotation.
 * Annotate class with @SessionAttributes this  annotation is used to store the model attribute in the session.
 */
@RestController
@Scope("session")
public class UserAuthenticationController {

    /*
     * Autowiring should be implemented for the UserService. (Use Constructor-based
     * autowiring) Please note that we should not create any object using the new
     * keyword
     */
    @Autowired
    private UserService userService;

    public UserAuthenticationController(UserService userService) {
        this.userService = userService;
    }

    /*
     * Define a handler method which will authenticate a user by reading the
     * Serialized user object from request body containing the userId and password
     * and validating the same. Post login, the userId will have to be stored into
     * session object, so that we can check whether the user is logged in for all
     * other services handle UserNotFoundException as well. This handler method
     * should return any one of the status messages basis on different situations:
     * 1. 200(OK) - If login is successful. 2. 401(UNAUTHORIZED) - If login is not
     * successful
     *
     * This handler method should map to the URL "/login" using HTTP POST method
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<User> login(@RequestBody User user, HttpServletRequest request) {
        boolean isValidUser = false;
        try {
            isValidUser = userService.validateUser(user.getUserId(), user.getUserPassword());
            request.getSession().setAttribute("loggedInUserId", user.getUserId());
            request.getSession().setAttribute("userpassword", user.getUserPassword());
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        if (isValidUser) {
            return new ResponseEntity<User>(HttpStatus.OK);
        } else {
            return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
        }
    }

	/*
     * Define a handler method which will perform logout. Post logout, the user
	 * session is to be destroyed. This handler method should return any one of the
	 * status messages basis on different situations: 1. 200(OK) - If logout is
	 * successful 2. 400(BAD REQUEST) - If logout has failed
	 * 
	 * This handler method should map to the URL "/logout" using HTTP GET method
	 */

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity<User> logout(HttpServletRequest request) {
        HttpSession httpSession = request.getSession();

        if (httpSession.getAttribute("loggedInUserId") != null) {
            request.getSession().removeAttribute("userid");
            request.getSession().removeAttribute("password");
            return new ResponseEntity<User>(HttpStatus.OK);
        } else {
            return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
        }
    }

}
