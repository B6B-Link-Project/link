import {ReservationService} from "@/api/ReservationService.ts";

class OpenViduService {

    // apiService: ApiService;
    reservationService: ReservationService;

    constructor() {
        // this.apiService = new ApiService();
        this.reservationService = new ReservationService();
    }

    async getToken(reservationId: number) {
        await this.reservationService.createOpenViduSession(reservationId);
        return await this.reservationService.createOpenViduToken(reservationId);
    }

    // @CatchError
    // async createSession(sessionId: string) {
    //     const response = await this.apiService.postData(true, url + '/sessions', { customSessionId: sessionId });
    //     return response.data; // The sessionId
    // }
    //
    // @CatchError
    // async createToken(sessionId: string) {
    //     const response = await this.apiService.postData(true, url + '/sessions/' + sessionId + '/connections', {});
    //     return response.data; // The token
    // }

}

export {OpenViduService}
