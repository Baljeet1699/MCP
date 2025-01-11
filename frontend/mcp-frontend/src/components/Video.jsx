import { useEffect, useState } from 'react'
import VideoUpload from './VideoUpload';
import { loadVideo } from '../services/video-service';


const Video = () => {

    const [count, setCount] = useState(0);
    const [video,setVideo] = useState(null);
    const [videoId, setVideoId] = useState("ce16e7db-1168-4f6f-a9f0-b1c827293f9f");
  
    useEffect(() => {
        loadVideo(videoId).then((data)=> {
            setVideo(data);
        }).catch((error)=> {
            console.log(error);
        }) 
    
      },[]);

    return(
        <div className='flex flex-col items-center space-y-10 justify-center py-10'>
        <h1 className="text-5xl font-bold dark:text-gray-100">
          MCP
        </h1>
        <div className='flex w-full justify-around'>
          <div>
            <h1 className='text-white'>Playing Video</h1>
            {/* <video
            style={{
              width: 500,
            }}
            controls
            src={`http://localhost:8989/MCP_BACKEND/api/v1/videos/range/stream/${videoId}`}></video> */}

            <video
                id="my-video"
                className="video-js"
                controls
                preload="auto"
                width="840"
                data-setup="{}"
              >
                <source src={video} type="video/mp4" />
              
                <p className="vjs-no-js">
                  To view this video please enable JavaScript, and consider upgrading to a
                  web browser that
                  <a href="https://videojs.com/html5-video-support/" target="_blank"
                    >supports HTML5 video</a
                  >
                </p>
              </video>
          </div>
          <VideoUpload />
          </div>
      </div>
    );
};

export default Video;