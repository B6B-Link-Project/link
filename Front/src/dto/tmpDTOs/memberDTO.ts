import {Field, Role} from "@/dto/tmpDTOs/commonDTO.ts";
import {SkillDtoT, SkillResponseDto} from "@/dto/tmpDTOs/skillDTO.ts";

export interface MemberDetailResponseDto {
    userId: number;
    name: string;
    introduce: string;
    rating: number;
    career: number;
    referenceUrl: string;
    field: Field;
    profileOriginImageName: string;
    profileImageURL: string;
    skillSets: SkillDtoT[];
}


export interface MemberResponseDto {
    name: string;
    role: Role;
    rating: number;
    introduce: string;
    userImageUrl: string;
    userOriginImageName: string;
    skillSets: SkillResponseDto[];
}

export interface MembersResponseDto {
    members: MemberResponseDto[];
}
