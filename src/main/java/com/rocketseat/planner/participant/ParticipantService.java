package com.rocketseat.planner.participant;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocketseat.planner.participant.models.Participant;
import com.rocketseat.planner.participant.models.ParticipantData;
import com.rocketseat.planner.trip.models.Trip;

@Service
public class ParticipantService {

  @Autowired
  private ParticipantRepository repository;

  public List<UUID> registerParticipantsToTrip(Trip trip, List<String> participantsToInvite) {
    List<Participant> participants = participantsToInvite.stream().map(email -> new Participant(email, trip)).toList();

    this.repository.saveAll(participants);

    System.out.println(participants.stream().map(p -> p.getId()).toList());

    return participants.stream().map(p -> p.getId()).toList();
  }

  public void triggerConfirmationEmailToTripParticipants(Trip trip) {
  }

  public void triggerConfirmationEmailToParticipantsList(List<String> participantsEmails) {
  }

  public List<ParticipantData> getParticipantsByTripId(UUID tripId) {
    return this.repository.findByTripId(tripId).stream()
        .map(p -> new ParticipantData(p.getId(), p.getName(), p.getEmail(), p.getIsConfirmed())).toList();
  }
}
