package com.rocketseat.planner.trip;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rocketseat.planner.trip.models.Trip;

public interface TripRepository extends JpaRepository<Trip, UUID> {

}
