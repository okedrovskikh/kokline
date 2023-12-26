import { Cross1Icon } from "@radix-ui/react-icons";
import classNames from "classnames";
import { useState } from "react";
import "./style.scss";

const Popup = ({
    text,
    type,
    isOpen,
    setIsOpen,
    opening,
    setOpening,
}: PopupProps) => {
    const [closing, setClosing] = useState(false);

    if (isOpen) {
        setTimeout(() => {
            setClosing(true);
        }, 3000);
    }

    if (!isOpen) {
        return null;
    }

    return (
        <div className="popup-wrapper">
            <div
                className={classNames(
                    "popup",
                    type,
                    opening ? "on-open" : "",
                    closing ? "popup-on-close" : ""
                )}
                onAnimationEnd={(event) => {
                    if (event.animationName === "fade-in") {
                        setOpening(false);
                    }

                    if (event.animationName === "popup-fade-out") {
                        setIsOpen(false);
                        setClosing(false);
                    }
                }}
            >
                <span className="popup__text">{text}</span>
                <Cross1Icon
                    className="popup__close"
                    width={16}
                    height={16}
                    onClick={() => setClosing(true)}
                />
            </div>
        </div>
    );
};

interface PopupProps {
    text: string;
    type: "success" | "info" | "error";
    isOpen: boolean;
    setIsOpen: (isOpen: boolean) => void;
    opening: boolean;
    setOpening: (opening: boolean) => void;
}

export default Popup;
