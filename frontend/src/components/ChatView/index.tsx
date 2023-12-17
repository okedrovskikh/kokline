import {
    DotsVerticalIcon,
    MagnifyingGlassIcon,
    PaperPlaneIcon,
    ViewVerticalIcon,
} from "@radix-ui/react-icons";
import { useEffect, useState } from "react";
import { getChat } from "../../api/chats";
import { Chat, Message, User } from "../../api/entities";
import { getUser } from "../../api/users";
import placeholder from "../../assets/placeholder.png";
import ChatMessage from "../ChatMessage";
import "./style.scss";

interface DirectMessages extends Chat {
    username?: string;
}

const ChatView = ({
    user,
    chatId,
    messages,
    users,
    sendMessage,
}: ChatViewProps) => {
    const [message, setMessage] = useState("");

    const [chat, setChat] = useState<Chat | DirectMessages | null>();

    useEffect(() => {
        getChat(chatId).then((chat) => {
            setChat(chat);

            if (chat.users.length === 2) {
                const otherUserId = chat.users.find(
                    (userId) => user.id !== userId
                );

                if (!otherUserId) {
                    return;
                }

                getUser(otherUserId).then((otherUser) => {
                    if (!otherUser) {
                        return;
                    }

                    setChat((chat) => {
                        if (!chat) {
                            return null;
                        }

                        return {
                            ...chat,
                            username: otherUser.nickname,
                            avatarUrl: otherUser.avatarUrl ?? placeholder,
                            name: otherUser.name,
                            id: chat.id,
                        };
                    });
                });
            }
        });
    }, [chatId]);

    if (!chat) {
        return (
            <div className="chat-selection-wrapper">
                <span className="chat-selection-text">Loading...</span>
            </div>
        );
    }

    return (
        <main className="chat-view">
            <div className="chat-view__header">
                <div className="chat-view__header__left">
                    <img
                        src={chat.avatarUrl}
                        alt={chat.name}
                        className="chat-view__header__img"
                    />
                    <div className="chat-view__header__info">
                        <span className="chat-view__header__info__name">
                            {chat.name}
                        </span>
                        <span className="chat-view__header__info__members">
                            {chat.users.length === 2
                                ? `@${(chat as DirectMessages).username}`
                                : `${chat.users.length} members`}
                        </span>
                    </div>
                </div>
                <div className="chat-view__header__right">
                    <MagnifyingGlassIcon width={20} height={20} />
                    <ViewVerticalIcon width={20} height={20} />
                    <DotsVerticalIcon width={20} height={20} />
                </div>
            </div>
            <div className="chat-view__messages">
                {messages.map((msg) => (
                    <ChatMessage
                        isMe={user.id === msg.userId}
                        message={msg}
                        user={users.find((u) => u.id === msg.userId)!}
                        key={msg.id}
                    />
                ))}
            </div>
            <div className="chat-view__input-wrapper">
                <input
                    className="chat-view__input-wrapper__input"
                    type="text"
                    placeholder="Write a message..."
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    onKeyDown={(e) => {
                        if (e.key === "Enter" && message.length > 0) {
                            sendMessage(message);
                            setMessage("");
                        }
                    }}
                    tabIndex={0}
                    autoFocus
                />
                <button
                    className="chat-view__input-wrapper__button"
                    onClick={() => {
                        if (message.length === 0) {
                            return;
                        }
                        sendMessage(message);
                        setMessage("");
                    }}
                    disabled={message.length === 0}
                >
                    <PaperPlaneIcon width={16} height={16} />
                </button>
            </div>
        </main>
    );
};

interface ChatViewProps {
    user: User;
    chatId: number;
    users: User[];
    messages: Message[];
    setInfoVisible?: (visible: boolean) => void;
    sendMessage: (message: string) => void;
}

export default ChatView;
