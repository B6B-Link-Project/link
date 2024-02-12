import {ApiService} from "@/api/ApiService.ts";
import {httpStatusCode} from "@/util/httpStatus.ts";
import {
    BackPerformanceResponseDto,
    PageableBackPerformance, PageableProjects,
    ProjectDetailDto,
    ProjectInfoDTO,
    ProjectRequestDto
} from "@/dto/projectDTO.ts";
import {Builder} from "builder-pattern";
import {AxiosResponse} from "axios";
import {BackPerformanceMessageResponseDto} from "@/dto/BackPerformanceMessageResponseDto.ts";
import {CatchError} from "@/util/error.ts";

const apiService = new ApiService();

const url = "/api/projects"

interface pageableData {
    content: Object,
    pageable: {
        pageNumber: number,
    }
    totalPages: number,
}

class ProjectService {

    toPageableProjects = (response: AxiosResponse<any, any>): PageableProjects => {
        const data = response.data as pageableData;
        return Builder<PageableProjects>()
            .projects(data.content as ProjectInfoDTO[])
            .pageable(Builder<PageableDto>()
                .pageNumber(data.pageable.pageNumber)
                .totalPages(data.totalPages)
                .build())
            .build();
    }

    // 전송 관련 메서드들

    async getALlProjects(pageNumber: number | undefined, pageSize: number | undefined): Promise<PageableProjects> {
        try {
            const newUrl = `${url}`;
            const params = {
                page: pageNumber,
                size: pageSize
            }
            try {
                const response = await apiService.getData(true, newUrl, {params});
                if (response && response.status === httpStatusCode.OK) {
                    return this.toPageableProjects(response);
                }
            } catch (error) {
                const response = await apiService.getData(false, newUrl, {params});
                if (response && response.status === httpStatusCode.OK) {
                    return this.toPageableProjects(response);
                }
            }
        } catch (error) {
            console.error(error);
        }
        return {} as PageableProjects;
    }

    async getMyLikedProjects(): Promise<PageableProjects> {
        try {
            const response = await apiService.getData(true, `${url}/like`);
            if (response && response.status === httpStatusCode.OK) {
                return this.toPageableProjects(response);
            }
        } catch (error) {
            console.error(error);
        }
        return {} as PageableProjects;
    }

    async getPopularProjects(pageNumber: number | undefined, pageSize: number | undefined): Promise<PageableProjects> {
        try {
            const newUrl = `${url}/popular`;
            const params = {
                page: pageNumber,
                size: pageSize
            }
            try {
                const response = await apiService.getData(true, newUrl, {params});
                if (response && response.status === httpStatusCode.OK) {
                    return this.toPageableProjects(response);
                }
            } catch (error) {
                const response = await apiService.getData(false, newUrl, {params});
                if (response && response.status === httpStatusCode.OK) {
                    return this.toPageableProjects(response);
                }
            }
        } catch (error) {
            console.error(error);
        }
        return {} as PageableProjects;
    }

    async getMyProjects(): Promise<PageableProjects> {
        try {
            const response = await apiService.getData(true, `${url}/my-project`);
            if (response && response.status === httpStatusCode.OK) {
                return this.toPageableProjects(response);
            }
        } catch (error) {
            console.error(error);
        }
        return {} as PageableProjects;
    }

    async getProjectDetail(projectId: number): Promise<ProjectDetailDto> {
        try {
            const response = await apiService.getData(false, `${url}/${projectId}`);
            if (response && response.status === httpStatusCode.OK) {
                return response.data as ProjectDetailDto;
            }
        } catch (error) {
            console.error(error);
            return Promise.reject();
        }
        return {} as ProjectDetailDto;
    }


    async updateProject(projectId: number, projectRequestDto: ProjectRequestDto, image: File | null): Promise<void> {
        try {
            const data: FormData = new FormData();
            data.append('project', new Blob([JSON.stringify(projectRequestDto as any)], {type: "application/json"}));
            data.append('image', image as any);

            const response = await apiService.postMultipartData(true, `${url}/${projectId}`, data);
            if (response && response.status === httpStatusCode.OK) {
                return;
            }
        } catch (error) {
            console.error(error);
            return Promise.reject();
        }
    }

    @CatchError
    async deleteProject(projectId: number): Promise<void> {
        const response = await apiService.deleteData(true, `${url}/${projectId}`, null);
        if (response && response.status === httpStatusCode.OK) {
            return;
        }
    }

    @CatchError
    async submitProject(projectId: number): Promise<void> {
        const response = await apiService.postData(true, `${url}/${projectId}/submit`, null);
        if (response && response.status === httpStatusCode.OK) {
            return;
        }
    }

    @CatchError
    async likeProject(projectId: number): Promise<void> {
        const response = await apiService.postData(true, `${url}/${projectId}/like`, null);
        if (response && response.status === httpStatusCode.OK) {
            return;
        }
    }

    @CatchError
    async unlikeProject(projectId: number): Promise<void> {
        const response = await apiService.deleteData(true, `${url}/${projectId}/like`, null);
        if (response && response.status === httpStatusCode.OK) {
            return;
        }
    }

    @CatchError
    async getProjectContributions(owner: String, repo: String): Promise<GitStatusDTO[]> {
        const response = await apiService.getData(true, `${url}/contributions/${owner}/${repo}`);
        if (response && response.status === httpStatusCode.OK) {
            console.log(owner,repo)
            return response.data as GitStatusDTO[];
        }
        return [] as GitStatusDTO[];
    }

    @CatchError
    async getBackMetrics(projectId: number): Promise<PageableBackPerformance> {
        const response = await apiService.getData(true, `${url}/${projectId}/back-metrics`);
        if (response && response.status === httpStatusCode.OK) {
            return this.toPageableBackMetrics(response) as PageableBackPerformance;
        }
        return {} as PageableBackPerformance;
    }

    @CatchError
    async postBackMetrics(projectId: number) {
        await apiService.postData(true, `${url}/${projectId}/back-metrics`, '');
    }

    toPageableBackMetrics = (response: AxiosResponse<any, any>): PageableBackPerformance => {
        const data = response.data as pageableData;
        return Builder<PageableBackPerformance>()
            .backMetrics(data.content as BackPerformanceResponseDto[])
            .pageable(Builder<PageableDto>()
                .pageNumber(data.pageable.pageNumber)
                .totalPages(data.totalPages)
                .build())
            .build();
    }

    @CatchError
    async getBackMetricsMessageCounts(projectId: number): Promise<BackPerformanceMessageResponseDto> {
        const response = await apiService.getData(true, `${url}/${projectId}/message-count`);
        if (response && response.status === httpStatusCode.OK) {
            console.log(response.data);
            return response.data;
        }
        return {} as BackPerformanceMessageResponseDto;
    }
}

export {
    ProjectService
}