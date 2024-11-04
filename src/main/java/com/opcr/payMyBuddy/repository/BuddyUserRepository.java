package com.opcr.payMyBuddy.repository;

import com.opcr.payMyBuddy.model.BuddyUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuddyUserRepository extends CrudRepository<BuddyUser,Integer> {

    BuddyUser findByEmail(String email);

    BuddyUser findByUsername(String username);
}
