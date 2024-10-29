package com.opcr.payMyBuddy.repository;

import com.opcr.payMyBuddy.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User,Integer> {

    User findByEmail(String email);

    User findByUsername(String username);
}
