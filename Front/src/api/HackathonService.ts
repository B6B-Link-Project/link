import {ApiService} from "@/api/ApiService.ts";
import {httpStatusCode} from "@/util/httpStatus.ts";
import {WinnerProjectResponseDto} from "@/dto/tmpDTOs/hackathonDTO.ts";
import {
    HackathonProceedingProjectResponseDto,
    PageableProceedingHackathons
} from "@/dto/tmpDTOs/hackathonProceedingProjectResponseDto.ts";
import {AxiosResponse} from "axios";
import {Builder} from "builder-pattern";
import {HackathonInfoDTO, PageableHackathonList} from "@/dto/hackathonInfoDTO.ts";
import {CatchError} from "@/util/error.ts";
import {HackathonTeamDTO} from "@/dto/tmpDTOs/HackathonTeamDTO.ts";

const apiService = new ApiService();

const url = "/api"
interface pageableData {
    content: Object,
    pageable: {
        pageNumber: number,
    }
    totalPages: number,
}
class HackathonService {
    @CatchError
    async getHackathonList(hackathonStatus: string, page: number, size: number): Promise<PageableHackathonList> {
        const response = await apiService.getData(false, `${url}/hackathons`, {
            params: {
                    page: page,
                    size: size,
                    status: hackathonStatus
                }
            });

        if (response && response.status === httpStatusCode.OK) {
            console.log(response.data)
            return this.toPageableHackathonsList(response) as PageableHackathonList;
        }
        return {} as PageableHackathonList;
    }

    @CatchError
    async getHackathonDetail(hackathonId: number): Promise<HackathonInfoDTO> {
        const response = await apiService.getData(false, `${url}/hackathons/${hackathonId}`,undefined);
        if (response && response.status === httpStatusCode.OK) {
            return response.data as HackathonInfoDTO;
        }
        return {} as HackathonInfoDTO;

    }

    @CatchError
    async getWinnerProjects(hackathonId: number): Promise<WinnerProjectResponseDto[]> {
        const response = await apiService.getData(false, `${url}/hackathons/${hackathonId}/winners`,undefined)
        if (response && response.status === httpStatusCode.OK) {
            return response.data as WinnerProjectResponseDto[];
        }
        return [] as WinnerProjectResponseDto[];
    }

    @CatchError
    async getProceedingLeaderboards(hackathonId: number): Promise<PageableProceedingHackathons> {
        const response = await apiService.getData(true, `${url}/hackathons/${hackathonId}/proceeding`,undefined)
        if (response && response.status === httpStatusCode.OK) {
            console.log("r", response.data)
            return this.toPageableHackathons(response) as PageableProceedingHackathons;
        }
        return {} as PageableProceedingHackathons;
    }

    toPageableHackathons = (response: AxiosResponse<any, any>): PageableProceedingHackathons => {
        const data = response.data as pageableData;
        return Builder<PageableProceedingHackathons>()
            .hackathons(data.content as HackathonProceedingProjectResponseDto[])
            .pageable(Builder<PageableDto>()
                .pageNumber(data.pageable.pageNumber)
                .totalPages(data.totalPages)
                .build())
            .build();
    }

    toPageableHackathonsList = (response: AxiosResponse<any, any>): PageableHackathonList => {
        const data = response.data as pageableData;
        return Builder<PageableHackathonList>()
            .hackathons(data.content as HackathonInfoDTO[])
            .pageable(Builder<PageableDto>()
                .pageNumber(data.pageable.pageNumber)
                .totalPages(data.totalPages)
                .build())
            .build();
    }

    @CatchError
    async getAllTeamByHackathon(params?: {
        skillIds?: number[];
        page?: Partial<number>;
        size?: Partial<number>;
        hackathonId: number
    }): Promise<HackathonTeamDTO> {
        const formattedParams = Object.entries(params || {})
            .map(([key, value]) => Array.isArray(value) ?
                value.map(val => `${key}=${encodeURIComponent(val)}`).join('&')
                : `${key}=${encodeURIComponent(value) === "undefined" ? '' : encodeURIComponent(value)}`)
            .join('&');
        const urlWithParams = `${url}/teams/hackathon/${params?.hackathonId}${formattedParams ? `?${formattedParams}` : ''}`;
        return (await apiService.getData(true, urlWithParams)).data as HackathonTeamDTO;
    }

}

export {HackathonService};