import {HackathonInfoDTO} from "@/dto/hackathonInfoDTO.ts";
import {TeamMemberFindUserDTO} from "@/dto/tmpDTOs/teamBuildingDTO.ts";

export interface AppliedTeamDTO {
    teamId:number,
    teamName:string,
    teamDesc:string,
    hackathonInfoResponseDto:HackathonInfoDTO,
    leader:TeamMemberFindUserDTO,
    members:TeamMemberFindUserDTO[],
    teams:teamListDTO[]
}

export interface teamListDTO {
    teamId:number,
    teamName:string
}