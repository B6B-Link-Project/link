import {ApiService} from "@/api/ApiService.ts";
import {TeamMemberFindDTO} from "@/dto/tmpDTOs/teamBuildingDTO.ts";
import {CatchError} from "@/util/error.ts";
import {SkillCategoryResponseDto} from "@/dto/tmpDTOs/SkillTypeDTO.ts";
import {RecruitTeamDTO} from "@/dto/tmpDTOs/RecruitTeamDTO.ts";
import {AppliedTeamDTO} from "@/dto/tmpDTOs/AppliedTeamDTO.ts";
import {TeamResponseDto} from "@/dto/tmpDTOs/teamDTO.ts";
import {MemberDetailResponseDto} from "@/dto/tmpDTOs/memberDTO.ts";

const apiService = new ApiService();

const url = "/api"

class TeamBuildingService {
    // @CatchError
    // async getAllTeamFindMember(): Promise<TeamMemberFindDTO> {
    //     const response = await apiService.getData(false, `${url}/teams/recruit`);
    //     return response.data;
    // }

    @CatchError
    async getAllTeamFindMember(params?: {
        skillIds?: number[];
        tier?: Partial<number>;
        career?: Partial<number>;
        gender?: Partial<string>;
        field?: Partial<string>;
        page?: Partial<number>;
        size?: Partial<number>;
    }): Promise<TeamMemberFindDTO> {
        const formattedParams = Object.entries(params || {})
            .map(([key, value]) => Array.isArray(value) ?
                value.map(val => `${key}=${encodeURIComponent(val)}`).join('&')
                : `${key}=${encodeURIComponent(value) === "undefined" ? '' : encodeURIComponent(value)}`)
            .join('&');
        const urlWithParams = `${url}/teams/recruit${formattedParams ? `?${formattedParams}` : ''}`;
        return (await apiService.getData(true, urlWithParams)).data as TeamMemberFindDTO;
    }

    @CatchError
    async getAppliedTeam(): Promise<AppliedTeamDTO | undefined> {
        return (await apiService.getData(true, `${url}/teams/applying`)).data as AppliedTeamDTO;
    }

    @CatchError
    async getSuggestedTeam(): Promise<AppliedTeamDTO | undefined> {
        return (await apiService.getData(true, `${url}/teams/suggested`)).data as AppliedTeamDTO;
    }

    @CatchError
    async getRecruitTeam(): Promise<RecruitTeamDTO | undefined> {
        return (await apiService.getData(true, `${url}/teams/recruit/team`)).data as RecruitTeamDTO;
    }


    @CatchError
    async getAllSkillType(): Promise<SkillCategoryResponseDto> {
        const response = await apiService.getData(true, `${url}/skill`);
        return response.data;
    }

    @CatchError
    async postAppliedTeam(num: number | undefined)  {
        await apiService.postData(true,`${url}/teams/${num}/members/apply`, '')
    }

    @CatchError
    async deleteAppliedTeam(teamId : number) {
        await apiService.deleteData(true, `${url}/teams/${teamId}/members/apply`, '')
    }

    @CatchError
    async postAcceptApply(userId: number, teamId: number | undefined) {
        await apiService.postData(true, `${url}/teams/${teamId}/members/${userId}/apply`,'')
    }

    @CatchError
    async deleteRefuseApply(userId: number, teamId: number | undefined) {
        await apiService.deleteData(true, `${url}/teams/${teamId}/members/${userId}/apply`,'')
    }

    @CatchError
    async getIsLeader(): Promise<Boolean> {
        return (await apiService.getData(true, `${url}/teams/leader`)).data as Boolean;
    }

    @CatchError
    async getTeamDetailByTeamId(teamId: number): Promise<TeamResponseDto> {
        const response = await apiService.getData(false, `${url}/teams/${teamId}`);
        return response.data;
    }

    @CatchError
    async getMemberDetailByUserId(userId: number): Promise<MemberDetailResponseDto> {
        const response = await apiService.getData(false, `${url}/teams/recruit/detail/${userId}`);
        return response.data;
    }

}

export {
    TeamBuildingService
}
