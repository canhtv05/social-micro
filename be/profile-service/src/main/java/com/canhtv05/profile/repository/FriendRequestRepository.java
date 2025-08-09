package com.canhtv05.profile.repository;

import java.util.Optional;
import java.util.UUID;

import com.canhtv05.profile.entity.FriendRequest;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepository extends Neo4jRepository<FriendRequest, UUID> {
  @Query("""
        MATCH (fr:friend_request)
        WHERE id(fr) = $id OR fr.id = $id
        OPTIONAL MATCH (sender:user_profile)-[:SENDER]->(fr)
        OPTIONAL MATCH (receiver:user_profile)-[:RECEIVER]->(fr)
        RETURN fr, sender, receiver
      """)
  Optional<FriendRequest> findByIdWithSenderAndReceiver(UUID id);

  @Query("""
      MATCH (s:user_profile {user_id:$senderId})-[:FRIEND_REQUEST]->(r:friend_request)-[:RECEIVER]->(t:user_profile {user_id:$receiverId})
      WHERE r.status = 'PENDING' RETURN count(r) > 0
      """)
  boolean existsPendingBetween(String senderId, String receiverId);
}
