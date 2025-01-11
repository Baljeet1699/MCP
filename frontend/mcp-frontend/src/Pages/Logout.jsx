import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { doLogout } from "../auth";


const Logout = () => {
    const navigate = useNavigate();

    useEffect(() => {
        doLogout(()=> {navigate("/")}); // Perform logout logic
    }, [navigate]);

    return <div>Logging out...</div>;
};

export default Logout;
