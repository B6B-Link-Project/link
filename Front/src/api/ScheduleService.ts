import {ApiService} from "@/api/ApiService.ts";
import {httpStatusCode} from "@/util/httpStatus.ts";
import {ScheduleRequest, ScheduleResponse} from "@/dto/tmpDTOs/reservationDTO.ts";
import {CatchError} from "@/util/error.ts";

const apiService = new ApiService();

const url = "/api/schedules"

class ScheduleService {
    @CatchError
    async getMySchedule(): Promise<ScheduleResponse> {
        const newUrl = `${url}`;
        const response = await apiService.getData(true, newUrl);
        if (response && response.status === httpStatusCode.OK) {
            return response.data;
        }
        return {} as ScheduleResponse;
    }

    @CatchError
    async getUserSchedule(userId: number): Promise<ScheduleResponse> {
        const newUrl = `${url}/${userId}`;
        const response = await apiService.getData(false, newUrl);
        if (response && response.status === httpStatusCode.OK) {
            return response.data;
        }
        return {} as ScheduleResponse;
    }

    @CatchError
    async updateMySchedule(scheduleRequest: ScheduleRequest): Promise<ScheduleResponse> {
        const newUrl = `${url}`;
        const response = await apiService.putData(true, newUrl, scheduleRequest);
        if (response && response.status === httpStatusCode.OK) {
            return response.data;
        }
        return {} as ScheduleResponse;
    }

}

export {
    ScheduleService
};
