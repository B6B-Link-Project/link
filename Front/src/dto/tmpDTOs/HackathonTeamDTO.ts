import {TeamFindSkillDTO, TeamMemberFindUserDTO} from "@/dto/tmpDTOs/teamBuildingDTO.ts";

export interface HackathonTeamDTO {
    content: HackathonTeamInfo1DTO[],
    pageable: TeamFindPageableDTO,
    last: boolean,
    totalElements: number,
    totalPages: number,
    size: number,
    number: number,
    first: boolean
}

export interface HackathonTeamInfo1DTO{
    teamId:number,
    hackathonName:string,
    teamMaxPoint:number,
    teamMaxMember:number,
    teamDeadlineDate:Date,
    teamName:string,
    teamMember:number,
    teamDesc:string,
    teamSkills:TeamFindSkillDTO[],
    members:TeamMemberFindUserDTO[]
}
export interface TeamFindPageableDTO {
    pageNumber: number,
    pageSize: number
}