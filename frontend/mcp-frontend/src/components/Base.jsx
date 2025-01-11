import Header from "./Header";


const Base = ({title="Master Cullinary Process",children}) => {

    return (
        <div className="container-fluid p-0 m-0">
            <Header />
            {children}
        </div>
    );

}

export default Base;