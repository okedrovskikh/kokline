import { useNavigate } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import { createChat, deleteChat } from "../api/chats";
import { User } from "../api/entities";
import { getMe, getUser, updateUser } from "../api/users";
import { useChat, useNotifier } from "../api/websockets";
import { ChatView, Popup, SettingsModal, Sidebar } from "../components";

const App = () => {
    const navigate = useNavigate({ from: "/" });

    const [user, setUser] = useState<User | null>(null);
    const [currentChat, setCurrentChat] = useState<number | null>(null);

    const [settingsVisible, setSettingsVisible] = useState(false);
    const [settingsOpening, setSettingsOpening] = useState(false);

    const [popupText, setPopupText] = useState("");
    const [popupType, setPopupType] = useState<"success" | "info" | "error">(
        "info"
    );
    const [popupOpen, setPopupOpen] = useState(false);
    const [popupOpening, setPopupOpening] = useState(false);

    const { chat, users, messages, sendMessage } = useChat(user, currentChat);
    const { lastMessage } = useNotifier();

    const handleChatCreate = async (userId: number) => {
        const user = await getUser(Math.abs(userId));

        if (!user) {
            return;
        }

        const chat = await createChat(
            JSON.stringify({
                name: user.name,
                users: [user.id],
            })
        );

        const me = await getMe();
        sessionStorage.setItem("user", JSON.stringify(me));
        setUser(me);

        setCurrentChat(chat.id);
    };

    const handleChatDelete = async () => {
        if (!chat) {
            return;
        }

        try {
            await deleteChat(chat.id);
            setPopupText("Chat deleted successfully");
            setPopupType("success");
        } catch (error) {
            setPopupText("An error occurred while deleting the chat");
            setPopupType("error");
            return;
        }
        if (!popupOpen) {
            setPopupOpen(true);
            setPopupOpening(true);
        }
        setCurrentChat(null);
    };

    const handleUserUpdate = async (body: BodyInit) => {
        await updateUser(body);
        const newUser = await getMe();

        setUser({ ...newUser });
        sessionStorage.setItem("user", JSON.stringify(newUser));

        setPopupText("User updated successfully");
        setPopupType("success");

        if (!popupOpen) {
            setPopupOpen(true);
            setPopupOpening(true);
        }
    };

    useEffect(() => {
        getMe()
            .then((user) => {
                sessionStorage.setItem("user", JSON.stringify(user));
                setUser(user);
            })
            .catch(() => {
                setUser(null);
                sessionStorage.removeItem("user");
                navigate({ to: "/login", replace: true });
            });
    }, []);

    useEffect(() => {
        getMe().then((user) => {
            setUser({ ...user });
            sessionStorage.setItem("user", JSON.stringify(user));

            if (currentChat) {
                if (!user.chats) {
                    setCurrentChat(null);
                }

                if (!user.chats?.includes(currentChat)) {
                    handleChatCreate(currentChat);
                }
            }
        });
    }, [currentChat]);

    const chatSelectionText = () => {
        return (
            <div className="chat-selection-wrapper">
                <span className="chat-selection-text">
                    Select a chat to start messaging
                </span>
            </div>
        );
    };

    if (!user) {
        return <></>;
    }

    return (
        <>
            <Sidebar
                user={user}
                currentChat={currentChat}
                setCurrentChat={setCurrentChat}
                settingsVisible={settingsVisible}
                setSettingsVisible={() => {
                    setSettingsVisible(true);
                    setSettingsOpening(true);
                }}
                lastMessage={lastMessage ?? undefined}
            />
            {currentChat && users.length > 0 ? (
                <ChatView
                    user={user!}
                    chat={chat}
                    users={users}
                    messages={messages}
                    sendMessage={sendMessage}
                    handleChatDelete={handleChatDelete}
                />
            ) : (
                chatSelectionText()
            )}
            <SettingsModal
                user={user}
                isOpen={settingsVisible}
                setIsOpen={setSettingsVisible}
                opening={settingsOpening}
                setOpening={setSettingsOpening}
                handleUserUpdate={handleUserUpdate}
            />
            <Popup
                text={popupText}
                type={popupType}
                isOpen={popupOpen}
                setIsOpen={setPopupOpen}
                opening={popupOpening}
                setOpening={setPopupOpening}
            />
        </>
    );
};

export default App;
