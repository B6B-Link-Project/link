import axios, {AxiosError, AxiosInstance, AxiosResponse} from "axios";
import store from "@/store";

const {VITE_VUE_API_URL, VITE_VUE_API_LIVE_URL} = import.meta.env;

function createLocalAxios(requireAuth: boolean, isFormData?: boolean): AxiosInstance {
    const {DEV} = import.meta.env;
    let URL = "";
    if (DEV) URL = VITE_VUE_API_URL;
    else URL = VITE_VUE_API_LIVE_URL

    const local: any = axios.create({
        baseURL: URL,
        withCredentials: true,
        headers: {
            "Content-Type": !isFormData ? "application/json;charset=utf-8" : 'multipart/form-data',
        },
    });
    if (requireAuth) {
        authorization(local);
        tokenInterceptor(local);
    }
    return local;
}

class TokenMissingError extends Error {
    isAxiosError: boolean;
    response: { status: number };

    constructor(message: string, statusCode: number) {
        super(message); // 부모 클래스의 생성자 호출
        this.name = "TokenMissingError"; // 오류 이름 설정
        this.isAxiosError = true; // Axios 오류 표시
        this.response = {status: statusCode}; // 응답 상태 코드 설정
    }
}

function authorization(axiosInstance: any) {
    const token = store.getters.getToken;
    if(token) {
        if(token.substring(0,6) !== 'Bearer') {
            axiosInstance.defaults.headers['Authorization'] = `Bearer ${token}`;
        } else {
            axiosInstance.defaults.headers['Authorization'] = `${token}`;
        }
    }
    else {
        throw new TokenMissingError('Authorization token is missing', 401);
    }
}

function tokenInterceptor(axiosInstance: any) {
    axiosInstance.interceptors.response.use(
        async (response: AxiosResponse) => {
            const authToken = response.headers['authorization'];
            if (authToken) {
                await store.dispatch("updateToken", authToken);
            }
            return response;
        },
         async (error: AxiosError) => {
             return Promise.reject(error);
         }
    );
}
export {createLocalAxios};
