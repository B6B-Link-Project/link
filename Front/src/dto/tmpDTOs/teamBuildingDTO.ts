export interface TeamMemberFindDTO {
    content: TeamMemberFindUserDTO[],
    pageable: TeamFindPageableDTO,
    last: boolean,
    totalElements: number,
    totalPages: number,
    size: number,
    number: number,
    first: boolean
}

export interface TeamMemberFindUserDTO {
    userId: number,
    name: string,
    introduce: string,
    rating: number,
    career: number,
    referenceUrl: string,
    field: string,
    profileOriginImageName: string,
    userImageUrl: string,
    skillSets: TeamFindSkillDTO[]
}

export interface TeamFindSkillDTO {
    skillName: string,
    skillImageUrl: string,
    skillLevel: number,
    skillType: string
}

export interface TeamFindPageableDTO {
    pageNumber: number,
    pageSize: number
}

export interface TeamFindRespDTO {
    skillIds: number[],
    tier: number,
    career: number,
    gender: boolean,
    field: string
}

// {
//     "content": [
//     {
//         "userId": 11,
//         "name": "김철수",
//         "introduce": "반갑습니다, 김철수입니다.",
//         "rating": 3,
//         "career": 3,
//         "referenceUrl": "https://ref.com/u11",
//         "field": "BACKEND",
//         "profileOriginImageName": "user_image_origin11.jpg",
//         "profileImageURL": "http://example.com/images/user_image11.jpg",
//         "skillSets": [
//             {
//                 "skillName": "Spring Boot",
//                 "skillImageUrl": "http://example.com/skills/springboot.jpg",
//                 "skillLevel": 4
//             },
//             {
//                 "skillName": "Selenium",
//                 "skillImageUrl": "http://example.com/skills/selenium.jpg",
//                 "skillLevel": 3
//             }
//         ]
//     }
// ],
//     "pageable": {
//     "pageNumber": 0,
//         "pageSize": 20,
//         "sort": [],
//         "offset": 0,
//         "paged": true,
//         "unpaged": false
// },
//     "last": true,
//     "totalElements": 1,
//     "totalPages": 1,
//     "size": 20,
//     "number": 0,
//     "sort": [],
//     "first": true,
//     "numberOfElements": 1,
//     "empty": false
// }