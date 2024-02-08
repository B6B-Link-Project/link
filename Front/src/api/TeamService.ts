import {ApiService} from "@/api/ApiService.ts";
import {httpStatusCode} from "@/util/httpStatus.ts";
import {IdResponseDto, IdsResponseDto} from "@/dto/IdDto.ts";
import {Builder} from "builder-pattern";
import {CandidatesResponseDto} from "@/dto/tmpDTOs/userDTO.ts";
import {TeamApplicationResponseDto} from "@/dto/tmpDTOs/teamDTO.ts";

const apiService = new ApiService();
const url = "/api/teams";

export class TeamService {

    async getActiveTeamId() : Promise<IdResponseDto> {
        try {
            const response = await apiService.getData(true, `${url}/id`, null);
            if (response && response.status === httpStatusCode.OK) {
                return response.data as IdResponseDto;
            }
        } catch (error) {
            alert("팀 아이디 조회 실패");
        }

        return Builder<IdResponseDto>().build();
    }

    async getBuildingTeamIds() : Promise<IdsResponseDto> {
        try {
            const response = await apiService.getData(true, `${url}/ids`, null);
            if (response && response.status === httpStatusCode.OK) {
                return response.data as IdsResponseDto;
            }
        } catch (error) {
            alert("팀 아이디 조회 실패");
        }

        return Builder<IdsResponseDto>().build();
    }

    async postSuggestionByTeam(teamId: number, userId: number) : Promise<void> {
        try {
            const response = await apiService.postData(true, `${url}/${teamId}/members/${userId}/suggest`, null);
            if (response && response.status === httpStatusCode.CREATE) {
                return;
            }
        } catch (error) {
            alert("제안 실패");
        }
    }

    async deleteSuggestionByTeam(teamId: number, userId: number) : Promise<void> {
        try {
            const response = await apiService.deleteData(true, `${url}/${teamId}/members/${userId}/suggest`, null);
            if (response && response.status === httpStatusCode.NOCONTENT) {
                return;
            }
        } catch (error) {
            alert("제안 삭제 실패");
        }
    }

    async postSuggestionByUser(teamId: number) : Promise<void> {
        try {
            const response = await apiService.postData(true, `${url}/${teamId}/members/suggest`, null);
            if (response && response.status === httpStatusCode.OK) {
                return;
            }
        } catch (error) {
            alert("제안 수락 실패");
        }
    }

    async deleteSuggestionByUser(teamId: number) : Promise<void> {
        try {
            const response = await apiService.postData(true, `${url}/${teamId}/members/suggest`, null);
            if (response && response.status === httpStatusCode.NOCONTENT) {
                return;
            }
        } catch (error) {
            alert("제안 거절 실패");
        }
    }

    async getSuggestionListOfTeam(teamId: number) : Promise<CandidatesResponseDto> {
        try {
            const response = await apiService.getData(true, `${url}/${teamId}/suggesting`, null);
            if (response && response.status === httpStatusCode.OK) {
                return response.data as CandidatesResponseDto;
            }
        } catch (error) {
            alert("조회 실패");
        }

        return Builder<CandidatesResponseDto>().build();
    }

    async getTeamParticipantsSuggestionList() : Promise<TeamApplicationResponseDto> {
        try {
            const response = await apiService.getData(true, `${url}/suggested`, null);
            if (response && response.status === httpStatusCode.OK) {
                return response.data as TeamApplicationResponseDto;
            }
        } catch (error) {
            alert("조회 실패");
        }

        return Builder<TeamApplicationResponseDto>().build();
    }


    async deleteMember(userId: number) : Promise<void> {
        try {
            const response = await apiService.deleteData(true, `${url}/members/${userId}`, null);
            if (response && response.status === httpStatusCode.NOCONTENT) {
                return;
            }
        } catch (error) {
            alert("삭제 실패");
        }
    }

    async isLeader() : Promise<boolean> {
        try {
            const response = await apiService.getData(true, `${url}/leader`, null);
            if (response && response.status == httpStatusCode.OK) {
                return response.data;
            }
        } catch (error) {
            alert("리더 조회 실패");
        }

        return false;
    }
}