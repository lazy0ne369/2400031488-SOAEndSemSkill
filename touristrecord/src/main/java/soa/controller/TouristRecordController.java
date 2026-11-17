package soa.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import soa.model.TouristRecord;
import soa.service.TouristRecordService;

@RestController
@RequestMapping("/api/tourist-records")
public class TouristRecordController {
    private final TouristRecordService service;
    public TouristRecordController(TouristRecordService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TouristRecord create(@RequestBody TouristRecord record, Authentication authentication) {
        return service.create(record, authentication);
    }
    @GetMapping public List<TouristRecord> findAll() { return service.findAll(); }
    @GetMapping("/{id}") public TouristRecord findById(@PathVariable Long id) { return service.findById(id); }
    @PutMapping("/{id}") public TouristRecord update(@PathVariable Long id, @RequestBody TouristRecord record, Authentication authentication) {
        return service.update(id, record, authentication);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
