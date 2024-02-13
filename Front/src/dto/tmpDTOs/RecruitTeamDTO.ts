import {TeamMemberFindUserDTO} from "@/dto/tmpDTOs/teamBuildingDTO.ts";

export interface RecruitTeamDTO {
    teamId:number,
    teamName:string,
    teamDesc:string,
    teamMaxMember:number,
    teamMember:number,
    members:{ [key: string]: TeamMemberFindUserDTO[] };
}
