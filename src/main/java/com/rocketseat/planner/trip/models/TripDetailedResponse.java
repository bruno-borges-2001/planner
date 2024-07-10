package com.rocketseat.planner.trip.models;

import java.util.List;

import com.rocketseat.planner.activity.models.ActivityData;
import com.rocketseat.planner.participant.models.ParticipantData;

public record TripDetailedResponse(Trip data, List<ParticipantData> participants, List<ActivityData> activities) {

}
