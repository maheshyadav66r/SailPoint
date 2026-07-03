package Webservices.WebservicesDemo.Dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Webservices.WebservicesDemo.model.User;
import Webservices.WebservicesDemo.service.UserService;

@RestController
@RequestMapping("/users_table")

public class UserController 
{
	@Autowired
    private UserService service;

    @PostMapping("/singleuser")
    
    public User createUser(@RequestBody User user) 
    {
        return service.saveUser(user);
        
    }
    
    @PostMapping("/listofusers")
    
    public List<User> createUsers(@RequestBody List<User> users)
    {
    	List <User> savedUsers = new ArrayList<>();
    	
    	for(User user : users)
    	{
    		savedUsers.add(service.saveUser(user));
    	}
    	return savedUsers;
    }

    @GetMapping("/list")
    public List<User> getAllUsers() 
    {
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) 
    {
        return service.getUserById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/Pagenaition")
    public Page <User> getUserWithPagenation(
    		@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5")int size)
   
    {
     return service.getUserwithPagiantion(page, size);	
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) 
    {
        return service.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
       boolean deleted =  service.deleteUser(id);
        if (deleted) 
        {
            return ResponseEntity.ok("User with ID " + id + " deleted successfully.");
        } 
        else 
        {
            return ResponseEntity.status(404).body("User with ID " + id + " not found.");
        }
       // System.out.println( id+" deleted one user successfully " );
    }
    @DeleteMapping("/deleteall")
    public String deleteAllUsers()
    {
    	service.deleteAllusers();
    	return "all users deleted Successfully";
    }
    @PutMapping("/{id}/joiningdate")
    public String updatejoiningdate(@PathVariable Long id,@RequestBody User user)
    {
    	User existingUser  = service.getUserById(id).orElse(null);
    	if(existingUser != null)
    	{
    		existingUser.setJoiningdate(user.getJoiningdate());
    		service.saveUser(existingUser);
    		return "joining date is updated" +id;
    	}
    	else
    	{
    		return "user id is not found" +id ;
    	}
    }
    @PutMapping("/bulkupdate")
    public List<User> bulkupdate(@RequestBody List<User> users)
    {
    	return service.updatediffUser(users);
    }
}
