import { useEffect, useRef, useState } from "react";
import { Websocket, WebsocketBuilder, WebsocketEvent } from "websocket-ts";
import { Chat, Message, User } from "./entities";
import { getMessagesFromChat } from "./messages";
import { getUser } from "./users";

export const useNotifier = (userId: number | null) => {
    throw new Error("Not implemented");
};

export const useChat = (chatId: number | null) => {
    const [users, setUsers] = useState<User[]>([]);
    const [messages, setMessages] = useState<Message[]>([]);

    const socketRef = useRef<Websocket | null>(null);

    useEffect(() => {
        if (!chatId || chatId < 0) return;

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

        return () => {
            socketRef.current?.close();
        };
    }, [chatId]);

    const sendMessage = (message: string) => {
        socketRef.current?.send(JSON.stringify({ payload: { text: message } }));
    };

    return { users, messages, sendMessage };
};
