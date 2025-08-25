package com.gialong.facebook.userphoto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserPhotoRepository extends JpaRepository<UserPhoto, UUID> {
    List<UserPhoto> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
}
