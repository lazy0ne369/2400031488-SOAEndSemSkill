package soa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import soa.model.Users;

@Repository
public interface UsersRepo extends JpaRepository<Users, Long> {

    Users findByUsername(String username);

}