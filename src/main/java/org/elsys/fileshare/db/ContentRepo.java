package org.elsys.fileshare.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepo extends JpaRepository<ContentEntity, Integer> {
    ContentEntity findById(int id);
}
