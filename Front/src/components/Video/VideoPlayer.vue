<script setup lang="ts">
import sample1 from "@/assets/another.png";
import sample2 from "@/assets/my.png";
import {OpenVidu, Publisher, Session, StreamManager, Subscriber} from "openvidu-browser";
import {OpenViduService} from "@/api/OpenViduService.ts";
import {onMounted, Ref, ref, watch} from "vue";
import {onBeforeRouteLeave} from "vue-router";
import router from "@/router";


class OVManager {

  OpenVidu: OpenVidu | undefined
  session: Session | undefined
  mainStreamManager: StreamManager | undefined
  publisher: Publisher | undefined
  subscribers: Array<StreamManager>
  openViduService: OpenViduService

  constructor() {
    this.OpenVidu = undefined;
    this.session = undefined;
    this.mainStreamManager = undefined;
    this.publisher = undefined;
    this.subscribers = [];
    this.openViduService = new OpenViduService();
  }

  joinSession(reservationId: number, myVideoEleRef: Ref<HTMLVideoElement>, guestVideoEleRef: Ref<HTMLVideoElement>) {

    this.OpenVidu = new OpenVidu();

    // --- 2) Init a session ---
    this.session = this.OpenVidu.initSession();

    // --- 3) Specify the actions when events take place in the session ---

    // On every new Stream received...
    this.session.on("streamCreated", ({stream}) => {
      if (!this.session) return;
      const subscriber = this.session.subscribe(stream, undefined);
      this.subscribers.push(subscriber);
      subscriber.addVideoElement(guestVideoEleRef.value);
    });

    // On every Stream destroyed...
    this.session.on("streamDestroyed", ({stream}) => {
      const index = this.subscribers.indexOf(stream.streamManager, 0);
      if (index >= 0) {
        this.subscribers[index].removeAllVideos();
        this.subscribers.splice(index, 1);
      }
    });

    // On every asynchronous exception...
    this.session.on("exception", (exception) => {
      console.warn(exception);
    });

    // --- 4) Connect to the session with a valid user token ---

    // Get a token from the OpenVidu deployment
    this.openViduService.getToken(reservationId)
        .then((token) => {

          // First param is the token. Second param can be retrieved by every user on event
          // 'streamCreated' (property Stream.connection.data), and will be appended to DOM as the user's nickname
          if (!this.session) {
            console.warn("세션이 아직 생성되지 않았습니다.")
            return;
          }
          // FIXME: 랜덤이 아닌 유효한 userID 할당
          this.session.connect(token, {clientData: "USER" + Math.floor(Math.random() * 1000)})
              .then(() => {

                if (!this.OpenVidu || !this.session)
                  return Promise.reject();

                // --- 5) Get your own camera stream with the desired properties ---

                // Init a publisher passing undefined as targetElement (we don't want OpenVidu to insert a video
                // element: we will manage it on our own) and with the desired properties
                let publisher = this.OpenVidu.initPublisher(undefined, {
                  audioSource: undefined, // The source of audio. If undefined default microphone
                  videoSource: undefined, // The source of video. If undefined default webcam
                  publishAudio: false, // Whether you want to start publishing with your audio unmuted or not
                  publishVideo: false, // Whether you want to start publishing with your video enabled or not
                  resolution: "640x480", // The resolution of your video
                  frameRate: 30, // The frame rate of your video
                  insertMode: "APPEND", // How the video is inserted in the target element 'video-container'
                  mirror: false, // Whether to mirror your local video or not
                });

                // Set the main video in the page to display our webcam and store our Publisher
                this.mainStreamManager = publisher;
                this.publisher = publisher;

                // --- 6) Publish your stream ---
                this.session.publish(this.publisher);

                // --- 6) Publish your stream ---
                this.publisher.addVideoElement(myVideoEleRef.value);

              })
              .catch((error) => {
                console.log("There was an error connecting to the session:", error.code, error.message);
                return Promise.reject(error);
              });
        })
        .catch(error => {
          console.warn(error);
          this.leaveSession();
          alert("오류가 발생했습니다. 다시 접속해주세요.");
        });

    window.addEventListener("beforeunload", this.leaveSession);
  }

  leaveSession() {
    // --- 7) Leave the session by calling 'disconnect' method over the Session object ---
    if (this.session) this.session.disconnect();

    // Empty all properties...
    this.session = undefined;
    this.mainStreamManager = undefined;
    this.publisher = undefined;
    this.subscribers = [];
    this.OpenVidu = undefined;

    // Remove beforeunload listener
    window.removeEventListener("beforeunload", this.leaveSession);
  }

  updateMainVideoStreamManager(stream: StreamManager) {
    if (this.mainStreamManager === stream) return;
    this.mainStreamManager = stream;
  }

  // updateVideoStream(myVideoEleRef: Ref<HTMLVideoElement>, guestVideoEleRef: Ref<HTMLVideoElement>) {
  //   if (this.mainStreamManager?.targetElement !== myVideoEleRef.value)
  //     this.mainStreamManager?.addVideoElement(myVideoEleRef.value);
  //   this.
  // }

