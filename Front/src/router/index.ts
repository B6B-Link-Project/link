import {createRouter, createWebHistory} from 'vue-router';
import Index from '@/view/Index.vue';
import Recruit from '@/view/Recruit.vue'
import Login from "@/view/Login.vue";
import Register from "@/view/Register.vue";
import FindAccount from "@/view/FindAccount.vue";
import PasswordChange from "@/view/PasswordChange.vue";
import DetailInfo from "@/view/DetailInfo.vue";
import Video from "@/view/Video.vue";
import SharedProject from "@/view/SharedProject.vue";
import ProjectDetail from "@/view/ProjectDetail.vue";
import HackathonList from "@/view/HackathonList.vue";
import HackathonDetail from "@/view/HackathonDetail.vue";
import MyPage from "@/view/MyPage.vue";
import Exit from "@/view/Exit.vue";
import EditInfo from "@/view/EditInfo.vue";
import Refresh from "@/view/Refresh.vue";
import Test from "@/view/Test.vue";

const routes = [
    {path: '/', component: Index},
    {path: '/recruit', component: Recruit},
    {path: '/register', component: Register},
    {path: '/login', component: Login},
    {path: '/findAccount', component: FindAccount},
    {path: '/passwordChange', component: PasswordChange, props: true},
    {path: '/detailInfo', component: DetailInfo, meta:{ dataToSend: null }},
    {path: '/video/:id', component: Video},
    {path: '/sharedProject', component: SharedProject},
    {
        path: '/projectDetail', component: ProjectDetail, name: 'projectDetail',
        children: [{
            path: ':id',
            component: ProjectDetail,
            name: 'projectDetailById',
        },]
    },
    {path: '/hackathonList', component: HackathonList},
    {path: '/hackathonDetail', component: HackathonDetail},
    {path: '/myPage', component: MyPage},
    {path: '/exit', component: Exit},
    {path: '/editInfo', component: EditInfo},
    {path: '/refresh', component: Refresh},
    {path: '/test', component:Test}
];

const router = createRouter({
    history: createWebHistory('/'),
    routes,
    scrollBehavior() {
        return {top: 0};
    },
});

export default router;
