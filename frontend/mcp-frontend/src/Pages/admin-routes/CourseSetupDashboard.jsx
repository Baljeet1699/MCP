import { useEffect, useState } from "react";
import Base from "../../components/Base";
import { Table } from "reactstrap";
import { deleteCourseById, loadAllCourses } from "../../services/course-service";

const CourseSetupDashboard = () => {

    const [courses, setCourses] = useState([]);

    useEffect(() => {
        loadAllCourses()
            .then((data) => {
                if (Array.isArray(data)) {
                    setCourses(data);
                } else {
                    console.error("Unexpected data format:", data);
                }
            })
            .catch((error) => {
                console.error("Error loading courses:", error);
            });
    }, []);
    
    
    const handleDeleteCourse = (courseId) => {
        const isConfirmed = window.confirm("Are you sure you want to delete this course?");
        
        if (isConfirmed) {
            // Optimistic UI Update: Remove course locally from state immediately
            setCourses((prevCourses) => prevCourses.filter((course) => course.courseId !== courseId));
    
            deleteCourseById(courseId)
                .then((response) => {
                    console.log("Course deleted:", response);
                    // Optionally, reload the courses after deletion if the API does not automatically return the updated list
                    loadAllCourses()
                        .then((data) => {
                            setCourses(data); // Update the state with the new list
                        })
                        .catch((error) => {
                            console.error("Error reloading courses:", error);
                        });
                })
                .catch((error) => {
                    console.error("Error deleting course:", error);
                    // If deletion failed, we revert the optimistic UI update
                    loadAllCourses()
                        .then((data) => {
                            setCourses(data); // Re-fetch the list
                        })
                        .catch((error) => {
                            console.error("Error reloading courses:", error);
                        });
                });
        } else {
            console.log("Course deletion cancelled.");
        }
    };
    


    return (
        <Base>
            <div className="px-5 py-1 mt-4">
                <a style={{textDecoration: 'none'}} className="bg-orange-600 rounded-full shadow-lg px-2 py-1 text-white float-right " href="/admin/courseSetup/">CREATE COURSE</a>
                <Table>
                    <thead>
                        <tr>
                             <th>Course Name</th>
                             <th>Description</th>
                             <th></th>
                        </tr>
                    </thead>
                    <tbody>
                       
                            {
                                courses?.map( (course,index) => (
                                    <tr key={index}>
                                    <th scope="row">{course.courseName}</th>
                                    <td>{course.courseDesc}</td>
                                    <td>
                                        <a style={{textDecoration: 'none'}} className="bg-gray-500 rounded-full shadow-lg px-2 py-1 text-white float-right m-1" href={"/admin/courseSetup/"+course.courseId} >UPDATE</a>
                                        <a style={{textDecoration: 'none'}} className="bg-gray-800 rounded-full shadow-lg px-2 py-1 text-white float-right m-1" href="" onClick={() => handleDeleteCourse(course.courseId)}>DELETE</a>
                                    </td>
                                    </tr>
                                ))
                            }
                            
                    </tbody>
                </Table>
            </div>    
        </Base>
    );

}

export default CourseSetupDashboard;