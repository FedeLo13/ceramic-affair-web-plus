import { useState } from "react";
import { useGoogleReCaptcha } from "react-google-recaptcha-v3";
import { requestPasswordReset } from "../../api/auth-user";
import type { OlvidoDTO } from "../../types/auth-user.types";

export default function UserRegister() {
    const [email, setEmail] = useState("");
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

        try {
            await requestPasswordReset({ email: trimmedEmail, recaptchaToken } as OlvidoDTO);

            setSuccess("Password reset request successful. Please check your email for further instructions.");
            setEmail("");
        } catch (err) {
            console.error(err);
            setError("Password reset request failed. Please try again.");
        }
    };

    return (
        <div className="login">
            <form className="login-form" onSubmit={handleSubmit}>
                <h2 className="login-label">
                    Reset Password Request
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

                {error && <p className="error-message">{error}</p>}
                {success && <p className="success-message">{success}</p>}

                <button type="submit" className="login-button">Submit</button>
            </form>
        </div>
    );
}
