import { loginUser } from "../../api/auth-user";
import LoginForm from "./LoginForm";

export default function UserLogin() {
    return (
        <LoginForm
            onSubmit={loginUser}
            redirectTo="/pieces"
        />
    );
}