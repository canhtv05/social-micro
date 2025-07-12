package com.canhtv05.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.canhtv05.user.enity.Role;

public interface RoleRepository extends JpaRepository<Role, String> {

}
