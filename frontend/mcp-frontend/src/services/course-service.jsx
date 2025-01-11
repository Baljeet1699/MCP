import { privateAxios } from "./helper"

export const loadAllCourses = () => {
    return privateAxios.get('/MCP-BACKEND/api/v1/videos/courses').then((response) => response.data);
}

export const loadCourseById = (courseId) => {
    return privateAxios.get("/MCP-BACKEND/api/v1/videos/course/"+courseId).then((response) => response.data);
};

// Delete a course by ID
export const deleteCourseById = (courseId) => {
    return privateAxios.delete(`/MCP-BACKEND/api/v1/videos/course/${courseId}`)
        .then((response) => {
            console.log(`Course with ID ${courseId} deleted successfully.`);
            return response.data;  // Return the response data for further use (e.g., success message)
        })
        .catch((error) => {
            console.error(`Error deleting course with ID ${courseId}:`, error);
            throw error;  // Propagate error to be handled elsewhere
        });
};

