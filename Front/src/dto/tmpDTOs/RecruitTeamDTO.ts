import {TeamMemberFindUserDTO} from "@/dto/tmpDTOs/teamBuildingDTO.ts";

export interface RecruitTeamDTO {
    teamId:number,
    teamName:string,
    teamDesc:string,
    members:{ [key: string]: TeamMemberFindUserDTO[] };
}
