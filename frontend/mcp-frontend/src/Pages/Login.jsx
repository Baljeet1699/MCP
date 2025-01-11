
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Card, CardBody, CardHeader, Col, Container, Form, FormGroup, Input, Label, Row, Alert } from "reactstrap";
import { loginUser } from "../services/user-service";
import { doLogin } from "../auth";

const Login = () => {
    const navigate = useNavigate();
    const [loginDetails, setLoginDetails] = useState({
        username: "",
        password: ""
    });
    const [error, setError] = useState("");

    const handleChange = (event, field) => {
        let actualValue = event.target.value;
        setLoginDetails({ ...loginDetails, [field]: actualValue });
    };

    const handleReset = () => {
        setLoginDetails({
            username: "",
            password: ""
        });
        setError(""); // Clear error on reset
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        loginUser(loginDetails)
            .then(data => {
                // If login is successful, save user data and token
                doLogin(data, () => {
                    console.log("User logged in and details saved to localStorage.");
                });

                // Redirect to the course dashboard
                navigate("/admin/courseDashboard"); // Use absolute path
            })
            .catch(error => {
                console.error("Login failed:", error);
                setError("Invalid username or password. Please try again."); // Set error message
            });
    };

    return (
        <Container className="mt-5">
            <Row className="mt-3">
                <Col sm={{ size: 6, offset: 3 }}>
                    <Card color="dark" outline>
                        <CardHeader>
                            <h3>Fill in the login credentials</h3>
                        </CardHeader>
                        <CardBody>
                            {error && <Alert color="danger">{error}</Alert>}
                            <Form onSubmit={handleSubmit}>
                                <FormGroup>
                                    <Label for="email">Enter email</Label>
                                    <Input
                                        id="email"
                                        type="email"
                                        placeholder="Enter here"
                                        value={loginDetails.username}
                                        onChange={(e) => handleChange(e, 'username')}
                                    />
                                </FormGroup>
                                <FormGroup>
                                    <Label for="password">Enter password</Label>
                                    <Input
                                        id="password"
                                        type="password"
                                        placeholder="Enter here"
                                        value={loginDetails.password}
                                        onChange={(e) => handleChange(e, 'password')}
                                    />
                                </FormGroup>
                                <Container className="text-center">
                                    <Button color="dark" type="submit">Login</Button>
                                    <Button onClick={handleReset} type="reset" color="secondary" className="ms-2">
                                        Reset
                                    </Button>
                                </Container>
                            </Form>
                        </CardBody>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default Login;

