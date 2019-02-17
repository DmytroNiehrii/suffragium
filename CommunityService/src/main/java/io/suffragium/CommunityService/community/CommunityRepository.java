package io.suffragium.CommunityService.community;

import io.suffragium.common.entity.community.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface CommunityRepository extends JpaRepository<Community, Long> {
    Optional<Community> findByTitleContaining(String title);
    Optional<Community> findById(@Param("id") Long id);
}
