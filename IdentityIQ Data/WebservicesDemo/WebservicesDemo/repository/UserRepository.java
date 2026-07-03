package Webservices.WebservicesDemo.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import Webservices.WebservicesDemo.model.User;

public interface UserRepository extends JpaRepository<User,Long>
{

}
