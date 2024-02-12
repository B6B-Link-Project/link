package com.link.back.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.link.back.dto.request.ReservationRequest;
import com.link.back.entity.Reservation;
import com.link.back.entity.User;
import com.link.back.repository.ReservationRepository;
import com.link.back.repository.UserRepository;

@SpringBootTest
@Transactional
class ReservationServiceTest {

	private final UserRepository userRepository;
	private final ReservationService reservationService;
	private final ReservationRepository reservationRepository;

	private User user1, user2;

	@Autowired
	public ReservationServiceTest(UserRepository userRepository, ReservationService reservationService,
		ReservationRepository reservationRepository) {
		this.userRepository = userRepository;
		this.reservationService = reservationService;
		this.reservationRepository = reservationRepository;
	}

	// @BeforeEach
	// void setup() {
	// 	user1 = User.builder()
	// 		.email("asd")
	// 		.password("asd")
	// 		.phoneNumber("asd")
	// 		.name("asd")
	// 		.gender(true)
	// 		.birth(LocalDate.now())
	// 		.rating(2)
	// 		.registered(true)
	// 		.introduce("asd")
	// 		.joinState(false)
	// 		.build();
	// 	user2 = User.builder()
	// 		.email("asd")
	// 		.password("asd")
	// 		.phoneNumber("asd")
	// 		.name("asd")
	// 		.gender(true)
	// 		.birth(LocalDate.now())
	// 		.rating(2)
	// 		.registered(true)
	// 		.introduce("asd")
	// 		.joinState(false)
	// 		.build();
	// 	userRepository.save(user1);
	// 	userRepository.save(user2);
	// }

	@Test
	@DisplayName("Reservation service getMyReservations")
	void getMyReservations() {
		// given
		Reservation reservation = Reservation.builder()
			.leader(user1)
			.member(user2)
			.reservationDateTime(LocalDateTime.now())
			.build();
		reservationRepository.save(reservation);

		// when
		Reservation reservations1 = reservationRepository.findByLeaderOrMember(user1, user1).get(0);
		Reservation reservations2 = reservationRepository.findByLeaderOrMember(user2, user2).get(0);

		// then
		assertEquals(reservations1, reservation);
		assertEquals(reservations2, reservation);
	}

	@Test
	@DisplayName("Reservation service createMyReservation Success")
	void createMyReservation() {
		// give
		ReservationRequest reservationRequest = ReservationRequest.builder()
			.userId(user2.getUserId())
			.reservationDateTime(LocalDateTime.now())
			.build();

		// when
		reservationService.createMyReservation(user1.getUserId(), reservationRequest);

		// then
		Reservation reservation1 = reservationRepository.findByLeaderOrMember(user1, user1).get(0);
		Reservation reservation2 = reservationRepository.findByLeaderOrMember(user2, user2).get(0);

		assertEquals(reservation1, reservation2);
	}

	@Test
	@DisplayName("Reservation service createMyReservation Fail")
	void createMyReservationFail() {
		// give
		ReservationRequest reservationRequest = ReservationRequest.builder()
			.userId(user2.getUserId())
			.reservationDateTime(LocalDateTime.now())
			.build();

		// when
		reservationService.createMyReservation(user1.getUserId(), reservationRequest);

		// then
		Reservation reservation1 = reservationRepository.findByLeaderOrMember(user1, user1).get(0);
		Reservation reservation2 = reservationRepository.findByLeaderOrMember(user2, user2).get(0);

		assertEquals(reservation1, reservation2);

		assertThrowsExactly(
			RuntimeException.class,() -> reservationService.createMyReservation(user1.getUserId(), reservationRequest));
	}

	@Test
	@DisplayName("Reservation service updateMyReservation - failed by overlapped reservation")
	void updateMyReservation() {
		// given
		LocalDateTime tNow = LocalDateTime.now();
		LocalDateTime tAfter = tNow.plusDays(3);

		Reservation reservation = Reservation.builder()
			.leader(user1)
			.member(user2)
			.reservationDateTime(tNow)
			.build();
		reservationRepository.save(reservation);

		ReservationRequest reservationRequest = ReservationRequest.builder()
			.userId(user2.getUserId())
			.reservationDateTime(tAfter)
			.build();

		// when
		reservationService.updateMyReservation(reservation.getReservationId(), user1.getUserId(), reservationRequest);

		// then
		Reservation reservationA = reservationRepository.findById(reservation.getReservationId()).orElseThrow();
		assertEquals(reservationA.getReservationDateTime(), tAfter);

	}

	@Test
	@DisplayName("Reservation service deleteMyReservation")
	void deleteMyReservation() {
		// given
		Reservation reservation = Reservation.builder()
			.leader(user1)
			.member(user2)
			.reservationDateTime(LocalDateTime.now())
			.build();
		reservationRepository.save(reservation);

		// when
		reservationService.deleteReservation(reservation.getReservationId());

		// then
		assertTrue(reservationRepository.findById(reservation.getReservationId()).isEmpty());
	}
}