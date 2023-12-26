import { Chat, NotFoundError, UnauthorizedError } from "./entities";

export const getChats = async (): Promise<Chat[]> => {
    const user = JSON.parse(sessionStorage.getItem("user")!);

    if (!user) {
        throw new UnauthorizedError("Local user not found");
    }

    const chats: Chat[] = [];

    for (let i = 0; i < user.chats.length; i++) {
        const response = await fetch(
            `${import.meta.env.VITE_API_URL}/chats/${user.chats[i]}`,
            {
                method: "GET",
                headers: {
                    Accept: "application/json",
                },
                credentials: "include",
                mode: "cors",
            }
        );

        if (!response.ok) {
            if (response.status === 401) {
                throw new UnauthorizedError(response.statusText);
            }

            throw new Error(response.statusText);
        }

        chats.push(await response.json());
    }

    return chats;
};

export const getChat = async (chatId: number): Promise<Chat> => {
    const response = await fetch(
        `${import.meta.env.VITE_API_URL}/chats/${chatId}`,
        {
            method: "GET",
            headers: {
                Accept: "application/json",
            },
            credentials: "include",
            mode: "cors",
        }
    );

    if (!response.ok) {
        if (response.status === 404) {
            throw new NotFoundError("Chat not found");
        }

        if (response.status === 401) {
            throw new UnauthorizedError(response.statusText);
        }

        throw new Error(response.statusText);
    }

    return response.json();
};

export const updateChat = async (chat: Chat): Promise<Chat> => {
    const response = await fetch(
        `${import.meta.env.VITE_API_URL}/chats/${chat.id}`,
        {
            method: "PUT",
            headers: {
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            credentials: "include",
            mode: "cors",
            body: JSON.stringify(chat),
        }
    );

    if (!response.ok) {
        if (response.status === 404) {
            throw new NotFoundError("Chat not found");
        }

        if (response.status === 401) {
            throw new UnauthorizedError(response.statusText);
        }

        throw new Error(response.statusText);
    }

    return response.json();
};

export const deleteChat = async (chatId: number): Promise<void> => {
    const response = await fetch(
        `${import.meta.env.VITE_API_URL}/chats/${chatId}`,
        {
            method: "DELETE",
            headers: {
                Accept: "application/json",
            },
            credentials: "include",
            mode: "cors",
        }
    );

    if (!response.ok) {
        if (response.status === 404) {
            throw new NotFoundError("Chat not found");
        }

        if (response.status === 401) {
            throw new UnauthorizedError(response.statusText);
        }

        throw new Error(response.statusText);
    }
};

export const createChat = async (chat: BodyInit): Promise<Chat> => {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/chats`, {
        method: "POST",
        headers: {
            Accept: "application/json",
            "Content-Type": "application/json",
        },
        body: chat,
        credentials: "include",
        mode: "cors",
    });

    if (!response.ok) {
        if (response.status === 401) {
            throw new UnauthorizedError(response.statusText);
        }

        throw new Error(response.statusText);
    }

    return response.json();
};