  setVideoStat(stat: boolean, isPublisher?: boolean): void {
    if (isPublisher || isPublisher === undefined)
      this.publisher?.publishVideo(stat);
    else
      (this.subscribers[this.subscribers.length - 1] as Subscriber).subscribeToVideo(stat);
  }

  setAudioStat(stat: boolean, isPublisher?: boolean): void {
    if (isPublisher || isPublisher === undefined)
      this.publisher?.publishAudio(stat);
    else
      (this.subscribers[this.subscribers.length - 1] as Subscriber).subscribeToVideo(stat);
  }

}

const {reservationId} = defineProps({
  reservationId: {
    required: true,
    type: Number
  }
});

const myVideoEleRef = ref();
const guestVideoEleRef = ref();
const ovManager = new OVManager();

const videoStat = ref(false);
const audioStat = ref(false);
const changeVideoStat = () => {videoStat.value = !videoStat.value};
const changeAudioStat = () => {audioStat.value = !audioStat.value};
watch(() => videoStat.value, () => {
  ovManager.setVideoStat(videoStat.value);
});
watch(() => audioStat.value, () => {
  ovManager.setAudioStat(audioStat.value);
});

const leaveVideoCall = () => {
  router.push("/");
};

onMounted(() => {
  ovManager.joinSession(reservationId, myVideoEleRef, guestVideoEleRef);
});

onBeforeRouteLeave((to) => {
  try {
    if (to.path !== "/login" && !confirm("이 페이지를 떠나면 화상 대화가 종료됩니다."))
      return false;
    ovManager.leaveSession();
    return true;
  } catch (e) {
    // 정상적으로 종료되지 못해도 원하는 페이지 이동
    return true;
  }
});

</script>

<template>
  <div class="video-play-container">
    <!--    <img :src="sample1" alt="" class="video">-->
    <video ref="guestVideoEleRef" :poster="sample1" class="video"></video>
    <div class="myVideo-container">
      <!--      <img :src="sample2" alt="" class="myVideo" style="max-height: 200px">-->
      <video ref="myVideoEleRef" :poster="sample2" class="myVideo" style="max-height: 200px"></video>
    </div>
    <div class="button-container">
      <div class="video-button" @click="changeVideoStat()">
        <svg class="video-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="18" viewBox="0 0 24 18"
             fill="none">
          <path
              d="M15.9808 0.550534C16.6812 0.780002 17.1764 1.19361 17.6204 1.79999L17.7667 2.00926L18.3093 2.86583L18.3272 2.88975L18.3434 2.91057L18.3895 2.96243C18.6367 3.18419 19.0505 3.31816 19.3236 3.31816C21.8253 3.31816 23.8687 4.93544 23.9939 6.96859L24 7.1666V12.8909C24 15.5302 21.4678 17.6841 18.2946 17.7957L18.036 17.8002H5.964C2.75681 17.8002 0.140965 15.7161 0.00551006 13.1038L0 12.8909V7.1666C0 5.04146 2.09397 3.31816 4.6764 3.31816C4.94853 3.31816 5.3629 3.184 5.61053 2.96243L5.61906 2.95415L5.62974 2.94264L5.69145 2.86472L6.23258 2.00954C6.71493 1.28793 7.23964 0.805543 8.01826 0.55044C9.44507 0.0834314 14.5551 0.0834315 15.9808 0.550534ZM8.68362 1.92719C8.41796 2.01423 8.17743 2.2157 7.92074 2.56498L7.80962 2.72381L7.34779 3.45883L7.23412 3.62816C7.13923 3.75809 7.04432 3.86776 6.93594 3.96496C6.38343 4.45931 5.59016 4.74484 4.88498 4.79266L4.6764 4.79985L4.47949 4.80531C3.04797 4.88499 1.90345 5.82689 1.80664 7.0046L1.8 7.1666V12.8909C1.8 14.7205 3.54209 16.2158 5.73554 16.3134L5.964 16.3185H18.036C20.2583 16.3185 22.0752 14.8839 22.1938 13.0789L22.2 12.8909V7.1666C22.2 5.9143 21.0171 4.88861 19.5205 4.80531L19.3236 4.79985L19.1147 4.79266C18.4084 4.74483 17.6151 4.45923 17.0633 3.96431C16.9564 3.86839 16.862 3.7595 16.7677 3.63067L16.745 3.59874L16.718 3.55907L16.5787 3.34399L16.19 2.72409C15.9275 2.33183 15.6872 2.09209 15.4277 1.97162L15.3152 1.92719L15.172 1.888C13.937 1.60067 9.64129 1.61373 8.68362 1.92719ZM11.9996 6.32372C14.6039 6.32372 16.7144 8.06104 16.7144 10.2048C16.7144 12.3485 14.6039 14.0858 11.9996 14.0858C9.39538 14.0858 7.28484 12.3485 7.28484 10.2048C7.28484 8.06104 9.39538 6.32372 11.9996 6.32372ZM11.9996 7.80541C10.3895 7.80541 9.08484 8.87935 9.08484 10.2048C9.08484 11.5302 10.3895 12.6041 11.9996 12.6041C13.6098 12.6041 14.9144 11.5302 14.9144 10.2048C14.9144 8.87935 13.6098 7.80541 11.9996 7.80541ZM18.6052 5.63325C19.2679 5.63325 19.8052 6.0755 19.8052 6.62105C19.8052 7.12762 19.3419 7.54513 18.7451 7.60219L18.6052 7.60884C17.9316 7.60884 17.3944 7.16659 17.3944 6.62105C17.3944 6.11447 17.8576 5.69696 18.4544 5.6399L18.6052 5.63325Z"
              fill="white"/>
        </svg>
        <div class="button-text">비디오 {{videoStat ? "ON" : "OFF"}} </div>
      </div>
      <div class="mic-button" @click="changeAudioStat()">
        <svg class="mic-svg" xmlns="http://www.w3.org/2000/svg" width="19" height="18" viewBox="0 0 19 18" fill="none">
          <path fill-rule="evenodd" clip-rule="evenodd"
                d="M18.1857 7.08579C18.6363 7.08579 19 7.35631 19 7.68963C19 11.3869 15.1733 14.4306 10.3146 14.7393L10.3143 16.7063C10.3143 17.0396 9.94949 17.3101 9.5 17.3101C9.05052 17.3101 8.68571 17.0396 8.68571 16.7063L8.68646 14.7393C3.82729 14.431 0 11.3872 0 7.68963C0 7.35631 0.3648 7.08579 0.814286 7.08579C1.26486 7.08579 1.62857 7.35631 1.62857 7.68963C1.62857 10.9246 5.15931 13.5574 9.5 13.5574C13.8407 13.5574 17.3714 10.9246 17.3714 7.68963C17.3714 7.35631 17.7362 7.08579 18.1857 7.08579ZM9.5 0C12.3804 0 14.7245 1.7447 14.7245 3.88954V7.66395C14.7245 9.80799 12.3804 11.5527 9.5 11.5527C6.6196 11.5527 4.27554 9.80799 4.27554 7.66395V3.88954C4.27554 1.7447 6.6196 0 9.5 0ZM9.5 1.20768C7.51749 1.20768 5.90411 2.41053 5.90411 3.88954V7.66395C5.90411 9.14135 7.51749 10.345 9.5 10.345C11.4825 10.345 13.0959 9.14135 13.0959 7.66395V3.88954C13.0959 2.41053 11.4825 1.20768 9.5 1.20768Z"
                fill="#303030"/>
        </svg>
        <div class="button-text">음성 {{audioStat ? "ON" : "OFF"}}</div>
      </div>
      <div class="leave-button-container">
        <div class="leave-button" @click="leaveVideoCall()">
          화상 채팅 떠나기
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>

