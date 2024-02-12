export interface ReservationRequest {
    userId: number;
    reservationDateTime: Date;
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