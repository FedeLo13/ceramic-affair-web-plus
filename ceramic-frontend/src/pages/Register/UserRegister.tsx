import { useState } from "react";
import { useGoogleReCaptcha } from "react-google-recaptcha-v3";
import { registerUser } from "../../api/auth-user";
import type { LoginDTO } from "../../types/auth-user.types";

export default function UserRegister() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const { executeRecaptcha } = useGoogleReCaptcha();

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        setError("");
        setSuccess("");

        if (!executeRecaptcha) {
            console.error('Recaptcha not executed');
            return;
        }

        const recaptchaToken = await executeRecaptcha('register');

        const trimmedEmail = email.trim();
        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(trimmedEmail)) {
            setError("Please enter a valid email address.");
            return;
        }

        if (password.length < 8) {
            setError("Password must be at least 8 characters long.");
            return;
        }

        if (password !== confirmPassword) {
            setError("Passwords do not match.");
            return;
        }

        try {
            await registerUser({ email: trimmedEmail, password, recaptchaToken } as LoginDTO);

            setSuccess("Registration successful! Please check your email to verify your account.");
            setEmail("");
            setPassword("");
            setConfirmPassword("");
        } catch (err) {
            console.error(err);
            setError("Registration failed. Please try again.");
        }
    };

    return (
        <div className="login">
            <form className="login-form" onSubmit={handleSubmit}>
                <h2 className="login-label">
                    Create Account
                </h2>

                <div className="login-form-group">
                    <label htmlFor="email" className="login-label">Email</label>
                    <input
                        type="email"
                        id="email"
                        name="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        onKeyDown={(e) => {
                            if (e.key === " ") {
                                e.preventDefault();
                            }
                        }}
                        onPaste={(e) => {
                            const pasted = e.clipboardData.getData("text");
                            if (/\s/.test(pasted)) {
                                e.preventDefault();
                            }
                        }}
                        required 
                    />
                </div>

                <div className="login-form-group">
                    <label htmlFor="password" className="login-label">Password</label>
                    <input
                        type="password"
                        id="password"
                        name="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required 
                    />
                </div>

                <div className="login-form-group">
                    <label htmlFor="confirmPassword" className="login-label">Confirm Password</label>
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

                <button type="submit" className="login-button">Register</button>
            </form>
        </div>
    );
}
