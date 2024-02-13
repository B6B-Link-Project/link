export interface ReservationRequest {
    userId: number;
    reservationDateTime: string;
}

export interface ScheduleRequest {
    times: String[];
}

export interface ReservationResponse {
    reservationId: number;
    userId: number;
    isLeader: boolean;
    reservationDatetime: string;
}

export interface ScheduleResponse {
    userId: number;
    times: string[];
}