//@ts-nocheck
import {HackathonInfoResponseDto} from "@/dto/hackathonInfoDTO.ts";
import {SkillResponseDto} from "@/dto/skillDTO.ts";
import {Role, SkillType} from "@/dto/tmpDTOs/commonDTO.ts";

export interface CreateTeamRequestDto {
    teamName: string;
    teamDesc: string;
    teamSkills: number[];
}

export interface UpdateTeamRequestDto {
    teamName: string;
    teamDesc: string;
    // teamSkillSets: TeamSkill[];
}

export interface TeamIdsResponseDto {
    teamIds: number[];
}

export interface TeamApplicationResponseDto {
    teamName: string;
    teamDesc: string;
    hackathonInfoResponseDto: HackathonInfoResponseDto;
    members: TeamMemberResponseDto[];
    teams: TeamListSimpleResponseDto[];
}

export interface TeamListSimpleResponseDto {
    teamId: number;
    teamName: string;
}

export interface TeamMemberResponseDto {
    userId: number;
    name: string;
    rating: number;
    introduce: string;
    userImageUrl: string;
    userOriginImageName: string;
    role: Role;
    skillSets: SkillResponseDto[];
}

export interface TeamResponseDto {
    teamName: string;
    teamMember: number;
    teamDesc: string;
    teamSkills: TeamSkillDto[];
    members: TeamMemberResponseDto[];
}

export interface TeamSkillDto {
    skillName: string;
    skillImageUrl: string;
    skillType: SkillType;
}