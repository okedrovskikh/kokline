import { Message, User } from "../../api/entities";

import "./style.scss";

const ChatMessage = ({ user, message, isMe }: ChatMessageProps) => {
    message.timestamp = new Date(message.timestamp);

    return (
        <div className={`chat-message${isMe ? " me" : ""}`}>
            {!isMe && (
                <img
                    src={user.avatarUrl}
                    alt="avatar"
                    className="chat-message__avatar"
                />
            )}
            <svg
                width="7"
                height="17"
                viewBox="0 0 7 17"
                fill="inherit"
                xmlns="http://www.w3.org/2000/svg"
                className="chat-message__tail"
            >
                <path
                    fillRule="evenodd"
                    clipRule="evenodd"
                    d="M0.999956 17H6.99996V0C6.80696 2.84 7.174 5.985 6 9C5.096 11.325 2.50396 13.267 0.324955 15.262C0.176169 15.398 0.0719784 15.5759 0.0260504 15.7722C-0.0198776 15.9685 -0.00539772 16.1741 0.0675905 16.362C0.140579 16.55 0.268667 16.7114 0.435052 16.8253C0.601437 16.9391 0.798348 17 0.999956 17Z"
                    fill="inherit"
                />
            </svg>
            <div className="chat-message__bubble">
                {!isMe && (
                    <div className="chat-message__bubble__name">
                        {user.name}
                    </div>
                )}
                <div className="chat-message__bubble__content">
                    {message.payload}
                    <span className="chat-message__bubble__timestamp">
                        {message.timestamp.toLocaleTimeString("en-US", {
                            hour: "numeric",
                            minute: "numeric",
                            hourCycle: "h23",
                        })}
                    </span>
                </div>
            </div>
        </div>
    );
};

interface ChatMessageProps {
    user: User;
    message: Message;
    isMe: boolean;
}

export default ChatMessage;
