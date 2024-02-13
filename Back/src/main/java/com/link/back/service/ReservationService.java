package com.link.back.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.link.back.dto.request.ReservationRequest;
import com.link.back.dto.response.ReservationResponse;
import com.link.back.entity.Reservation;
import com.link.back.entity.User;
import com.link.back.exception.ContentNotFoundException;
import com.link.back.repository.ReservationRepository;
import com.link.back.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final UserRepository userRepository;

	public List<ReservationResponse> getReservations(Long userId) {

		User user = findUser(userId);
		// List<Reservation> reservations = reservationRepository.findByLeaderOrMember(user, user);
		LocalDateTime now = LocalDateTime.now();
		List<Reservation> reservations = reservationRepository.findAllByUserIdFilterByRecently(userId, LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth() - 1, 0, 0));

		return reservations.stream().map(reservation ->
			ReservationResponse.builder()
				.reservationId(reservation.getReservationId())
				.userId(
					reservation.isLeader(user.getUserId()) ? reservation.getMember().getUserId() :
						reservation.getLeader().getUserId())
				.isLeader(reservation.isLeader(user.getUserId()))
				.reservationDatetime(reservation.getReservationDateTime())
				.build()
		).toList();
	}

	public void createMyReservation(Long myUserId, ReservationRequest reservationRequest) {
		User user = findUser(myUserId);
		if (isOverlappedReservation(user, reservationRequest.reservationDateTime())) // 겹치는 예약 시간 확인
			throw new ResponseStatusException(HttpStatus.CONFLICT, "예약 시간이 겹칩니다.");

		reservationRepository.findAllByUserSet(myUserId,
			reservationRequest.userId()).forEach((r) -> reservationRepository.deleteById(r.getReservationId()));

		Reservation reservation = Reservation.builder()
			.leader(userRepository.getReferenceById(reservationRequest.userId()))
			.member(user)
			.reservationDateTime(reservationRequest.reservationDateTime())
			.build();
		reservationRepository.save(reservation);
	}

	public void updateMyReservation(Long reservationId, Long myUserId, ReservationRequest reservationRequest) {
		User user = findUser(myUserId);
		// if (isUsersReservation(reservationId, myUserId)) // 자신의 예약인지 확인
		// 	throw new RuntimeException();
		if (isOverlappedReservation(user, reservationRequest.reservationDateTime())) // 겹치는 예약 시간 확인
			throw new ResponseStatusException(HttpStatus.CONFLICT, "예약 시간이 겹칩니다.");
		Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();
		reservation.update(reservationRequest.reservationDateTime());
		reservationRepository.save(reservation);
	}

	public void deleteReservation(Long reservationId) {
		reservationRepository.deleteById(reservationId);
	}

	public boolean checkReservation(Long reservationId, Long userId) {
		Optional<Reservation> reservation = reservationRepository.findById(reservationId);
		return reservation.filter(value -> value.isMyReservation(userId)).isPresent();
	}

	private boolean isOverlappedReservation(User user, LocalDateTime reservationDateTime) {
		List<Reservation> reservations = reservationRepository.findByLeaderOrMember(user, user);
		for (Reservation r : reservations) {
			if (r.getReservationDateTime().equals(reservationDateTime))
				return true;
		}
		return false;
	}

	private User findUser(Long userId) {
		return userRepository.findById(userId).orElseThrow(ContentNotFoundException::new);
	}

}
