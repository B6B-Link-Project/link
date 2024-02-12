import {ApiService} from "@/api/ApiService.ts";
import {httpStatusCode} from "@/util/httpStatus.ts";
import {IdResponseDto, IdsResponseDto} from "@/dto/IdDto.ts";
import {Builder} from "builder-pattern";
import {CandidatesResponseDto} from "@/dto/tmpDTOs/userDTO.ts";
import {TeamApplicationResponseDto} from "@/dto/tmpDTOs/teamDTO.ts";
import {HackathonTeamInfo1DTO} from "@/dto/tmpDTOs/HackathonTeamDTO.ts";
import {CatchError} from "@/util/error.ts";
import {TeamSkillAddDto} from "@/dto/tmpDTOs/TeamSkillAddDto.ts";
import {CreateTeamDTO} from "@/dto/tmpDTOs/CreateTeamDTO.ts";

const apiService = new ApiService();
const url = "/api/teams";

export class TeamService {

    async getActiveTeamId() : Promise<IdResponseDto> {
        try {
            const response = await apiService.getData(true, `${url}/id`);
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
            const response = await apiService.getData(true, `${url}/ids`);
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
            const response = await apiService.postData(true, `${url}/${teamId}/members/${userId}/suggest`,{});
            if (response && response.status === httpStatusCode.CREATE) {
                return;
            }
        } catch (error) {
            alert("제안 실패");
        }
    }

    async deleteSuggestionByTeam(teamId: number, userId: number) : Promise<void> {
        try {
            const response = await apiService.deleteData(true, `${url}/${teamId}/members/${userId}/suggest`,{});
            if (response && response.status === httpStatusCode.NOCONTENT) {
                return;
            }
        } catch (error) {
            alert("제안 삭제 실패");
        }
    }

    async postSuggestionByUser(teamId: number) : Promise<void> {
        try {
            const response = await apiService.postData(true, `${url}/${teamId}/members/suggest`, {});
            if (response && response.status === httpStatusCode.OK) {
                return;
            }
        } catch (error) {
            alert("제안 수락 실패");
        }
    }

    async deleteSuggestionByUser(teamId: number) : Promise<void> {
        try {
            const response = await apiService.postData(true, `${url}/${teamId}/members/suggest`, {});
            if (response && response.status === httpStatusCode.NOCONTENT) {
                return;
            }
        } catch (error) {
            alert("제안 거절 실패");
        }
    }

    async getSuggestionListOfTeam(teamId: number) : Promise<CandidatesResponseDto> {
        try {
            const response = await apiService.getData(true, `${url}/${teamId}/suggesting`, {});
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
            const response = await apiService.getData(true, `${url}/suggested`, {});
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
            const response = await apiService.deleteData(true, `${url}/members/${userId}`, {});
            if (response && response.status === httpStatusCode.NOCONTENT) {
                return;
            }
        } catch (error) {
            alert("삭제 실패");
        }
    }

    async isLeader() : Promise<boolean> {
        try {
            const response = await apiService.getData(true, `${url}/leader`, {});
            if (response && response.status == httpStatusCode.OK) {
                return response.data;
            }
        } catch (error) {
            alert("리더 조회 실패");
        }

        return false;
    }

    @CatchError
    async getRecruitTeam() : Promise<HackathonTeamInfo1DTO> {
        return (await apiService.getData(false, `${url}/hackathon/recruit/team`)).data as HackathonTeamInfo1DTO;
    }

    @CatchError
    async getTeamSkillAdd(): Promise<TeamSkillAddDto[]> {
        return (await apiService.getData(true, `${url}/team/skills`)).data as TeamSkillAddDto[];
    }

    @CatchError
    async postCreateTeam(
        skillIds: number[],
        teamName: string,
        teamDesc: string
    ) {
        await apiService.postData(true, `${url}`,
            Builder<CreateTeamDTO>()
                .skillIds(skillIds)
                .teamName(teamName)
                .teamDesc(teamDesc)
                .build())
    }
}

