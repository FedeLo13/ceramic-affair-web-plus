import { useState } from "react";
import { useGoogleReCaptcha } from "react-google-recaptcha-v3";
import { changePassword } from "../../api/auth-user";
import type { CambioDTO } from "../../types/auth-user.types";
import { useAuth } from "../../context/AuthContext";

export default function UserRegister() {
    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmNewPassword, setConfirmNewPassword] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const { executeRecaptcha } = useGoogleReCaptcha();
    const { userEmail } = useAuth();

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        setError("");
        setSuccess("");

        if (!executeRecaptcha) {
            console.error('Recaptcha not executed');
            return;
        }

        const recaptchaToken = await executeRecaptcha('change_password');

        if (newPassword.length < 8) {
            setError("Password must be at least 8 characters long.");
            return;
        }

        if (newPassword !== confirmNewPassword) {
            setError("Passwords do not match.");
            return;
        }

        try {
            await changePassword({ email: userEmail!, token: recaptchaToken, antiguaPassword: oldPassword, nuevaPassword: newPassword } as CambioDTO);

            setSuccess("Password changed successfully!");
            setOldPassword("");
            setNewPassword("");
            setConfirmNewPassword("");
        } catch (err) {
            console.error(err);
            setError("Password change failed. Please try again.");
        }
    };

    return (
        <div className="login">
            <form className="login-form" onSubmit={handleSubmit}>
                <h2 className="login-label">
                    Change Password
                </h2>

                <div className="login-form-group">
                    <label htmlFor="oldPassword" className="login-label">Old Password</label>
                    <input
                        type="password"
                        id="oldPassword"
                        name="oldPassword"
                        value={oldPassword}
                        onChange={(e) => setOldPassword(e.target.value)}
                        required 
                    />
                </div>

                <div className="login-form-group">
                    <label htmlFor="password" className="login-label">New Password</label>
                    <input
                        type="password"
                        id="newPassword"
                        name="newPassword"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        required
                    />
                </div>

                <div className="login-form-group">
                    <label htmlFor="confirmNewPassword" className="login-label">Confirm New Password</label>
                    <input
                        type="password"
                        id="confirmNewPassword"
                        name="confirmNewPassword"
                        value={confirmNewPassword}
                        onChange={(e) => setConfirmNewPassword(e.target.value)}
                        required
                    />
                </div>

                {error && <p className="error-message">{error}</p>}
                {success && <p className="success-message">{success}</p>}

                <button type="submit" className="login-button">Confirm</button>
            </form>
        </div>
    );
}
