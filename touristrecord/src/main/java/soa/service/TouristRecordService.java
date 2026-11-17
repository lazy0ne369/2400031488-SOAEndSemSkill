package soa.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import soa.model.TouristRecord;
import soa.repo.TouristRecordRepo;

@Service
public class TouristRecordService {
    private final TouristRecordRepo records;

    public TouristRecordService(TouristRecordRepo records) { this.records = records; }

    public TouristRecord create(TouristRecord record, Authentication authentication) {
        record.setId(null);
        record.setCreatedBy(authentication.getName());
        return records.save(record);
    }

    public List<TouristRecord> findAll() { return records.findAll(); }

    public TouristRecord findById(Long id) { return getRequired(id); }

    public TouristRecord update(Long id, TouristRecord request, Authentication authentication) {
        TouristRecord stored = getRequired(id);
        requireOwnerOrAdmin(stored, authentication);
        stored.setFullName(request.getFullName());
        stored.setEmail(request.getEmail());
        stored.setPhone(request.getPhone());
        stored.setNationality(request.getNationality());
        stored.setDestination(request.getDestination());
        stored.setArrivalDate(request.getArrivalDate());
        stored.setDepartureDate(request.getDepartureDate());
        stored.setNotes(request.getNotes());
        return records.save(stored);
    }

    public void delete(Long id) { records.delete(getRequired(id)); }

    private TouristRecord getRequired(Long id) {
        return records.findById(id).orElseThrow(() -> new IllegalArgumentException("Tourist record not found: " + id));
    }

    private void requireOwnerOrAdmin(TouristRecord record, Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        if (!admin && !authentication.getName().equals(record.getCreatedBy())) {
            throw new AccessDeniedException("You can update only your own tourist records");
        }
    }
}
