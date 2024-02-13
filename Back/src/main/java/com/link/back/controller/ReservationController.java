package com.link.back.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.link.back.dto.request.ReservationRequest;
import com.link.back.dto.response.ReservationResponse;
import com.link.back.security.JwtTokenProvider;
import com.link.back.service.ReservationService;

import io.openvidu.java.client.Connection;
import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

	private final ReservationService reservationService;
	private final OpenVidu openVidu; // 오픈 비두 객체
	private final JwtTokenProvider jwtTokenProvider;

	@GetMapping
	public List<ReservationResponse> getMyReservation(
		@RequestHeader(value = "Authorization", required = true) String token) {

		Long myUserId = this.getUserIdFromToken(token);
		return reservationService.getReservations(myUserId);
	}

	@GetMapping("/users/{userId}")
	public List<ReservationResponse> getReservations(@PathVariable Long userId) {
		return reservationService.getReservations(userId);
	}

	@PostMapping
	public void postReservation(@RequestHeader(value = "Authorization", required = true) String token,
		@RequestBody @NotNull ReservationRequest reservationRequest) {

		Long myUserId = this.getUserIdFromToken(token);
		reservationService.createMyReservation(myUserId, reservationRequest);
	}

	@PutMapping("/{reservation_id}")
	public ResponseEntity<Void> putReservation(@RequestHeader(value = "Authorization", required = true) String token,
		@PathVariable("reservation_id") Long reservationId,
		@RequestBody @NotNull ReservationRequest reservationRequest) {

		Long myUserId = this.getUserIdFromToken(token);
		if (!reservationService.checkReservation(reservationId, myUserId))
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		reservationService.updateMyReservation(reservationId, myUserId, reservationRequest);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/{reservation_id}")
	public ResponseEntity<Void> deleteReservation(@PathVariable("reservation_id") Long reservationId,
		@RequestHeader(value = "Authorization", required = true) String token) {

		Long myUserId = this.getUserIdFromToken(token);
		if (!reservationService.checkReservation(reservationId, myUserId))
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		reservationService.deleteReservation(reservationId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/{reservation_id}/sessions")
	public ResponseEntity<Void> initializeSession(  // 오픈 비두 세션 초기화
		@RequestHeader(value = "Authorization", required = true) String token,
		@PathVariable("reservation_id") Long reservationId,
		@RequestBody(required = false) Map<String, Object> params) throws
		OpenViduJavaClientException,
		OpenViduHttpException {

		Long myUserId = this.getUserIdFromToken(token);
		if (!reservationService.checkReservation(reservationId, myUserId))
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		String sessionId = "RS-" + Long.toString(reservationId); // 예약 ID 정보로 세션 ID 생성
		params.put("customSessionId", sessionId);
		SessionProperties properties = SessionProperties.fromJson(params).build();
		Session session = openVidu.createSession(properties);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/{reservation_id}/sessions/connections")
	public ResponseEntity<String> createConnection( // 오픈 비두 토큰 발급
		@RequestHeader(value = "Authorization", required = true) String token,
		@PathVariable("reservation_id") Long reservationId,
		@RequestBody(required = false) Map<String, Object> params) throws
		OpenViduJavaClientException,
		OpenViduHttpException {

		Long myUserId = this.getUserIdFromToken(token);
		if (!reservationService.checkReservation(reservationId, myUserId))
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		String sessionId = "RS-" + Long.toString(reservationId); // 예약 ID로 세션 ID 생성
		Session session = openVidu.getActiveSession(sessionId);
		if (session == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		ConnectionProperties properties = ConnectionProperties.fromJson(params).build();
		Connection connection = session.createConnection(properties);
		return new ResponseEntity<>(connection.getToken(), HttpStatus.OK);
	}

	private Long getUserIdFromToken(String token) {
		if (token == null) return null;
		return jwtTokenProvider.getUserId(token);
	}

}
