package org.example.client_processing.repository;

import org.example.client_processing.enums.client.DocumentType;
import org.example.client_processing.model.BlacklistRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BlacklistRegistryRepository extends JpaRepository<BlacklistRegistry, Long> {

    Optional<BlacklistRegistry> findByDocumentId(String documentId);

    boolean existsByDocumentId(String documentId);
    
    /**
     * Проверить, заблокирован ли документ (активная блокировка)
     * Учитывает даты начала и окончания блокировки
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
           "FROM BlacklistRegistry b " +
           "WHERE b.documentType = :documentType AND b.documentId = :documentId " +
           "AND b.blacklistedAt <= :currentTime " +
           "AND (b.blacklistExpirationDate IS NULL OR b.blacklistExpirationDate > :currentTime)")
    boolean isDocumentBlacklisted(@Param("documentType") DocumentType documentType, 
                                 @Param("documentId") String documentId, 
                                 @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Найти активную блокировку документа
     */
    @Query("SELECT b FROM BlacklistRegistry b " +
           "WHERE b.documentType = :documentType AND b.documentId = :documentId " +
           "AND b.blacklistedAt <= :currentTime " +
           "AND (b.blacklistExpirationDate IS NULL OR b.blacklistExpirationDate > :currentTime)")
    Optional<BlacklistRegistry> findActiveBlacklistEntry(@Param("documentType") DocumentType documentType, 
                                                        @Param("documentId") String documentId, 
                                                        @Param("currentTime") LocalDateTime currentTime);
}
