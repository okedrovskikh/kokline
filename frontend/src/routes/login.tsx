import { Link, useNavigate } from "@tanstack/react-router";
import { useState } from "react";

const Login = () => {
    const navigate = useNavigate({ from: "/login" });

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const handleLogin = async () => {
        const response = await fetch(
            `${import.meta.env.VITE_API_URL}/auth/login`,
            {
                method: "POST",
                headers: {
                    "Access-Control-Allow-Origin": "*",
                    "Content-Type": "application/json",
                    Authorization: "Basic " + btoa(username + ":" + password),
                },
                credentials: "include",
                mode: "cors",
            }
        );

        if (response.ok) {
            const data = await response.json();

            sessionStorage.setItem("user", JSON.stringify(data));
            navigate({ to: "/", replace: true });
        }
    };

    return (
        <div className="login-wrapper">
            <div className="login">
                <header className="login__header">
                    <h2 className="login__header__title">Log in</h2>
                    <span className="login__header__subtitle">to continue</span>
                </header>
                <form className="login__form">
                    <input
                        type="text"
                        placeholder="Username"
                        onChange={(event) => setUsername(event.target.value)}
                        value={username}
                        onKeyDown={(e) => {
                            if (e.key === "Enter") {
                                handleLogin();
                            }
                        }}
                        tabIndex={0}
                        autoFocus
                    />
                    <input
                        type="password"
                        placeholder="Password"
                        onChange={(event) => setPassword(event.target.value)}
                        value={password}
                        onKeyDown={(e) => {
                            if (e.key === "Enter") {
                                handleLogin();
                            }
                        }}
                        tabIndex={0}
                    />
                </form>
                <div className="login__footer">
                    <button
                        className="login__footer__button"
                        onClick={handleLogin}
                    >
                        Continue
                    </button>
                    <Link className="login__footer__link" to="/signup">
                        Don't have an account?
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default Login;
