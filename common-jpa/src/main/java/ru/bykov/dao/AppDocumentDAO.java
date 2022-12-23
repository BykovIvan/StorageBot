package ru.bykov.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bykov.entity.AppDocument;

@Repository
public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
