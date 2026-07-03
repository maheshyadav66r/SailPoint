package Webservices.WebservicesDemo.model;

import java.time.LocalDate;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users_table")
public class User 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	    private Long id;
	    private String fname;
	    private String lname;
	    private String email;
        private LocalDate dateOFBirth;
	    private String location;
	    private LocalDate joiningdate;
	  
		public Long getId() 
	    { 
	    	return id; 
	    }
	    public void setId(Long id) 
	    { 
	    	this.id = id; 
	    }
	    public String getFname() 
	    { 
	    	return fname; 
	    }
	    public void setFname(String fname) 
	    {
	    	this.fname = fname; 
	    }
	    public String getLname() 
	    { 
	    	return lname; 
	    }
	    public void setLname(String lname) 
	    { 
	    	this.lname = lname; 
	    }
	    public String getEmail() 
	    { 
	    	return email; 
	    }
	    public void setEmail(String email) 
	    { 
	    	this.email = email; 
	    }
		public LocalDate getDateOFBirth() 
		{
			return dateOFBirth;
		}
		public void setDateOFBirth(LocalDate dateOFBirth) 
		{
			this.dateOFBirth = dateOFBirth;
		}
		public String getLocation() 
		{
			return location;
		}
		public void setLocation(String location) 
		{
			this.location = location;
		}
		public LocalDate getJoiningdate() 
		{
			return joiningdate;
		}
		public void setJoiningdate(LocalDate joiningdate) 
		{
			this.joiningdate = joiningdate;
		}
	    
}
