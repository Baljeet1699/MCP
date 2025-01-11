
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

const Header = () => {
    const [brandName] = useState("Master Cullinary Process");
    const navigate = useNavigate();

    const [menuItemsForAdmin] = useState([
        {
            title: "ManageCourses",
            link: "/admin/courseDashboard",
            id: 1,
        },
        {
            title: "ManageUsers",
            link: "/admin/manageUsers",
            id: 2,
        },
    ]);

    const [selectedItem, setSelectedItem] = useState(1);

    useEffect(() => {
        const savedItem = localStorage.getItem("selectedMenuItem");
        if (savedItem) {
            setSelectedItem(parseInt(savedItem, 10));
        }
    }, []);

    const handleItemClick = (id, link) => {
        setSelectedItem(id);
        localStorage.setItem("selectedMenuItem", id);
        navigate(link); // Use React Router's navigate
    };

    return (
        <div className="h-16 main flex justify-between items-center px-5 py-5 bg-gray-100">
            <div>
                <h1 className="font-bold text-2xl">{brandName}</h1>
            </div>
            <div className="space-x-7">
                {menuItemsForAdmin.map((item) => (
                    <button
                        key={item.id}
                        onClick={() => handleItemClick(item.id, item.link)}
                        className={`text-xl ${
                            selectedItem === item.id
                                ? "text-orange-600 font-bold"
                                : "text-black"
                        } hover:text-orange-600`}
                        style={{
                            textDecoration: "none",
                            background: "none",
                            border: "none",
                            cursor: "pointer",
                        }}
                    >
                        {item.title}
                    </button>
                ))}
            </div>
            <div>
                <a
                    style={{ textDecoration: "none" }}
                    className="bg-orange-600 rounded-full shadow-lg px-2 py-1 text-2xl text-white"
                    href="/logout"
                >
                    Log Out
                </a>
            </div>
        </div>
    );
};

export default Header;



