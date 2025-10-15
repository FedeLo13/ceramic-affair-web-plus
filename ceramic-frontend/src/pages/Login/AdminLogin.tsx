import { loginAdmin } from "../../api/auth-user";
import LoginForm from "./LoginForm";

export default function AdminLogin() {
    return (
        <LoginForm
            onSubmit={loginAdmin}
            redirectTo="/pieces"
        />
    );
}