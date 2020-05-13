package org.elsys.fileshare.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepo extends JpaRepository<LinkEntity, Integer> {
    LinkEntity findByToken(String token);
}
