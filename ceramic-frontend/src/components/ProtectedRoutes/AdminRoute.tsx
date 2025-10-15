import { Navigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import type { JSX } from "react";

interface AdminRouteProps {
  children: JSX.Element;
}

const AdminRoute = ({ children }: AdminRouteProps) => {
  const { hasRole } = useAuth();

  if (!hasRole("ADMIN")) {
    return <Navigate to="/pieces" replace />;
  }

  return children;
};

export default AdminRoute;
