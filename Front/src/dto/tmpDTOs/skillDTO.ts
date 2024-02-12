export interface SkillDTO {
    skillId:number,
    skillName:string,
    skillImgUrl:string,
    skillYear:number
}

export interface SkillDto {
    skillName: string;
    skillImageUrl: string;
    skillLevel: number;
}

export interface SkillDtoT {
    skillName: string;
    skillImageUrl: string;
    skillLevel: number;
    skillType: string;
}

export interface SkillResponseDto {
    skillName: string;
    skillImageUrl: string;
    skillLevel: string;
    skillType: string;
}

//사용자 정보 추가 등록, 갱신에 사용
export interface SkillRequestDto {
    skillId: number;
    skillName: string;
    skillImageUrl: string;
    skillType: string;
}