package com.rocketseat.planner.participant;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rocketseat.planner.participant.models.Participant;
import com.rocketseat.planner.participant.models.ParticipantRequestPayload;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

  @Autowired
  private ParticipantRepository repository;

  @GetMapping()
  public ResponseEntity<Iterable<Participant>> getParticipants() {
    return ResponseEntity.ok(this.repository.findAll());
  }

  @PostMapping("/{id}/confirm")
  public ResponseEntity<String> confirmParticipant(@PathVariable UUID id,
      @RequestBody ParticipantRequestPayload payload) {
    Optional<Participant> participant = this.repository.findById(id);

    if (participant.isPresent()) {
      Participant updatedParticipant = participant.get();
      updatedParticipant.setIsConfirmed(true);
      updatedParticipant.setName(payload.name());

      this.repository.save(updatedParticipant);
      return ResponseEntity.ok("Participant confirmed");
    }

    return ResponseEntity.notFound().build();
  }
}
