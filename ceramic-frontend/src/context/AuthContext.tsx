import { jwtDecode } from "jwt-decode";
import { createContext, useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

interface jwtPayload {
    exp: number;
    sub: string;
    userId: number;
    roles: string[];
}

interface AuthContextProps {
    token: string | null;
    isAuthenticated: boolean;
    roles: string[];
    hasRole: (role: string) => boolean;
    login: (token: string) => void;
    logout: () => void;
    userEmail: string | null;
    userId: number | null;
}

const AuthContext = createContext<AuthContextProps | undefined>(undefined);

export let globalLogout: (() => void) | null = null;

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [token, setToken] = useState<string | null>(localStorage.getItem("token"));
    const [roles, setRoles] = useState<string[]>([]);
    const [userEmail, setUserEmail] = useState<string | null>(null);
    const [userId, setUserId] = useState<number | null>(null);
    const navigate = useNavigate();

    // Verificar expiración al cargar la aplicación
    useEffect(() => {
        if (token && isTokenExpired(token)) {
            handleLogout();
        } else if (token) {
            const decoded = jwtDecode<jwtPayload>(token);
            setRoles(decoded.roles || []);
            setUserEmail(decoded.sub || null);
        }
    }, []);

    // Use effect para verificar el estado del token (temporal para desarrollo)
    useEffect(() => {
        if (token && !isTokenExpired(token)) {
            const decoded = jwtDecode<jwtPayload>(token);
            console.log("✅ Usuario logueado:", decoded.sub);
            console.log("🧩 Roles del usuario:", decoded.roles);
        } else {
            console.log("❌ Usuario no logueado");
        }
    }, [token]);

    const handleLogin = (newToken: string) => {
        setToken(newToken);
        localStorage.setItem("token", newToken);
        
        const decoded = jwtDecode<jwtPayload>(newToken);
        setRoles(decoded.roles || []);
        setUserEmail(decoded.sub || null);
        setUserId(decoded.userId || null);

        // Auto logout si el token expira
        const expirationTime = decoded.exp * 1000 - Date.now();

        setTimeout(() => {
            console.log("⏰ Token expirado. Cerrando sesión automáticamente...");
            handleLogout();
        }, expirationTime);
    }

    const handleLogout = () => {
        setToken(null);
        setRoles([]);
        setUserEmail(null);
        localStorage.removeItem("token");
        navigate("/pieces"); // Redirigir a la página de piezas
    }

    useEffect(() => {
        globalLogout = handleLogout;
        return () => { globalLogout = null; };
    } , []);

    const isTokenExpired = (token: string): boolean => {
        try {
            const { exp } = jwtDecode<jwtPayload>(token);
            return Date.now() >= exp * 1000;
        } catch {
            return true; // Si hay un error al decodificar, consideramos que el token está expirado
        }
    };

    const hasRole = (role: string): boolean => roles.includes(role);

    return (
        <AuthContext.Provider 
            value={{ 
                token, 
                isAuthenticated: !!token && !isTokenExpired(token),
                roles,
                hasRole, 
                login: handleLogin, 
                logout: handleLogout,
                userEmail,
                userId
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
};