import { Cross1Icon, ExitIcon } from "@radix-ui/react-icons";
import { useNavigate } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import { handleUpload } from "../../api/cloudinary";
import { User } from "../../api/entities";
import { logout } from "../../api/users";
import AvatarUpload from "../AvatarUpload";
import "./style.scss";

const SettingsModal = ({
    user,
    isOpen,
    setIsOpen,
    opening,
    setOpening,
    handleUserUpdate,
}: SettingsModalProps) => {
    const navigate = useNavigate({ from: "/" });

    const [avatar, setAvatar] = useState<string>(user.avatarUrl ?? "");
    const [name, setName] = useState<string>("");
    const [password, setPassword] = useState<string>("");

    const [closing, setClosing] = useState(false);

    useEffect(() => {
        if (avatar === user.avatarUrl || avatar == "") return;

        handleUpload(avatar).then((url) => {
            handleUserUpdate(JSON.stringify({ avatarUrl: url }));
        });
    }, [avatar]);

    if (!isOpen) return null;

    return (
        <div
            className={"overlay" + (opening ? " on-open" : "")}
            onAnimationEnd={(event) => {
                if (event.animationName !== "fade-in") return;
                setOpening(false);
            }}
            onClick={(event) => {
                if (event.target !== event.currentTarget) return;
                setClosing(true);
            }}
        >
            <div
                className={"modal settings" + (closing ? " on-close" : "")}
                onAnimationEnd={(event) => {
                    if (event.animationName !== "fade-out") {
                        return;
                    }

                    setIsOpen(false);
                    setClosing(false);
                }}
            >
                <div className="settings__header">
                    <h3>Settings</h3>
                    <Cross1Icon
                        width={16}
                        height={16}
                        onClick={() => setClosing(true)}
                    />
                </div>
                <div className="settings__user">
                    <AvatarUpload avatar={avatar} setAvatar={setAvatar} />
                    <div className="settings__user__info">
                        <h3>{user.name}</h3>
                        <span>@{user.nickname}</span>
                    </div>
                </div>
                <div className="settings__form">
                    <div className="settings__form__item">
                        <label htmlFor="text" className="settings__form__label">
                            Change name
                        </label>
                        <div className="settings__form__input-wrapper">
                            <input
                                type="text"
                                placeholder="Name"
                                className="settings__form__input"
                                onChange={(event) =>
                                    setName(event.target.value)
                                }
                                onKeyDown={(e) => {
                                    if (name.length < 1) return;
                                    if (e.key === "Enter") {
                                        handleUserUpdate(
                                            JSON.stringify({ name })
                                        );
                                    }
                                }}
                                value={name}
                                autoComplete="off"
                            />
                            <button
                                type="button"
                                className="settings__form__button"
                                onClick={async () => {
                                    if (name.length < 1) return;
                                    await handleUserUpdate(
                                        JSON.stringify({ name })
                                    );
                                }}
                            >
                                Change
                            </button>
                        </div>
                    </div>
                    <div className="settings__form__item">
                        <label htmlFor="text" className="settings__form__label">
                            Change password
                        </label>
                        <div className="settings__form__input-wrapper">
                            <input
                                type="password"
                                placeholder="Password"
                                className="settings__form__input"
                                onChange={(event) =>
                                    setPassword(event.target.value)
                                }
                                onKeyDown={(e) => {
                                    if (password.length < 1) return;
                                    if (e.key === "Enter") {
                                        handleUserUpdate(
                                            JSON.stringify({
                                                credits: password,
                                            })
                                        );
                                    }
                                }}
                                value={password}
                                autoComplete="off"
                            />
                            <button
                                type="button"
                                className="settings__form__button"
                                onClick={async () => {
                                    if (password.length < 1) return;
                                    await handleUserUpdate(
                                        JSON.stringify({ credits: password })
                                    );
                                }}
                            >
                                Change
                            </button>
                        </div>
                    </div>
                </div>
                <div className="settings__footer">
                    <div
                        className="settings__footer__item"
                        onClick={() => {
                            logout();
                            navigate({ to: "/login", replace: true });
                        }}
                    >
                        <ExitIcon width={16} height={16} />
                        <span>Logout</span>
                    </div>
                </div>
            </div>
        </div>
    );
};

interface SettingsModalProps {
    user: User;
    isOpen: boolean;
    setIsOpen: (isOpen: boolean) => void;
    opening: boolean;
    setOpening: (opening: boolean) => void;
    handleUserUpdate: (body: BodyInit) => void;
}

export default SettingsModal;
