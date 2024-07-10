package com.rocketseat.planner.participant;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rocketseat.planner.participant.models.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {
  List<Participant> findByTripId(UUID tripId);
}
