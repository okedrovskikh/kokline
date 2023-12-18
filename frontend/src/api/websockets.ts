import { useEffect, useRef, useState } from "react";
import { Websocket, WebsocketBuilder, WebsocketEvent } from "websocket-ts";
import placeholder from "../assets/placeholder.png";
import { getChat } from "./chats";
import { Chat, Message, User } from "./entities";
import { getMessage, getMessagesFromChat } from "./messages";
import { getUser } from "./users";

interface NotifierMessage {
    entity: string;
    operation: string;
    resourceId: number;
}

export interface DirectMessages extends Chat {
    username?: string;
}

export const useNotifier = () => {
    const [lastMessage, setLastMessage] = useState<Message | null>(null);

    const socketRef = useRef<Websocket | null>(null);

    useEffect(() => {
        if (!socketRef.current) {
            socketRef.current = new WebsocketBuilder(
                `${import.meta.env.VITE_WS_URL}/notifier/listen`
            ).build() as Websocket;

            socketRef.current.addEventListener(
                WebsocketEvent.message,
                (_ws, event) => {
                    const eventMessage: NotifierMessage = JSON.parse(
                        event.data
                    );
                    if (eventMessage.entity !== "MESSAGE") return;
                    // TODO: add other operations support
                    if (eventMessage.operation !== "CREATE") return;

                    getMessage(eventMessage.resourceId).then((message) => {
                        setLastMessage({ ...message });
                    });
                }
            );
        }

        return () => {
            if (socketRef.current?.readyState !== 1) return;
            socketRef.current?.close();
            socketRef.current = null;
        };
    }, []);

    return { lastMessage };
};

export const useChat = (user: User | null, chatId: number | null) => {
    const [chat, setChat] = useState<Chat | DirectMessages | null>(null);
    const [users, setUsers] = useState<User[]>([]);
    const [messages, setMessages] = useState<Message[]>([]);

    const socketRef = useRef<Websocket | null>(null);

    useEffect(() => {
        if (!chatId || !user || chatId < 0) return;

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

        fetch(`${import.meta.env.VITE_API_URL}/chats/${chatId}`, {
            method: "GET",
            credentials: "include",
            mode: "cors",
        })
            .then((response) => response.json())
            .then((chat: Chat) => {
                for (const userId of chat.users) {
                    getUser(userId).then((user) => {
                        setUsers((users) => [...users, user]);
                    });
                }
            });

        getMessagesFromChat(chatId, 0, 100).then((messages) => {
            setMessages(messages);
        });

        if (!socketRef.current) {
            socketRef.current = new WebsocketBuilder(
                `${import.meta.env.VITE_WS_URL}/chat/joinChat?id=${chatId}`
            ).build() as Websocket;

            socketRef.current.addEventListener(
                WebsocketEvent.message,
                (_ws, event) => {
                    setMessages((messages) => [
                        JSON.parse(event.data),
                        ...messages,
                    ]);
                }
            );
        }

        return () => {
            if (socketRef.current?.readyState !== 1) return;
            socketRef.current?.close();
            socketRef.current = null;
        };
    }, [chatId]);

    const sendMessage = (message: string) => {
        socketRef.current?.send(JSON.stringify({ payload: { text: message } }));
    };

    return { chat, users, messages, sendMessage };
};
