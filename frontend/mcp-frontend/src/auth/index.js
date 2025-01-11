
export const doLogin = (data,next) => {
    localStorage.setItem("data",JSON.stringify(data));
    next();
}

export const doLogout = (next) => {
    localStorage.removeItem("data");
    next();
}


export const isLoggedIn = () => {
    // Checks if user data is available in localStorage
    return localStorage.getItem('data') !== null;
};

export const getCurrentUser = () => {
    if(isLoggedIn())
        return JSON.parse(localStorage.getItem("data"))?.user;
    else
        return undefined;
}

export const getToken = () => {
    if(isLoggedIn())
        return JSON.parse(localStorage.getItem("data"))?.token;
    else
        return null;
}