package com.canhtv05.profile.repository;

import com.canhtv05.profile.entity.UserProfile;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends Neo4jRepository<UserProfile, UUID> {

    Optional<UserProfile> findByUserId(String userId);

    Optional<UserProfile> findByEmail(String email);

    @Query("""
                MATCH (sender:user_profile)-[:SENDER]->(fr:friend_request {id: $requestId})
                RETURN sender
            """)
    Optional<UserProfile> findSenderByFriendRequestId(@Param("requestId") UUID requestId);

}
