package ru.bykov.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bykov.entity.RawData;

@Repository
public interface RawDataDAO extends JpaRepository<RawData, Long> {
}
