package com.canhtv05.profile.repository;

import com.canhtv05.profile.entity.UserProfile;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserProfileRepository extends Neo4jRepository<UserProfile, UUID> {

}
