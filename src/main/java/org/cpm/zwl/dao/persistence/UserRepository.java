package org.cpm.zwl.dao.persistence;

import org.cpm.zwl.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  public User findByUserId(String userId);
}
