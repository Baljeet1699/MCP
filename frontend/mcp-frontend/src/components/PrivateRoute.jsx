
import { Navigate, Outlet } from "react-router-dom";
import { isLoggedIn } from "../auth"; // Assuming this checks login status

const PrivateRoute = () => {
    if (!isLoggedIn()) {
        // Redirect to login if not authenticated
        return <Navigate to="/" />;
    }
    return <Outlet />; // Render nested routes
};

export default PrivateRoute;

