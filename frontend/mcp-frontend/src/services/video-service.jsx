import { privateAxios } from "./helper"

export const loadVideo = (videoId) => {
    return privateAxios.get('/MCP_BACKEND/api/v1/videos/stream/range/'+videoId).then((response) => response.data);
}
