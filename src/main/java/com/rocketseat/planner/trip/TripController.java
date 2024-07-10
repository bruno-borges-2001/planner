package com.rocketseat.planner.trip;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rocketseat.planner.activity.ActivityService;
import com.rocketseat.planner.activity.models.ActivityData;
import com.rocketseat.planner.activity.models.ActivityRequestPayload;
import com.rocketseat.planner.participant.ParticipantService;
import com.rocketseat.planner.participant.models.ParticipantData;
import com.rocketseat.planner.participant.models.ParticipantInviteRequest;
import com.rocketseat.planner.trip.models.Trip;
import com.rocketseat.planner.trip.models.TripCreateResponse;
import com.rocketseat.planner.trip.models.TripDetailedResponse;
import com.rocketseat.planner.trip.models.TripRequestPayload;

@RestController
@RequestMapping("/trips")
public class TripController {

  @Autowired
  private ParticipantService participantService;

  @Autowired
  private ActivityService activityService;

  @Autowired
  private TripRepository repository;

  @GetMapping("/{id}")
  public ResponseEntity<TripDetailedResponse> getTripDetails(@PathVariable UUID id) {
    Optional<Trip> trip = this.repository.findById(id);

    if (trip.isPresent()) {
      Trip _trip = trip.get();
      UUID tripId = _trip.getId();

      List<ParticipantData> participants = this.participantService.getParticipantsByTripId(tripId);
      List<ActivityData> activities = this.activityService.getActivitiesByTripId(tripId);

      return ResponseEntity.ok(new TripDetailedResponse(_trip, participants, activities));
    }

    return ResponseEntity.notFound().build();
  }

  @GetMapping("/{id}/participants")
  public ResponseEntity<List<ParticipantData>> getTripParticipants(@PathVariable UUID id) {
    return ResponseEntity.ok(this.participantService.getParticipantsByTripId(id));
  }

  @GetMapping("/{id}/activities")
  public ResponseEntity<List<ActivityData>> getTripActivities(@PathVariable UUID id) {
    return ResponseEntity.ok(this.activityService.getActivitiesByTripId(id));
  }

  @PostMapping
  public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload) {
    Trip newTrip = new Trip(payload);

    this.repository.save(newTrip);

    this.participantService.registerParticipantsToTrip(newTrip, payload.emails_to_invite());

    return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload) {
    Optional<Trip> trip = this.repository.findById(id);

    if (trip.isPresent()) {
      Trip updatedTrip = trip.get();

      updatedTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
      updatedTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
      updatedTrip.setDestination(payload.destination());

      this.repository.save(updatedTrip);
      return ResponseEntity.ok(updatedTrip);
    }

    return ResponseEntity.notFound().build();
  }

  @GetMapping("/{id}/confirm")
  public ResponseEntity<String> confirmTrip(@PathVariable UUID id) {
    Optional<Trip> trip = this.repository.findById(id);

    if (trip.isPresent()) {
      Trip confirmedTrip = trip.get();
      confirmedTrip.setIsConfirmed(true);

      this.repository.save(confirmedTrip);

      this.participantService.triggerConfirmationEmailToTripParticipants(confirmedTrip);

      return ResponseEntity.ok("Trip confirmed!");
    }

    return ResponseEntity.notFound().build();
  }

  @PostMapping("/{id}/invites")
  public ResponseEntity<List<UUID>> inviteParticipants(@PathVariable UUID id,
      @RequestBody ParticipantInviteRequest payload) {
    Optional<Trip> trip = this.repository.findById(id);

    if (trip.isPresent()) {
      Trip rawTrip = trip.get();

      List<String> participantsEmails = payload.participants();

      List<UUID> participants = this.participantService.registerParticipantsToTrip(rawTrip,
          participantsEmails);

      if (rawTrip.getIsConfirmed()) {
        this.participantService.triggerConfirmationEmailToParticipantsList(participantsEmails);
      }

      return ResponseEntity.ok(participants);
    }

    return ResponseEntity.notFound().build();
  }

  @PostMapping("/{id}/activities")
  public ResponseEntity<String> createActivity(@PathVariable UUID id,
      @RequestBody ActivityRequestPayload payload) {
    Optional<Trip> trip = this.repository.findById(id);

    if (trip.isPresent()) {
      this.activityService.createActivity(payload, trip.get());

      return ResponseEntity.ok("Success");
    }

    return ResponseEntity.notFound().build();
  }
}
