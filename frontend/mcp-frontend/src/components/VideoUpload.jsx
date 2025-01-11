
import React, { useState } from 'react';
import videoLogo from '../assets/upload.png';
import { v4 as uuidv4 } from 'uuid';
import { Alert, Progress } from "flowbite-react";
import { Card, CardBody, CardHeader } from 'reactstrap';


const VideoUpload = ({ onVideoUpload, onfileUpload }) => {
    const [selectedFile, setSelectedFile] = useState(null);
    const [meta, setMeta] = useState({
        title: "",
        description: "",
    });
    const [progress, setProgress] = useState(0);
    const [uploading, setUploading] = useState(false);
    const [message, setMessage] = useState('');

    function handleFileChange(event) {
      const file = event.target.files[0];
      if (file) {
          console.log("Selected file:", file);
          setSelectedFile(file);
      } else {
          console.error("No file selected or invalid file.");
      }
  }
  

    function formFieldChange(event) {
        setMeta({
            ...meta,
            [event.target.name]: event.target.value,
        });
    }

    // function handleUpload(event) {
    //   event.preventDefault();
    //     if (!selectedFile) {
    //         alert('Please select a file');
    //         return;
    //     }

    //     const VideoData = {
    //         id: uuidv4(),
    //         title: meta.title,
    //         description: meta.description,
    //     };

    //     onVideoUpload(VideoData);
    //     onfileUpload(selectedFile);

    //     setMeta({
    //       title: "",
    //       description: "",
    //     });
    //     setSelectedFile(null);
    // }

    function handleUpload(event) {
      event.preventDefault();
      if (!selectedFile) {
          alert('Please select a file');
          return;
      }
  
      // Generate a unique ID once
      const videoId = uuidv4();
  
      // Create video metadata
      const videoData = {
          id: videoId,
          title: meta.title,
          description: meta.description,
      };

      if (!(selectedFile instanceof File)) {
        alert('Invalid file selected. Please try again.');
        return;
    }    
  
      // Send VideoData and file with the same ID
      onVideoUpload(videoData);
      onfileUpload({ file: selectedFile, id: videoId }); // Match file ID with video ID
  
      // Reset form
      setMeta({ title: "", description: "" });
      setSelectedFile(null);
  }
  
  

    return (
        <div>
            <Card className="shadow-lg">
                <CardHeader className="text-center bg-blue-600 text-white py-4 rounded-t-lg">
                    <h2 className="text-lg font-semibold">Upload Videos</h2>
                </CardHeader>
                <CardBody className="p-6 bg-white">
                    <div className="mb-4">
                        <label htmlFor="title" className="block text-gray-700 font-medium mb-2">
                            Video Title
                        </label>
                        <input
                            type="text"
                            id="title"
                            name="title"
                            placeholder="Enter video title"
                            onChange={formFieldChange}
                            value={meta.title}
                            className="block w-full border border-gray-300 rounded-md p-2 focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    <div className="mb-4">
                        <label htmlFor="description" className="block text-gray-700 font-medium mb-2">
                            Video Description
                        </label>
                        <textarea
                            id="description"
                            name="description"
                            placeholder="Enter video description"
                            rows={4}
                            onChange={formFieldChange}
                            value={meta.description}
                            className="block w-full border border-gray-300 rounded-md p-2 focus:ring-2 focus:ring-blue-500"
                        ></textarea>
                    </div>

                    <div className="flex items-center space-x-4 mb-4">
                        <div className="shrink-0">
                            <img
                                className="h-16 w-16 object-cover rounded-full"
                                src={videoLogo}
                                alt="Current profile"
                            />
                        </div>
                        <label className="block">
                            <span className="sr-only">Choose video to upload</span>
                            <input
                                type="file"
                                name="file"
                                onChange={handleFileChange}
                                className="block w-full text-sm text-gray-500
                                    file:mr-4 file:py-2 file:px-4
                                    file:rounded-full file:border-0
                                    file:bg-blue-50 file:text-blue-700
                                    hover:file:bg-blue-100"
                            />
                        </label>
                    </div>

                    {uploading && (
                        <Progress
                            progress={progress}
                            textLabel="Uploading"
                            size="lg"
                            labelProgress
                            labelText
                        />
                    )}

                    {message && (
                        <Alert color="success" onDismiss={() => setMessage('')}>
                            <span className="font-medium">Success alert! </span>
                            {message}
                        </Alert>
                    )}

                    <div className="flex justify-center">
                        <button
                            onClick={(e) => handleUpload(e)}
                            className="btn-primary"
                        >
                            Upload
                        </button>
                    </div>
                </CardBody>
            </Card>
        </div>
    );
};

export default VideoUpload;




