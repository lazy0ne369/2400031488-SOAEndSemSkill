package soa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import soa.model.TouristRecord;

public interface TouristRecordRepo extends JpaRepository<TouristRecord, Long> { }
