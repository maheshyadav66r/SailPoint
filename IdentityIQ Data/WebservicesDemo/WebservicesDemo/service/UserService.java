package Webservices.WebservicesDemo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import Webservices.WebservicesDemo.model.User;
import Webservices.WebservicesDemo.repository.UserRepository;
@Service
public class UserService 
{
	@Autowired
	private UserRepository repo;

    public User saveUser(User user) 
    {
        return repo.save(user);
    }

    public List<User> getAllUsers() 
    {
        return repo.findAll();
    }

    public Optional<User> getUserById(Long id) 
    {
        return repo.findById(id);
    }
    public Page<User> getUserwithPagiantion(int page,int size)
    {
    	Pageable pageable = PageRequest.of(page, size);
    	return  repo.findAll(pageable);
    }

    public User updateUser(Long id, User updatedUser) 
    {
        return repo.findById(id).map(user -> 
        {
            user.setFname(updatedUser.getFname());
            user.setLname(updatedUser.getLname());
            user.setEmail(updatedUser.getEmail());
            user.setDateOFBirth(updatedUser.getDateOFBirth());
            user.setLocation(updatedUser.getLocation());
            user.setJoiningdate(updatedUser.getJoiningdate());
            return repo.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }
   
    @PutMapping("/bulkupdate")  
    public List<User> updatediffUser(@RequestBody List<User> users) 
    {
    	List savedUsers = new ArrayList<>();
    	
    	for(User updatedUser:users)
    	{
    		User existing = repo.findById(updatedUser.getId()).orElse(null);
    		if (existing != null) 
    	  {
        	if (updatedUser.getFname() != null) existing.setFname(updatedUser.getFname());
        	if (updatedUser.getLname() != null) existing.setLname(updatedUser.getLname());
        	if (updatedUser.getEmail() != null) existing.setEmail(updatedUser.getEmail());
        	if (updatedUser.getLocation() != null) existing.setLocation(updatedUser.getLocation());
        	if (updatedUser.getDateOFBirth() != null) existing.setDateOFBirth(updatedUser.getDateOFBirth());
        	if (updatedUser.getJoiningdate() != null) existing.setJoiningdate(updatedUser.getJoiningdate());

            savedUsers.add(repo.save(existing));
         
    	  }
       }
    	return savedUsers;
    } 
    public boolean deleteUser(Long id) 
    {
    	if(repo.existsById(id))
    	{
    		repo.deleteById(id);
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    public void deleteAllusers()
    {
    	repo.deleteAll();
    }
    
}
