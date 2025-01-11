import { myAxios } from "./helper"

export const loginUser = (loginDetails) => {
    return myAxios.post('/AUTH-SERVICE/api/v1/auth/login',loginDetails).then((response) => response.data);
}