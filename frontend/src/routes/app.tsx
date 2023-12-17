import { useNavigate } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import { createChat } from "../api/chats";
import { User } from "../api/entities";
import { getMe, getUser } from "../api/users";
import { useChat } from "../api/websockets";
import { ChatView, SettingsModal, Sidebar } from "../components";

const App = () => {
    const navigate = useNavigate({ from: "/" });

    const [user, setUser] = useState<User | null>(null);
    const [currentChat, setCurrentChat] = useState<number | null>(null);

    const [settingsVisible, setSettingsVisible] = useState(false);
    const [settingsOpening, setSettingsOpening] = useState(false);

    const createUserChat = async (userId: number) => {
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
        if (currentChat) {
            getMe().then((user) => {
                if (!user.chats) {
                    setCurrentChat(null);
                }

                if (!user.chats?.includes(currentChat)) {
                    createUserChat(currentChat);
                }
            });
        }
    }, [currentChat]);

    const { users, messages, sendMessage } = useChat(currentChat);

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
            />
            {currentChat && users.length > 0 ? (
                <ChatView
                    user={user!}
                    chatId={currentChat}
                    users={users}
                    messages={messages}
                    sendMessage={sendMessage}
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
            />
        </>
    );
};

export default App;
