import classNames from "classnames";
import { Message } from "../../api/entities";
import placeholder from "../../assets/placeholder.png";
import "./style.scss";

const SidebarItem = ({
    avatarUrl,
    name,
    username,
    lastMessage,
    selected,
    onClick,
}: SidebarItemProps) => {
    if (lastMessage) {
        lastMessage.timestamp = new Date(lastMessage.timestamp);
    }

    return (
        <div
            className={classNames("sidebar-item", { selected: selected })}
            onClick={onClick}
        >
            <img
                src={avatarUrl ?? placeholder}
                alt={username}
                className="sidebar-item__img"
            />
            <div className="sidebar-item__content">
                <div className="sidebar-item__content__header">
                    <span className="sidebar-item__content__name">{name}</span>
                    <span className="sidebar-item__content__timestamp">
                        {lastMessage?.timestamp.toLocaleString("en-US", {
                            hour: "numeric",
                            minute: "numeric",
                            hourCycle: "h23",
                        })}
                    </span>
                </div>
                {lastMessage && (
                    <div className="sidebar-item__content__message">
                        <span className="sidebar-item__content__last-message">
                            {lastMessage?.payload}
                        </span>
                    </div>
                )}
            </div>
        </div>
    );
};

interface SidebarItemProps {
    avatarUrl?: string;
    name?: string;
    username?: string;
    lastMessage?: Message;
    selected?: boolean;
    onClick?: () => void;
}

export default SidebarItem;
