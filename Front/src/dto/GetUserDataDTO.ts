//@ts-nocheck
import {UserImageDTO} from "@/dto/UserImageDTO.ts";
import {UserSkillDTO} from "@/dto/tmpDTOs/UserSkillDTO.ts";

export interface GetUserDataDTO{
    image: UserImageDTO;
    name: string;
    referenceUrl: string;
    field: string;
    career: number;
    userSkills: UserSkillDTO[];
    introduce: string;
    registered: Boolean;
    rating: number;
}
