package com.link.back.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.link.back.entity.Reservation;
import com.link.back.entity.User;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	List<Reservation> findByLeaderOrMember(User leader, User member);

	@Query("select r from Reservation r where (r.member.userId = :userId or r.leader.userId = :userId) and r.reservationDateTime > :dateTime ")
	List<Reservation> findAllByUserIdFilterByRecently(Long userId, LocalDateTime dateTime);

	@Query("select r from Reservation r where (r.member.userId, r.leader.userId) in ((:userId1, :userId2), (:userId2, :userId1))")
	List<Reservation> findAllByUserSet(Long userId1, Long userId2);
}
