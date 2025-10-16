import type { JSX } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

interface UserRouteProps {
  children: JSX.Element;
}

const UserRoute = ({ children }: UserRouteProps) => {
    const { isAuthenticated } = useAuth();

    if (!isAuthenticated) {
        return <Navigate to="/pieces" replace />;
    }

    return children;
}

export default UserRoute;