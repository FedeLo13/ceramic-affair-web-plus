import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { resetPassword } from "../../api/auth-user";
import type { RecuperacionDTO } from "../../types/auth-user.types";

export default function ResetPassword() {
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [countdown, setCountdown] = useState<number | null>(null);

    const [searchParams] = useSearchParams();
    const token = searchParams.get("token");
    const navigate = useNavigate();

    if(!token) {
        return <p className="error-message">Invalid request.</p>;
    }

    useEffect(() => {
        if (countdown === null) return;

        if (countdown === 0) {
            navigate("/user-login");
            return;
        }

        const timer = setTimeout(() => setCountdown((prev) => (prev ?? 0) - 1), 1000);
        return () => clearTimeout(timer);
    }, [countdown, navigate]);

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        setError("");
        setSuccess("");

        if (newPassword.length < 8) {
            setError("Password must be at least 8 characters long.");
            return;
        }

        if (newPassword !== confirmPassword) {
            setError("Passwords do not match.");
            return;
        }

        try {
            await resetPassword({ token, nuevaPassword: newPassword } as RecuperacionDTO);
            setSuccess("Password reset successful! Redirecting to login...");
            setCountdown(3);
        } catch (err) {
            console.error(err);
            const message = err instanceof Error ? err.message : String(err);

            if (message.includes("Verification token has expired")) {
                setError("Recovery link expired. Please request a new one.");
            } else {
                setError("Password reset failed. Please try again.");
            }
        }
    };

    return (
        <div className="login">
            <form className="login-form" onSubmit={handleSubmit}>
                <h2 className="login-label">
                    Reset Password
                </h2>

                <div className="login-form-group">
                    <label htmlFor="newPassword" className="login-label">New Password</label>
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
                    <label htmlFor="confirmPassword" className="login-label">Confirm New Password</label>
                    <input
                        type="password"
                        id="confirmPassword"
                        name="confirmPassword"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
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
