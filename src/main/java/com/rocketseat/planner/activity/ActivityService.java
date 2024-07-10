package com.rocketseat.planner.activity;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocketseat.planner.activity.models.Activity;
import com.rocketseat.planner.activity.models.ActivityData;
import com.rocketseat.planner.activity.models.ActivityRequestPayload;
import com.rocketseat.planner.trip.models.Trip;

@Service
public class ActivityService {

  @Autowired
  private ActivityRepository repository;

  public List<ActivityData> getActivitiesByTripId(UUID tripId) {
    List<Activity> activities = repository.findByTripId(tripId);

    return activities.stream().map(a -> new ActivityData(a.getId(), a.getTitle(), a.getOccursAt())).toList();
  }

  public Activity createActivity(ActivityRequestPayload payload, Trip trip) {
    Activity activity = new Activity(payload, trip);

    repository.save(activity);

    return activity;
  }
}