.leave-button-container {
  display: flex;
  width: 100%;
  justify-content: right;
}

.leave-button {
  width: 168px;
  height: 40px;
  border-radius: 5px;
  border: #FF6161 1px solid;
  background: #FF6161;
  color: #FFF;
  text-align: center;
  font-size: 16px;
  font-style: normal;
  font-weight: 600;
  line-height: 39px;
  transition: color 0.3s ease;
}

.leave-button:hover {
  background: white;
  color: #FF6161;
}

@media screen and (max-width: 573px) {
  .button-container {
    flex-direction: column;
  }

  .leave-button {
    width: 100%;
  }
}

@media screen and (min-width: 573px) {
  .button-container {
    position: absolute;
    flex-direction: row;
  }
}

.button-container {
  width: 100%;
  bottom: 45px;
  display: flex;
  max-height: 10px;
  gap: 16px;
  padding: 12px;
}

.button-text {
  line-height: 17px;
}

.video-button, .mic-button {
  padding: 10px;
  font-size: 14px;
  font-style: normal;
  font-weight: 600;
  text-align: center;
  display: flex;
  height: 40px;
  justify-content: center;
}

.video-button {
  gap: 13px;
  min-width: 130px;
  border-radius: 5px;
  background: #7D3CFF;
  color: white;
  transition: 0.3s color ease;
  border: #7D3CFF 1px solid;
}

.video-button:hover .video-icon path {
  fill: #7D3CFF;
}

.video-button:hover {
  background: white;
  color: #7D3CFF;
}

.mic-button {
  gap: 8px;
  min-width: 110px;
  border-radius: 5px;
  border: 2px solid #7D3CFF;
  background: white;
  color: #303030;
  transition: color 0.3s ease;
}

.mic-button:hover {
  background: #7D3CFF;
  color: white;
}

.mic-button:hover .mic-svg path {
  fill: white;
}

img {
  height: 100%;
  max-height: 600px;
  object-fit: cover;
}

video {
  height: 100%;
  max-height: 600px;
  object-fit: cover;
}

.video-play-container {
  position: relative;
}

.video {
  border-radius: 10px;
  width: 100%;
  object-fit: cover;
}

.myVideo {
  max-width: 300px;
  width: 20vw;
  border-radius: 10px;
}

.myVideo-container {
  position: absolute;
  top: 1%;
  right: 1%;
  display: flex;
  max-height: 10px;
}
</style>
