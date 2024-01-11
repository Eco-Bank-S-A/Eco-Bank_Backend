package com.ecobank.api.database.repositories;

import com.ecobank.api.database.entities.Chat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface IChatRepository extends CrudRepository<Chat, Long> {


    Optional<Chat> findById(long id);

    @Query("SELECT c FROM Chat c WHERE (c.user1.id = ?1 AND c.user2.id = ?2) OR (c.user1.id = ?2 AND c.user2.id = ?1)")
    Optional<Chat> findByUser1AndUser2(long user1, long user2);

    @Query("SELECT c FROM Chat c WHERE c.user1.id = ?1 OR c.user2.id = ?1")
    ArrayList<Chat> findByUser1(long user1);
}
