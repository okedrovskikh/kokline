import { Link, useNavigate } from "@tanstack/react-router";
import { useState } from "react";
import { handleUpload } from "../api/cloudinary";
import { AvatarUpload, Popup } from "../components";

const Signup = () => {
    const navigate = useNavigate({ from: "/signup" });

    const [name, setName] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [avatar, setAvatar] = useState<string>("");

    const [popupText, setPopupText] = useState("");
    const [popupType, setPopupType] = useState<"success" | "info" | "error">(
        "info"
    );
    const [popupOpen, setPopupOpen] = useState(false);
    const [popupOpening, setPopupOpening] = useState(false);

    const handleSignup = async () => {
        const response = await fetch(
            `${import.meta.env.VITE_API_URL}/auth/signup`,
            {
                method: "POST",
                headers: {
                    "Access-Control-Allow-Origin": "*",
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    name: name,
                    nickname: username,
                    credits: password,
                    avatarUrl: avatar ? await handleUpload(avatar) : null,
                }),
                credentials: "include",
                mode: "cors",
            }
        );

        if (response.ok) {
            navigate({ to: "/login", replace: true });
        } else {
            if (!popupOpen) {
                setPopupText("Invalid credentials");
                setPopupType("error");
                setPopupOpen(true);
                setPopupOpening(true);
            }
        }
    };

    return (
        <div className="login-wrapper">
            <div className="login">
                <header className="login__header">
                    <h2 className="login__header__title">Sign up</h2>
                    <span className="login__header__subtitle">to continue</span>
                </header>
                <form className="login__form">
                    <AvatarUpload avatar={avatar} setAvatar={setAvatar} />
                    <input
                        type="text"
                        placeholder="Name"
                        onChange={(event) => {
                            setName(event.target.value);
                        }}
                        value={name}
                        onKeyDown={(e) => {
                            if (e.key === "Enter") {
                                handleSignup();
                            }
                        }}
                        tabIndex={0}
                        autoFocus
                    />
                    <input
                        type="text"
                        placeholder="Username"
                        onChange={(event) => {
                            setUsername(event.target.value);
                        }}
                        value={username}
                        onKeyDown={(e) => {
                            if (e.key === "Enter") {
                                handleSignup();
                            }
                        }}
                        tabIndex={0}
                    />
                    <input
                        type="password"
                        placeholder="Password"
                        onChange={(event) => {
                            setPassword(event.target.value);
                        }}
                        value={password}
                        onKeyDown={(e) => {
                            if (e.key === "Enter") {
                                handleSignup();
                            }
                        }}
                        tabIndex={0}
                    />
                </form>
                <div className="login__footer">
                    <button
                        className="login__footer__button"
                        disabled={!name || !username || !password}
                        onClick={handleSignup}
                    >
                        Continue
                    </button>
                    <Link className="login__footer__link" to="/login">
                        Already have an account?
                    </Link>
                </div>
            </div>
            <Popup
                text={popupText}
                type={popupType}
                isOpen={popupOpen}
                setIsOpen={setPopupOpen}
                opening={popupOpening}
                setOpening={setPopupOpening}
            />
        </div>
    );
};

export default Signup;
