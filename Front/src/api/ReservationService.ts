import {CatchError} from "@/util/error.ts";
import {ReservationRequest, ReservationResponse} from "@/dto/tmpDTOs/reservationDTO.ts";
import {httpStatusCode} from "@/util/httpStatus.ts";
import {ApiService} from "@/api/ApiService.ts";

const apiService = new ApiService();

const url = "/api/reservations"

class ReservationService {
    @CatchError
    async getMyReservations(): Promise<ReservationResponse[]> {
        const newUrl = `${url}`;
        const response = await apiService.getData(true, newUrl);
        if (response && response.status === httpStatusCode.OK) {
            return response.data;
        }
        return {} as ReservationResponse[];
    }

    @CatchError
    async getReservationsByUserId(userId: number): Promise<ReservationResponse[]> {
        const newUrl = `${url}/users/${userId}`;
        const response = await apiService.getData(true, newUrl);
        if (response && response.status === httpStatusCode.OK) {
            return response.data;
        }
        return {} as ReservationResponse[];
    }

    @CatchError
    async registerReservation(data: ReservationRequest): Promise<void> {
        const newUrl = `${url}`;
        const response = await apiService.postData(true, newUrl, data);
        if (response && response.status === httpStatusCode.OK) {
            return;
        }
    }

    @CatchError
    async updateReservation(reservationId: number, data: ReservationRequest): Promise<void> {
        const newUrl = `${url}/${reservationId}`;
        const response = await apiService.putData(true, newUrl, data);
        if (response && response.status === httpStatusCode.OK) {
            return;
        }
    }

    @CatchError
    async deleteReservation(reservationId: number, data: ReservationRequest): Promise<void> {
        const newUrl = `${url}/${reservationId}`;
        const response = await apiService.deleteData(true, newUrl, data);
        if (response && response.status === httpStatusCode.OK) {
            return;
        }
    }

    async createOpenViduSession(reservationId: number): Promise<void> {
        const newUrl = `${url}/${reservationId}/sessions`;
        try {
            const response = await apiService.postData(true, newUrl, {});
            if (response && response.status === httpStatusCode.OK) {
                return;
            }
        } catch (error) {
            // 예외 상황
            //      - 사용자 권한 에러
            //      - 오픈 비두 서버 통신 에러
            return Promise.reject(error);
        }
    }

    async createOpenViduToken(reservationId: number) :Promise<any> {
        const newUrl = `${url}/${reservationId}/sessions/connections`;
        try {
            const response = await apiService.postData(true, newUrl, {});
            if (response && response.status === httpStatusCode.OK) {
                return response.data;
            }
        } catch (error) {
            // 예외 상황
            //      - 사용자 권한 에러
            //      - 오픈 비두 서버 통신 에러
            return Promise.reject(error);
        }
    }

}

export {
    ReservationService
};
