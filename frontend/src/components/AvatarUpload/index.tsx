import { FilePlusIcon } from "@radix-ui/react-icons";
import { useRef, useState } from "react";

import "./style.scss";

const AvatarUpload = ({ avatar, setAvatar }: AvatarUploadProps) => {
    const [opacity, setOpacity] = useState(avatar ? 1 : 0);
    const inputRef = useRef<HTMLInputElement>(null);

    return (
        <div
            className="avatar-wrapper"
            onClick={() => {
                inputRef.current?.click();
            }}
        >
            <img src={avatar} alt="" style={{ opacity: opacity }} />
            <div className="avatar-wrapper__icon">
                <FilePlusIcon width={16} height={16} />
                Upload
            </div>
            <input
                type="file"
                accept="image/*"
                className="avatar-wrapper__input"
                ref={inputRef}
                onChange={(event) => {
                    const reader = new FileReader();

                    reader.onload = (readerEvent) => {
                        if (readerEvent.target?.result) {
                            setAvatar(readerEvent.target.result.toString());
                        }
                    };

                    reader.readAsDataURL(event.target.files![0]);

                    if (event.target.files) {
                        setOpacity(1);
                    }

                    if (!event.target.files) {
                        setOpacity(0);
                    }
                }}
            />
        </div>
    );
};

interface AvatarUploadProps {
    avatar: string;
    setAvatar: (avatar: string) => void;
}

export default AvatarUpload;
