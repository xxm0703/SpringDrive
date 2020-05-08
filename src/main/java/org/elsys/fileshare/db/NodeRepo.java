package org.elsys.fileshare.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRepo extends JpaRepository<NodeEntity, Integer> {
    NodeEntity findById(int id);
}
