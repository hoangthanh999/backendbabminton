package com.badminton.repository.post;

import com.badminton.entity.post.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    // Basic Queries
    Optional<PostTag> findByName(String name);

    Optional<PostTag> findBySlug(String slug);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    // Search
    @Query("SELECT t FROM PostTag t WHERE " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<PostTag> searchTags(@Param("keyword") String keyword);

    // Popular Tags
    @Query("SELECT t FROM PostTag t ORDER BY t.usageCount DESC")
    List<PostTag> findPopularTags(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT t FROM PostTag t WHERE t.usageCount > 0 ORDER BY t.usageCount DESC")
    List<PostTag> findUsedTags();

    // Unused Tags
    @Query("SELECT t FROM PostTag t WHERE t.usageCount = 0")
    List<PostTag> findUnusedTags();

    // Post Tags
    @Query("SELECT t FROM PostTag t JOIN t.posts p WHERE p.id = :postId")
    List<PostTag> findTagsByPost(@Param("postId") Long postId);

    @Query("SELECT COUNT(t.posts) FROM PostTag t WHERE t.id = :tagId")
    long countPostsByTag(@Param("tagId") Long tagId);

    // Update Operations
    @Modifying
    @Query("UPDATE PostTag t SET t.usageCount = t.usageCount + 1 WHERE t.id = :tagId")
    void incrementUsageCount(@Param("tagId") Long tagId);

    @Modifying
    @Query("UPDATE PostTag t SET t.usageCount = t.usageCount - 1 WHERE t.id = :tagId AND t.usageCount > 0")
    void decrementUsageCount(@Param("tagId") Long tagId);

    // Statistics
    @Query("SELECT COUNT(t) FROM PostTag t WHERE t.usageCount > 0")
    long countUsedTags();

    @Query("SELECT SUM(t.usageCount) FROM PostTag t")
    Long sumTotalUsage();
}
