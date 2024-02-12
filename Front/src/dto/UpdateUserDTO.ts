//@ts-nocheck
import {UserImageDTO} from "@/dto/UserImageDTO.ts";
import {UserSkillDTO} from "@/dto/UserSkillDTO.ts";

export interface UpdateUserDTO {
    // password: string;
    phoneNumber: string;
    name: string;
    introduce: string;
    birth: Date;
    gender: boolean;
    userSkills: UserSkillDTO[];
    referenceUrl: string;
    field: string;
    career: number;
    userImage: UserImageDTO;
    registered: boolean;
}