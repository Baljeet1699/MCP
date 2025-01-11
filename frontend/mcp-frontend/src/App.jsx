
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Login from './Pages/Login';
import 'bootstrap/dist/css/bootstrap.min.css';
import Video from './components/Video';
import UserHome from './Pages/user-routes/UserHome';
import AdminHome from './Pages/admin-routes/AdminHome';
import PrivateRoute from './components/PrivateRoute'; // Make sure this is properly imported
import CourseSetupDashboard from './Pages/admin-routes/CourseSetupDashboard';
import CourseSetup from './Pages/admin-routes/CourseSetup';
import ManageUsers from './Pages/admin-routes/ManageUsers';
import Logout from './Pages/Logout';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public Route */}
        <Route path="/" element={<Login />} />
        <Route path="/logout" element={<Logout />} />

        {/* Protected Routes for User */}
        <Route element={<PrivateRoute />}> {/* Wrap the user-specific routes here */}
          <Route path="/user/userHome" element={<UserHome />} />
        </Route>

        {/* Protected Routes for Admin */}
        <Route path="/admin" element={<PrivateRoute />}> {/* Wrap the admin-specific routes here */}
          <Route path="home" element={<AdminHome />} />
          <Route path="courseDashboard" element={<CourseSetupDashboard />} />
          <Route path="courseSetup" element={<CourseSetup />} />
          <Route path="manageUsers" element={<ManageUsers />} />
          <Route path="courseSetup/:courseId" element={<CourseSetup />} />
        </Route>

        {/* Public Route for Video */}
        <Route path="/video" element={<Video />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;


