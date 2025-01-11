
import { Button, Card, CardBody, CardHeader, Col, Container, Form, FormGroup, Input, Label, Row } from "reactstrap";
import Base from "../../components/Base";
import { v4 as uuidv4 } from 'uuid';
import VideoUpload from "../../components/VideoUpload";
import { useEffect, useState } from "react";
import { privateAxios } from "../../services/helper";
import { useNavigate, useParams } from "react-router-dom";
import { loadCourseById } from "../../services/course-service";

const CourseSetup = () => {
    const { courseId } = useParams();
    const navigate = useNavigate();

    const [isForUpdation, setIsForUpdation] = useState(false);
    const [videos, setVideos] = useState([]);
    const [courseData, setCourseData] = useState({
        courseName: "",
        courseDesc: "",
        videos: [],
    });

    useEffect(() => {
        if (courseId) {
            loadCourseById(courseId).then((data) => {
                setCourseData(data);
                setVideos(data.videos);
                setIsForUpdation(true);
            }).catch((error) => {
                console.log(error);
            });
        }
    }, [courseId]);

    const [files, setFiles] = useState([]);
    const [errors, setErrors] = useState({});

    const validateForm = () => {
        const validationErrors = {};
        if (!courseData.courseName.trim()) {
            validationErrors.courseName = "Course name is required.";
        } else if (courseData.courseName.length < 3) {
            validationErrors.courseName = "Course name must be at least 3 characters.";
        }

        if (!courseData.courseDesc.trim()) {
            validationErrors.courseDesc = "Course description is required.";
        } else if (courseData.courseDesc.length < 10) {
            validationErrors.courseDesc = "Description must be at least 10 characters.";
        }

        if (videos.length === 0) {
            validationErrors.videos = "At least one video must be uploaded.";
        }

        return validationErrors;
    };

    const addVideo = (video) => {
        setVideos((previousVideo) => [...previousVideo, video]);
    };

    // const addFile = (file) => {
    //     setFiles((previousfile) => [...previousfile, file]);
    // };

   
    const addFile = (fileObj) => {
        // Validate if the object contains a valid file
        if (fileObj.file instanceof File) {
            setFiles((prevFiles) => [...prevFiles, fileObj]); // Directly add the file object with ID
        } else {
            console.error("Attempted to add an invalid file:", fileObj);
        }
    };
    
    

    const formFieldChange = (event) => {
        setCourseData({
            ...courseData,
            [event.target.name]: event.target.value,
        });
    };

    useEffect(() => {
        setCourseData((prevData) => ({
            ...prevData,
            videos: videos,
        }));
    }, [videos]);

    const handleForm = (formEvent) => {
        formEvent.preventDefault();
        const validationErrors = validateForm();
        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors);
        } else {
            setErrors({});
            saveCourseToServer(courseData);
        }
    };

    // async function saveCourseToServer(courseData) {
    //     try {
    //         let formdata = new FormData();
    //         formdata.append("courseData", JSON.stringify(courseData));


    //         files.forEach((file) => {
    //             formdata.append("files", file);
    //         });

    //         if (!isForUpdation) {
    //             await privateAxios.post(`/MCP-BACKEND/api/v1/videos/course`, formdata, {
    //                 headers: { "Content-Type": "multipart/form-data" },
    //             }).then(() => navigate("/admin/courseDashboard"));
    //         } else {
    //             formdata.append("courseId", courseId);
    //             await privateAxios.put(`/MCP-BACKEND/api/v1/videos/course`, formdata, {
    //                 headers: { "Content-Type": "multipart/form-data" },
    //             }).then(() => navigate("/admin/courseDashboard"));
    //         }
    //     } catch (error) {
    //         console.log(error);
    //     }
    // }

    async function saveCourseToServer(courseData) {
        try {
            let formdata = new FormData();
    
            formdata.append("courseData", JSON.stringify(courseData));
    
            // Append files
            files.forEach((fileObj) => {
                if (fileObj.file instanceof File) {
                    formdata.append("files", fileObj.file, `${fileObj.id}_${fileObj.file.name}`);
                } else {
                    console.error("Invalid file type detected in files array:", fileObj.file);
                }
            });
            
            
    
            const url = isForUpdation
                ? `/MCP-BACKEND/api/v1/videos/course`
                : `/MCP-BACKEND/api/v1/videos/course`;
    
            const method = isForUpdation ? "put" : "post";
    
            await privateAxios[method](url, formdata, {
                headers: { "Content-Type": "multipart/form-data" },
            }).then(() => navigate("/admin/courseDashboard"));
        } catch (error) {
            console.error("Error saving course to server:", error);
        }
    }
    
    

    const removeVideo = (indexToRemove, event) => {
        event.preventDefault();
        setVideos((prevVideos) => prevVideos.filter((_, index) => index !== indexToRemove));
    };

    return (
        <Base>
            <div className="bg-gray-100 min-h-screen py-8">
                <Container className="mx-auto max-w-5xl bg-white shadow-lg rounded-lg p-8">
                    <div className="flex justify-between items-center mb-6">
                        <h1 className="text-2xl font-bold text-gray-800">{isForUpdation ? 'Update Course' : 'Create Course'}</h1>
                        <button
                            type="submit"
                            onClick={handleForm}
                            className="btn-primary"
                            >
                            {isForUpdation ? 'Update' : 'Create'}
                        </button>

                    </div>

                    <Form>
                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                            <Card className="p-6">
                                <CardHeader className="text-xl font-bold text-gray-800 mb-4">Course Details</CardHeader>
                                <CardBody>
                                    <FormGroup className="mb-4">
                                        <Label for="courseName" className="block text-gray-600 font-semibold mb-2">Course Name</Label>
                                        <Input
                                            type="text"
                                            id="courseName"
                                            name="courseName"
                                            placeholder="Enter course name"
                                            onChange={formFieldChange}
                                            value={courseData.courseName}
                                            className="w-full border rounded-lg py-2 px-4 focus:ring-2 focus:ring-blue-500"
                                        />
                                        {errors.courseName && (
                                            <p className="text-red-600 text-sm mt-2">{errors.courseName}</p>
                                        )}
                                    </FormGroup>

                                    <FormGroup>
                                        <Label for="courseDesc" className="block text-gray-600 font-semibold mb-2">Course Description</Label>
                                        <Input
                                            type="textarea"
                                            id="courseDesc"
                                            name="courseDesc"
                                            placeholder="Enter course description"
                                            onChange={formFieldChange}
                                            value={courseData.courseDesc}
                                            className="w-full border rounded-lg py-2 px-4 focus:ring-2 focus:ring-blue-500"
                                        />
                                        {errors.courseDesc && (
                                            <p className="text-red-600 text-sm mt-2">{errors.courseDesc}</p>
                                        )}
                                    </FormGroup>
                                </CardBody>
                            </Card>

                            <VideoUpload onVideoUpload={addVideo} onfileUpload={addFile} />
                        </div>

                        <div className="mt-6">
                            <Card className="p-6">
                                <CardHeader className="text-xl font-bold text-gray-800 mb-4">Uploaded Videos</CardHeader>
                                <CardBody>
                                    {videos.length > 0 ? (
                                        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                                            {videos.map((video, index) => (
                                                <div key={index} className="bg-white shadow rounded-lg p-4">
                                                    <h3 className="text-lg font-semibold text-gray-800 mb-2">{video.title}</h3>
                                                    <p className="text-gray-600 mb-4">{video.description}</p>
                                                    <button
                                                        onClick={(e) => removeVideo(index, e)}
                                                        className="bg-red-600 text-white py-2 px-4 rounded-lg hover:bg-red-700 focus:ring-4 focus:ring-red-300"
                                                    >
                                                        Delete
                                                    </button>
                                                </div>
                                            ))}
                                        </div>
                                    ) : (
                                        <p className="text-gray-600 text-center">No videos uploaded yet.</p>
                                    )}
                                </CardBody>
                            </Card>
                        </div>
                    </Form>
                </Container>
            </div>
        </Base>
    );
};

export default CourseSetup;


